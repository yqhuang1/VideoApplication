package com.li.videoapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.li.videoapplication.entity.UploadStateEntity;
import com.li.videoapplication.entity.UserEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;


/**
 * 数据保存类
 * Created by li on 2014/9/24.
 */
public class SharePreferenceUtil {


    public static void setPreference(Context context, String key, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
        editor = null;
    }

    public static String getPreference(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String value = preferences.getString(key, "");
        return value;
    }

    //----------------------------------------------------------------------------------------------

    /**
     * 保存 首页的（头条）广告 ID信息
     */
    public static void setHomeAdId(Context context, Set<String> adId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet("HomeAdId", adId);
        Log.e("setHomeAdId===", adId.toString());
        editor.commit();
        editor = null;
    }

    /**
     * 获取 首页的（头条）广告 ID信息
     */
    public static Set<String> getHomeAdId(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> adSet = preferences.getStringSet("HomeAdId", null);
        return adSet;
    }

    /**
     * 保存 首页的（头条）广告 json信息
     */
    public static void setHomeAd(Context context, String json) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("HomeAd", json);
        Log.e("setHomeAd===", json);
        editor.commit();
        editor = null;
    }

    /**
     * 获取 首页的（头条）广告 json信息
     */
    public static String getHomeColumn(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String json = preferences.getString("HomeColumn", "");
        return json;
    }

    /**
     * 保存 首页的专栏 json信息
     */
    public static void setHomeColumn(Context context, String json) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("HomeColumn", json);
        Log.e("setHomeColumn===", json);
        editor.commit();
        editor = null;
    }

    /**
     * 获取 首页的（头条）广告 json信息
     */
    public static String getHomeAd(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String json = preferences.getString("HomeAd", "");
        return json;
    }

    /**
     * 保存 发现的（大家还在看）列表 json信息
     */
    public static void setDiscoverList(Context context, String json) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("DiscoverList", json);
        Log.e("setDiscoverList===", json);
        editor.commit();
        editor = null;
    }

    /**
     * 获取 发现的（大家还在看）列表 json信息
     */
    public static String getDiscoverList(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String json = preferences.getString("DiscoverList", "");
        return json;
    }

    /**
     * 保存 礼包列表 json信息
     */
    public static void setGiftList(Context context, String json) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("GiftList", json);
        Log.e("setGiftList===", json);
        editor.commit();
        editor = null;
    }

    /**
     * 获取 礼包列表 json信息
     */
    public static String getGiftList(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String json = preferences.getString("GiftList", "");
        return json;
    }

    /**
     * 保存 活动 热门活动列表 json信息
     */
    public static void setHotActivityList(Context context, String json) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("HotActivityList", json);
        Log.e("setHotActivityList===", json);
        editor.commit();
        editor = null;
    }

    /**
     * 获取 活动 热门活动列表 json信息
     */
    public static String getHotActivityList(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String json = preferences.getString("HotActivityList", "");
        return json;
    }


    //----------------------------------------------------------------------------------------------

    /**
     * 设置保存登录用户信息
     */
    public static void setUserEntity(Context context, String json) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("User", json);
        Log.e("user", json);
        editor.commit();
        editor = null;
    }


    /**
     * 获取登录用户信息
     */
    public static UserEntity getUserEntity(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String json = preferences.getString("User", "");
        String result = "";
        JSONObject dasta = new JSONObject();
        Log.e("user", json);
        UserEntity user = new UserEntity();
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals("true")) {
                return null;
            }
            dasta = object.getJSONObject("data");
            if (dasta.toString().equals("false")) {
                return null;
            }
            user.setImgPath(dasta.getString("avatar"));
            user.setId(dasta.getString("id"));
            user.setName(dasta.getString("name"));
            user.setTitle(dasta.getString("nickname"));
            user.setOpenId(dasta.getString("openid"));
            if (dasta.has("address")) {
                user.setAddress(dasta.getString("address"));
            }

            if (dasta.has("degree")) {
                user.setGrace(dasta.getString("degree"));
            }
            if (dasta.has("isAdmin")) {
                user.setIsAdmin(dasta.getString("isAdmin"));
            }
            if (dasta.has("like_gametype")) {
                user.setLike_gametype(dasta.getString("like_gametype"));
            }
            if (dasta.has("mobile")) {
                user.setMobile(dasta.getString("mobile"));
            }
            user.setPassword(dasta.getString("password"));
            user.setRank(dasta.getString("rank"));
            user.setSex(dasta.getString("sex"));
            user.setTime(dasta.getString("time"));
            return user;
        } catch (Exception e) {
            Log.e("getUerInfo", e.toString());
        }
        return null;
    }

    public static SharedPreferences getMinJieKaiFaPreferences(Context context) {
        return context.getSharedPreferences("com_fmscreenrecord.properties",
                Context.MODE_PRIVATE);
    }


    /**
     * 保存视频上传状态信息
     */
    public static void setUploadStateEntity(Context context, String flag, String json) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(flag, json);
        Log.e(flag, json);
        editor.commit();
        editor = null;
    }

    /**
     * 获取视频上传状态
     */
    public static UploadStateEntity getUploadStateEntity(Context context, String flag) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String json = preferences.getString(flag, "");
        Log.e(flag, json);
        UploadStateEntity uploadState = new UploadStateEntity();
        try {
            if (!"".equals(json)) {
                JSONObject dasta = new JSONObject(json);
                uploadState.setTitle(dasta.getString("title"));
                uploadState.setFlagPath(dasta.getString("flagPath"));
                uploadState.setGameName(dasta.getString("gameName"));
                uploadState.setLocalPath(dasta.getString("localPath"));
                uploadState.setColudPath(dasta.getString("coludPath"));
                uploadState.setPercent(dasta.getDouble("percent"));
                uploadState.setState(dasta.getInt("state"));
                uploadState.setVideoId(dasta.getString("videoId"));
            } else {
                uploadState.setTitle("");
                uploadState.setFlagPath("");
                uploadState.setGameName("");
                uploadState.setLocalPath("");
                uploadState.setColudPath("");
                uploadState.setPercent(0);
                uploadState.setState(0);
                uploadState.setVideoId("");
            }
            return uploadState;
        } catch (Exception e) {
            Log.e("getUploadStateEntity_e", e.toString());
        }
        return null;
    }

    //获取视频上传状态json字符串
    public static String getUploadStateJsonStr(String title, String flagPath, String gameName, String match_id,
                                               String timeLength, String videoHeight, String videoWidth, String localPath, String coludPath, double percent, int state, String videoId) {
        try {
            JSONObject object = new JSONObject();
            object.put("title", title);
            object.put("flagPath", flagPath);
            object.put("gameName", gameName);
            object.put("matchId", match_id);
            object.put("timeLength", timeLength);
            object.put("videoHeight", videoHeight);
            object.put("videoWidth", videoWidth);
            object.put("localPath", localPath);
            object.put("coludPath", coludPath);
            object.put("percent", percent);
            object.put("state", state);
            object.put("videoId", videoId);
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String turnUploadStateEntityToJson(UploadStateEntity uploadState) {
        try {
            JSONObject object = new JSONObject();
            object.put("title", uploadState.getTitle());
            object.put("flagPath", uploadState.getFlagPath());
            object.put("gameName", uploadState.getGameName());
            object.put("localPath", uploadState.getLocalPath());
            object.put("coludPath", uploadState.getColudPath());
            object.put("percent", uploadState.getPercent());
            object.put("state", uploadState.getState());
            object.put("videoId", uploadState.getVideoId());
            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
