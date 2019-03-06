/*
 * Copyright (c) 2016. André Mion
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lzx.nicemusic.activities

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.transition.Transition
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.bumptech.glide.Glide
import com.lzx.nicemusic.R
import com.lzx.nicemusic.utils.Utils
import com.lzx.nicemusic.view.ProgressView
import com.lzx.nicemusic.view.TransitionAdapter
import com.lzx.nicemusic.view.musiccoverview.MusicCoverView
import com.lzx.starrysky.manager.MusicManager
import com.lzx.starrysky.manager.OnPlayerEventListener
import com.lzx.starrysky.model.SongInfo
import com.lzx.starrysky.utils.TimerTaskManager

class DetailActivity : PlayerActivity(), MusicCoverView.Callbacks, OnPlayerEventListener, View.OnClickListener {

    private var mCoverView: MusicCoverView? = null
    private var mSongTitle: TextView? = null
    private var mArtist: TextView? = null
    private var mRepeatView: ImageView? = null
    private var mShuffleView: ImageView? = null
    private var mPrevious: ImageView? = null
    private var mRewind: ImageView? = null
    private var mForward: ImageView? = null
    private var mNext: ImageView? = null
    private var mTimerTaskManager: TimerTaskManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_detail)

        mCoverView = findViewById(R.id.cover)
        mSongTitle = findViewById(R.id.song_title)
        mArtist = findViewById(R.id.artist)
        mRepeatView = findViewById(R.id.repeat)
        mShuffleView = findViewById(R.id.shuffle)
        mPrevious = findViewById(R.id.previous)
        mRewind = findViewById(R.id.rewind)
        mForward = findViewById(R.id.forward)
        mNext = findViewById(R.id.next)

        mTimerTaskManager = TimerTaskManager()

        val songInfo = MusicManager.getInstance().nowPlayingSongInfo
        updateUI(songInfo)

        mCoverView!!.setCallbacks(this)

        window.sharedElementEnterTransition.addListener(object : TransitionAdapter() {
            override fun onTransitionEnd(transition: Transition) {
                MusicManager.getInstance().playMusic()
                mCoverView!!.start()
            }
        })
        MusicManager.getInstance().addPlayerEventListener(this)

        mTimerTaskManager!!.setUpdateProgressTask {
            val position = MusicManager.getInstance().playingPosition
            val duration = MusicManager.getInstance().duration / 1000
            if (mProgressView!!.max.toLong() != duration) {
                mProgressView!!.max = duration.toInt()
                mDurationView!!.text = Utils.formatMusicTime(duration)
            }
            mProgressView!!.progress = position.toInt()
            mDurationView!!.text = Utils.formatMusicTime(duration)
            mTimeView!!.text = Utils.formatMusicTime(position)
        }

        if (MusicManager.getInstance().isPlaying) {
            mTimerTaskManager!!.startToUpdateProgress()
        }

        mRepeatView!!.setOnClickListener(this)
        mShuffleView!!.setOnClickListener(this)
        mPrevious!!.setOnClickListener(this)
        mRewind!!.setOnClickListener(this)
        mForward!!.setOnClickListener(this)
        mNext!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.repeat -> {
                val repeatMode = MusicManager.getInstance().repeatMode
                when (repeatMode) {
                    PlaybackStateCompat.REPEAT_MODE_NONE -> {

                        MusicManager.getInstance().repeatMode = PlaybackStateCompat.REPEAT_MODE_ONE
                        Toast.makeText(this, "设置为单曲循环", Toast.LENGTH_SHORT).show()
                        mRepeatView!!.setImageResource(R.drawable.ic_repeat_one_white_24dp)

                    }
                    PlaybackStateCompat.REPEAT_MODE_ONE -> {
                        MusicManager.getInstance().repeatMode = PlaybackStateCompat.REPEAT_MODE_ALL
                        Toast.makeText(this, "设置为列表循环", Toast.LENGTH_SHORT).show()
                        mRepeatView!!.setImageResource(R.drawable.ic_repeat_white_24dp)

                    }
                    PlaybackStateCompat.REPEAT_MODE_ALL -> {
                        MusicManager.getInstance().repeatMode = PlaybackStateCompat.REPEAT_MODE_NONE
                        mRepeatView!!.setImageResource(R.drawable.ic_mode_none_white_24dp)
                        Toast.makeText(this, "设置为列表播放", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            R.id.shuffle -> {
                val shuffleMode = MusicManager.getInstance().shuffleMode

                if (shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_NONE) {
                    MusicManager.getInstance().shuffleMode = PlaybackStateCompat.SHUFFLE_MODE_ALL
                    mShuffleView!!.setImageResource(R.drawable.ic_shuffle_white_24dp)
                    Toast.makeText(this, "设置为随机播放", Toast.LENGTH_SHORT).show()

                } else {
                    MusicManager.getInstance().shuffleMode = PlaybackStateCompat.SHUFFLE_MODE_NONE
                    mShuffleView!!.setImageResource(R.drawable.ic_repeat_all_white_24dp)
                    Toast.makeText(this, "设置为顺序播放", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.previous -> if (MusicManager.getInstance().isSkipToPreviousEnabled) {
                MusicManager.getInstance().skipToPrevious()
            } else {
                Toast.makeText(this, "没有上一首了", Toast.LENGTH_SHORT).show()
            }
            R.id.rewind -> MusicManager.getInstance().rewind()
            R.id.forward -> MusicManager.getInstance().fastForward()
            R.id.next -> if (MusicManager.getInstance().isSkipToNextEnabled) {
                MusicManager.getInstance().skipToNext()
            } else {
                Toast.makeText(this, "没有下一首了", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(songInfo: SongInfo?) {
        if (songInfo != null && !isDestroyed && !isFinishing) {
            Glide.with(this).load(songInfo.songCover).into(mCoverView!!)
            mSongTitle!!.text = songInfo.songName
            mArtist!!.text = songInfo.artist
        } else {
            mCoverView!!.setImageResource(R.drawable.album_cover_daft_punk)
        }
    }

    override fun onMorphEnd(coverView: MusicCoverView) {
        // Nothing to do
    }

    override fun onRotateEnd(coverView: MusicCoverView) {
        supportFinishAfterTransition()
    }

    fun onFabClick(view: View) {
        MusicManager.getInstance().pauseMusic()
        mCoverView!!.stop()
    }

    override fun onBackPressed() {
        mCoverView!!.stop()
    }

    override fun onMusicSwitch(songInfo: SongInfo) {
        updateUI(songInfo)
    }

    override fun onPlayerStart() {
        mTimerTaskManager!!.startToUpdateProgress()
        mCoverView!!.start()
    }

    override fun onPlayerPause() {
        mTimerTaskManager!!.stopToUpdateProgress()
        mCoverView!!.stop()
    }

    override fun onPlayerStop() {
        mTimerTaskManager!!.stopToUpdateProgress()
    }

    override fun onPlayCompletion(songInfo: SongInfo) {
        mTimerTaskManager!!.stopToUpdateProgress()
    }

    override fun onBuffering() {

    }

    override fun onError(errorCode: Int, errorMsg: String) {
        mTimerTaskManager!!.stopToUpdateProgress()
    }

    override fun onDestroy() {
        super.onDestroy()
        mTimerTaskManager!!.removeUpdateProgressTask()
    }


}
