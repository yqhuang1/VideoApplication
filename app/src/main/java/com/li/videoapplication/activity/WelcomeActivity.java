package com.li.videoapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.li.videoapplication.R;
import com.li.videoapplication.XmlParser.PullLaunchImgParser;
import com.li.videoapplication.entity.LaunchImgEntity;
import com.li.videoapplication.utils.DownloadUtils;
import com.li.videoapplication.utils.JsonHelper;
import com.umeng.message.PushAgent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 启动页面*
 */
public class WelcomeActivity extends Activity {

    private Context context;
    private ExApplication exApplication;
    private List<LaunchImgEntity> launchImgList;
    private ImageView launchIv;
    private ImageView defaultIv;

    private String time = "";

    protected static final int SUCCESS_GET_FLAG = 0;
    protected static final int FAILURE_GET_FLAG = 1;
    protected static final int JUMP_MAINACTIVITY = 2;

    private DownloadUtils utils;
    private File cache;
    private String filePath = "videoapplication/hb";
    private String finalPath = "";//最终图片保存的路径
    private File file;
    private List<LaunchImgEntity> readXmlList;

    private long startTime = 0;
    private long endTime = 0;
    private long nowTime = 0;

    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SUCCESS_GET_FLAG:

                    List<LaunchImgEntity> list = (List<LaunchImgEntity>) msg.obj;

                    nowTime = System.currentTimeMillis();
                    //直接读取Xml文件下的海报路径
                    for (LaunchImgEntity imgEntity : list) {
                        startTime = Long.parseLong(imgEntity.getStarttime()) * 1000L;
                        endTime = Long.parseLong(imgEntity.getEndtime()) * 1000L;
//                        System.out.println("start Time===" + startTime);
//                        System.out.println("now   Time===" + nowTime);
//                        System.out.println("end   Time===" + endTime);
                        if (startTime < nowTime && nowTime < endTime) {//存在显示时间合适的海报
//                            System.out.println("显示这张海报图 imgEntity.getFlag()===" + imgEntity.getFlag());
                            utils.asyncloadImage(launchIv, imgEntity.getFlag(), cache);
                            launchIv.setVisibility(View.VISIBLE);

                            //透明度渐变动画
                            Animation aIn = new AlphaAnimation(0f, 1f);
                            aIn.setDuration(2000);
                            aIn.setFillAfter(true);
                            launchIv.startAnimation(aIn);

                            Message message = new Message();
                            message.what = JUMP_MAINACTIVITY;
                            mHandler.sendMessageDelayed(message, 2000);
                            return;
                        }
                    }

                    //完全没有符合显示时间合适的海报
                    Message message = new Message();
                    message.what = JUMP_MAINACTIVITY;
                    mHandler.sendMessageDelayed(message, 0);
                    break;

                case JUMP_MAINACTIVITY:
//                    System.out.println("JUMP_MAINACTIVITY===intent");
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    WelcomeActivity.this.finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        /**设置activity的title不显示，效果相当**/
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /**全屏设置，隐藏窗口所有装饰**/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /**标题是属于View的，所以窗口所有的修饰部分被隐藏后标题依然有效,需要去掉标题**/
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_welcome);
        context = WelcomeActivity.this;
        exApplication = new ExApplication(context);
        launchImgList = new ArrayList<>();
        launchIv = (ImageView) findViewById(R.id.activity_welcome_Iv);
        defaultIv = (ImageView) findViewById(R.id.activity_welcome_default);

        /**友盟 统计应用启动数据
         * 注意: 如果不调用此方法，将会导致按照"几天不活跃"条件来推送失效。可以只在应用的主Activity中调用此方法，
         * 但是由于SDK的日志发送策略，不能保证一定可以统计到日活数据。
         **/
        PushAgent.getInstance(context).onAppStart();

        finalPath = Environment.getExternalStorageDirectory() + File.separator + filePath;
        cache = new File(Environment.getExternalStorageDirectory(), filePath);
        utils = new DownloadUtils(context);
        file = new File(Environment.getExternalStorageDirectory() + File.separator + "videoapplication", "launchImgs.xml");
        if (!cache.exists()) {
            cache.mkdirs();
        }

        readXmlList = readXml();
        if (readXmlList == null || readXml().size() == 0) {
            time = "";
        } else {
            time = readXmlList.get(0).getChangetime();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetLaunchImageTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetLaunchImageTask().execute();
        }

    }

    private PullLaunchImgParser parser = new PullLaunchImgParser();

    public boolean writeXml(List<LaunchImgEntity> launchImgs) {
        try {
            String xml = parser.serialize(launchImgs);  //序列化
//            System.out.println("launchImgs===" + launchImgs.get(0).toString());
//            System.out.println("xml===" + xml);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(xml.getBytes("UTF-8"));
        } catch (Exception e) {
            Log.e("writeXml", e.getMessage());
            return false;
        }
        return true;
    }

    public List<LaunchImgEntity> readXml() {
        List<LaunchImgEntity> launchImgs;
        try {
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                launchImgs = parser.parse(fis);
//                for (LaunchImgEntity launchImg : launchImgs) {
//                    System.out.println("readXml===" + launchImg.toString());
//                }
//                System.out.println("file.exists()===true");
            } else {
//                System.out.println("file.exists()===false");
                return null;
            }
        } catch (Exception e) {
            Log.e("readXml", e.getMessage());
            return null;
        }
        return launchImgs;
    }

    /**
     * 异步获取 启动页图片信息*
     */
    private class GetLaunchImageTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            launchImgList = JsonHelper.getLaunchImage(time);
            if (launchImgList != null) {
                return "s";
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("s")) {
//                System.out.println("WelcomeActivity启动页面===需要下载更新海报文件");
                writeXml(launchImgList);
                //需要下载更新海报文件时，先清空原来的文件夹
                utils.deleteCache(cache);

                //更新海报的Xml文件后直接跳转MainActivity
                Message message = new Message();
                message.what = JUMP_MAINACTIVITY;
                mHandler.sendMessageDelayed(message, 2000);
            } else {
//                System.out.println("WelcomeActivity启动页面===获取后台启动图片失败");
                //根据changetime，不用更新海报图片
                Message message = new Message();
                readXmlList = readXml();
                message.obj = readXmlList;
                if (readXmlList == null) {
                    message.what = JUMP_MAINACTIVITY;
                } else {
                    message.what = SUCCESS_GET_FLAG;
                }
                mHandler.sendMessageDelayed(message, 2000);
            }

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
