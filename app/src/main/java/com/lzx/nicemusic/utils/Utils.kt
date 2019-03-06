package com.lzx.nicemusic.utils

object Utils {
    fun formatMusicTime(duration: Long): String {
        var time = ""
        val minute = duration / 60000
        val seconds = duration % 60000
        val second = Math.round((seconds.toInt() / 1000).toFloat()).toLong()
        if (minute < 10) {
            time += "0"
        }
        time += "$minute:"
        if (second < 10) {
            time += "0"
        }
        time += second
        return time
    }
}
