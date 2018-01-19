package com.lzx.nicemusic.lib.model;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;

import com.lzx.nicemusic.R;
import com.lzx.nicemusic.lib.bean.MusicInfo;
import com.lzx.nicemusic.lib.utils.MediaIDHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.lzx.nicemusic.lib.utils.MediaIDHelper.MEDIA_ID_MUSICS_BY_GENRE;
import static com.lzx.nicemusic.lib.utils.MediaIDHelper.MEDIA_ID_ROOT;

/**
 * @author lzx
 * @date 2018/1/16
 */

public class MusicProvider {

    private MusicProviderSource mSource;
    //根据id来做索引的音乐列表
    private ConcurrentMap<String, MutableMediaMetadata> mMusicListById;
    //根据albumId来做索引的音乐列表
    private ConcurrentMap<String, List<MusicInfo>> mMusicListByAlbumId;

    /**
     * 初始化状态
     */
    enum State {
        NON_INITIALIZED, INITIALIZING, INITIALIZED
    }

    private volatile State mCurrentState = State.NON_INITIALIZED;

    public interface Callback {
        void onMusicCatalogReady(boolean success);
    }

    public MusicProvider(MusicProviderSource source) {
        mSource = source;
        mMusicListById = new ConcurrentHashMap<>();
        mMusicListByAlbumId = new ConcurrentHashMap<>();
    }

    /**
     * 获取专辑id列表的迭代器
     */
    public Iterable<String> getAlbumIds() {
        if (mCurrentState != State.INITIALIZED) {
            return Collections.emptyList();
        }
        return mMusicListByAlbumId.keySet();
    }

    /**
     * 得到随机排序的歌曲列表
     */
    public Iterable<MusicInfo> getShuffledMusic() {
        if (mCurrentState != State.INITIALIZED) {
            return Collections.emptyList();
        }
        List<MusicInfo> shuffled = new ArrayList<>(mMusicListById.size());
        for (MutableMediaMetadata mutableMetadata : mMusicListById.values()) {
            shuffled.add(mutableMetadata.metadata);
        }
        Collections.shuffle(shuffled);
        return shuffled;
    }

    /**
     * 根据专辑id获取音乐列表
     *
     * @param albumId
     * @return
     */
    public List<MusicInfo> getMusicsByAlbumId(String albumId) {
        if (mCurrentState != State.INITIALIZED || !mMusicListByAlbumId.containsKey(albumId)) {
            return Collections.emptyList();
        }
        return mMusicListByAlbumId.get(albumId);
    }

    /**
     * 搜索 包含标题的音乐曲目
     */
    public List<MusicInfo> searchMusicBySongTitle(String query) {
        return searchMusic(MediaMetadataCompat.METADATA_KEY_TITLE, query);
    }

    /**
     * 搜索 包含专辑名称的音乐曲目
     */
    public List<MusicInfo> searchMusicByAlbum(String query) {
        return searchMusic(MediaMetadataCompat.METADATA_KEY_ALBUM, query);
    }

    /**
     * 搜索 包含艺术家名字的音乐曲目
     */
    public List<MusicInfo> searchMusicByArtist(String query) {
        return searchMusic(MediaMetadataCompat.METADATA_KEY_ARTIST, query);
    }

    /**
     * 搜索 包含音乐流派的音乐曲目
     */
    public List<MusicInfo> searchMusicByGenre(String query) {
        return searchMusic(MediaMetadataCompat.METADATA_KEY_GENRE, query);
    }

    /**
     * 搜索音乐
     *
     * @param metadataField
     * @param query
     * @return
     */
    private List<MusicInfo> searchMusic(String metadataField, String query) {
        if (mCurrentState != State.INITIALIZED) {
            return Collections.emptyList();
        }
        ArrayList<MusicInfo> result = new ArrayList<>();
        query = query.toLowerCase(Locale.US);
        for (MutableMediaMetadata track : mMusicListById.values()) {
            if (track.metadata.metadataCompat.getString(metadataField).toLowerCase(Locale.US).contains(query)) {
                result.add(track.metadata);
            }
        }
        return result;
    }

    /**
     * 根据音乐id获取音乐信息
     *
     * @param musicId
     * @return
     */
    public MusicInfo getMusic(String musicId) {
        return mMusicListById.containsKey(musicId) ? mMusicListById.get(musicId).metadata : null;
    }

    public MediaMetadataCompat getMediaMetadataCompatById(String musicId) {
        MusicInfo musicInfo = getMusic(musicId);
        return musicInfo == null ? null : musicInfo.metadataCompat;
    }

    /**
     * 更新媒体信息
     *
     * @param musicId
     * @param albumArt
     * @param icon
     */
    public synchronized void updateMusicArt(String musicId, Bitmap albumArt, Bitmap icon) {
        MediaMetadataCompat metadata = getMediaMetadataCompatById(musicId);
        metadata = new MediaMetadataCompat.Builder(metadata)

                // set high resolution bitmap in METADATA_KEY_ALBUM_ART. This is used, for
                // example, on the lockscreen background when the media session is active.
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)

                // set small version of the album art in the DISPLAY_ICON. This is used on
                // the MediaDescription and thus it should be small to be serialized if
                // necessary
                .putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, icon)

                .build();

        MutableMediaMetadata mutableMetadata = mMusicListById.get(musicId);
        if (mutableMetadata == null) {
            throw new IllegalStateException("Unexpected error: Inconsistent data structures in " +
                    "MusicProvider");
        }

        mutableMetadata.metadata.metadataCompat = metadata;
    }


    public boolean isInitialized() {
        return mCurrentState == State.INITIALIZED;
    }


    /**
     * 从服务器获取音乐曲目列表并缓存曲目信息
     * 供将来参考，按musicId键入音轨并按流派分组。
     * Get the list of music tracks from a server and caches the track information
     * for future reference, keying tracks by musicId and grouping by genre.
     */
    public void retrieveMediaAsync(final Callback callback) {
        if (mCurrentState == State.INITIALIZED) {
            if (callback != null) {
                // Nothing to do, execute callback immediately
                callback.onMusicCatalogReady(true);
            }
            return;
        }

        // Asynchronously load the music catalog in a separate thread
        new AsyncTask<Void, Void, State>() {
            @Override
            protected State doInBackground(Void... params) {
                retrieveMedia();
                return mCurrentState;
            }

            @Override
            protected void onPostExecute(State current) {
                if (callback != null) {
                    callback.onMusicCatalogReady(current == State.INITIALIZED);
                }
            }
        }.execute();
    }

    private synchronized void buildListsById() {
        ConcurrentMap<String, List<MusicInfo>> newMusicListByAlbumId = new ConcurrentHashMap<>();

        for (MutableMediaMetadata m : mMusicListById.values()) {
            String genre = m.metadata.musicGenre;
            List<MusicInfo> list = newMusicListByAlbumId.get(genre);
            if (list == null) {
                list = new ArrayList<>();
                newMusicListByAlbumId.put(genre, list);
            }
            list.add(m.metadata);
        }
        mMusicListByAlbumId = newMusicListByAlbumId;
    }

    /**
     * 异步获取音频
     */
    private synchronized void retrieveMedia() {
        try {
            if (mCurrentState == State.NON_INITIALIZED) {
                mCurrentState = State.INITIALIZING;

                //Iterator<MediaMetadataCompat> tracks = mSource.iterator();
                Iterator<MusicInfo> tracks = mSource.iterator();
                while (tracks.hasNext()) {
                    //MediaMetadataCompat item = tracks.next();
                    MusicInfo item = tracks.next();
                    //String musicId = item.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);
                    String musicId = item.musicId;
                    mMusicListById.put(musicId, new MutableMediaMetadata(musicId, item));
                }
                buildListsById();
                mCurrentState = State.INITIALIZED;
            }
        } finally {
            if (mCurrentState != State.INITIALIZED) {
                // Something bad happened, so we reset state to NON_INITIALIZED to allow
                // retries (eg if the network connection is temporary unavailable)
                mCurrentState = State.NON_INITIALIZED;
            }
        }
    }


    public List<MusicInfo> getChildren(String mediaId, Resources resources) {
        List<MusicInfo> mediaItems = new ArrayList<>();

        if (!MediaIDHelper.isBrowseable(mediaId)) {
            return mediaItems;
        }

        if (MEDIA_ID_ROOT.equals(mediaId)) {
            mediaItems.add(createBrowsableMediaItemForRoot(resources));

        } else if (MEDIA_ID_MUSICS_BY_GENRE.equals(mediaId)) {
            for (String genre : getGenres()) {
                mediaItems.add(createBrowsableMediaItemForGenre(genre, resources));
            }

        } else if (mediaId.startsWith(MEDIA_ID_MUSICS_BY_GENRE)) {
            String genre = MediaIDHelper.getHierarchy(mediaId)[1];
            for (MediaMetadataCompat metadata : getMusicsByGenre(genre)) {
                mediaItems.add(createMediaItem(metadata));
            }

        } else {
        }
        return mediaItems;
    }

    private MediaBrowserCompat.MediaItem createBrowsableMediaItemForRoot(Resources resources) {
        MediaDescriptionCompat description = new MediaDescriptionCompat.Builder()
                .setMediaId(MEDIA_ID_MUSICS_BY_GENRE)
                .setTitle(resources.getString(R.string.browse_genres))
                .setSubtitle(resources.getString(R.string.browse_genre_subtitle))
                .setIconUri(Uri.parse("android.resource://" + "com.example.android.uamp/drawable/ic_by_genre"))
                .build();
        return new MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
    }

    private MediaBrowserCompat.MediaItem createBrowsableMediaItemForGenre(String genre,
                                                                          Resources resources) {
        MediaDescriptionCompat description = new MediaDescriptionCompat.Builder()
                .setMediaId(MediaIDHelper.createMediaID(null, MEDIA_ID_MUSICS_BY_GENRE, genre))
                .setTitle(genre)
                .setSubtitle(resources.getString(
                        R.string.browse_musics_by_genre_subtitle, genre))
                .build();
        return new MediaBrowserCompat.MediaItem(description,
                MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
    }

    private MediaBrowserCompat.MediaItem createMediaItem(MediaMetadataCompat metadata) {
        // Since mediaMetadata fields are immutable, we need to create a copy, so we
        // can set a hierarchy-aware mediaID. We will need to know the media hierarchy
        // when we get a onPlayFromMusicID call, so we can create the proper queue based
        // on where the music was selected from (by artist, by genre, random, etc)
        String genre = metadata.getString(MediaMetadataCompat.METADATA_KEY_GENRE);
        String hierarchyAwareMediaID = MediaIDHelper.createMediaID(
                metadata.getDescription().getMediaId(), MEDIA_ID_MUSICS_BY_GENRE, genre);
        MediaMetadataCompat copy = new MediaMetadataCompat.Builder(metadata)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, hierarchyAwareMediaID)
                .build();
        return new MediaBrowserCompat.MediaItem(copy.getDescription(),
                MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);

    }
}
