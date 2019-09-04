package com.li.videoapplication.utils;

import android.app.Activity;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.widget.TextView;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * *************
 * 用来加载开启线程加载VideoTimeThumbnailLoader度 目的是不将视频时间长度的方法直接在主线程中调用，防止阻塞主线程
 *
 * @author WYX
 */
public class VideoTimeThumbnailLoader {

    private Map<TextView, String> imageViews = Collections
            .synchronizedMap(new WeakHashMap<TextView, String>());
    // 线程池
    ExecutorService executorService;
    Context mContext;
    MediaMetadataRetriever mmr = new MediaMetadataRetriever();

    public VideoTimeThumbnailLoader(Context context) {
        mContext = context;
        // 线程池开一个线程即可，开多有时间长度有错位现象
        executorService = Executors.newFixedThreadPool(1);
    }

    /**
     * @param dir      视频路径
     * @param textview 时间显示textview
     */
    public void DisplayThumbnailForLocalTime(String dir, TextView textview) {
        imageViews.put(textview, dir);

        // 开启线程加载时间长度
        queuePhoto(dir, textview);

    }

    private void queuePhoto(String url, TextView textview) {
        TimeToLoad p = new TimeToLoad(url, textview);
        executorService.submit(new TimeLoader(p));
    }

    private class TimeToLoad {
        public String url;
        public TextView textview;

        public TimeToLoad(String u, TextView i) {
            url = u;
            textview = i;
        }
    }

    class TimeLoader implements Runnable {
        TimeToLoad timeToLoad;

        TimeLoader(TimeToLoad timeToLoad) {
            this.timeToLoad = timeToLoad;
        }

        @Override
        public void run() {
            if (textViewReused(timeToLoad)) {
                return;
            }

            String str = GetThumbnailForLocalVideo(timeToLoad.url);
            TextViewDisplayer bd = new TextViewDisplayer(str, timeToLoad);
            // 更新的操作放在UI线程中
            Activity a = (Activity) timeToLoad.textview.getContext();
            a.runOnUiThread(bd);
        }
    }

    /**
     * 防止文本错位
     *
     * @param timeToLoad
     * @return
     */
    boolean textViewReused(TimeToLoad timeToLoad) {
        String tag = imageViews.get(timeToLoad.textview);

        if (tag == null || !tag.equals(timeToLoad.url))
            return true;
        return false;
    }

    public String GetThumbnailForLocalVideo(String dir) {
        String duration = "0";
        mmr.setDataSource(dir);
        duration = mmr
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return duration;
    }

    /**
     * 用于在UI线程中更新界面 *
     */
    class TextViewDisplayer implements Runnable {
        String text;
        TimeToLoad timeToLoad;

        public TextViewDisplayer(String b, TimeToLoad p) {
            text = b;
            timeToLoad = p;
        }

        public void run() {
            if (textViewReused(timeToLoad))
                return;
            if (text != null) {

                long time = Long.valueOf(text).longValue();
                if (time < 1000) {
                    time = 1000;
                }
                int min = (int) (time / 1000 / 60);
                int sec = (int) (time / 1000 % 60);
                if (min < 10) {
                    if (sec < 10) {
                        timeToLoad.textview
                                .setText("0" + min + ":" + "0" + sec);
                    } else {
                        timeToLoad.textview.setText("0" + min + ":" + sec);
                    }
                } else {
                    if (sec < 10) {
                        timeToLoad.textview.setText(min + ":" + "0" + sec);
                    } else {
                        timeToLoad.textview.setText(min + ":" + sec);
                    }
                }

            } else
                timeToLoad.textview.setText("00:00");
        }
    }
}
