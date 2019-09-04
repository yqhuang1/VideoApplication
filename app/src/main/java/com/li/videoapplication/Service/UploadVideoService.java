package com.li.videoapplication.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.li.videoapplication.DB.VideoDB;
import com.li.videoapplication.R;
import com.li.videoapplication.activity.ShareActivity;
import com.li.videoapplication.callback.UpVideoCallBack;
import com.li.videoapplication.utils.ResumableUpload;


/**
 * 视频上传服务
 *
 * @author WYX
 */
public class UploadVideoService extends Service {

    private MsgBinder binder = new MsgBinder();
    public static ResumableUpload resumableUpload;

    private static final int NOTIFY_ID = 0;
    private int progress;

    private Context mContext = this;
    private NotificationManager mNotificationManager;
    private Notification mNotification;

    public static Handler handler;

    // 绑定server时回调该方法
    public IBinder onBind(Intent intent) {

        return binder;

    }

    public void getInstance(Context context) {
        if (null == resumableUpload) {
            resumableUpload = new ResumableUpload(context);

        } else {

        }

    }

    public class MsgBinder extends Binder {

        Context mContext;
        String memberID;
        String matchID;
        String videotitle;
        String gamename;
        String filePath;
        UpVideoCallBack myCallBack;

        // 上传视频
        public void startUpLoadVideo(Context mContext, String memberID, String matchID,
                                     String videotitle, String gamename, String filePath,
                                     UpVideoCallBack myCallBack) {

            progress = 0;
            setUploadNotification(videotitle);

            getInstance(mContext);

            resumableUpload.GetUploadToken(memberID, matchID, videotitle, gamename,
                    filePath, myCallBack);
            this.mContext = mContext;
            this.memberID = memberID;
            this.matchID = matchID;
            this.videotitle = videotitle;
            this.gamename = gamename;
            this.filePath = filePath;
            this.myCallBack = myCallBack;

           /* Log.e("UpLoadVideo==memberID===", memberID);
            Log.e("UpLoadVideo==matchID===", matchID);
            Log.e("UpLoadVideo==videotitle===", videotitle);
            Log.e("UpLoadVideo==gamename===", gamename);
            Log.e("UpLoadVideo==filePath===", filePath);*/
        }

        public void setvalue(String memberID, String matchID, String videotitle,
                             String gamename, String filePath, UpVideoCallBack myCallBack) {
            this.memberID = memberID;
            this.matchID = matchID;
            this.videotitle = videotitle;
            this.gamename = gamename;
            this.filePath = filePath;
            this.myCallBack = myCallBack;

        }

        // 暂停上传视频
        public void stopUpLoadVideo() {

            resumableUpload.cancelUpload();
        }

        // 暂停上传视频 (供 点击应用退出按钮时调用的方法)
        public void stopUpLoadVideoFroExit(Context context) {
            // 获取数据库中的token
            VideoDB videodb = new VideoDB(context);
            if (filePath != null
                    && videodb.getVideoToken(filePath).getToken() != null) {
                String token = videodb.getVideoToken(filePath).getToken();
                // 将token加上exit作为键名存入sharedPreferences
                PreferenceManager.getDefaultSharedPreferences(context).edit()
                        .putBoolean(token + "exit", true).commit();
                resumableUpload.cancelUpload();
            }

        }

        // 继续上传视频
        public void goonUploadVideo(Context context) {
            if (memberID == null || videotitle == null || gamename == null) {

                ShareActivity.handler.sendEmptyMessage(4);

            } else {
                getInstance(context);
                resumableUpload.GetUploadToken(memberID, matchID, videotitle, gamename,
                        filePath, myCallBack);
            }

        }

    }

    @Override
    public void onCreate() {
        // 服务创建");
        super.onCreate();

        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 1:
                        int percent = msg.arg1;
                        String obj = (String) msg.obj;
                        if (percent < 100) {
                            // 更新进度
                            RemoteViews contentView = mNotification.contentView;
                            contentView.setTextViewText(R.id.upload_notification_Percent, percent + "%");
                            contentView.setTextViewText(R.id.upload_notification_FinishText, obj);
                            contentView.setProgressBar(R.id.upload_notification_Progress, 100, percent, false);
                        } else {
                            // 下载完毕后变换通知形式
                            mNotification.flags = Notification.FLAG_AUTO_CANCEL;
                            mNotification.contentView = null;
                            Intent intent = new Intent(mContext, ShareActivity.class);
                            // 告知已完成
                            intent.putExtra("completed", "yes");
                            //更新参数,注意flags要使用FLAG_UPDATE_CURRENT
                            PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            mNotification.setLatestEventInfo(mContext, binder.videotitle, "视频文件已上传完毕", contentIntent);
                            stopSelf();//停掉服务自身
                        }

                        // 最后别忘了通知一下,否则不会更新
                        mNotificationManager.notify(NOTIFY_ID, mNotification);
                        break;
                    case 0:
                        // 取消通知
                        mNotificationManager.cancel(NOTIFY_ID);
                        break;
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 服务开始");
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        // "服务被摧毁");
        super.onDestroy();
    }

    /**
     * 创建通知
     */
    private void setUploadNotification(String title) {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotification = new Notification();
        mNotification.icon = R.drawable.tubiao_top;// 这个图标必须要设置，不然下面那个RemoteViews不起作用.
        mNotification.tickerText = "正在上传...";

        // 放置在"正在运行"栏目中
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;

        RemoteViews contentView = new RemoteViews(mContext.getPackageName(), R.layout.upload_notification_item);
        contentView.setTextViewText(R.id.upload_notification_Title, title);
        contentView.setTextViewText(R.id.upload_notification_Percent, "0%");
        contentView.setProgressBar(R.id.upload_notification_Progress, 100, 0, false);
        // 指定个性化视图
        mNotification.contentView = contentView;

        Intent intent = new Intent(this, ShareActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 指定内容意图
        mNotification.contentIntent = contentIntent;
        mNotificationManager.notify(NOTIFY_ID, mNotification);
    }

}
