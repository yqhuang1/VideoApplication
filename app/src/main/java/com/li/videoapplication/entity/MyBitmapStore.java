package com.li.videoapplication.entity;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2015/11/23 0023.
 */
public class MyBitmapStore {
    static private Bitmap bmp = null;

    public static Bitmap getBmp() {
        return bmp;
    }

    public static void setBmp(Bitmap bmp) {
        MyBitmapStore.bmp = bmp;
    }
}
