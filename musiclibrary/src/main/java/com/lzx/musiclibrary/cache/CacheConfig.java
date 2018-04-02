package com.lzx.musiclibrary.cache;

import android.app.PendingIntent;
import android.os.Parcel;
import android.os.Parcelable;

import com.danikula.videocache.file.DiskUsage;
import com.danikula.videocache.file.FileNameGenerator;
import com.lzx.musiclibrary.aidl.source.IFileNameGenerator;
import com.lzx.musiclibrary.notification.NotificationCreater;

/**
 * Created by xian on 2018/4/2.
 */

public class CacheConfig implements Parcelable {

    private boolean openCacheWhenPlaying = false;
    private String cachePath;
    private int maxCacheSize = 0;
    private int maxCacheFilesCount = 0;
    private IFileNameGenerator mIFileNameGenerator;

    private CacheConfig(Builder builder) {
        this.openCacheWhenPlaying = builder.openCacheWhenPlaying;
        this.cachePath = builder.cachePath;
        this.maxCacheSize = builder.maxCacheSize;
        this.maxCacheFilesCount = builder.maxCacheFilesCount;
        this.mIFileNameGenerator = builder.mIFileNameGenerator;
    }

    public static class Builder {
        private boolean openCacheWhenPlaying = false;
        private String cachePath;
        private int maxCacheSize = 0;
        private int maxCacheFilesCount = 0;
        private IFileNameGenerator mIFileNameGenerator;

        public Builder setOpenCacheWhenPlaying(boolean openCacheWhenPlaying) {
            this.openCacheWhenPlaying = openCacheWhenPlaying;
            return this;
        }

        public Builder setCachePath(String cachePath) {
            this.cachePath = cachePath;
            return this;
        }

        public Builder setMaxCacheSize(int maxCacheSize) {
            this.maxCacheSize = maxCacheSize;
            return this;
        }

        public Builder setMaxCacheFilesCount(int maxCacheFilesCount) {
            this.maxCacheFilesCount = maxCacheFilesCount;
            return this;
        }

        public Builder setIFileNameGenerator(IFileNameGenerator IFileNameGenerator) {
            mIFileNameGenerator = IFileNameGenerator;
            return this;
        }

        public CacheConfig build() {
            return new CacheConfig(this);
        }
    }


    public String getCachePath() {
        return cachePath;
    }

    public int getMaxCacheSize() {
        return maxCacheSize;
    }

    public int getMaxCacheFilesCount() {
        return maxCacheFilesCount;
    }

    public boolean isOpenCacheWhenPlaying() {
        return openCacheWhenPlaying;
    }

    public IFileNameGenerator getIFileNameGenerator() {
        return mIFileNameGenerator;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.openCacheWhenPlaying ? (byte) 1 : (byte) 0);
        dest.writeString(this.cachePath);
        dest.writeInt(this.maxCacheSize);
        dest.writeInt(this.maxCacheFilesCount);
    }


    protected CacheConfig(Parcel in) {
        this.openCacheWhenPlaying = in.readByte() != 0;
        this.cachePath = in.readString();
        this.maxCacheSize = in.readInt();
        this.maxCacheFilesCount = in.readInt();
    }

    public static final Parcelable.Creator<CacheConfig> CREATOR = new Parcelable.Creator<CacheConfig>() {
        @Override
        public CacheConfig createFromParcel(Parcel source) {
            return new CacheConfig(source);
        }

        @Override
        public CacheConfig[] newArray(int size) {
            return new CacheConfig[size];
        }
    };
}
