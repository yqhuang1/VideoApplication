package com.li.videoapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fmscreenrecord.utils.MinUtil;
import com.fmscreenrecord.video.VideoInfo;
import com.li.videoapplication.Adapter.LocalVideoAdapter;
import com.li.videoapplication.DB.VideoDB;
import com.li.videoapplication.activity.VideoManagerActivity;
import com.li.videoapplication.callback.UpVideoCallBack;
import com.li.videoapplication.entity.UploadVideoInfo;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.KeyGenerator;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.android.storage.persistent.FileRecorder;
import com.qiniu.android.utils.UrlSafeBase64;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * 七牛端视频上传方法
 *
 * @author LYG
 */
public class ResumableUpload {

    private Context context;

    private boolean cancelUpload;

    private UploadManager uploadManager;
    private long uploadLastTimePoint;
    private long uploadLastOffset;
    private long uploadFileLength;

    private static String memberID = "28191";
    private static String videoTitle = "这是视频标题";
    private static String gameName = "这是游戏名";
    private static String matchID = "这是活动ID";
    private static String channel = "1";
    private static String filepath = null;
    private String localimageurl;// 本地图片地址
    /**
     * 七牛返回的视频链接ID
     */
    private String videoUrlId = null;

    private SharedPreferences sharedPreferences;
    UpVideoCallBack callBack;
    // 视频文件长度
    long filelong = 0;
    // 文件总大小(M,K,B)
    String filesize = null;
    // 已经上传的文件大小
    String upFileSize = null;

    // 存放到服务器的进度
    private double dbpercent;

    VideoDB videoDB = null;
    // 获取token时 的异常信息
    String msgErros = "";

    /**
     * @param context
     */
    public ResumableUpload(Context context) {
        this.context = context;
        cancelUpload = false;

        videoDB = new VideoDB(context);
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
    }

    /**
     * 视频上传 外部直接调用这个方法
     *
     * @param memberID   用户ID
     * @param videoTitle 视频标题
     * @param gameName   游戏类型名称
     * @param filepath   视频路径
     * @param callback   回调
     */
    public void GetUploadToken(String memberID, String matchID, String videoTitle,
                               String gameName, String filepath, UpVideoCallBack callback) {

        ResumableUpload.videoTitle = videoTitle;
        ResumableUpload.gameName = gameName;
        ResumableUpload.filepath = filepath;
        ResumableUpload.memberID = memberID;
        ResumableUpload.matchID = matchID;
        this.callBack = callback;
        // 视频设置为上传状态
        sharedPreferences.edit().putBoolean("videoUploading", true).commit();
        videoDB.updateContect(filepath, "uploading", gameName, videoTitle,
                dbpercent, videoUrlId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new postVideoEntityBackTokenTask()
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new postVideoEntityBackTokenTask().execute();
        }
    }

    /**
     * @param uploadToken 上传凭证
     * @param fileKey     上传完在7牛显示的视频名（视频ID,加上前缀作为播放链接)
     * @param filepath    本地视频地址
     */
    private void upload(String uploadToken, final String fileKey,
                        final String filepath) {
        this.cancelUpload = false;
        this.videoUrlId = fileKey;
        if (this.uploadManager == null) {
            if (this.uploadManager == null) {
                try {// TODO
                    this.uploadManager = new UploadManager(new FileRecorder(
                            "/mnt/sdcard/LuPingDaShi" + "/uploadvideo"),
                            new KeyGenerator() {

                                @Override
                                public String gen(String key, File file) {
                                    return UrlSafeBase64.encodeToString(file
                                            .getAbsolutePath());
                                }
                            });
                } catch (IOException e) {
                    Log.e("QiniuAndoridSDK", e.getMessage());
                }
            }
        }
        // 传入上传视频地址
        File uploadFile = new File(filepath);
        String uploadFileKey = fileKey;
        UploadOptions uploadOptions = new UploadOptions(null, null, false,
                new UpProgressHandler() {
                    @Override
                    public void progress(String key, double percent) {
                        updateStatus(percent);
                        // 七牛上传进度
                        dbpercent = percent;

                        // TODO
                    }
                }, new UpCancellationSignal() {

            @Override
            public boolean isCancelled() {
                return cancelUpload;
            }
        });
        final long startTime = System.currentTimeMillis();
        final long fileLength = uploadFile.length();
        this.uploadFileLength = fileLength;
        this.uploadLastTimePoint = startTime;
        this.uploadLastOffset = 0;

        this.uploadManager.put(uploadFile, uploadFileKey, uploadToken,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo respInfo,
                                         JSONObject jsonData) {

                        if (respInfo.isOK()) {
                            // 上传成功
                            MinUtil.upUmenEventValue(context, "视频上传成功次数",
                                    "bt_success");
                            UpVideoSuccess success = new UpVideoSuccess(
                                    filepath);
                            success.execute();
                        } else {

                            String errorCode = getErroeCode(respInfo.error
                                    .toString());
                            // 用户暂停上传不提示
                            if (!respInfo.error.toString().equals(
                                    "cancelled by user")) {
                                MinUtil.showToast(context, "上传停止" + "("
                                        + respInfo.error.toString() + "):"
                                        + errorCode);
                            }

                        }
                    }

                    private String getErroeCode(String code) {
                        if (code.equals("cancelled by user")) {
                            return "用户取消上传";
                        } else if (code.equals("400")) {

                            return "请求报文格式错误";
                        } else if (code.equals("401")) {
                            return "认证授权失败";
                        } else if (code.equals("401")) {
                            return "认证授权失败";
                        } else if (code.equals("404")) {
                            return "资源不存在";
                        } else if (code.equals("405")) {
                            return "请求方式错误";
                        } else if (code.equals("406")) {
                            return "上传的数据 CRC32 校验错误";
                        } else if (code.equals("419")) {
                            return "用户账号被冻结";
                        } else if (code.equals("478")) {
                            return "镜像回源失败";
                        } else if (code.equals("503")) {
                            return "服务端不可用";
                        } else if (code.equals("504")) {
                            return "服务端操作超时";
                        } else if (code.equals("579")) {
                            return "上传成功但是回调失败";
                        } else if (code.equals("599")) {
                            return "服务端操作失败";
                        } else if (code.equals("608")) {
                            return "指定资源不存在或已被删除";
                        } else if (code.equals("612")) {
                            return "上传的数据 CRC32 校验错误";
                        } else if (code.equals("614")) {
                            return "目标资源已存在";
                        } else if (code.equals("630")) {
                            return "已创建的空间数量达到上限，无法创建新空间";
                        } else if (code.equals("631")) {
                            return "指定空间不存在";
                        } else if (code.equals("640")) {
                            return "指定非法的marker参数";
                        } else if (code.equals("701")) {
                            return "上传接收地址不正确或ctx信息已过期";
                        }
                        return "";

                    }

                }, uploadOptions);
    }

    /**
     * @param percentage 进度
     */

    private synchronized void updateStatus(final double percentage) {

        // 回调修改shareActivity页面上传进度条
        callBack.showupdateStatus(percentage, uploadLastTimePoint,
                uploadFileLength, uploadLastOffset, filelong, filesize);

    }

    /**
     * 暂停上传
     */
    public void cancelUpload() {

        this.cancelUpload = true;
        sharedPreferences.edit().putBoolean("videoUploading", false).commit();
        videoDB.updateContect(filepath, "pauseupvideo", gameName, videoTitle,
                dbpercent, videoUrlId);
        VideoManagerActivity.handlerViewChange.sendEmptyMessage(9);

    }

    private class postVideoEntityBackTokenTask extends
            AsyncTask<Void, Void, String> {
        UploadVideoInfo.BackVideoInfo upBackInfo = new UploadVideoInfo.BackVideoInfo();
        // 七牛上传凭证
        String token;
        // 视频播放链接
        String videoKey;
        VideoInfo videoToken = null;

        @Override
        protected String doInBackground(Void... voids) {
            String msg = "";

            // 计算视频文件的长度
            filelong = LocalVideoAdapter.getFileSizes(filepath);
            // 视频文件的显示大小
            filesize = LocalVideoAdapter.FormetFileSize(filelong);

            // 验证token是否过期
            if (verifyToken()) {

                // 获取到数据库token信息;

                msg = "true";
            } else {// 如果token过期或者未获得，向后台申请新的token

                // 获取视频长度
                VideoTimeThumbnailLoader videoTimeThumbnailLoader = new VideoTimeThumbnailLoader(
                        context);
                String videoLength = videoTimeThumbnailLoader
                        .GetThumbnailForLocalVideo(filepath);
                // 获取视频分辨率
                Bitmap bitmap = getBitmapForVideo(filepath);
                String width = bitmap.getWidth() + "";
                String height = bitmap.getHeight() + "";

                // 这里返回七牛上传凭证等信息,包含视频ID
                upBackInfo = JsonHelper.postVideoEntityBackToken(context,
                        memberID, videoTitle, gameName, matchID, videoLength, width,
                        height, channel);
                if (upBackInfo == null) {
                    return null;
                }
                // 对获取结果进行判断
                if (upBackInfo.getResult().equals("true")) {
                    // 获取到服务器token信息;
                    token = upBackInfo.getVideoUploadToken();
                    videoKey = upBackInfo.getVideoKey();

                    msg = upBackInfo.getMSG();
                    Date dt = new Date();
                    Long time = dt.getTime();
                    // 将获得的token存进数据库
                    videoDB.saveVideoToken(filepath, token, time, videoKey);

                } else {
                    msgErros = upBackInfo.getMSG();
                    return null;
                }

            }

            localimageurl = getImageFromVideoPath(filepath);
            return msg;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                // 在此回调分享接口
                callBack.share(videoKey, localimageurl);
                // 开始上传
                upload(token, videoKey, filepath);

            } else {
                MinUtil.showToast(context, msgErros);
                callBack.recoveryWindow(filepath);
            }
        }

        /**
         * 验证token是否过期
         *
         * @return
         */
        private boolean verifyToken() {
            videoToken = videoDB.getVideoToken(filepath);
            long videoTokentime = videoToken.getTokenTime();
            // 如果token不过期，采用之前上传时获得的token，不再向后台申请新的token
            token = videoToken.getToken();
            videoKey = videoToken.getVideoURL();

            if (videoTokentime != 0) {

                Date dt = new Date();
                Long time = dt.getTime();

                // 计算获得token的时间是否已经超过24小时,并且token和videokeyza不为空
                if (time - videoTokentime < 86400000 && !token.equals("null")
                        && !videoKey.equals("null")) {

                    return true;
                }
            }

            return false;
        }
    }

    /**
     * 上传结果接口，调用postResult即可
     *
     * @param memberID  用户ID
     * @param videokey  视频ID
     * @param isSuccess 上传结果
     * @param coverurl  封面链接
     */
    public void postResult(String memberID, String matchID, String videokey,
                           String isSuccess, String coverurl) {

        // 提交上传结果
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new postVideoUploadResultTask(memberID, matchID, videokey, isSuccess)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new postVideoUploadResultTask(memberID, matchID, videokey, isSuccess)
                    .execute();
        }

        // 提交视频封面
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new UploadImgTask(videokey, coverurl)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new UploadImgTask(videokey, coverurl).execute();
        }
    }

    /**
     * 提交上传结果异步类
     */
    public class postVideoUploadResultTask extends
            AsyncTask<Void, Void, Boolean> {
        String imageUrl = null; // 上传后返回的图片链接
        String memberID = "";
        String matchID = "";
        String videokey = "";

        String isSuccess = "";
        String coverurl = "";

        public postVideoUploadResultTask(String memberID, String matchID, String videokey,
                                         String isSuccess) {

            this.memberID = memberID;
            this.matchID = matchID;
            this.videokey = videokey;

            this.isSuccess = isSuccess;

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            imageUrl = JsonHelper.postVideoUploadResult(context, memberID, matchID,
                    videokey, isSuccess);
            if (imageUrl.equals("")) {
                return false;
            }
            // 提交结果返回
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

    public class UploadImgTask extends AsyncTask<Void, Void, Boolean> {
        String imagDir = ""; // 本地的图片地址
        String imageUrl = ""; // 上传后返回的图片链接
        String videokey = "";

        public UploadImgTask(String videokey, String imagDir) {
            this.videokey = videokey;
            this.imagDir = imagDir;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            imageUrl = JsonHelper.uploadVideoCoverImage(context, videokey,
                    imagDir);
            if (imageUrl.equals("")) {
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

    /**
     * 异步上传视频发布结果
     *
     * @author WYX
     */
    class UpVideoSuccess extends AsyncTask<Void, Void, Boolean> {
        String filepath;
        String iamgeUrl;

        public UpVideoSuccess(String filepath) {
            this.filepath = filepath;

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // 获取视频封面
            String fileimageurl = "/mnt/sdcard/LuPingDaShi/tmp/";
            File file = new File(filepath);
            // 截取视频文件后缀名前的字符串
            final String[] fileName = file.getName().split("\\.mp4");
            iamgeUrl = fileimageurl + fileName[0] + ".png";

            return null;
        }

        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            postResult(memberID, matchID, videoUrlId, "1", iamgeUrl);
            MinUtil.showToast(context, "视频上传成功，请在云端视频查看所上传的视频");
            // 上传标志取消
            sharedPreferences.edit().putBoolean("videoUploading", false)
                    .commit();

            // 回复视频编辑框
            callBack.recoveryWindow(filepath, gameName, videoTitle, videoUrlId);
        }
    }

    /**
     * 根据视频地址获取视频封面
     *
     * @param filepath
     * @return
     */
    public static String getImageFromVideoPath(String filepath) {

        // 从视频中获取一张位图
        Bitmap srcBitmap = getBitmapForVideo(filepath);
        // 封面大小处理
        int newWidth = 450;
        int newHeight = 254;
        Bitmap finallBitmap;

        int srcW, srcH;
        srcW = srcBitmap.getWidth();
        srcH = srcBitmap.getHeight();

        if ((srcW < 720 && srcH < 720) || srcW > srcH) // 直接按比压缩
        {

            Bitmap turnBmp;
            if (srcW < srcH) // 竖屏
            {
                Matrix mt = new Matrix();
                mt.setRotate(90);
                turnBmp = Bitmap.createBitmap(srcBitmap, 0, 0, srcW, srcH, mt,
                        true);
            } else {
                turnBmp = srcBitmap;
            }
            int trunW = turnBmp.getWidth();
            int trunH = turnBmp.getHeight();

            Matrix matrix = new Matrix();
            // 计算缩放率，新尺寸除原始尺寸
            float scaleWidth = ((float) newWidth) / trunW;
            float scaleHeight = ((float) newHeight) / trunH;
            matrix.postScale(scaleWidth, scaleHeight);
            finallBitmap = Bitmap.createBitmap(turnBmp, 0, 0, trunW, trunH,
                    matrix, true);
        } else // 截剪
        {
            // "不直接压缩");
            int widleft = (srcW - newWidth) / 2;
            int Heighteft = (srcH - newHeight) / 2;
            finallBitmap = Bitmap.createBitmap(srcBitmap, widleft, Heighteft,
                    newWidth, newHeight);

        }

        File file = new File("/mnt/sdcard/LuPingDaShi/tmp");
        File file2 = new File(filepath);
        // 截取视频文件后缀名前的字符串
        final String[] fileName = file2.getName().split("\\.mp4");

        if (file.exists() == false) {
            file.mkdirs();
            file = null;
        }
        File f = new File("/mnt/sdcard/LuPingDaShi/tmp/" + fileName[0] + ".png");
        try {
            f.createNewFile();

        } catch (IOException e) {

        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finallBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return f.getPath();
    }

    /**
     * 从视频中生成一张bitmap
     *
     * @param filepath
     * @return
     */
    private static Bitmap getBitmapForVideo(String filepath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(filepath);
        String time = retriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int seconds = Integer.valueOf(time);
        Bitmap srcBitmap = retriever.getFrameAtTime(seconds / 2 * 1000,
                MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        return srcBitmap;
    }
}