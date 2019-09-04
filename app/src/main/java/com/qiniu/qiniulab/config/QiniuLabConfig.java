package com.qiniu.qiniulab.config;

import android.util.Log;

public class QiniuLabConfig {
    public final static byte[] EMPTY_BODY = new byte[0];
    public final static String REMOTE_SERVICE_SERVER = "http://115.231.183.102:9090";

    // resumable upload
    public final static String RESUMABLE_UPLOAD_WITH_KEY_PATH = "/demos/api/resumable_upload_with_key_upload_token.php";

    // callback upload
    public final static String CALLBACK_UPLOAD_WITH_KEY_IN_URL_FORMAT_PATH = "/demos/api/callback_upload_with_key_in_url_format_upload_token.php";
    public final static String CALLBACK_UPLOAD_WITH_KEY_IN_JSON_FORMAT_PATH = "/demos/api/callback_upload_with_key_in_json_format_upload_token.php";

    public final static String PUBLIC_VIDEO_PLAY_LIST_PATH = "/demos/api/public_video_play_list.php";

    public static String makeUrl(String remoteServer, String reqPath) {
        StringBuilder sb = new StringBuilder();
        sb.append(remoteServer);
        sb.append(reqPath);
        
        Log.e("string====", sb.toString());
        return sb.toString();
    }
}
