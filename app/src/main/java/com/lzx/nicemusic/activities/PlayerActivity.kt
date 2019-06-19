package com.lzx.nicemusic.activities

import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.lzx.nicemusic.R
import com.lzx.nicemusic.view.ProgressView

abstract class PlayerActivity : AppCompatActivity() {
    var mTimeView: TextView? = null
    var mDurationView: TextView? = null
    var mProgressView: ProgressView? = null

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        mTimeView = findViewById(R.id.time)
        mDurationView = findViewById(R.id.duration)
        mProgressView = findViewById(R.id.progress)
    }
}