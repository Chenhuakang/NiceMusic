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
import java.util.Set;
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
    //private ConcurrentMap<String, List<MediaMetadataCompat>> mMusicListByGenre;
    private ConcurrentMap<String, List<MusicInfo>> mMusicListByGenre;
    private final ConcurrentMap<String, MutableMediaMetadata> mMusicListById;
    //喜欢列表
    private final Set<String> mFavoriteTracks;

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

//    public MusicProvider() {
//        this(new RemoteJSONSource()); //加载音乐数据
//    }

    public MusicProvider(MusicProviderSource source) {
        mSource = source;
        mMusicListByGenre = new ConcurrentHashMap<>();
        mMusicListById = new ConcurrentHashMap<>();
        mFavoriteTracks = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    }

    /**
     * Get an iterator over the list of genres
     * 初始化完成后获取列表迭代器
     *
     * @return genres
     */
    public Iterable<String> getGenres() {
        if (mCurrentState != State.INITIALIZED) {
            return Collections.emptyList();
        }
        return mMusicListByGenre.keySet();
    }

    /**
     * 获取所有歌曲的洗牌集合的迭代器(随机排序)
     * Get an iterator over a shuffled collection of all songs
     */
//    public Iterable<MediaMetadataCompat> getShuffledMusic() {
//        if (mCurrentState != State.INITIALIZED) {
//            return Collections.emptyList();
//        }
//        List<MediaMetadataCompat> shuffled = new ArrayList<>(mMusicListById.size());
//        for (MutableMediaMetadata mutableMetadata : mMusicListById.values()) {
//            shuffled.add(mutableMetadata.metadata);
//        }
//        Collections.shuffle(shuffled);
//        return shuffled;
//    }
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
     * 获取给定流派的音乐曲目
     * Get music tracks of the given genre
     */
//    public List<MediaMetadataCompat> getMusicsByGenre(String genre) {
//        if (mCurrentState != State.INITIALIZED || !mMusicListByGenre.containsKey(genre)) {
//            return Collections.emptyList();
//        }
//        return mMusicListByGenre.get(genre);
//    }
    public List<MusicInfo> getMusicsByGenre(String genre) {
        if (mCurrentState != State.INITIALIZED || !mMusicListByGenre.containsKey(genre)) {
            return Collections.emptyList();
        }
        return mMusicListByGenre.get(genre);
    }


    /**
     * Very basic implementation of a search that filter music tracks with title containing
     * the given query.
     * 搜索 包含标题的音乐曲目
     */
    public List<MusicInfo> searchMusicBySongTitle(String query) {
        return searchMusic(MediaMetadataCompat.METADATA_KEY_TITLE, query);
    }

    /**
     * Very basic implementation of a search that filter music tracks with album containing
     * the given query.
     */
    public List<MusicInfo> searchMusicByAlbum(String query) {
        return searchMusic(MediaMetadataCompat.METADATA_KEY_ALBUM, query);
    }

    /**
     * Very basic implementation of a search that filter music tracks with artist containing
     * the given query.
     */
    public List<MusicInfo> searchMusicByArtist(String query) {
        return searchMusic(MediaMetadataCompat.METADATA_KEY_ARTIST, query);
    }

    /**
     * Very basic implementation of a search that filter music tracks with a genre containing
     * the given query.
     */
    public List<MusicInfo> searchMusicByGenre(String query) {
        return searchMusic(MediaMetadataCompat.METADATA_KEY_GENRE, query);
    }

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
//    private List<MediaMetadataCompat> searchMusic(String metadataField, String query) {
//        if (mCurrentState != State.INITIALIZED) {
//            return Collections.emptyList();
//        }
//        ArrayList<MediaMetadataCompat> result = new ArrayList<>();
//        query = query.toLowerCase(Locale.US);
//        for (MutableMediaMetadata track : mMusicListById.values()) {
//            if (track.metadata.getString(metadataField).toLowerCase(Locale.US)
//                    .contains(query)) {
//                result.add(track.metadata);
//            }
//        }
//        return result;
//    }


    /**
     * Return the MediaMetadataCompat for the given musicID.
     *
     * @param musicId The unique, non-hierarchical music ID.
     */
    public MediaMetadataCompat getMusic(String musicId) {
        return mMusicListById.containsKey(musicId) ? mMusicListById.get(musicId).metadata : null;
    }

    public synchronized void updateMusicArt(String musicId, Bitmap albumArt, Bitmap icon) {
        MediaMetadataCompat metadata = getMusic(musicId);
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

        mutableMetadata.metadata = metadata;
    }

    /**
     * 设置喜爱音乐
     *
     * @param musicId
     * @param favorite
     */
    public void setFavorite(String musicId, boolean favorite) {
        if (favorite) {
            mFavoriteTracks.add(musicId);
        } else {
            mFavoriteTracks.remove(musicId);
        }
    }

    public boolean isInitialized() {
        return mCurrentState == State.INITIALIZED;
    }

    public boolean isFavorite(String musicId) {
        return mFavoriteTracks.contains(musicId);
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

    private synchronized void buildListsByGenre() {
        ConcurrentMap<String, List<MusicInfo>> newMusicListByGenre = new ConcurrentHashMap<>();

        for (MutableMediaMetadata m : mMusicListById.values()) {
            String genre = m.metadata.musicGenre;
            List<MusicInfo> list = newMusicListByGenre.get(genre);
            if (list == null) {
                list = new ArrayList<>();
                newMusicListByGenre.put(genre, list);
            }
            list.add(m.metadata);
        }
        mMusicListByGenre = newMusicListByGenre;
    }

    /**
     * 获取音频
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
                buildListsByGenre();
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
        return new MediaBrowserCompat.MediaItem(description,  MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
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
