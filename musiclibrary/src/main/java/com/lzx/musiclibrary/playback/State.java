package com.lzx.musiclibrary.playback;

/**
 * @author lzx
 * @date 2018/2/3
 */

public class State {
    public final static int STATE_IDLE = 1;
    public final static int STATE_BUFFERING = 2;
    public final static int STATE_PLAYING = 3;
    public final static int STATE_PAUSED = 4;
    public final static int STATE_ENDED = 5;
    public final static int STATE_STOPPED = 6;
    public final static int STATE_NONE = 7;
    public final static int STATE_ERROR = 8;
}
