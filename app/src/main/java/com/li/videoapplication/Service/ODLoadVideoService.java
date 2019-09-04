package com.li.videoapplication.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.li.videoapplication.DB.DBManager;
import com.li.videoapplication.R;
import com.li.videoapplication.activity.OfflineDownloadActivity;
import com.li.videoapplication.entity.DownloadVideo;
import com.li.videoapplication.utils.ToastUtils;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 执行下载的Service
 * Service for 离线download
 */
public class ODLoadVideoService extends Service {

    private ODBinder binder = new ODBinder();

    private static final int NOTIFY_ID = 0;
    private boolean cancelled;
    private Context mContext = this;

    private DBManager dbManager;

    private String saveName = "";

    private NotificationManager mNotificationManager;
    private Notification mNotification;
    //创建通知栏
    RemoteViews contentView;
    private Intent oldlIntent;
    private PendingIntent pendingIntent;

    private long timerCurrent = 0;
    private long timerLast = 0;
    //计算下载速度的计时器
    private Timer timer = new Timer();
    private TimerTask task;

    public static Handler handler;

    // 绑定server时回调该方法
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class ODBinder extends Binder {
        Context mContext;
        String video_id = "";
        String download_url = "";
        String path = "";
        String qn_key = "";
        String youku_url = "";
        String title = "";
        String flagPath = "";
        int notification_id;

        /**
         * 下载视频
         */
        public void StartDownLoadVideo(Context mContext, String video_id, String download_url, String path,
                                       String qn_key, String youku_url, String title, String flagPath,
                                       int notification_id) {
            this.mContext = mContext;
            this.video_id = video_id;
            this.download_url = download_url;
            this.path = path;
            this.qn_key = qn_key;
            this.youku_url = youku_url;
            this.title = title;
            this.flagPath = flagPath;
            this.notification_id = notification_id;

            saveName = qn_key + ".mp4";

            dbManager = new DBManager(getApplicationContext());

            DownloadVideo video = new DownloadVideo();
            video.setVideo_id(video_id);
            video.setName(qn_key + ".mp4");//保存的名称
            video.setTitle(title);
            video.setImgUrl(flagPath);//视频封面地址
            video.setDownloadUrl(download_url);//网络视频下载地址
            video.setPlayUrl(path + File.separator + qn_key + ".pm4");//视频保存的地址
            video.setQn_key(qn_key);
            video.setYouku_url(youku_url);
            video.setDownload_state("LOADING");
            Log.e("DownloadVideo===", video.toString());
            dbManager.addDownloadView(video);

            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                setDownloadNotification(title, notification_id);


            } else {
                ToastUtils.showToast(getApplicationContext(), "没有找到外置SD卡");
            }

        }


        public void setvalue(String video_id, String download_url, String path,
                             String qn_key, String youku_url, String title, String flagPath,
                             int notification_id) {
            this.video_id = video_id;
            this.download_url = download_url;
            this.path = path;
            this.qn_key = qn_key;
            this.youku_url = youku_url;
            this.title = title;
            this.flagPath = flagPath;
            this.notification_id = notification_id;
        }

        // 暂停下载视频
        //todo
        public void stopDownLoadVideo() {

        }

        // 继续上传视频
        // todo
        public void goonDownloadVideo(Context context) {
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelled = true; // 取消下载线程
    }

    public void setDownloadNotification(String title, int notification_id) {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotification = new Notification();
        mNotification.icon = R.drawable.tubiao_top;// 这个图标必须要设置，不然下面那个RemoteViews不起作用.
        mNotification.tickerText = "正在下载...";

        // 放置在"正在运行"栏目中
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;

        /***
         * 在这里我们用自定的view来显示Notification
         */
        contentView = new RemoteViews(getPackageName(),
                R.layout.oldl_notification_item);
        contentView.setTextViewText(R.id.oldl_notification_Title, title);
        contentView.setTextViewText(R.id.oldl_notification_Percent, "0%");
        contentView.setTextViewText(R.id.oldl_notification_Speed, "0KB");
        contentView.setTextViewText(R.id.oldl_notification_State, "...");
        contentView.setProgressBar(R.id.oldl_notification_Progress, 100, 0, false);
        // 指定个性化视图
        mNotification.contentView = contentView;

        oldlIntent = new Intent(this, OfflineDownloadActivity.class);
        oldlIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, oldlIntent, 0);
        // 指定内容意图
        mNotification.contentIntent = pendingIntent;
        mNotificationManager.notify(notification_id, mNotification);
    }

}
