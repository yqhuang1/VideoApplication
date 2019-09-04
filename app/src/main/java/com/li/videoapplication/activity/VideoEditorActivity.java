package com.li.videoapplication.activity;

/**
 * @author lin
 * Creat:2014-12
 * 视频管理 本地视频 视频编辑页面
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.fmscreenrecord.VideoList.VideoThumbnailLoader;
import com.fmscreenrecord.utils.MResource;
import com.fmscreenrecord.utils.MinUtil;
import com.fmscreenrecord.videoeditor.VideoEditorUtil;
import com.fmscreenrecord.videoeditor.VideoSliceSeekBar;
import com.li.videoapplication.DB.VideoDB;
import com.li.videoapplication.R;

import java.io.File;

public class VideoEditorActivity extends Activity implements OnClickListener {
    // TextView textViewLeft, textViewRight;
    VideoSliceSeekBar videoSliceSeekBar;

    VideoView videoView;
    /**
     * 预览按钮 *
     */
    ImageView videoControlBtn;

    /**
     * 源视频名称
     */
    String[] sourceFileName;
    /**
     * 保存按钮
     */
    Button saveBtn;
    /**
     * 实时图片
     */
    private static ImageView coverImage;
    /**
     * 填充进度条的bitmap
     */
    static Bitmap srcBitmap1, srcBitmap2, srcBitmap3, srcBitmap4;
    /**
     * 填充进度条的imageview
     */
    static ImageView seekBarIV1, seekBarIV2, seekBarIV3, seekBarIV4;

    private StateObserver videoStateObserver;
    // 视频简介的结束时间
    public static TextView textTimeMax;
    // 视频剪辑的开始时间
    public static TextView textTimeMin;
    // 时间剪辑的总时长
    private TextView textTimeSel;

    /**
     * 是否已经上传到友盟 *
     */
    private int UpUmen = -1;

    private static long[] textTime;
    // 秒数
    static long seconds;

    // 视频文件的ID，名称，路径,分享的内容
    String fileID, fileName;

    static String filePath;

    String shareText;

    String listID;

    // 移动进度条时显示的图片
    static Bitmap videoBitmap;

    // 视频标题
    String videoTitle;
    private static long startMs = 0;
    private long endMs = 1000;

    private Button btBack;

    private MediaMetadataRetriever retriever;

    VideoThumbnailLoader videoThumbnailLoader;

    private static Context mContext;

    public static VideoEditorActivity last;
    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    // 从VideoSliceSeekBar传过来的时间数值
                    textTime = (long[]) msg.obj;

                    String textright;
                    // 获得右边游标得到的时间
                    if (textTime[1] <= 1000) {// 小于1000毫秒按1000毫秒
                        textright = getTimeForTrackFormat(1000, true);

                    } else {
                        textright = getTimeForTrackFormat(textTime[1], true);
                    }

                    // 获得左边游标得到的时间
                    String textleft = getTimeForTrackFormat(textTime[0], true);
                    textTimeMax.setText(textright);
                    textTimeMin.setText(textleft);
                    break;
                case 2:
                    seekBarIV1.setBackground(new BitmapDrawable(mContext
                            .getResources(), srcBitmap1));
                    seekBarIV2.setBackground(new BitmapDrawable(mContext
                            .getResources(), srcBitmap2));
                    seekBarIV3.setBackground(new BitmapDrawable(mContext
                            .getResources(), srcBitmap3));
                    seekBarIV4.setBackground(new BitmapDrawable(mContext
                            .getResources(), srcBitmap4));

                    break;

                case 4:
                    String toastContent = (String) msg.obj;
                    MinUtil.showToast(mContext, toastContent);
                    VideoManagerActivity.handlerViewChange.sendEmptyMessage(9);

                    break;

            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fm_video_editor_activity);
        mContext = VideoEditorActivity.this;
        last = this;
        findviewid();
        setonclick();
        videoStateObserver = new StateObserver();

        Intent intent = getIntent();

        // 获取视频文件ID,路径，名称

        filePath = intent.getStringExtra("filePath");

        videoTitle = intent.getStringExtra("videoTitle");

        initVideoView(filePath);

        setview();

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (!srcBitmap1.isRecycled()) {
            srcBitmap1.recycle();
            System.gc();
        }
        if (!srcBitmap2.isRecycled()) {
            srcBitmap2.recycle();
            System.gc();
        }
        if (!srcBitmap3.isRecycled()) {
            srcBitmap3.recycle();
            System.gc();
        }
        if (!srcBitmap4.isRecycled()) {
            srcBitmap4.recycle();
            System.gc();
        }

    }

    // 初始化控件
    private void setview() {

        // 设置播放预览图
        // videoThumbnailLoader
        // .DisplayThumbnailForLocalVideo(filePath, coverImage);
        // 设置进度栏图片
        GetVideoMidFrme(filePath);
    }

    /**
     * 从视频中生成图片
     *
     * @param dataPath 视频路径
     * @return
     */

    public void GetVideoMidFrme(final String dataPath) {
        new Thread() {

            public void run() {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(dataPath);
                String time = retriever
                        .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                int seconds = Integer.valueOf(time);
                srcBitmap1 = retriever.getFrameAtTime(seconds / 8 * 1000,
                        MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                srcBitmap2 = retriever.getFrameAtTime(seconds / 6 * 1000,
                        MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                srcBitmap3 = retriever.getFrameAtTime(seconds / 4 * 1000,
                        MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                srcBitmap4 = retriever.getFrameAtTime(seconds / 2 * 1000,
                        MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                handler.sendEmptyMessage(2);

            }

            ;
        }.start();

    }

    private void setonclick() {
        btBack.setOnClickListener(this);
        saveBtn.setOnClickListener(this);

    }

    private void findviewid() {

        textTimeMin = (TextView) findViewById(MResource.getIdByName(
                getApplication(), "id", "video_editor_time_min"));
        videoView = (VideoView) findViewById(MResource.getIdByName(
                getApplication(), "id", "video_editor_video"));
        textTimeMax = (TextView) findViewById(MResource.getIdByName(
                getApplication(), "id", "video_editor_time_max"));

        textTimeSel = (TextView) findViewById(MResource.getIdByName(
                getApplication(), "id", "video_editor_time_sel"));
        videoSliceSeekBar = (VideoSliceSeekBar) findViewById(MResource
                .getIdByName(getApplication(), "id", "video_editor_seek_bar"));

        btBack = (Button) findViewById(MResource.getIdByName(getApplication(),
                "id", "video_editor_imageButton_back"));

        coverImage = (ImageView) findViewById(MResource.getIdByName(
                getApplication(), "id", "video_cover_imageview"));
        videoControlBtn = (ImageView) findViewById(MResource.getIdByName(
                getApplication(), "id", "video_editor_control_btn"));

        seekBarIV1 = (ImageView) findViewById(MResource.getIdByName(
                getApplication(), "id", "fm_seekbar_imageview1"));
        seekBarIV2 = (ImageView) findViewById(MResource.getIdByName(
                getApplication(), "id", "fm_seekbar_imageview2"));
        seekBarIV3 = (ImageView) findViewById(MResource.getIdByName(
                getApplication(), "id", "fm_seekbar_imageview3"));
        seekBarIV4 = (ImageView) findViewById(MResource.getIdByName(
                getApplication(), "id", "fm_seekbar_imageview4"));

        saveBtn = (Button) findViewById(MResource.getIdByName(getApplication(),
                "id", "video_editor_imageButton_save"));

        videoThumbnailLoader = new VideoThumbnailLoader(mContext);
    }

    @SuppressLint("NewApi")
    private void initVideoView(String videoSrc) {
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoSliceSeekBar
                        .setSeekBarChangeListener(new VideoSliceSeekBar.SeekBarChangeListener() {

                            public void SeekBarValueChanged(long leftThumb,
                                                            long rightThumb) {
                                if (startMs != leftThumb) {
                                    // MoveThread moveThread = new MoveThread(
                                    // leftThumb);
                                    // pool.execute(moveThread);

                                    videoView.seekTo((int) leftThumb);

                                    startMs = leftThumb;
                                }
                                if (endMs != rightThumb) {
                                    videoView.seekTo((int) rightThumb);
                                    endMs = rightThumb;
                                }

                                textTimeSel.setText(getTimeForTrackFormat(endMs
                                        - startMs, true));
                                UpUmen++;

                                // TODO

                            }
                        });

                textTimeMax.setText(getTimeForTrackFormat(mp.getDuration(),
                        true));
                if (mp.getDuration() >= 10000) {
                    textTimeMin.setText(getTimeForTrackFormat(10000, true));
                } else {
                    textTimeMin.setText(getTimeForTrackFormat(mp.getDuration(),
                            true));
                }

                startMs = 0;
                endMs = mp.getDuration();

                videoSliceSeekBar.setMaxValue(mp.getDuration());
                videoSliceSeekBar.setLeftProgress(0);
                videoSliceSeekBar.setRightProgress(mp.getDuration());
                videoSliceSeekBar.setProgressMinDiff(10000);

                // 播放
                videoControlBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        performVideoViewClick();

                    }
                });
                // 暂停
                coverImage.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        performVideoViewClick();

                    }

                });
            }
        });
        videoView.setVideoURI(Uri.parse(videoSrc));
        videoView.seekTo(1);
        videoView.start();
        videoView.pause();

    }

    public Boolean decodeFrame(String file, long timeMs) {
        retriever = new MediaMetadataRetriever();
        retriever.setDataSource(file);
        if (retriever == null) {
            return false;
        }

        return true;

    }

    /**
     * 播放视频方法
     */
    @SuppressWarnings("deprecation")
    private void performVideoViewClick() {
        if (videoView.isPlaying()) {

            videoView.pause();
            videoSliceSeekBar.setSliceBlocked(false);
            videoSliceSeekBar.removeVideoStatusThumb();
            videoControlBtn.setBackgroundDrawable(mContext.getResources()
                    .getDrawable(
                            MResource.getIdByName(mContext, "drawable",
                                    "fm_play_normal")));
        } else {

            videoView.seekTo((int) videoSliceSeekBar.getLeftProgress());
            videoView.start();
            videoSliceSeekBar.setSliceBlocked(true);
            videoSliceSeekBar.videoPlayingProgress(videoSliceSeekBar
                    .getLeftProgress());
            videoStateObserver.startVideoProgressObserving();
            videoControlBtn.setBackgroundDrawable(null);
        }
    }

    /**
     * 将时间毫秒值转为hh:mm:ss
     *
     * @param timeInMills                 毫秒值
     * @param display2DigitsInMinsSection
     * @return
     */

    static String getTimeForTrackFormat(long timeInMills,
                                        boolean display2DigitsInMinsSection) {
        long hours = (timeInMills / (60 * 60 * 1000));
        long minutes = timeInMills / 1000 / 60 % 60;
        seconds = timeInMills / 1000;
        long mseconds = timeInMills / 1000 % 60 % 60;

        String resultsec = (display2DigitsInMinsSection && mseconds < 10 ? "0"
                : "") + mseconds;
        String resultmin = (display2DigitsInMinsSection && minutes < 10 ? "0"
                : "") + minutes;
        String resulthours = (display2DigitsInMinsSection && hours < 10 ? "0"
                : "") + hours;

        String result = null;
        result = resulthours + ":" + resultmin + ":" + resultsec;

        return result;
    }

    @SuppressLint("HandlerLeak")
    private class StateObserver extends Handler {

        private boolean alreadyStarted = false;

        private void startVideoProgressObserving() {
            if (!alreadyStarted) {
                alreadyStarted = true;
                sendEmptyMessage(0);
            }
        }

        private Runnable observerWork = new Runnable() {
            @Override
            public void run() {
                startVideoProgressObserving();
            }
        };

        @SuppressWarnings("deprecation")
        @Override
        public void handleMessage(Message msg) {
            alreadyStarted = false;
            videoSliceSeekBar.videoPlayingProgress(videoView
                    .getCurrentPosition());
            if (videoView.isPlaying()
                    && videoView.getCurrentPosition() < videoSliceSeekBar
                    .getRightProgress()) {
                postDelayed(observerWork, 50);

            } else {

                if (videoView.isPlaying())
                    videoView.pause();

                videoSliceSeekBar.setSliceBlocked(false);
                videoSliceSeekBar.removeVideoStatusThumb();
                // 重新设置播放图片
                videoControlBtn.setBackgroundDrawable(mContext.getResources()
                        .getDrawable(
                                MResource.getIdByName(mContext, "drawable",
                                        "fm_play_normal")));
            }
        }
    }

    @Override
    public void onClick(View v) {

        if (v == btBack) {
            finish();
        } else if (v == saveBtn) {

            File file = new File(filePath);
            // 截取视频文件后缀名前的字符串
            sourceFileName = file.getName().split("\\.mp4");

            createCustomDialog(sourceFileName[0]);
        }

    }

    void createCustomDialog(final String videoName) {

        AlertDialog.Builder builder = null;

        LayoutInflater factory = LayoutInflater.from(mContext);
        View view = factory.inflate(
                MResource.getIdByName(mContext, "layout", "fm_edittext"), null);
        final EditText ditText = (EditText) view.findViewById(MResource
                .getIdByName(mContext, "id", "fm_rename_edittext"));
        ditText.setText(videoName + "_tmp");
        builder = new AlertDialog.Builder(mContext);
        builder.setTitle("请输入视频名称");
        builder.setView(view);

        builder.setPositiveButton("确定", null);
        builder.setNegativeButton("取消", null);

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String newVideoName = ditText.getText()
                                .toString();
                        if (newVideoName.isEmpty()) {
                            MinUtil.showToast(mContext, "视频名不能为空");
                            return;
                        } else {
                            if (newVideoName.equals(videoName)) {
                                MinUtil.showToast(mContext, "不能与源视频同名");
                                return;
                            } else {

                                new Thread() {
                                    public void run() {

                                        String newfilePath = "/mnt/sdcard/LuPingDaShi/"
                                                + newVideoName + ".mp4";
                                        // 尝试插入数据库
                                        int result = new VideoDB(mContext)
                                                .insertForCutVideo(newVideoName
                                                                + ".mp4", newfilePath,
                                                        "cut", "local");
                                        Message msg = new Message();
                                        msg.what = 4;
                                        if (result == 1) {// 数据库插入成功
                                            dialog.dismiss();
                                            // 开始视频剪辑
                                            boolean success = VideoEditorUtil
                                                    .VideoCut(filePath,
                                                            newfilePath,
                                                            textTime[0],
                                                            textTime[1]);

                                            if (success) {

                                                msg.obj = "视频剪辑成功";
                                                finish();
                                                MinUtil.upUmenEventValue(mContext, "视频剪辑成功", "bt_videoEditor");

                                            } else {
                                                MinUtil.upUmenEventValue(mContext, "视频剪辑失败", "bt_videoEditor");
                                                msg.obj = "视频剪辑失败了";

                                            }
                                        } else if (result == 0) {// 数据库存在重名
                                            msg.obj = "视频名称已存在";
                                            MinUtil.upUmenEventValue(mContext, "视频剪辑失败", "bt_videoEditor");

                                        } else {
                                            MinUtil.upUmenEventValue(mContext, "视频剪辑失败", "bt_videoEditor");
                                            dialog.dismiss();
                                            msg.obj = "视频剪辑失败了";
                                        }
                                        handler.sendMessage(msg);
                                    }

                                    ;
                                }.start();

                            }
                            return;
                        }

                    }
                });

    }

}
