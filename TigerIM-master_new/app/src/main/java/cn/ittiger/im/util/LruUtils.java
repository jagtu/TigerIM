package cn.ittiger.im.util;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * <p></p>
 * <p>
 * Created by zhangli on 2018/4/18 15:08.<br/>
 * Copyright (c) 2015年 Beijing Yunshan Information Technology Co., Ltd. All rights reserved.<br/>
 */
public class LruUtils {

    private static LruUtils instance = new LruUtils();

    private LruCache<String, Bitmap> mMemoryCache;

    private LruUtils() {

        //获取系统分配给每个应用程序的最大内存，每个应用系统分配32M
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int mCacheSize = maxMemory / 8;
        //给LruCache分配1/8 4M
        mMemoryCache = new LruCache<String, Bitmap>(mCacheSize) {

            //必须重写此方法，来测量Bitmap的大小
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }

        };
    }

    public static LruUtils getInstance() {
        return instance;
    }

    public LruCache<String, Bitmap> getMemoryCache() {
        return mMemoryCache;
    }
}
