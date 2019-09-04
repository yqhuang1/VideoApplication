package com.li.videoapplication.utils;

import android.content.Context;
import android.content.res.Resources;

import com.fmscreenrecord.app.SRApplication;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.umeng.fb.push.FeedbackPush;

/**
 * Created by feimoyuangong on 2015/1/26.
 */
public class MyApplication extends SRApplication {
    private static MyApplication mcontext;
    private RefWatcher refWatcher;

    private int playPos;
    private String playUrl;

    public int getPlayPos() {
        return playPos;
    }

    public void setPlayPos(int playPos) {
        this.playPos = playPos;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mcontext = this;
        FeedbackPush.getInstance(this).init(false);

        refWatcher= setupLeakCanary();
    }

    private RefWatcher setupLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return RefWatcher.DISABLED;
        }
        return LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        MyApplication leakApplication = (MyApplication) context.getApplicationContext();
        return leakApplication.refWatcher;
    }

    public static Context getAppContext() {
        return mcontext;
    }

    public static Resources getAppResources() {
        return getAppResources();
    }
}
