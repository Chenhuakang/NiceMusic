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

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.bumptech.glide.Glide
import com.lzx.nicemusic.R
import com.lzx.nicemusic.activities.model.MainViewModel
import com.lzx.nicemusic.bean.Personalized
import com.lzx.nicemusic.utils.InjectorUtils
import com.lzx.nicemusic.utils.LogUtil
import com.lzx.nicemusic.utils.Utils
import com.lzx.nicemusic.view.ProgressView
import com.lzx.nicemusic.view.RecyclerViewAdapter
import com.lzx.starrysky.manager.MediaSessionConnection
import com.lzx.starrysky.manager.MusicManager
import com.lzx.starrysky.manager.OnPlayerEventListener
import com.lzx.starrysky.model.SongInfo
import com.lzx.starrysky.utils.TimerTaskManager
import io.reactivex.functions.Consumer


class MainActivity : PlayerActivity(), OnPlayerEventListener {

    private var mCoverView: ImageView? = null
    private var mTitleView: View? = null
    private var mFabView: View? = null
    private var mSongTitle: TextView? = null
    private var mArtist: TextView? = null
    private var mName: TextView? = null
    private var mCounter: TextView? = null
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: RecyclerViewAdapter? = null

    private var mViewModel: MainViewModel? = null

    private var mTimerTaskManager: TimerTaskManager? = null

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_list)
        mCoverView = findViewById(R.id.cover)
        mTitleView = findViewById(R.id.title)
        mFabView = findViewById(R.id.fab)
        mSongTitle = findViewById(R.id.song_title)
        mArtist = findViewById(R.id.artist)
        mName = findViewById(R.id.name)
        mCounter = findViewById(R.id.counter)

        mRecyclerView = findViewById(R.id.tracks)

        MediaSessionConnection.getInstance().connect()

        mRecyclerView!!.layoutManager = LinearLayoutManager(this)
        mAdapter = RecyclerViewAdapter(this)
        mRecyclerView!!.adapter = mAdapter
        mAdapter!!.setOnItemClickListener(object : RecyclerViewAdapter.onItemClickListener {
            override fun onItemClick(personalized: Personalized, position: Int) {
                updatePlayList(personalized, position)
            }

            override fun onItemClick(list: List<SongInfo>, position: Int) {
                MusicManager.getInstance().playMusicByInfo(list[position])
            }
        })

        mViewModel = ViewModelProviders
                .of(this, InjectorUtils.provideMainActivityViewModel(this))
                .get(MainViewModel::class.java)

        mTimerTaskManager = TimerTaskManager()

        MusicManager.getInstance().addPlayerEventListener(this)

        mTimerTaskManager!!.setUpdateProgressTask {
            val position = MusicManager.getInstance().playingPosition
            val duration = MusicManager.getInstance().duration / 1000
            if (mProgressView!!.max.toLong() != duration) {
                mProgressView!!.max = duration.toInt()
                mDurationView!!.text = Utils.formatMusicTime(duration)
            }
            mProgressView!!.progress = position.toInt()
            mTimeView!!.text = Utils.formatMusicTime(position)
        }

        mViewModel!!.requestPersonalized().subscribe({
            mAdapter!!.setPersonalizeds(it)
        }, { it.printStackTrace() })
    }

    @SuppressLint("CheckResult", "SetTextI18n")
    fun updatePlayList(personalized: Personalized, position: Int) {
        mViewModel!!.requestPlayListDetail(personalized.id)
                .subscribe({
                    if (it.size > 0) {
                        MusicManager.getInstance().updatePlayList(it)
                        mAdapter!!.isShowPlayList = true
                        mAdapter!!.setSongInfos(it, false)
                        val songInfo = it[0]
                        mName!!.text = songInfo.albumName
                        mCounter!!.text = "By:" + songInfo.albumArtist
                    }
                }, { it.printStackTrace() })
    }

    fun onFabClick(view: View) {
        if (MusicManager.getInstance().playList.size == 0) {
            val songInfo = SongInfo()
            songInfo.songId = "30431376"
            songInfo.songName = "易燃易爆炸"
            songInfo.artist = "陈粒"
            songInfo.albumName = "如也"
            songInfo.duration = 200000
            songInfo.songCover = "http://img.jammyfm.com/wordpress/wp-content/uploads/2017/07/201707261110447854.jpg"
            songInfo.songUrl = "http://music.163.com/song/media/outer/url?id=" + songInfo.songId + ".mp3"
            MusicManager.getInstance().playMusicByInfo(songInfo)
        }

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                Pair(mCoverView, ViewCompat.getTransitionName(mCoverView!!)),
                Pair(mTitleView, ViewCompat.getTransitionName(mTitleView!!)),
                Pair(mTimeView, ViewCompat.getTransitionName(mTimeView!!)),
                Pair(mDurationView, ViewCompat.getTransitionName(mDurationView!!)),
                Pair(mProgressView, ViewCompat.getTransitionName(mProgressView!!)),
                Pair(mFabView, ViewCompat.getTransitionName(mFabView!!)))
        ActivityCompat.startActivity(this, Intent(this, DetailActivity::class.java), options.toBundle())
    }

    override fun onMusicSwitch(songInfo: SongInfo) {
        LogUtil.i("= onMusicSwitch = " + songInfo.songName)
        Glide.with(this)
                .load(songInfo.songCover)
                .into(mCoverView!!)
        mSongTitle!!.text = songInfo.songName
        mArtist!!.text = songInfo.artist
    }

    override fun onPlayerStart() {
        mTimerTaskManager!!.startToUpdateProgress()
    }

    override fun onPlayerPause() {
        mTimerTaskManager!!.stopToUpdateProgress()
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
        Toast.makeText(this, "播放失败", Toast.LENGTH_SHORT).show()
        LogUtil.i("errorCode = $errorCode errorMsg = $errorMsg")
    }

    override fun onBackPressed() {
        if (mAdapter!!.isShowPlayList) {
            mAdapter!!.isShowPlayList = false
            mName!!.text = "精品歌单"
            mCounter!!.text = "属于你的歌单"
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mTimerTaskManager!!.removeUpdateProgressTask()
        MusicManager.getInstance().removePlayerEventListener(this)
    }
}
