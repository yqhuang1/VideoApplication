package com.li.videoapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fmscreenrecord.video.VideoInfo;
import com.li.videoapplication.activity.ExApplication;
import com.li.videoapplication.entity.ActivityEntity;
import com.li.videoapplication.entity.Advertisement;
import com.li.videoapplication.entity.BannerEntity;
import com.li.videoapplication.entity.Business;
import com.li.videoapplication.entity.CommentEntity;
import com.li.videoapplication.entity.DiscoverVideoEntity;
import com.li.videoapplication.entity.DoTaskEntity;
import com.li.videoapplication.entity.ExpertEntity;
import com.li.videoapplication.entity.Game;
import com.li.videoapplication.entity.GameCircleVideoEntity;
import com.li.videoapplication.entity.GameEntity;
import com.li.videoapplication.entity.GameType;
import com.li.videoapplication.entity.GiftEntity;
import com.li.videoapplication.entity.HomeColumnEntity;
import com.li.videoapplication.entity.HomeHotEntity;
import com.li.videoapplication.entity.KeyWord;
import com.li.videoapplication.entity.LaunchImgEntity;
import com.li.videoapplication.entity.MasterEntity;
import com.li.videoapplication.entity.MatchEntity;
import com.li.videoapplication.entity.MessageEntity;
import com.li.videoapplication.entity.MissionEntity;
import com.li.videoapplication.entity.PlayerShowEntity;
import com.li.videoapplication.entity.RecommendEntity;
import com.li.videoapplication.entity.Update;
import com.li.videoapplication.entity.UploadVideoInfo;
import com.li.videoapplication.entity.UserEntity;
import com.li.videoapplication.entity.VedioDetail;
import com.li.videoapplication.entity.VideoEntity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by li on 2014/9/23.
 */
public class JsonHelper {

    private static final String TRUE = "true";
    private static final String MSG = "msg";
    private static final String DATA = "data";
    private static final String DASTA = "dasta";

    private static final String YES = "yes";
    private static final String NO = "no";


    public static UserEntity getUserInfo(Context context, String key) {
        UserEntity user = new UserEntity();
        String result = "";
        JSONObject dasta = new JSONObject();
        String json = "";
        json = HttpUtils.httpGet(HttpUtils.getPhoneLogin(key));
        Log.e("getUserInfo_url", HttpUtils.getPhoneLogin(key));
        Log.e("getUerInfo_json", json);
        try {
            if (json.equals("")) {
                return null;
            }

            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            dasta = object.getJSONObject(DATA);
            if (dasta.toString().equals("false")) {
                System.out.println("register_msg=======" + object.getJSONObject("msg"));
                return null;
            }
            SharePreferenceUtil.setUserEntity(context, json);
            user.setAddress(dasta.has("address") ? dasta.getString("address") : "");
            user.setImgPath(dasta.has("avatar") ? dasta.getString("avatar") : "");
            user.setGrace(dasta.has("degree") ? dasta.getString("degree") : "");
            user.setId(dasta.has("id") ? dasta.getString("id") : "");
            user.setIsAdmin(dasta.has("isAdmin") ? dasta.getString("isAdmin") : "");
            user.setLike_gametype(dasta.has("like_gametype") ? dasta.getString("like_gametype") : "");
            user.setMobile(dasta.has("mobile") ? dasta.getString("mobile") : "");
            user.setEmail(dasta.has("email") ? dasta.getString("email") : "");
            user.setIsTelpass(dasta.has("isTelpass") ? dasta.getString("isTelpass") : "");
            user.setIsMailpass(dasta.has("isMailpass") ? dasta.getString("isMailpass") : "");
            user.setName(dasta.has("name") ? dasta.getString("name") : "");
            user.setTitle(dasta.has("nickname") ? dasta.getString("nickname") : "");
            user.setOpenId(dasta.has("openid") ? dasta.getString("openid") : "");
            user.setPassword(dasta.has("password") ? dasta.getString("password") : "");
            user.setRank(dasta.has("rank") ? dasta.getString("rank") : "");
            user.setSex(dasta.has("sex") ? dasta.getString("sex") : "");
            user.setTime(dasta.has("time") ? dasta.getString("time") : "");
            return user;
        } catch (Exception e) {
            Log.e("getUerInfo", e.toString());
        }
        return null;
    }


    /**
     * 直接修改密码
     *
     * @param uid
     * @param oldpwd
     * @param newpwd
     * @return
     */
    public static boolean directModifyPsd(String uid, String oldpwd, String newpwd) {
        String json = HttpUtils.httpGet(HttpUtils.directModifyPsdUrl(uid, oldpwd, newpwd));
        Log.e("directModifyPsd_url", HttpUtils.directModifyPsdUrl(uid, oldpwd, newpwd));
        Log.e("directModifyPsd_json", json);
        String result = "";
        try {
            if (json.equals("")) {
                return false;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return false;
            }

        } catch (Exception e) {
            Log.e("directModifyPsd", e.toString());
            return false;
        }
        return true;
    }

    /**
     * 验证修改密码
     *
     * @param uid
     * @param newpwd
     * @return
     */
    public static boolean verifyModifyPsd(String uid, String newpwd) {
        String json = HttpUtils.httpGet(HttpUtils.verifyModifyPsdUrl(uid, newpwd));
        Log.e("verifyModifyPsd_url", HttpUtils.verifyModifyPsdUrl(uid, newpwd));
        Log.e("verifyModifyPsd_json", json);
        String result = "";
        try {
            if (json.equals("")) {
                return false;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return false;
            }

        } catch (Exception e) {
            Log.e("verifyModifyPsd", e.toString());
            return false;
        }
        return true;
    }

    /**
     * 邮箱找回密码
     *
     * @param email
     * @return
     */
    public static boolean findPassWord(String email) {
        String json = HttpUtils.httpGet(HttpUtils.findPassWordUrl(email));
        Log.e("findPassWord_url", HttpUtils.findPassWordUrl(email));
        Log.e("findPassWord_json", json);
        String result = "";
        try {
            if (json.equals("")) {
                return false;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return false;
            }

        } catch (Exception e) {
            Log.e("findPassWord", e.toString());
            return false;
        }
        return true;
    }

    /**
     * 获取游戏名称搜索结果
     */
    public static ArrayList<VideoInfo> getSearchGameNameData(String type,
                                                             String keyword) {

        String url = "http://apps.ifeimo.com/home/search/associate201.html?classType="
                + type + "&keyWord=" + keyword;

        String json = HttpUtils.httpGet(url);

        JSONObject object = null;
        // 游戏类型ID
        String id;
        // 游戏名称8
        String name;
        // 游戏图片
        String imageUrl;

        JSONObject temp;
        VideoInfo videoInfo;
        ArrayList<VideoInfo> arraylist = new ArrayList<VideoInfo>();
        try {

            object = new JSONObject(json);

            JSONArray arrayList = object.getJSONArray("data");

            for (int i = 0; i < arrayList.length(); i++) {
                videoInfo = new VideoInfo();
                temp = (JSONObject) arrayList.get(i);

                // 视频名称
                name = temp.getString("name");
                // 图片链接
                imageUrl = temp.getString("flag");
                id = temp.getString("id");

                videoInfo.setGamename(name);
                videoInfo.setGameTypeId(id);
                videoInfo.setImageUrl(imageUrl);

                arraylist.add(videoInfo);

            }
        } catch (JSONException e) {

            e.printStackTrace();
        }

        return arraylist;

    }

    /**
     * 返回精彩推荐的情况
     *
     * @return null 没有信息
     * <p/>
     * *
     */
    public static String getFavorState() {
        String json = HttpUtils.httpGet(HttpUtils.getFavorRecommendUrl());
        String result = "";
        String msg = "";
        String data = "";

        String state = "";

        try {
            if (json.equals("")) {
                state = "";
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                state = "";
            }
            msg = object.getString("msg");
            if (msg.equals("没有选择喜欢类型，获取数据成功")) {
                state = NO;
            } else if (msg.equals("有选择喜欢类型，获取数据成功")) {
                state = YES;
            } else {
                state = "";
            }
        } catch (Exception e) {
            Log.e("getVedioList", e.toString());
            state = "";
        }

        return state;
    }


    /**
     * 返回精彩推荐的信息
     * <p/>
     * 没有选择喜欢类型，获取数据成功
     * 返回10个视频推荐的信息
     * <p/>
     * 有选择喜欢类型，获取数据成功
     * 返回个人偏好视频
     * <p/>
     * V1.1.5
     *
     * @return
     */
    public static List<?> getFavorVedioList() {
        String json = HttpUtils.httpGet(HttpUtils.getFavorRecommendUrl());
        String result = "";
        String msg = "";
        String data = "";
        List<VideoEntity> notList = new ArrayList<VideoEntity>();
        List<HomeHotEntity> yesList = new ArrayList<HomeHotEntity>();
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            msg = object.getString("msg");
            JSONArray array = object.getJSONArray(DATA);
            if (msg.equals("没有选择喜欢类型，获取数据成功")) {
                for (int i = 0; i < array.length(); i++) {
                    VideoEntity vedio = new VideoEntity();
                    JSONObject temp = (JSONObject) array.get(i);
                    vedio.setId(temp.getString("id"));
                    vedio.setTitle_content(temp.getString("title"));
                    vedio.setType_id(temp.getString("type_id"));
                    vedio.setSimg_url(temp.getString("flagPath"));
                    vedio.setFlower(temp.getString("flower_count"));
                    String aa = temp.getString("time_length");
                    int b = aa.indexOf(".");
                    if (b != -1) {
                        String c = aa.substring(0, b);
                        vedio.setTime(TimeUtils.secToTime(Integer.parseInt(c)));
                    } else {
                        vedio.setTime(TimeUtils.secToTime(Integer.parseInt(aa)));
                    }
                    vedio.setComment(temp.getString("comment_count"));
                    vedio.setViewCount(temp.getString("view_count"));
                    notList.add(vedio);
                }
                System.out.println("notList" + notList.toString());
                return notList;
            } else if (msg.equals("有选择喜欢类型，获取数据成功")) {
                for (int i = 0; i < array.length(); i++) {
                    List<VideoEntity> list = new ArrayList<VideoEntity>();
                    HomeHotEntity hot = new HomeHotEntity();
                    JSONObject temp = (JSONObject) array.get(i);
                    hot.setColumn(temp.has("typeName") ? temp.getString("typeName") : "");
                    hot.setColumnId(temp.has("type_id") ? temp.getString("type_id") : "");
                    JSONArray hotArray = temp.getJSONArray("videoList");
                    for (int j = 0; j < hotArray.length(); j++) {
                        JSONObject tempVideo = (JSONObject) hotArray.get(j);
                        VideoEntity vedio = new VideoEntity();

                        hot.setColumnId(tempVideo.has("type_id") ? tempVideo.getString("type_id") : "");

                        vedio.setId(tempVideo.has("id") ? tempVideo.getString("id") : "");
                        vedio.setTitle_content(tempVideo.has("title") ? tempVideo.getString("title") : "");
                        vedio.setType_id(tempVideo.has("type_id") ? tempVideo.getString("type_id") : "");
                        vedio.setSimg_url(tempVideo.has("flagPath") ? tempVideo.getString("flagPath") : "");
                        vedio.setFlower(tempVideo.has("flower_count") ? tempVideo.getString("flower_count") : "");
                        String aa = tempVideo.has("time_length") ? tempVideo.getString("time_length") : "0";
                        int b = aa.indexOf(".");
                        if (b != -1) {
                            String c = aa.substring(0, b);
                            vedio.setTime(TimeUtils.secToTime(Integer.parseInt(c)));
                        } else {
                            vedio.setTime(TimeUtils.secToTime(Integer.parseInt(aa)));
                        }
                        vedio.setComment(tempVideo.has("comment_count") ? tempVideo.getString("comment_count") : "");
                        vedio.setViewCount(tempVideo.has("view_count") ? tempVideo.getString("view_count") : "");
                        list.add(vedio);
                    }
                    hot.setHotList(list);
                    yesList.add(hot);
                }
                System.out.println("yesList" + yesList.toString());
                return yesList;
            }
        } catch (Exception e) {
            Log.e("getVedioList", e.toString());
            return null;
        }
        return null;
    }


    /**
     * 大神详情页头部信息，包含4个大神视频
     *
     * @param id        玩家id
     * @param member_id 大神id
     * @return
     */
    public static List<Object> getMasterHeadInfo(String id, String member_id) {
        String json = HttpUtils.httpGet(HttpUtils.getMasterHeadInfoUrl(id, member_id));
        Log.e("getMasterHeadInfo_url", HttpUtils.getMasterHeadInfoUrl(id, member_id));
        Log.e("getMasterHeadInfo_json", json);
        String result = "";
        String msg = "";
        String data = "";
        List<Object> list = new ArrayList<Object>();
        List<VideoEntity> videoList = new ArrayList<VideoEntity>();
//        List<MasterEntity> master = new ArrayList<MasterEntity>();
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONObject object1 = object.getJSONObject("data").getJSONObject("topic");
            try {
                MasterEntity masterEntity = new MasterEntity();
                masterEntity.setMember_id(object1.has("member_id") ? object1.getString("member_id") : "");
                masterEntity.setTopic_avatar(object1.has("topic_avatar") ? object1.getString("topic_avatar") : "");
                masterEntity.setHobby(object1.has("hobby") ? object1.getString("hobby") : "");
                masterEntity.setGame_career(object1.has("game_career") ? object1.getString("game_career") : "");
                masterEntity.setOften_game(object1.has("often_game") ? object1.getString("often_game") : "");
                masterEntity.setManifesto(object1.has("testimonials") ? object1.getString("testimonials") : "");
                masterEntity.setPraise(object1.has("praise") ? object1.getString("praise") : "");
                masterEntity.setUrl(object1.has("url") ? object1.getString("url") : "");
                masterEntity.setNickname(object1.has("nickname") ? object1.getString("nickname") : "");
                masterEntity.setFans(object1.has("fans") ? object1.getString("fans") : "");
                masterEntity.setLikeMark(object1.has("likeMark") ? object1.getInt("likeMark") : 0);
                masterEntity.setMark(object1.has("mark") ? object1.getInt("mark") : 0);
//                master.add(masterEntity);
                list.add(0, masterEntity);
            } catch (Exception e) {
                Log.e("getMasterHeadInfo_master", e.toString());
            }

//            data=object.getString(DATA);
            JSONArray array = object.getJSONObject(DATA).getJSONArray("video");
            for (int i = 0; i < array.length(); i++) {
                VideoEntity vedio = new VideoEntity();
                JSONObject temp = (JSONObject) array.get(i);
                vedio.setId(temp.getString("id"));
                vedio.setTitle_content(temp.getString("name"));
                vedio.setSimg_url(temp.getString("pic_flsp"));
                vedio.setFlower(temp.getString("flower_count"));
                String aa = temp.getString("time_length");
                int b = aa.indexOf(".");
                if (b != -1) {
                    String c = aa.substring(0, b);
                    vedio.setTime(TimeUtils.secToTime(Integer.parseInt(c)));
                } else {
                    vedio.setTime(TimeUtils.secToTime(Integer.parseInt(aa)));
                }
                vedio.setComment(temp.getString("comment_count"));
                vedio.setViewCount(temp.getString("view_count"));
                videoList.add(vedio);
            }
            list.add(1, videoList);
        } catch (Exception e) {
            Log.e("getMasterHeadInfo_video", e.toString());
            return null;
        }
        return list;
    }


    /**
     * 返回首页视界专栏
     * <p/>
     * 热门视频
     * 视界专栏 （舞大大学堂/小小舞玩新游/阿沫爱品评）
     * 热门游戏、热门分类
     * <p/>
     * V1.1.6
     *
     * @return
     */
    public static List<HomeColumnEntity> getHomeColumnList(Context context, String page) {
        String json = HttpUtils.httpGet(HttpUtils.getHomeColumnUrl(page));

        Log.e("getHomeColumnUrl_url", HttpUtils.getHomeColumnUrl(page));
        Log.e("getHomeColumnUrl_json", json);
        String result = "";
        String msg = "";
        String data = "";
        List<HomeColumnEntity> columnList = new ArrayList<HomeColumnEntity>();
        try {
            if (json.equals("")) {
                json = SharePreferenceUtil.getHomeColumn(context);
//                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            } else if (page.equals("1")) {
                SharePreferenceUtil.setHomeColumn(context, json);
            }

//            data=object.getString(DATA);
            JSONArray array = object.getJSONArray(DATA);
            for (int i = 0; i < array.length(); i++) {
                List<VideoEntity> list = new ArrayList<VideoEntity>();
                HomeColumnEntity column = new HomeColumnEntity();
                JSONObject temp = (JSONObject) array.get(i);
                column.setTitle(temp.has("title") ? temp.getString("title") : "");
                column.setIcon_pic(temp.has("icon_pic") ? temp.getString("icon_pic") : "");
                column.setMore_mark(temp.has("more_mark") ? temp.getString("more_mark") : "");

                JSONArray hotArray = temp.getJSONArray("list");
                for (int j = 0; j < hotArray.length(); j++) {
                    JSONObject tempVideo = (JSONObject) hotArray.get(j);
                    VideoEntity video = new VideoEntity();
                    video.setId(tempVideo.has("id") ? tempVideo.getString("id") : "");
                    video.setTitle(tempVideo.has("title") ? tempVideo.getString("title") : "");
                    video.setViewCount(tempVideo.has("view_count") ? tempVideo.getString("view_count") : "");
                    video.setFlagPath(tempVideo.has("flagPath") ? tempVideo.getString("flagPath") : "");
                    String aa = tempVideo.has("time_length") ? tempVideo.getString("time_length") : "0";//视频长度
                    int b = aa.indexOf(".");
                    if (b != -1) {
                        String c = aa.substring(0, b);
                        video.setTime(TimeUtils.secToTime(Integer.parseInt(c)));
                    } else {
                        video.setTime(TimeUtils.secToTime(Integer.parseInt(aa)));
                    }
                    video.setFlower(tempVideo.has("flower_count") ? tempVideo.getString("flower_count") : "");
                    video.setComment(tempVideo.has("comment_count") ? tempVideo.getString("comment_count") : "");

                    list.add(video);
                }
                column.setColumnList(list);
                columnList.add(column);
            }
        } catch (Exception e) {
            Log.e("getHomeColumnUrl_e", e.toString());
            return null;
        }
        return columnList;
    }


    /**
     * 返回更多热门视频的信息
     *
     * @return
     */
    public static List<VideoEntity> getMoreRecomVedioList(String page) {
        String json = HttpUtils.httpGet(HttpUtils.getMoreRecommendUrl(page));
        String result = "";
        String msg = "";
        String data = "";
        List<VideoEntity> list = new ArrayList<VideoEntity>();
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
//            data=object.getString(DATA);
            JSONArray array = object.getJSONArray(DATA);
            for (int i = 0; i < array.length(); i++) {
                VideoEntity vedio = new VideoEntity();
                JSONObject temp = (JSONObject) array.get(i);
                vedio.setId(temp.has("id") ? temp.getString("id") : "");
                vedio.setTitle_content(temp.has("name") ? temp.getString("name") : "");
                vedio.setSimg_url(temp.has("flagPath") ? temp.getString("flagPath") : "");
                vedio.setFlower(temp.has("flower_count") ? temp.getString("flower_count") : "0");
                String aa = temp.getString("time_length");
                int b = aa.indexOf(".");
                if (b != -1) {
                    String c = aa.substring(0, b);
                    vedio.setTime(TimeUtils.secToTime(Integer.parseInt(c)));
                } else {
                    vedio.setTime(TimeUtils.secToTime(Integer.parseInt(aa)));
                }
                vedio.setComment(temp.has("comment_count") ? temp.getString("comment_count") : "0");
                vedio.setViewCount(temp.has("view_count") ? temp.getString("view_count") : "0");
                list.add(vedio);
            }
        } catch (Exception e) {
            Log.e("getMoreRecomVedioList_e", e.toString());
            return null;
        }
        return list;
    }

    /**
     * 轻松一刻视频列表
     *
     * @param page
     * @return
     */
    public static List<VideoEntity> getRelaxeVedioList(String page) {
        String json = HttpUtils.httpGet(HttpUtils.getRelaxeVideoUrl(page));
        String result = "";
        String msg = "";
        String data = "";
        List<VideoEntity> list = new ArrayList<VideoEntity>();
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
//            data=object.getString(DATA);
            JSONArray array = object.getJSONObject("data").getJSONArray("videoList");
            for (int i = 0; i < array.length(); i++) {
                VideoEntity vedio = new VideoEntity();
                JSONObject temp = (JSONObject) array.get(i);
                vedio.setId(temp.getString("id"));
                vedio.setTitle_content(temp.getString("name"));
                vedio.setSimg_url(temp.getString("flagPath"));
                vedio.setFlower(temp.getString("flower_count"));
                String aa = temp.getString("time_length");
                int b = aa.indexOf(".");
                if (b != -1) {
                    String c = aa.substring(0, b);
                    vedio.setTime(TimeUtils.secToTime(Integer.parseInt(c)));
                } else {
                    vedio.setTime(TimeUtils.secToTime(Integer.parseInt(aa)));
                }
                vedio.setComment(temp.getString("comment_count"));
                vedio.setViewCount(temp.getString("view_count"));
                list.add(vedio);
            }
        } catch (Exception e) {
            Log.e("getRelaxeVedioList_e", e.toString());
            return null;
        }
        return list;
    }

    /**
     * 返回小编荐视频列表
     *
     * @param context
     * @param page
     * @return
     */
    public static List<VideoEntity> getShowPointList(Context context, String page) {
        String json = HttpUtils.httpGet(HttpUtils.getShowPointList(page));
        Log.e("getShowPointList", json);
        Log.e("getShowPointList_url", HttpUtils.getShowPointList(page));
        String result = "";
        String msg = "";
        String data = "";
        List<VideoEntity> list = new ArrayList<VideoEntity>();
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
//            data=object.getString(DATA);
            JSONArray array = object.getJSONArray(DATA);
            for (int i = 0; i < array.length(); i++) {
                VideoEntity vedio = new VideoEntity();
                JSONObject temp = (JSONObject) array.get(i);
                vedio.setId(temp.has("id") ? temp.getString("id") : "");
                vedio.setComment_count(temp.has("comment_count") ? temp.getString("comment_count") : "");
                vedio.setFlagPath(temp.has("flagPath") ? temp.getString("flagPath") : "");
                vedio.setFlower_count(temp.has("flower_count") ? temp.getString("flower_count") : "");
                String aa = temp.has("time_length") ? temp.getString("time_length") : "";
                int b = aa.indexOf(".");
                if (b != -1) {
                    String c = aa.substring(0, b);
                    vedio.setTime(TimeUtils.secToTime(Integer.parseInt(c)));
                } else {
                    vedio.setTime(TimeUtils.secToTime(Integer.parseInt(aa)));
                }
                vedio.setTitle(temp.has("name") ? temp.getString("name") : "");
                vedio.setView_count(temp.has("view_count") ? temp.getString("view_count") : "");
                list.add(vedio);
            }
        } catch (Exception e) {
            Log.e("getShowPoint", e.toString());
            return null;
        }
        return list;
    }

    /**
     * 异步获取云端视频列表
     */
    public static List<VideoEntity> getColudVideoList(String page) {
        String json = HttpUtils.httpGet(HttpUtils.getColudVideoUrl(page));
        Log.e("getColudVideoList", json);
        Log.e("getColudVideoList_url", HttpUtils.getColudVideoUrl(page));
        String result = "";
        String msg = "";
        String data = "";
        List<VideoEntity> list = new ArrayList<VideoEntity>();
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
//            data=object.getString(DATA);
            JSONArray array = object.getJSONArray(DATA);
            for (int i = 0; i < array.length(); i++) {
                VideoEntity vedio = new VideoEntity();
                JSONObject temp = (JSONObject) array.get(i);
                vedio.setId(temp.has("id") ? temp.getString("id") : "");
                vedio.setComment_count(temp.has("comment_count") ? temp.getString("comment_count") : "");
                vedio.setFlagPath(temp.has("flagPath") ? temp.getString("flagPath") : "");
                vedio.setFlower_count(temp.has("flower_count") ? temp.getString("flower_count") : "");
                vedio.setUrl(temp.has("url") ? temp.getString("url") : "");
                vedio.setQn_key(temp.has("qn_key") ? temp.getString("qn_key") : "");
                String aa = temp.has("time_length") ? temp.getString("time_length") : "";
                int b = aa.indexOf(".");
                if (b != -1) {
                    String c = aa.substring(0, b);
                    vedio.setTime(TimeUtils.secToTime(Integer.parseInt(c)));
                } else {
                    vedio.setTime(TimeUtils.secToTime(Integer.parseInt(aa)));
                }
                vedio.setTitle(temp.has("name") ? temp.getString("name") : "");
                vedio.setViewCount(temp.has("view_count") ? temp.getString("view_count") : "");
                vedio.setShare_count(temp.has("share_count") ? temp.getString("share_count") : "0");
                vedio.setUpload_time(temp.has("upload_time") ? temp.getString("upload_time") : "");
                list.add(vedio);
            }
        } catch (Exception e) {
            Log.e("getColudVideoList_e", e.toString());
            return null;
        }
        return list;
    }

    /**
     * 返回视界专栏视频二级列表
     *
     * @param context
     * @param page
     * @return
     */
    public static List<VideoEntity> getHomeColumnList(Context context, String page, String mark) {
        String json = HttpUtils.httpGet(HttpUtils.getHomeColumnListUrl(page, mark));
        Log.e("getHomeColumnList", json);
        Log.e("getHomeColumnList_url", HttpUtils.getHomeColumnListUrl(page, mark));
        String result = "";
        String msg = "";
        String data = "";
        List<VideoEntity> list = new ArrayList<VideoEntity>();
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
//            data=object.getString(DATA);
            JSONArray array = object.getJSONArray(DATA);
            for (int i = 0; i < array.length(); i++) {
                VideoEntity vedio = new VideoEntity();
                JSONObject temp = (JSONObject) array.get(i);
                vedio.setId(temp.has("id") ? temp.getString("id") : "");
                vedio.setComment_count(temp.has("comment_count") ? temp.getString("comment_count") : "");
                vedio.setFlagPath(temp.has("flagPath") ? temp.getString("flagPath") : "");
                vedio.setFlower_count(temp.has("flower_count") ? temp.getString("flower_count") : "");
                String aa = temp.has("time_length") ? temp.getString("time_length") : "";
                int b = aa.indexOf(".");
                if (b != -1) {
                    String c = aa.substring(0, b);
                    vedio.setTime(TimeUtils.secToTime(Integer.parseInt(c)));
                } else {
                    vedio.setTime(TimeUtils.secToTime(Integer.parseInt(aa)));
                }
                vedio.setTitle(temp.has("name") ? temp.getString("name") : "");
                vedio.setView_count(temp.has("view_count") ? temp.getString("view_count") : "");
                list.add(vedio);
            }
        } catch (Exception e) {
            Log.e("getHomeColumnList", e.toString());
            return null;
        }
        return list;
    }

    public static String getNextVideoId(Context context, String id) {
        String json = HttpUtils.httpGet(HttpUtils.getNextVideoUrl(id));
        Log.e("getNextVideoId", json);
        Log.e("getNextVideoId", HttpUtils.getNextVideoUrl(id));
        String result = "";
        String data = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            data = object.getString("data");
        } catch (Exception e) {
            Log.e("getNextVideoId", e.toString());
            return null;
        }
        return data;
    }


    /**
     * 获取视频详情
     *
     * @param context
     * @param id
     * @return
     */
    public static VedioDetail getVedioDetail(Context context, String id, String member_id) {
        VedioDetail detail = new VedioDetail();
        String json = HttpUtils.httpGet(HttpUtils.getVedioUrl(id, member_id));
        Log.e("VedioDetail", json);
        Log.e("VedioDetail_url", HttpUtils.getVedioUrl(id, member_id));
        String result = "";
        String msg = "";
        String data = "";

        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
//            data=object.getString(DATA);
            JSONObject temp = object.getJSONObject(DATA);
            detail.setUrl(temp.getString("url"));
            detail.setId(temp.getString("id"));
            detail.setType_id(temp.getString("type_id"));
            detail.setGame_id(temp.getString("game_id"));
            detail.setMember_id(temp.getString("member_id"));
            detail.setName(temp.getString("name"));
            detail.setDescriptioin(temp.getString("description"));
            detail.setContent(temp.getString("content"));
            detail.setView_count(temp.getString("view_count"));
            detail.setFlower_count(StringUtils.adjustCount(temp.getString("flower_count")));
            detail.setCollection_count(StringUtils.adjustCount(temp.getString("collection_count")));
            detail.setFlagPath(temp.getString("flagPath"));
            detail.setComment_count(temp.getString("comment_count"));
            String time = temp.has("time") ? temp.getString("time") : "";//玩家上传时间
            detail.setTime(DateUtils.convertTimeToFormat(Long.parseLong(time)));
//            detail.setUpload_time(TimeUtils.timestampToDate(temp.getString("upload_time")));//编辑上传时间
            String upload_time = temp.has("upload_time") ? temp.getString("upload_time") : "";
            detail.setUpload_time(DateUtils.convertTimeToFormat(Long.parseLong(upload_time)));//编辑上传时间
            detail.setTime_length(TimeUtils.secToTime(Integer.parseInt(temp.getString("time_length"))));//视频长度
            detail.setType_name(temp.getString("type_name"));
            detail.setDownload_count(temp.getString("download_count"));
            detail.setGameDownloadUrl(temp.getString("gameDownloadUrl"));
            detail.setUserName(temp.getString("userName"));
            detail.setAvatar(temp.getString("avatar"));
            detail.setDescription(temp.getString("description"));
            detail.setQn_key(temp.has("qn_key") ? temp.getString("qn_key") : "");
            detail.setFlower_mark(temp.has("flower_mark") ? temp.getInt("flower_mark") : 0);
            detail.setCollection_mark(temp.has("collection_mark") ? temp.getInt("collection_mark") : 0);
        } catch (Exception e) {
            Log.e("getVedioDetail", e.toString());
            return null;
        }
        return detail;

    }


    /**
     * 获取玩家秀视频列表
     *
     * @return
     */
    public static List<PlayerShowEntity> getPlayerShowList(String page) {
        List<PlayerShowEntity> list = new ArrayList<PlayerShowEntity>();
        String json = HttpUtils.httpGet(HttpUtils.getPlayerShowUrl(page));
        Log.e("PlayerShowList", json);
        Log.e("PlayerShowList_url", HttpUtils.getPlayerShowUrl(page));
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);//将String格式的json数据转化成JSONObject
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONArray array = object.getJSONArray("data");//从JSONObject中根据键值key获取JSONArray
            for (int i = 0; i < array.length(); i++) {
                JSONObject temp = (JSONObject) array.get(i);//从JSONArray中根据index获取对象，再转化成JSONObject
                PlayerShowEntity player = new PlayerShowEntity();
                player.setUrl(temp.has("url") ? temp.getString("url") : "");;//从JSONObject中根据键值key获取String
                player.setNickName(temp.has("nickname") ? temp.getString("nickname") : "");
                player.setName(temp.has("name") ? temp.getString("name") : "");
                player.setFlower_count(temp.has("flower_count") ? temp.getString("flower_count") : "");
                player.setComment_count(temp.has("comment_count") ? temp.getString("comment_count") : "");
                player.setView_count(temp.has("view_count") ? temp.getString("view_count") : "");
                player.setTime(temp.has("time") ? temp.getString("time") : "");

                String aa = temp.getString("time_length");
                int b = aa.indexOf(".");
                if (b != -1) {
                    String c = aa.substring(0, b);
                    player.setTime_length(TimeUtils.secToTime(Integer.parseInt(c)));
                } else {
                    player.setTime_length(TimeUtils.secToTime(Integer.parseInt(aa)));
                }

                player.setFlagPath(temp.has("flagPath") ? temp.getString("flagPath") : "");
                player.setAvatar(temp.has("avatar") ? temp.getString("avatar") : "");
                player.setQn_key(temp.has("qn_key") ? temp.getString("qn_key") : "");
                player.setId(temp.has("id") ? temp.getString("id") : "");
                list.add(player);
            }
        } catch (Exception e) {
            Log.e("getPlayerShowList", e.toString());
            return null;
        }
        return list;

    }

    /**
     * 获取大神更新列表
     *
     * @param page
     * @return
     */
    public static List<VideoEntity> getMasterUpdateVideoList(String id, String page) {
        List<VideoEntity> list = new ArrayList<VideoEntity>();
        String json = HttpUtils.httpGet(HttpUtils.getMasterUpdateUrl(id, page));
        Log.e("getMasterUpdateVideoList", json);
        Log.e("getMasterUpdateVideoList_url", HttpUtils.getMasterUpdateUrl(id, page));
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONArray array = object.getJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                JSONObject temp = (JSONObject) array.get(i);
                VideoEntity player = new VideoEntity();
                player.setName(temp.has("name") ? temp.getString("name") : "");
                player.setFlagPath(temp.has("flagPath") ? temp.getString("flagPath") : "");
                player.setTime(TimeUtils.secToTime(Integer.parseInt(temp.getString("time_length"))));
                player.setFlower_count(temp.has("flower_count") ? temp.getString("flower_count") : "");
                player.setComment_count(temp.has("comment_count") ? temp.getString("comment_count") : "");
                player.setViewCount(temp.has("view_count") ? temp.getString("view_count") : "");
                player.setId(temp.has("id") ? temp.getString("id") : "");
                list.add(player);
            }
        } catch (Exception e) {
            Log.e("getMasterUpdateVideoList", e.toString());
            return null;
        }
        return list;

    }

    /**
     * 获取发现视频列表
     *
     * @return
     */
    public static List<DiscoverVideoEntity> getDiscoverVideoList(Context context) {
        List<DiscoverVideoEntity> list = new ArrayList<DiscoverVideoEntity>();
        String json = HttpUtils.httpGet(HttpUtils.getDiscoverVideoUrl());
        Log.e("getDiscoverVideoList", json);
        Log.e("getDiscoverVideoList_url", HttpUtils.getDiscoverVideoUrl());
        String result = "";
        try {
            if (json.equals("")) {
                json = SharePreferenceUtil.getDiscoverList(context);
//                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            SharePreferenceUtil.setDiscoverList(context, json);

            JSONArray array = object.getJSONObject("data").getJSONArray("videoList");
            for (int i = 0; i < array.length(); i++) {
                JSONObject temp = (JSONObject) array.get(i);
                DiscoverVideoEntity discover = new DiscoverVideoEntity();
                discover.setUrl(temp.has("url") ? temp.getString("url") : "");
                discover.setNickName(temp.has("userName") ? temp.getString("userName") : "");
                discover.setName(temp.has("name") ? temp.getString("name") : "");
                discover.setFlower_count(temp.has("flower_count") ? temp.getString("flower_count") : "");
                discover.setFlagPath(temp.has("flagPath") ? temp.getString("flagPath") : "");
                discover.setComment_count(temp.has("comment_count") ? temp.getString("comment_count") : "");
                discover.setAvatar(temp.has("avatar") ? temp.getString("avatar") : "");
                discover.setQn_key(temp.has("qn_key") ? temp.getString("qn_key") : "");
                discover.setId(temp.has("id") ? temp.getString("id") : "");
                list.add(discover);
            }
        } catch (Exception e) {
            Log.e("getDiscoverVideoList", e.toString());
            return null;
        }
        return list;

    }

    /**
     * 推荐ing列表
     *
     * @param page
     * @return
     */
    public static List<RecommendEntity> getRecommendIngList(String page) {
        List<RecommendEntity> list = new ArrayList<RecommendEntity>();
        String json = HttpUtils.httpGet(HttpUtils.getRecommendingUrl(page));
        Log.e("getRecommendIngList", json);
        Log.e("getRecommendIngList_url", HttpUtils.getRecommendingUrl(page));
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONObject data = object.getJSONObject("data");
            JSONArray array = data.getJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                JSONObject temp = (JSONObject) array.get(i);
                RecommendEntity recommendEntity = new RecommendEntity();
                recommendEntity.setUrl(temp.has("url") ? temp.getString("url") : "");
                recommendEntity.setFlagPath(temp.has("flagPath") ? temp.getString("flagPath") : "");
                recommendEntity.setVideo_id(temp.has("video_id") ? temp.getString("video_id") : "");
                recommendEntity.setTitle(temp.has("title") ? temp.getString("title") : "");
                recommendEntity.setActivity_id(temp.has("activity_id") ? temp.getString("activity_id") : "");
                recommendEntity.setPackage_id(temp.has("package_id") ? temp.getString("package_id") : "");
                recommendEntity.setType(temp.has("type") ? temp.getString("type") : "");
                list.add(recommendEntity);
            }
        } catch (Exception e) {
            Log.e("getRecommendIngList", e.toString());
            return null;
        }
        return list;

    }

    /**
     * 获取 启动页 图片（广告）大图列表
     *
     * @param time
     * @return list
     */
    public static List<LaunchImgEntity> getLaunchImage(String time) {
        List<LaunchImgEntity> list = new ArrayList<LaunchImgEntity>();
        String json = HttpUtils.httpGet(HttpUtils.getLaunchImageUrl(time));
        Log.e("getLaunchImage_url", HttpUtils.getLaunchImageUrl(time));
        Log.e("getLaunchImage_json", json);
        String result = "";
        String changetime = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            changetime = object.getString("changetime");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONArray array = object.getJSONArray(DATA);
            for (int i = 0; i < array.length(); i++) {
                JSONObject temp = (JSONObject) array.get(i);
                LaunchImgEntity launchImg = new LaunchImgEntity();
                launchImg.setChangetime(changetime);
                launchImg.setLaunch_id(temp.has("launch_id") ? temp.getString("launch_id") : "");
                launchImg.setTitle(temp.has("title") ? temp.getString("title") : "");
                launchImg.setFlag(temp.has("flag") ? temp.getString("flag") : "");
                launchImg.setStarttime(temp.has("starttime") ? temp.getString("starttime") : "");
                launchImg.setEndtime(temp.has("endtime") ? temp.getString("endtime") : "");
                launchImg.setAlone_id(temp.has("alone_id") ? temp.getString("alone_id") : "");
                list.add(launchImg);
            }
        } catch (Exception e) {
            Log.e("getLaunchImage", e.toString());
            return null;
        }
        return list;
    }


    /**
     * 首页广告位
     *
     * @return
     */
    public static List<RecommendEntity> getHomeAdList(Context context) {
        List<RecommendEntity> list = new ArrayList<RecommendEntity>();
        String json = HttpUtils.httpGet(HttpUtils.getHomeAdUrl());
        Log.e("getHomeAdList", json);
        Log.e("getHomeAdList_url", HttpUtils.getHomeAdUrl());
        String result = "";
        try {
            if (json.equals("")) {
                json = SharePreferenceUtil.getHomeAd(context);
//                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            } else {
                SharePreferenceUtil.setHomeAd(context, json);
            }
            JSONArray array = object.getJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                JSONObject temp = (JSONObject) array.get(i);
                RecommendEntity recommendEntity = new RecommendEntity();
                recommendEntity.setUrl(temp.has("url") ? temp.getString("url") : "");
                recommendEntity.setFlagPath(temp.has("flagPath") ? temp.getString("flagPath") : "");
                recommendEntity.setVideo_id(temp.has("video_id") ? temp.getString("video_id") : "");
                recommendEntity.setTitle(temp.has("title") ? temp.getString("title") : "");
                recommendEntity.setActivity_id(temp.has("activity_id") ? temp.getString("activity_id") : "");
                recommendEntity.setPackage_id(temp.has("package_id") ? temp.getString("package_id") : "");
                recommendEntity.setType(temp.has("type") ? temp.getString("type") : "");
                list.add(recommendEntity);
            }
        } catch (Exception e) {
            Log.e("getHomeAdList", e.toString());
            return null;
        }
        return list;

    }

    /**
     * 首页焦点
     *
     * @param context
     * @return
     */
    public static List<Advertisement> getAdvertiseList(Context context) {
        List<Advertisement> list = new ArrayList<Advertisement>();
        String json = HttpUtils.httpGet(HttpUtils.getAdVedioUrl());
        Log.e("getAdvertiseList", HttpUtils.getAdVedioUrl());
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONArray array = object.getJSONArray(DATA);
            for (int i = 0; i < array.length(); i++) {
                JSONObject temp = (JSONObject) array.get(i);
                Advertisement advertise = new Advertisement();
                advertise.setVideo_id(temp.getString("video_id"));
//                advertise.setFlagPath(temp.getString("flagPath"));
                advertise.setTitle(temp.getString("title"));
                advertise.setPosition(temp.getString("position"));
                advertise.setImg_url(temp.getString("imgPath"));
                if (temp.getInt("mark") == 1) {
                    advertise.setUrl(temp.getString("url"));
                }
                advertise.setMark(temp.getInt("mark"));
                list.add(advertise);
            }
        } catch (Exception e) {
            Log.e("getAdvertise", e.toString());
            return null;
        }
        return list;
    }

    /**
     * 获取玩家秀 滚动广告 大图列表
     *
     * @return
     */
    public static List<RecommendEntity> getAdPlayerShowList() {
        List<RecommendEntity> list = new ArrayList<RecommendEntity>();
        String json = HttpUtils.httpGet(HttpUtils.getAdPlayerShowUrl());
        Log.e("getAdPlayerShowList_url", HttpUtils.getAdPlayerShowUrl());
        Log.e("getAdPlayerShowList_json", json);
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONArray array = object.getJSONArray(DATA);
            for (int i = 0; i < array.length(); i++) {
                JSONObject temp = (JSONObject) array.get(i);
                RecommendEntity recommend = new RecommendEntity();
                recommend.setFlagPath(temp.has("flagPath") ? temp.getString("flagPath") : "");
                recommend.setTitle(temp.has("title") ? temp.getString("title") : "");
                recommend.setVideo_id(temp.has("video_id") ? temp.getString("video_id") : "");
                list.add(recommend);
            }
        } catch (Exception e) {
            Log.e("getAdPlayerShowList", e.toString());
            return null;
        }
        return list;
    }

    /**
     * 获取Banner广告大图列表
     *
     * @param situation
     * @return map
     */
    public static List<BannerEntity> getBannerAd(String situation) {
        List<BannerEntity> list = new ArrayList<BannerEntity>();
        String json = HttpUtils.httpGet(HttpUtils.getBannerAdUrl(situation));
        Log.e("getBannerAdUrl_url", HttpUtils.getBannerAdUrl(situation));
        Log.e("getBannerAdUrl_json", json);
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONArray array = object.getJSONArray(DATA);
            for (int i = 0; i < array.length(); i++) {
                JSONObject temp = (JSONObject) array.get(i);
                BannerEntity banner = new BannerEntity();
                banner.setId(temp.has("id") ? temp.getString("id") : "");
                banner.setTitle(temp.has("title") ? temp.getString("title") : "");
                banner.setFlag(temp.has("flag") ? temp.getString("flag") : "");
                banner.setUrl(temp.has("url") ? temp.getString("url") : "");
                banner.setSituation(temp.has("situation") ? temp.getString("situation") : "");
                banner.setFlagPath(temp.has("flagPath") ? temp.getString("flagPath") : "");
                list.add(banner);
            }
        } catch (Exception e) {
            Log.e("getBannerAdUrl", e.toString());
            return null;
        }
        return list;
    }

    /**
     * 获取视频评论列表
     *
     * @param context
     * @param id
     * @param page
     * @return
     */
    public static List<CommentEntity> getCommentList(Context context, String id, String page) {
        List<CommentEntity> list = new ArrayList<CommentEntity>();
        String json = HttpUtils.httpGet(HttpUtils.getCommentUrl(id, page));
        Log.e("CommentUrl", HttpUtils.getCommentUrl(id, page));
        Log.e("comment_json", json);
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            String itemsCount = object.getJSONObject(DATA).getString("itemsCount");
            JSONArray array = object.getJSONObject(DATA).getJSONArray("list");
            for (int i = 0; i < array.length(); i++) {
                CommentEntity entity = new CommentEntity();
                JSONObject temp = (JSONObject) array.get(i);
                entity.setItemsCount(itemsCount);
                entity.setContent(temp.has("content") ? temp.getString("content") : "");
                entity.setImgPath(temp.has("avatar") ? temp.getString("avatar") : "");
                entity.setName(temp.has("nickname") ? temp.getString("nickname") : "");
                entity.setTime(temp.has("time") ? temp.getString("time") : "");
                entity.setTime_x(temp.has("time_x") ? temp.getString("time_x") : "");
                entity.setHonour(temp.has("title") ? temp.getString("title") : "");
                entity.setLevel(temp.has("rank") ? temp.getInt("rank") : 1);
                entity.setMemberId(temp.has("member_id") ? temp.getString("member_id") : "");
                entity.setComment_id(temp.has("comment_id") ? temp.getString("comment_id") : "");
                entity.setReview_id(temp.has("review_id") ? temp.getString("review_id") : "");
                entity.setLike(temp.has("num") ? temp.getString("num") : "");
                entity.setMark(temp.has("mark") ? temp.getString("mark") : "");
                entity.setLikeMark(temp.has("likeMark") ? temp.getString("likeMark") : "");
                entity.setLikeNum(temp.has("likeNum") ? temp.getString("likeNum") : "");
                list.add(entity);
            }
        } catch (Exception e) {
            Log.e("comment", e.toString());
            return null;
        }
        return list;
    }

    /**
     * 获取个人空间评论列表
     *
     * @param id
     * @param page
     * @return
     */
    public static List<CommentEntity> getPersonCommentList(String member_id, String page, String id) {
        List<CommentEntity> list = new ArrayList<CommentEntity>();
        String json = HttpUtils.httpGet(HttpUtils.getPersonCommentUrl(member_id, page, id));
        Log.e("getPersonCommentList_url", HttpUtils.getPersonCommentUrl(member_id, page, id));
        Log.e("getPersonCommentList_json", json);
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONArray array = object.getJSONObject(DATA).getJSONArray("list");
            for (int i = 0; i < array.length(); i++) {
                CommentEntity entity = new CommentEntity();
                JSONObject temp = (JSONObject) array.get(i);
                entity.setContent(temp.has("content") ? temp.getString("content") : "");
                entity.setImgPath(temp.has("avatar") ? temp.getString("avatar") : "");
                entity.setName(temp.has("nickname") ? temp.getString("nickname") : "");
                entity.setTime(temp.has("time") ? temp.getString("time") : "");
                entity.setHonour(temp.has("title") ? temp.getString("title") : "");
                entity.setLevel(temp.has("rank") ? temp.getInt("rank") : 1);
                entity.setMemberId(temp.has("member_id") ? temp.getString("member_id") : "");
                entity.setComment_id(temp.has("comment_id") ? temp.getString("comment_id") : "");
                entity.setReview_id(temp.has("review_id") ? temp.getString("review_id") : "");
                entity.setLike(temp.has("num") ? temp.getString("num") : "0");
                entity.setMark(temp.has("mark") ? temp.getString("mark") : "0");
                entity.setLikeMark(temp.has("likeMark") ? temp.getString("likeMark") : "0");
                entity.setLikeNum(temp.has("likeNum") ? temp.getString("likeNum") : "0");
                list.add(entity);
            }
        } catch (Exception e) {
            Log.e("getPersonCommentList_e", e.toString());
            return null;
        }
        return list;
    }

    /**
     * 游戏圈子评论列表
     *
     * @param group_id
     * @param member_id
     * @param page
     * @return
     */
    public static List<CommentEntity> getGameCircleCommentList(String group_id, String member_id, String page) {
        List<CommentEntity> list = new ArrayList<CommentEntity>();
        String json = HttpUtils.httpGet(HttpUtils.getGameCircleCommentUrl(group_id, member_id, page));
        Log.e("getGameCircleCommentList_url", HttpUtils.getGameCircleCommentUrl(group_id, member_id, page));
        Log.e("getGameCircleCommentList_json", json);
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONArray array = object.getJSONArray(DATA);
            for (int i = 0; i < array.length(); i++) {
                CommentEntity entity = new CommentEntity();
                JSONObject temp = (JSONObject) array.get(i);
                entity.setContent(temp.has("content") ? temp.getString("content") : "");
                entity.setImgPath(temp.has("avatar") ? temp.getString("avatar") : "");
                entity.setName(temp.has("nickname") ? temp.getString("nickname") : "");
                entity.setTime(temp.has("time") ? temp.getString("time") : "");
                entity.setHonour(temp.has("title") ? temp.getString("title") : "");
                entity.setLevel(temp.has("rank") ? temp.getInt("rank") : 1);
                entity.setMemberId(temp.has("member_id") ? temp.getString("member_id") : "");
                entity.setComment_id(temp.has("comment_id") ? temp.getString("comment_id") : "");
                entity.setReview_id(temp.has("review_id") ? temp.getString("review_id") : "");
                entity.setLike(temp.has("num") ? temp.getString("num") : "0");
                entity.setMark(temp.has("mark") ? temp.getString("mark") : "0");
                entity.setLikeMark(temp.has("likeMark") ? temp.getString("likeMark") : "0");
                entity.setLikeNum(temp.has("like_num") ? temp.getString("like_num") : "0");
                list.add(entity);
            }
        } catch (Exception e) {
            Log.e("getGameCircleCommentList_e", e.toString());
            return null;
        }
        return list;
    }

    /**
     * 得到反馈的信息
     *
     * @param member_id
     * @param content
     * @param email
     * @return
     */
    public static String getFeekBack(String member_id, String content, String email) {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("member_id", member_id));
        pairs.add(new BasicNameValuePair("content", content));
        pairs.add(new BasicNameValuePair("email", email));
        pairs.add(new BasicNameValuePair("target", "a_sysj"));
        String json = HttpUtils.httpPost(pairs, HttpUtils.getFeekbackUrl());
        try {
            JSONObject object = new JSONObject(json);
            String result = object.getString("result");
            Log.e("-----", object.getString("result") + "-" + object.getString("msg") + "-" + object.getString("data"));
            if (result.equals("true")) {
                return "s";
            }
        } catch (Exception e) {
            return "";
        }

        return "";
    }

    /**
     * 获取手机验证码
     *
     * @param key
     * @return
     */
    public static String getPhoneCode(String key) {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("key", key));
        pairs.add(new BasicNameValuePair("target", "sysj"));
        String json = HttpUtils.httpPost(pairs, HttpUtils.getPhoneCodeUrl());
        try {
            JSONObject object = new JSONObject(json);
            String result = object.getString("result");
            if (result.equals("true")) {
                return "s";
            }
        } catch (Exception e) {
            return "";
        }

        return "";
    }

    /**
     * 手机注册
     *
     * @param key
     * @param code
     * @return
     */
    public static String phoneRegister(String key, String code) {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("key", key));
        pairs.add(new BasicNameValuePair("code", code));
        String json = HttpUtils.httpPost(pairs, HttpUtils.getPhoneRegisterUrl());
        try {
            JSONObject object = new JSONObject(json);
            String result = object.getString("result");
            if (result.equals("true")) {
                return "s";
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }


    /**
     * 获取关于我们的数据
     *
     * @return
     */
    public static String getAboutUs() {
        String json = HttpUtils.httpGet(HttpUtils.getAboutUsUrl());
        try {
            JSONObject object = new JSONObject(json);
            String result = object.getString("result");
            if (result.equals("true")) {
                return object.getString("data");
            }

        } catch (Exception e) {
            return "";
        }
        return "";
    }

    /**
     * 达人榜列表
     *
     * @param member_id
     * @param flag
     * @param page
     * @return
     */
    public static List<ExpertEntity> getExpertList(String member_id, String flag, String page) {
        List<ExpertEntity> list = new ArrayList<ExpertEntity>();
        String json = HttpUtils.httpGet(HttpUtils.getExpertUrl(member_id, flag, page));
        Log.e("getExpertList_url", HttpUtils.getExpertUrl(member_id, flag, page));
        Log.e("getExpertList_json", json);
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONArray array = object.getJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                ExpertEntity entity = new ExpertEntity();
                JSONObject temp = (JSONObject) array.get(i);
                entity.setMember_id(temp.has("member_id") ? temp.getString("member_id") : "");
                entity.setNickname(temp.has("nickname") ? temp.getString("nickname") : "");
                entity.setSex(temp.has("sex") ? temp.getString("sex") : "");
                entity.setAvatar(temp.has("avatar") ? temp.getString("avatar") : "");
                entity.setRank(temp.has("degree") ? temp.getString("degree") : "0");
                entity.setDescription(temp.has("description") ? temp.getString("description") : "");
                entity.setFans(temp.has("fans") ? temp.getString("fans") : "0");
                entity.setNum(temp.has("video_num") ? temp.getString("video_num") : "0");
                entity.setMark(temp.has("mark") ? temp.getString("mark") : "");
                list.add(entity);
            }
        } catch (Exception e) {
            Log.e("getExpertList", e.toString());
            return null;
        }
        return list;
    }


    /**
     * 关注列表
     *
     * @param member_id
     * @param page
     * @return
     */
    public static List<ExpertEntity> getMyAttentionList(String member_id, String page) {
        List<ExpertEntity> list = new ArrayList<ExpertEntity>();
        String json = HttpUtils.httpGet(HttpUtils.getMyAttentionUrl(member_id, page));
        Log.e("getMyAttentionList_url", HttpUtils.getMyAttentionUrl(member_id, page));
        Log.e("getMyAttentionList_json", json);
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONArray array = object.getJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                ExpertEntity entity = new ExpertEntity();
                JSONObject temp = (JSONObject) array.get(i);
                entity.setMember_id(temp.has("member_id") ? temp.getString("member_id") : "");
                entity.setNickname(temp.has("nickname") ? temp.getString("nickname") : "");
                entity.setSex(temp.has("sex") ? temp.getString("sex") : "");
                entity.setAvatar(temp.has("avatar") ? temp.getString("avatar") : "");
                entity.setRank(temp.has("degree") ? temp.getString("degree") : "0");
                entity.setDescription(temp.has("description") ? temp.getString("description") : "");
                entity.setFans(temp.has("fans") ? temp.getString("fans") : "0");
                entity.setNum(temp.has("video_num") ? temp.getString("video_num") : "0");
                entity.setMark(temp.has("mark") ? temp.getString("mark") : "");
                list.add(entity);
            }
        } catch (Exception e) {
            Log.e("getMyAttentionList_e", e.toString());
            return null;
        }
        return list;
    }

    /**
     * 粉丝列表
     *
     * @param member_id
     * @param page
     * @return
     */
    public static List<ExpertEntity> getMyFansList(String member_id, String page) {
        List<ExpertEntity> list = new ArrayList<ExpertEntity>();
        String json = HttpUtils.httpGet(HttpUtils.getMyFansUrl(member_id, page));
        Log.e("getMyFansList_url", HttpUtils.getMyFansUrl(member_id, page));
        Log.e("getMyFansList_json", json);
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONArray array = object.getJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                ExpertEntity entity = new ExpertEntity();
                JSONObject temp = (JSONObject) array.get(i);
                entity.setMember_id(temp.has("member_id") ? temp.getString("member_id") : "");
                entity.setNickname(temp.has("nickname") ? temp.getString("nickname") : "");
                entity.setSex(temp.has("sex") ? temp.getString("sex") : "");
                entity.setAvatar(temp.has("avatar") ? temp.getString("avatar") : "");
                entity.setRank(temp.has("degree") ? temp.getString("degree") : "0");
                entity.setDescription(temp.has("description") ? temp.getString("description") : "");
                entity.setFans(temp.has("fans") ? temp.getString("fans") : "0");
                entity.setNum(temp.has("video_num") ? temp.getString("video_num") : "0");
                entity.setMark(temp.has("mark") ? temp.getString("mark") : "");
                list.add(entity);
            }
        } catch (Exception e) {
            Log.e("getMyFansList_e", e.toString());
            return null;
        }
        return list;
    }

    /**
     * 个人排名
     *
     * @param flag
     * @return
     */
    public static String getExpertRank(String flag) {
        String json = HttpUtils.httpGet(HttpUtils.getExpertRankUrl());
        Log.e("getExpertRank_url", HttpUtils.getExpertRankUrl());
        Log.e("getExpertRank_json", json);
        String result = "";
        String rank;
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONObject object1 = object.getJSONObject("data");
            if (flag.equals("video")) {
                rank = "您当前在视频达人榜排名为:第" + object1.getString("video_rank") + "名";
            } else if (flag.equals("fan")) {
                rank = "您当前在粉丝达人榜排名为:第" + object1.getString("fans_rank") + "名";
            } else {
                rank = "您当前在等级达人榜排名为:第" + object1.getString("exp_rank") + "名";
            }
        } catch (Exception e) {
            Log.e("getExpertRank_e", e.toString());
            return "";
        }
        return rank;
    }

    /**
     * 游戏圈子玩家列表
     *
     * @param group_id
     * @param member_id
     * @param page
     * @return
     */
    public static List<ExpertEntity> getGameCircleUserList(String group_id, String member_id, String page) {
        List<ExpertEntity> list = new ArrayList<ExpertEntity>();
        String json = HttpUtils.httpGet(HttpUtils.getGameCircleUserUrl(group_id, member_id, page));
        Log.e("getGameCircleUserList_url", HttpUtils.getGameCircleUserUrl(group_id, member_id, page));
        Log.e("getGameCircleUserList_json", json);
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONArray array = object.getJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                ExpertEntity entity = new ExpertEntity();
                JSONObject temp = (JSONObject) array.get(i);
                entity.setMember_id(temp.has("member_id") ? temp.getString("member_id") : "");
                entity.setNickname(temp.has("nickname") ? temp.getString("nickname") : "");
                entity.setSex(temp.has("sex") ? temp.getString("sex") : "");
                entity.setAvatar(temp.has("avatar") ? temp.getString("avatar") : "");
                entity.setRank(temp.has("rank") ? temp.getString("rank") : "");
                entity.setDescription(temp.has("description") ? temp.getString("description") : "");
                entity.setFans(temp.has("fans") ? temp.getString("fans") : "");
                entity.setNum(temp.has("video_num") ? temp.getString("video_num") : "");
                entity.setMark(temp.has("attentionMark") ? temp.getString("attentionMark") : "");
                list.add(entity);
            }
        } catch (Exception e) {
            Log.e("getGameCircleUserList_e", e.toString());
            return null;
        }
        return list;
    }

    /**
     * 搜索结果相关用户列表
     *
     * @param nickname
     * @param page
     * @return
     */
    public static List<UserEntity> getSearchUserList(String nickname, String page) {

        List<UserEntity> list = new ArrayList<UserEntity>();
        String json = HttpUtils.httpGet(HttpUtils.getFindUser(nickname, page));
        Log.e("getSearchUserList_url", HttpUtils.getFindUser(nickname, page));
        Log.e("getSearchUserList_json", json);
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONArray array = object.getJSONObject(DATA).getJSONArray("list");
            for (int i = 0; i < array.length(); i++) {
                UserEntity entity = new UserEntity();
                JSONObject temp = (JSONObject) array.get(i);
                entity.setImgPath(temp.getString("avatar"));
                entity.setTitle(temp.getString("nickname"));
                entity.setName(temp.getString("NAME"));
                entity.setGrace(temp.getString("degree"));
                entity.setSex(temp.getString("sex"));
                entity.setRank(temp.getString("rank"));
                entity.setUploadVideoCount(temp.getString("uploadVideoCount"));
                entity.setAddress(temp.getString("address"));
                entity.setMobile(temp.getString("mobile"));
                entity.setGrace(temp.has("grace") ? temp.getString("grace") : "");
                entity.setId(temp.has("member_id") ? temp.getString("member_id") : "");
                list.add(entity);
            }
        } catch (Exception e) {
            Log.e("exception", e.toString());
            return null;
        }
        return list;
    }

    /**
     * 搜索结果相关任务
     *
     * @param name
     * @param page
     * @return
     */
    public static List<MissionEntity> getSearchMissionList(String name, String page) {
        List<MissionEntity> list = new ArrayList<MissionEntity>();
        String json = HttpUtils.httpGet(HttpUtils.getSearchMisssionUrl(name, page));
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONArray array = object.getJSONObject(DATA).getJSONArray("list");
            for (int i = 0; i < array.length(); i++) {
                MissionEntity entity = new MissionEntity();
                JSONObject temp = (JSONObject) array.get(i);
                entity.setImgPath(temp.getString("flagPath"));
                entity.setTitle(temp.getString("name"));
                entity.setType_id(temp.getString("type_id"));
                entity.setGame_id(temp.getString("game_id"));
                entity.setDescription(temp.getString("description"));
                entity.setContent(temp.getString("content"));
                entity.setReward(temp.getString("reward"));
                entity.setStarttime(temp.getString("starttime"));
                entity.setEndtime(temp.getString("endtime"));
                entity.setAddtime(temp.getString("addtime"));
                entity.setTaskTypeName(temp.getString("taskTypeName"));
                list.add(entity);
            }
        } catch (Exception e) {
            Log.e("exception", e.toString());
            return null;
        }
        return list;
    }

    /**
     * 搜索结果相关礼包
     *
     * @param name
     * @param page
     * @return
     */
    public static List<GiftEntity> getSearchGift(String name, String page) {
        List<GiftEntity> list = new ArrayList<GiftEntity>();
        String json = HttpUtils.httpGet(HttpUtils.getSearchGiftUrl(name, page));
        Log.e("gift_url", HttpUtils.getSearchGiftUrl(name, page));
        Log.e("gift_json", json);
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONArray array = object.getJSONObject(DATA).getJSONArray("list");
            for (int i = 0; i < array.length(); i++) {
                GiftEntity entity = new GiftEntity();
                JSONObject temp = (JSONObject) array.get(i);
                entity.setId(temp.getString("id"));
                entity.setTitle(temp.getString("title"));
                entity.setImgPath(temp.getString("flagPath"));
                entity.setContent(temp.getString("content"));
                entity.setTrade_type(temp.getString("trade_type"));
                entity.setStarttime(temp.getString("starttime"));
                entity.setEndtime(temp.getString("endtime"));
                entity.setAddtime(temp.getString("addtime"));
                entity.setCount(temp.getString("count"));
                entity.setNum(temp.getString("num"));
                list.add(entity);
            }
        } catch (Exception e) {
            Log.e("exception", e.toString());
            return null;
        }
        return list;
    }

    public static List<String> getGameCircleSearchkey(String keyWord) {
        List<String> list = new ArrayList<String>();
        String json = HttpUtils.httpGet(HttpUtils.getGameCircleSearchKey(keyWord));
        Log.e("getGameCircleSearchkey_url", HttpUtils.getGameCircleSearchKey(keyWord));
        Log.e("getGameCircleSearchkey_json", json);
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONObject data = object.getJSONObject(DATA);
            list.add(0, data.has("group_id") ? data.getString("group_id") : "");
            list.add(1, data.has("type_name") ? data.getString("type_name") : "");
        } catch (Exception e) {
            Log.e("exception", e.toString());
            return null;
        }
        return list;
    }

    /**
     * 自动获取礼包详情
     *
     * @param id
     * @return
     */
    public static GiftEntity getAutoGift(String id, String member_id) {
        String json = HttpUtils.httpGet(HttpUtils.getAutoGift(id, member_id));
        Log.e("getAutoGift_url", HttpUtils.getAutoGift(id, member_id));
        Log.e("getAutoGift_json", json);
        String result = "";
        String mess = "";
        GiftEntity entity = new GiftEntity();
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject jsonObject = new JSONObject(json);
            result = jsonObject.getString("result");
            Log.e("msg", jsonObject.getString(MSG));
            JSONObject temp = jsonObject.getJSONObject(DATA);
            if (!result.equals(TRUE)) {
                return null;
            }
            entity.setId(temp.getString("id"));
            entity.setTitle(temp.getString("title"));
            entity.setImgPath(temp.getString("flagPath"));
            entity.setContent(temp.getString("content"));
            entity.setTrade_type(temp.getString("trade_type"));
            entity.setStarttime(temp.getString("starttime"));
            entity.setEndtime(temp.getString("endtime"));
            entity.setAddtime(temp.getString("addtime"));
            entity.setCount(temp.getString("count"));
            entity.setNum(temp.has("num") ? temp.getString("num") : "");
            entity.setActivity_code(temp.has("activity_code") ? temp.getString("activity_code") : "");
        } catch (Exception e) {
            Log.e("getAutoGift", e.toString());
            return null;
        }
        return entity;
    }

    /**
     * 返回默认任务列表
     *
     * @param name
     * @param page
     * @return
     */
    public static List<MissionEntity> getMissionList(String name, String page) {
        List<MissionEntity> list = new ArrayList<MissionEntity>();
        String json = HttpUtils.httpGet(HttpUtils.getTaskList115(page));
        Log.e("getMissionList_url", HttpUtils.getTaskList115(page));
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            String msg = object.getString("msg");
            Log.e("msg", msg);
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONArray array = object.getJSONObject(DATA).getJSONArray("list");
            for (int i = 0; i < array.length(); i++) {
                MissionEntity entity = new MissionEntity();
                JSONObject temp = (JSONObject) array.get(i);
                entity.setId(temp.getString("id"));
                entity.setImgPath(temp.getString("flagPath"));
                entity.setTitle(temp.getString("name"));
                entity.setType_id(temp.getString("type_id"));
                entity.setGame_id(temp.getString("game_id"));
                entity.setDescription(temp.getString("description"));
                entity.setContent(temp.getString("content"));
                entity.setReward(temp.getString("reward"));
                entity.setAdd_exp(temp.getString("add_exp"));
                entity.setAdd_money(temp.getString("add_money"));
                entity.setStarttime(temp.getString("starttime"));
                entity.setEndtime(temp.getString("endtime"));
                entity.setAddtime(temp.getString("addtime"));
                entity.setTaskTypeName(temp.getString("type_name"));
                entity.setTaskTimeLength(temp.getString("taskTimeLength"));
//                entity.setLastCompleteTime(temp.getString("task_time"));
                entity.setTask_flag(temp.getString("task_flag"));
                entity.setIs_get(temp.has("is_get") ? temp.getString("is_get") : "");
                entity.setIs_accept(temp.has("is_accept") ? temp.getString("is_accept") : "");
                entity.setNum(temp.has("num") ? temp.getString("num") : "1");
                entity.setFlaging(temp.has("flaging") ? temp.getString("flaging") : "0");
                entity.setStatus_txt(temp.has("status_txt") ? temp.getString("status_txt") : "");
                list.add(entity);
            }
        } catch (Exception e) {
            Log.e("exception", e.toString());
            return null;
        }
        return list;
    }

    /**
     * 接受任务
     *
     * @param task_id
     * @return
     */
    public static boolean acceptMission(String task_id) {
        String json = HttpUtils.httpGet(HttpUtils.acceptTask(task_id));
        Log.e("acceptMission_url", HttpUtils.acceptTask(task_id));
        String result = "";
        try {
            if (json.equals("")) {
                return false;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return false;
            }

        } catch (Exception e) {
            Log.e("exception", e.toString());
            return false;
        }
        return true;
    }


    /**
     * 做任务方法
     * v1.1.5
     *
     * @param task_id
     * @return
     */
    public static boolean doMission115(String task_id) {
        String json = HttpUtils.httpGet(HttpUtils.doTask115(task_id));
        Log.e("doMission115_url", HttpUtils.doTask115(task_id));
        String result = "";
        try {
            if (json.equals("")) {
                return false;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return false;
            }

        } catch (Exception e) {
            Log.e("exception", e.toString());
            return false;
        }
        return true;
    }

    /**
     * 做任务方法
     * v1.1.7
     *
     * @param task_id
     * @return
     */
    public static DoTaskEntity doMission117(String task_id) {
        String json = HttpUtils.httpGet(HttpUtils.doTask117(task_id));
        Log.e("doMission117_url", HttpUtils.doTask117(task_id));
        String result = "";
        String aNum = "";
        String mNum = "";
        String msg = "";
        DoTaskEntity entity = new DoTaskEntity();
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }

            JSONObject temp = object.getJSONObject(DATA);
            entity.setaNum(temp.has("aNum") ? temp.getString("aNum") : "");
            entity.setmNum(temp.has("mNum") ? temp.getString("mNum") : "");
            entity.setMsg(object.has("msg") ? object.getString("msg") : "");

        } catch (Exception e) {
            Log.e("exception", e.toString());
            return null;
        }
        return entity;
    }

    /**
     * 判断是否完成任务
     *
     * @param task_id
     * @return
     */
    public static boolean finishMissionNew(String task_id) {
        String json = HttpUtils.httpGet(HttpUtils.finishTaskNew(task_id));
        Log.e("finishMissionNew_url", HttpUtils.finishTaskNew(task_id));
        String result = "";
        try {
            if (json.equals("")) {
                return false;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return false;
            }

        } catch (Exception e) {
            Log.e("exception", e.toString());
            return false;
        }
        return true;
    }

    /**
     * 领取任务奖励
     *
     * @param task_id
     * @return
     */
    public static boolean getMissionReward(String task_id) {
        String json = HttpUtils.httpGet(HttpUtils.getTaskReward(task_id));
        Log.e("getMissionList_url", HttpUtils.getTaskReward(task_id));
        String result = "";
        try {
            if (json.equals("")) {
                return false;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return false;
            }

        } catch (Exception e) {
            Log.e("exception", e.toString());
            return false;
        }
        return true;
    }

    /**
     * 获取 未完成任务个数
     *
     * @param name
     * @param page
     * @return
     */
    public static int getMissionUnDoNum(String name, String page) {
        int unDoNum = 0;
        String json = HttpUtils.httpGet(HttpUtils.getPackageList(ExApplication.MEMBER_ID, page));
        String result = "";
        try {
            if (json.equals("")) {
                return 0;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return 0;
            }
            unDoNum = object.getJSONObject(DATA).getInt("itemsCount");
        } catch (Exception e) {
            Log.e("exception", e.toString());
            return 0;
        }
        return unDoNum;
    }

    /**
     * 获取默认礼包列表
     *
     * @param name
     * @param page
     * @return
     */
    public static List<GiftEntity> getPakage(Context context, String name, String page) {
        List<GiftEntity> list = new ArrayList<GiftEntity>();
        String json = HttpUtils.httpGet(HttpUtils.getPackageList(ExApplication.MEMBER_ID, page));
        String result = "";
        try {
            if (json.equals("")) {
                json = SharePreferenceUtil.getGiftList(context);
//                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            } else if (page.equals("1")) {
                SharePreferenceUtil.setGiftList(context, json);
            }

            JSONArray array = object.getJSONObject(DATA).getJSONArray("list");
            for (int i = 0; i < array.length(); i++) {
                GiftEntity entity = new GiftEntity();
                JSONObject temp = (JSONObject) array.get(i);
                entity.setId(temp.getString("id"));
                entity.setTitle(temp.getString("title"));
                entity.setImgPath(temp.getString("flagPath"));
                entity.setContent(temp.getString("content"));
                entity.setTrade_type(temp.getString("trade_type"));
                entity.setStarttime(temp.getString("starttime"));
                entity.setEndtime(temp.getString("endtime"));
                entity.setAddtime(temp.getString("addtime"));
                entity.setCount(temp.getString("count"));
                entity.setActivity_code(temp.getString("activity_code"));
                entity.setNum(temp.getString("num"));
                list.add(entity);
            }
        } catch (Exception e) {
            Log.e("exception", e.toString());
            return null;
        }
        return list;
    }

    /**
     * 获取我的礼包
     *
     * @param name
     * @param page
     * @return
     */
    public static List<GiftEntity> getMyPakage(String name, String page) {
        List<GiftEntity> list = new ArrayList<GiftEntity>();
        String json = HttpUtils.httpGet(HttpUtils.getMyGift(page));
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONArray array = object.getJSONObject(DATA).getJSONArray("list");
            for (int i = 0; i < array.length(); i++) {
                GiftEntity entity = new GiftEntity();
                JSONObject temp = (JSONObject) array.get(i);
                entity.setId(temp.getString("id"));
                entity.setTitle(temp.getString("title"));
                entity.setImgPath(temp.getString("flagPath"));
                entity.setContent(temp.getString("content"));
                entity.setTrade_type(temp.getString("trade_type"));
                entity.setStarttime(temp.getString("starttime"));
                entity.setEndtime(temp.getString("endtime"));
                entity.setAddtime(temp.getString("addtime"));
                entity.setCount(temp.getString("count"));
                entity.setNum(temp.has("num") ? temp.getString("num") : "");
                entity.setActivity_code(temp.getString("activity_code"));
                list.add(entity);
            }
        } catch (Exception e) {
            Log.e("exception", e.toString());
            return null;
        }
        return list;
    }

    public static List<MessageEntity> getMessageList(String type_id, String member_id, String page) {
        List<MessageEntity> list = new ArrayList<MessageEntity>();
        String json = HttpUtils.httpGet(HttpUtils.getMyMessage(type_id, member_id, page));
        Log.e("getMessageList_url", HttpUtils.getMyMessage(type_id, member_id, page));
        Log.e("getMessageList_json", json);
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONArray array = object.getJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                MessageEntity entity = new MessageEntity();
                JSONObject temp = (JSONObject) array.get(i);
                entity.setMember_id(temp.has("member_id") ? temp.getString("member_id") : "");
                entity.setAvatar(temp.has("avatar") ? temp.getString("avatar") : "");
                entity.setNickname(temp.has("nickname") ? temp.getString("nickname") : "");
                entity.setContent(temp.has("content") ? temp.getString("content") : "");
                entity.setTime(temp.has("time") ? temp.getString("time") : "");
                entity.setMark(temp.has("mark") ? temp.getString("mark") : "");
                entity.setVideo_id(temp.has("video_id") ? temp.getString("video_id") : "");
                entity.setFlag(temp.has("flag") ? temp.getString("flag") : "");
                list.add(entity);
            }
        } catch (Exception e) {
            Log.e("getMessageList_e", e.toString());
            return null;
        }
        return list;
    }

    /**
     * 获取商务信息
     *
     * @return
     */
    public static List<Business> getBusiness() {
        List<Business> list = new ArrayList<Business>();
        String json = HttpUtils.httpGet(HttpUtils.getBussinessUrl());
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONArray array = object.getJSONArray(DATA);
            for (int i = 0; i < array.length(); i++) {
                Business entity = new Business();
                JSONObject temp = (JSONObject) array.get(i);
                entity.setId(temp.getString("id"));
                entity.setEmail(temp.getString("email"));
                entity.setPhone(temp.getString("phone"));
                entity.setAddress(temp.getString("address"));
                entity.setWebsite(temp.getString("website"));
                list.add(entity);
            }
        } catch (Exception e) {
            Log.e("exception", e.toString());
            return null;
        }
        return list;
    }

    /**
     * 获取活动列表
     *
     * @param page
     * @return
     */
    public static List<ActivityEntity> getMoreActivityList(String page) {
        List<ActivityEntity> activityList = new ArrayList<ActivityEntity>();
        String json = HttpUtils.httpGet(HttpUtils.getMoreActivityUrl(page));
        Log.e("activity_url", HttpUtils.getMoreActivityUrl(page));
        Log.e("activity_json", json);
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONArray array = object.getJSONArray(DATA);
            ActivityEntity activity = null;
            JSONObject temp = null;
            if (!array.equals("")) {
                for (int i = 0; i < array.length(); i++) {
                    activity = new ActivityEntity();
                    temp = (JSONObject) array.get(i);
                    activity.setActivity_id(temp.has("activity_id") ? temp.getInt("activity_id") : -1);
                    activity.setHeat(temp.has("heat") ? temp.getInt("heat") : -1);
                    activity.setMark(temp.has("mark") ? temp.getInt("mark") : -1);
                    activity.setDisplay(temp.has("display") ? temp.getInt("display") : -1);
                    activity.setCover_chart1(temp.has("cover_chart1") ? temp.getString("cover_chart1") : "");
                    activity.setCover_chart2(temp.has("cover_chart2") ? temp.getString("cover_chart2") : "");
                    activity.setName(temp.has("name") ? temp.getString("name") : "");
                    activity.setUrl(temp.has("url") ? temp.getString("url") : "");
                    activityList.add(activity);
                }
                Log.e("activityList", activityList.toString());
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return activityList;
    }

    /**
     * 获取赛事列表
     *
     * @param page
     * @return
     */
    public static List<MatchEntity> getMoreMatchList(String page) {
        List<MatchEntity> matchList = new ArrayList<MatchEntity>();
        String json = HttpUtils.httpGet(HttpUtils.getMoreMatchUrl(page));
        Log.e("match_url", HttpUtils.getMoreMatchUrl(page));
        Log.e("match_json", json);
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONArray array = object.getJSONArray(DATA);
            MatchEntity match = null;
            JSONObject temp = null;
            if (!array.equals("")) {
                for (int i = 0; i < array.length(); i++) {
                    match = new MatchEntity();
                    temp = (JSONObject) array.get(i);
                    match.setMark(temp.has("mark") ? temp.getInt("mark") : 0);
                    match.setAddtime(temp.has("addtime") ? temp.getString("addtime") : "");
                    match.setDisplay(temp.has("display") ? temp.getInt("display") : -1);
                    match.setEndtime(temp.has("endtime") ? temp.getString("endtime") : "");
                    match.setGame_id(temp.has("game_id") ? temp.getInt("game_id") : -1);
                    match.setMatch_id(temp.has("match_id") ? temp.getInt("match_id") : -1);
                    match.setMatch_type(temp.has("match_type") ? temp.getInt("match_type") : -1);
                    match.setMember_id(temp.has("member_id") ? temp.getInt("member_id") : -1);
                    match.setStarttime(temp.has("starttime") ? temp.getString("starttime") : "");
                    match.setCover_chart1(temp.has("cover_chart1") ? temp.getString("cover_chart1") : "");
                    match.setCover_chart2(temp.has("cover_chart2") ? temp.getString("cover_chart2") : "");
                    match.setDescription(temp.has("description") ? temp.getString("description") : "");
                    match.setRewards(temp.has("rewards") ? temp.getString("rewards") : "");
                    match.setName(temp.has("name") ? temp.getString("name") : "");
                    match.setUrl(temp.has("url") ? temp.getString("url") : "");
                    matchList.add(match);
                }
                Log.e("matchList", matchList.toString());
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return matchList;
    }

    /**
     * 获取 首页活动 热门活动 列表
     * V1.1.5
     *
     * @param page
     * @return
     */
    public static List<MatchEntity> getHotMatchList(Context context, String page) {
        List<MatchEntity> matchList = new ArrayList<MatchEntity>();
        String json = HttpUtils.httpGet(HttpUtils.getHotMatchUrl(page));
        Log.e("hotmatch_url", HttpUtils.getHotMatchUrl(page));
        Log.e("hotmatch_json", json);
        String result = "";
        try {
            if (json.equals("")) {
                json = SharePreferenceUtil.getHotActivityList(context);
//                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            SharePreferenceUtil.setHotActivityList(context, json);

            JSONArray array = object.getJSONArray(DATA);
            MatchEntity match = null;
            JSONObject temp = null;
            if (!array.equals("")) {
                for (int i = 0; i < array.length(); i++) {
                    match = new MatchEntity();
                    temp = (JSONObject) array.get(i);
                    match.setMatch_id(temp.has("match_id") ? temp.getInt("match_id") : -1);
                    match.setMatch_type(temp.has("match_type") ? temp.getInt("match_type") : -1);
                    match.setName(temp.has("name") ? temp.getString("name") : "");
                    match.setEndtime(temp.has("endtime") ? temp.getString("endtime") : "");
                    match.setUpload_time(temp.has("upload_time") ? temp.getString("upload_time") : "");
                    match.setDisplay(temp.has("display") ? temp.getInt("display") : -1);
                    match.setPic_600_x(temp.has("pic_600_x") ? temp.getString("pic_600_x") : "");
                    match.setWidth(temp.has("width") ? temp.getInt("width") : -1);
                    match.setHeight(temp.has("height") ? temp.getInt("height") : -1);
                    match.setMark(temp.has("mark") ? temp.getInt("mark") : 0);
                    matchList.add(match);
                }
                Log.e("matchList", matchList.toString());
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return matchList;
    }

    /**
     * 获取 首页活动 我的活动 列表
     * V1.1.5
     *
     * @param
     * @return
     */
    public static List<MatchEntity> getMyMatchList() {
        List<MatchEntity> matchList = new ArrayList<MatchEntity>();
        String json = HttpUtils.httpGet(HttpUtils.getMyMatchUrl());
        Log.e("mymatch_url", HttpUtils.getMyMatchUrl());
        Log.e("mymatch_json", json);
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONArray array = object.getJSONArray(DATA);
            MatchEntity match = null;
            JSONObject temp = null;
            if (!array.equals("")) {
                for (int i = 0; i < array.length(); i++) {
                    match = new MatchEntity();
                    temp = (JSONObject) array.get(i);
                    match.setMatch_id(temp.has("match_id") ? temp.getInt("match_id") : -1);
                    match.setName(temp.has("name") ? temp.getString("name") : "");
                    match.setStarttime(temp.has("starttime") ? temp.getString("starttime") : "");
                    match.setEndtime(temp.has("endtime") ? temp.getString("endtime") : "");
                    match.setPic_110_110(temp.has("pic_110_110") ? temp.getString("pic_110_110") : "");
                    match.setGame_name(temp.has("game_name") ? temp.getString("game_name") : "");
                    match.setMember_id(temp.has("member_id") ? temp.getInt("member_id") : -1);
                    match.setMark(temp.has("mark") ? temp.getInt("mark") : 0);
                    match.setStatus(temp.has("status") ? temp.getInt("status") : 0);
                    matchList.add(match);
                }
                Log.e("matchList", matchList.toString());
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return matchList;
    }

    /**
     * 提交 参加赛事活动
     *
     * @param match_id
     * @return
     */
    public static boolean getJoinMatch(String match_id) {
        String url = HttpUtils.getJoinMatchUrl(match_id);
        Log.e("url", url);
        String json = HttpUtils.httpGet(url);

        String result = "";
        String msg = "";
        if (json.equals("")) {
            return false;
        }
        try {
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            msg = object.getString("msg");
            Log.e("getJoinMatch_msg", msg);
            if (result.equals(TRUE)) {
                return true;
            }
        } catch (Exception e) {
            Log.e("getJoinMatch", "getJoinMatch");
            return false;
        }
        return false;
    }


    /**
     * 获取活动页面数据
     *
     * @param page
     * @return
     */
    public static List<Object> getActivityFrameList(String page) {
        List<Object> all = new ArrayList<Object>();
        List<MissionEntity> missionList = new ArrayList<MissionEntity>();
        List<MatchEntity> matchList = new ArrayList<MatchEntity>();
        List<ActivityEntity> activityList = new ArrayList<ActivityEntity>();
        String json = HttpUtils.httpGet(HttpUtils.getActivityFrameList(page));
        Log.e("video_url", HttpUtils.getActivityFrameList(page));
        Log.e("video_json", json);
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONArray array = object.getJSONObject(DATA).getJSONArray("taskList");
            JSONObject temp = null;
            MissionEntity mission = null;
            if (!array.equals("")) {
                for (int i = 0; i < array.length(); i++) {
                    mission = new MissionEntity();
                    temp = (JSONObject) array.get(i);
                    mission.setId(temp.getString("id"));
                    mission.setImgPath(temp.getString("flagPath"));
                    mission.setTitle(temp.getString("name"));
                    mission.setType_id(temp.getString("type_id"));
                    mission.setGame_id(temp.getString("game_id"));
                    mission.setDescription(temp.getString("description"));
                    mission.setContent(temp.getString("content"));
                    mission.setReward(temp.getString("reward"));
                    mission.setStarttime(temp.getString("starttime"));
                    mission.setEndtime(temp.getString("endtime"));
                    mission.setAddtime(temp.getString("addtime"));
                    mission.setTaskTypeName(temp.getString("type_name"));
                    mission.setTaskTimeLength(temp.getString("taskTimeLength"));
                    mission.setTask_flag(temp.getString("task_flag"));
                    missionList.add(mission);
                }
                all.add(0, missionList);
                Log.e("missionList", missionList.toString());
            } else {
                all.add(0, null);
            }
            array = object.getJSONObject(DATA).getJSONArray("matchList");
            MatchEntity match = null;
            if (!array.equals("")) {
                for (int i = 0; i < array.length(); i++) {
                    match = new MatchEntity();
                    temp = (JSONObject) array.get(i);
                    match.setAddtime(temp.has("addtime") ? temp.getString("addtime") : "");
                    match.setDisplay(temp.has("display") ? temp.getInt("display") : -1);
                    match.setEndtime(temp.has("endtime") ? temp.getString("endtime") : "");
                    match.setGame_id(temp.has("game_id") ? temp.getInt("game_id") : -1);
                    match.setMatch_id(temp.has("match_id") ? temp.getInt("match_id") : -1);
                    match.setMatch_type(temp.has("match_type") ? temp.getInt("match_type") : -1);
                    match.setMember_id(temp.has("member_id") ? temp.getInt("member_id") : -1);
                    match.setStarttime(temp.has("starttime") ? temp.getString("starttime") : "");
                    match.setCover_chart1(temp.has("cover_chart1") ? temp.getString("cover_chart1") : "");
                    match.setCover_chart2(temp.has("cover_chart2") ? temp.getString("cover_chart2") : "");
                    match.setDescription(temp.has("description") ? temp.getString("description") : "");
                    match.setRewards(temp.has("rewards") ? temp.getString("rewards") : "");
                    match.setName(temp.has("name") ? temp.getString("name") : "");
                    match.setUrl(temp.has("url") ? temp.getString("url") : "");
                    matchList.add(match);
                }
                all.add(1, matchList);
                Log.e("matchList", matchList.toString());
            } else {
                all.add(1, null);
            }
            array = object.getJSONObject(DATA).getJSONArray("activityList");
            ActivityEntity activity = null;
            if (!array.equals("")) {
                for (int i = 0; i < array.length(); i++) {
                    activity = new ActivityEntity();
                    temp = (JSONObject) array.get(i);
                    activity.setActivity_id(temp.has("activity_id") ? temp.getInt("activity_id") : -1);
                    activity.setHeat(temp.has("heat") ? temp.getInt("heat") : -1);
                    activity.setMark(temp.has("mark") ? temp.getInt("mark") : -1);
                    activity.setDisplay(temp.has("display") ? temp.getInt("display") : -1);
                    activity.setCover_chart1(temp.has("cover_chart1") ? temp.getString("cover_chart1") : "");
                    activity.setCover_chart2(temp.has("cover_chart2") ? temp.getString("cover_chart2") : "");
                    activity.setName(temp.has("name") ? temp.getString("name") : "");
                    activity.setUrl(temp.has("url") ? temp.getString("url") : "");
                    activityList.add(activity);
                }
                all.add(2, activityList);
                Log.e("activityList", activityList.toString());
            } else {
                all.add(2, null);
            }
        } catch (Exception e) {
            Log.e("exception", e.toString());
            return null;
        }
        return all;
    }

    /**
     * 活动详情
     *
     * @param id
     * @return
     */
    public static ActivityEntity getActivityEntity(String id) {
        ActivityEntity activity = new ActivityEntity();
        String json = HttpUtils.httpGet(HttpUtils.getActivityDetailUrl(id));
        Log.e("getActivityEntity_url", HttpUtils.getActivityDetailUrl(id));
        Log.e("getActivityEntity_json", json);
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONObject temp = object.getJSONObject(DATA);
            activity.setActivity_id(temp.has("activity_id") ? temp.getInt("activity_id") : -1);
            activity.setHeat(temp.has("heat") ? temp.getInt("heat") : -1);
            activity.setMark(temp.has("mark") ? temp.getInt("mark") : -1);
            activity.setDisplay(temp.has("display") ? temp.getInt("display") : -1);
            activity.setCover_chart1(temp.has("cover_chart1") ? temp.getString("cover_chart1") : "");
            activity.setCover_chart2(temp.has("cover_chart2") ? temp.getString("cover_chart2") : "");
            activity.setName(temp.has("name") ? temp.getString("name") : "");
            activity.setUrl(temp.has("url") ? temp.getString("url") : "");
            activity.setActivity_rule(temp.has("match_rule") ? temp.getString("match_rule") : "");
            activity.setRewards(temp.has("rewards") ? temp.getString("rewards") : "");
            activity.setAndroidUrl(temp.has("androidUrl") ? temp.getString("androidUrl") : "");
            activity.setGame(temp.has("game") ? temp.getString("game") : "");
            activity.setGameFlag(temp.has("gameFlag") ? temp.getString("gameFlag") : "");
            activity.setStartTime(temp.has("starttime") ? temp.getString("starttime") : "");
            activity.setEndTime(temp.has("endtime") ? temp.getString("endtime") : "");
        } catch (Exception e) {
            Log.e("getActivityEntity_e", e.toString());
            return null;
        }
        return activity;
    }

    /**
     * 活动信息
     * <p/>
     * V1.1.5
     *
     * @param match_id
     * @return
     */
    public static MatchEntity getMatchInfo(String match_id) {
        MatchEntity matchInfo = new MatchEntity();
        String json = HttpUtils.httpGet(HttpUtils.getMatchInfoUrl(match_id));
        Log.e("MatchInfo_url", HttpUtils.getMatchInfoUrl(match_id));
        Log.e("MatchInfo_json", json);
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONObject temp = object.getJSONObject(DATA);
            matchInfo.setMatch_id(temp.has("match_id") ? temp.getInt("match_id") : -1);
            matchInfo.setName(temp.has("name") ? temp.getString("name") : "");
            matchInfo.setStarttime(temp.has("starttime") ? temp.getString("starttime") : "");
            matchInfo.setEndtime(temp.has("endtime") ? temp.getString("endtime") : "");
            matchInfo.setDescription(temp.has("description") ? temp.getString("description") : "");
            matchInfo.setRewards(temp.has("rewards") ? temp.getString("rewards") : "");
            matchInfo.setMatch_rule(temp.has("match_rule") ? temp.getString("match_rule") : "");
            matchInfo.setWinners(temp.has("winners") ? temp.getString("winners") : "");
            matchInfo.setPic_hd(temp.has("pic_hd") ? temp.getString("pic_hd") : "");
            matchInfo.setPic_pld(temp.has("pic_pld") ? temp.getString("pic_pld") : "");
            matchInfo.setIos_download(temp.has("ios_download") ? temp.getString("ios_download") : "");
            matchInfo.setAndroid_download(temp.has("android_download") ? temp.getString("android_download") : "");
            matchInfo.setMark(temp.has("mark") ? temp.getInt("mark") : 0);
            matchInfo.setStatus(temp.has("status") ? temp.getInt("status") : 0);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return matchInfo;
    }

    /**
     * 获取 活动信息 对应赛事的参赛视频列表
     * V1.1.5
     *
     * @param
     * @return
     */
    public static List<VedioDetail> getMatchVideoList(String match_id, String page) {
        List<VedioDetail> videoList = new ArrayList<VedioDetail>();
        String json = HttpUtils.httpGet(HttpUtils.getMatchVideoListUrl(match_id, page));
        Log.e("MatchVideoList_url", HttpUtils.getMatchVideoListUrl(match_id, page));
        Log.e("MatchVideoList_json", json);
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONArray array = object.getJSONArray(DATA);
            VedioDetail video = null;
            JSONObject temp = null;
            if (!array.equals("")) {
                for (int i = 0; i < array.length(); i++) {
                    video = new VedioDetail();
                    temp = (JSONObject) array.get(i);
                    video.setId(temp.has("video_id") ? temp.getString("video_id") : "");
                    video.setType_id(temp.has("type_id") ? temp.getString("type_id") : "");
                    video.setGame_id(temp.has("game_id") ? temp.getString("game_id") : "");
                    video.setMember_id(temp.has("member_id") ? temp.getString("member_id") : "");
                    video.setName(temp.has("name") ? temp.getString("name") : "");
                    video.setUrl(temp.has("url") ? temp.getString("url") : "");
                    video.setQn_key(temp.has("qn_key") ? temp.getString("qn_key") : "");
                    video.setFlagPath(temp.has("flag") ? temp.getString("flag") : "");
                    video.setView_count(temp.has("view_count") ? temp.getString("view_count") : "");
                    video.setTime_length(temp.has("time_length") ? temp.getString("time_length") : "");
                    video.setPic_flsp(temp.has("pic_flsp") ? temp.getString("pic_flsp") : "");
                    video.setUserName(temp.has("nickname") ? temp.getString("nickname") : "");
                    video.setStatus(temp.has("status") ? temp.getInt("status") : 0);
                    videoList.add(video);
                }
                Log.e("videoList", videoList.toString());
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return videoList;
    }


    /**
     * 返回搜索到的视频
     *
     * @param name
     * @param type sort: time(按时间排序)  flower（按点赞数排序）
     * @param page
     * @return
     */
    public static List<Object> getSearchVideo(String name, String type, String page) {
        List<Object> all = new ArrayList<Object>();
        List<VideoEntity> list = new ArrayList<VideoEntity>();
        List<GameEntity> gameList = new ArrayList<GameEntity>();
        String json = HttpUtils.httpGet(HttpUtils.getSearchVideoUrl(name, type, page));
        Log.e("video_url", HttpUtils.getSearchVideoUrl(name, type, page));
        Log.e("video_json", json);
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONArray array = object.getJSONObject(DATA).getJSONArray("list");
            VideoEntity vedio = null;
            JSONObject temp = null;
            if (!array.equals("")) {
                for (int i = 0; i < array.length(); i++) {
                    vedio = new VideoEntity();
                    temp = (JSONObject) array.get(i);
                    vedio.setId(temp.getString("id"));
                    vedio.setTitle(temp.has("gameName") ? temp.getString("gameName") : "");
                    vedio.setTitle_content(temp.getString("name"));
                    vedio.setSimg_url(temp.getString("flagPath"));
                    vedio.setFlower(temp.getString("flower_count"));
                    vedio.setTime(TimeUtils.secToTime(Integer.parseInt(temp.getString("time_length"))));
                    vedio.setComment_count(temp.getString("comment_count"));
                    vedio.setViewCount(temp.getString("view_count"));
                    list.add(vedio);
                }
                all.add(0, list);
            } else {
                all.add(0, null);
            }

            array = object.getJSONObject("game").getJSONArray("gameList");
            GameEntity gameEntity = null;
            if (!array.equals("")) {
                for (int i = 0; i < array.length(); i++) {
                    gameEntity = new GameEntity();
                    temp = (JSONObject) array.get(i);
                    if (temp.has("name")) {
                        gameEntity.setName(temp.getString("name"));
                    }
                    if (temp.has("flagPath")) {
                        gameEntity.setGame_icon(temp.getString("flagPath"));
                    }
                    if (temp.has("total")) {
                        gameEntity.setTotal(temp.getDouble("total"));
                    }
                    if (temp.has("address")) {
                        gameEntity.setDown_address(temp.getString("address"));
                    }
                    if (temp.has("down_num")) {
                        gameEntity.setDown_count(temp.getInt("down_num"));
                    }
                    if (temp.has("id")) {
                        gameEntity.setGame_id(temp.getInt("id"));
                    }
                    if (temp.has("description")) {
                        gameEntity.setDescription(temp.getString("description"));
                    }
                    gameList.add(gameEntity);
                }
                all.add(1, gameList);
            } else {
                all.add(1, null);
            }

        } catch (Exception e) {
            Log.e("exception", e.toString());
            return null;
        }
        return all;
    }

    /**
     * 获取关键字
     *
     * @return
     */
    public static List<KeyWord> getKeyWord() {
        List<KeyWord> list = new ArrayList<KeyWord>();
        String json = HttpUtils.httpGet(HttpUtils.getKeyWordUrlByNum());
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONArray array = object.getJSONObject(DATA).getJSONArray("list");
            for (int i = 0; i < array.length(); i++) {
                KeyWord entity = new KeyWord();
                JSONObject temp = (JSONObject) array.get(i);
                entity.setId(temp.getString("id"));
                entity.setWord(temp.getString("name"));
                list.add(entity);
            }
        } catch (Exception e) {
            Log.e("keyword_exception", e.toString());
            return null;
        }
        return list;
    }


    /**
     * 提交（二级）评论
     * V1.1.6
     *
     * @param id      对应视频ID
     * @param userId  用户ID
     * @param content 评论内容
     * @param mark    0或1，0为一级评级，1为非一级评级
     * @param last_id 是对应的哪一条评论的id
     * @return
     */
    public static boolean submitComment(String id, String userId, String content, String mark, String last_id) {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("id", id));
        pairs.add(new BasicNameValuePair("member_id", userId));
        pairs.add(new BasicNameValuePair("content", content));
        pairs.add(new BasicNameValuePair("mark", mark));
        pairs.add(new BasicNameValuePair("last_id", last_id));
        String json = HttpUtils.httpPost(pairs, HttpUtils.submitCommentUrl());
        String result = "";
        if (json.equals("")) {
            return false;
        }
        try {
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (result.equals(TRUE)) {
                return true;
            }
        } catch (Exception e) {
            Log.e("submitComment", "submitComment");
            return false;
        }
        return false;
    }


    /**
     * 个人空间评论
     *
     * @param id
     * @param userId
     * @param content
     * @return
     */
    public static boolean submitPersonComment(String id, String userId, String content) {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("id", id));
        pairs.add(new BasicNameValuePair("member_id", userId));
        pairs.add(new BasicNameValuePair("content", content));
        String json = HttpUtils.httpPost(pairs, HttpUtils.submitPersonCommentUrl());
        String result = "";
        if (json.equals("")) {
            return false;
        }
        try {
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (result.equals(TRUE)) {
                return true;
            }
        } catch (Exception e) {
            Log.e("submitPersonComment", e.toString());
            return false;
        }
        return false;
    }

    public static boolean submitGameCircleComment(String group_id, String member_id, String content) {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("group_id", group_id));
        pairs.add(new BasicNameValuePair("member_id", member_id));
        pairs.add(new BasicNameValuePair("content", content));
        String json = HttpUtils.httpPost(pairs, HttpUtils.submitGameCircleCommentUrl());
        String result = "";
        if (json.equals("")) {
            return false;
        }
        try {
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (result.equals(TRUE)) {
                return true;
            }
        } catch (Exception e) {
            Log.e("submitGameCircleComment_e", e.toString());
            return false;
        }
        return false;
    }

    /**
     * 领取礼包
     *
     * @param userId
     * @param id
     * @return
     */
    public static boolean getGiftResponse(String userId, String id) {
        String url = HttpUtils.getGiftUrl(userId, id);
        Log.e("url", url);
        String json = HttpUtils.httpGet(url);

        String result = "";
        if (json.equals("")) {
            return false;
        }
        try {
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (result.equals(TRUE)) {
                return true;
            }
        } catch (Exception e) {
            Log.e("submitComment", "submitComment");
            return false;
        }
        return false;
    }

    /**
     * 点击下载
     *
     * @param id
     * @return
     */
    public static boolean addDownLoadCount(String id) {
        String json = HttpUtils.httpGet(HttpUtils.getDownLoadUrl(id));
        Log.e("download", json);
        String result = "";
        try {
            if (json.equals("")) {
                return false;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (result.equals(TRUE)) {
                return true;
            }
        } catch (Exception e) {
            Log.e("download_exception", e.toString());
        }
        return false;
    }

    /**
     * 获取返回游戏类型列表
     *
     * @return
     */
    public static List<GameType> getGameType() {
        List<GameType> list = new ArrayList<GameType>();
        String json = HttpUtils.httpGet(HttpUtils.getGameTypeList());
        Log.e("getGameType", json);
        Log.e("getGameType_url", HttpUtils.getGameTypeList());
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONObject data = object.getJSONObject(DATA);
            JSONArray array = data.getJSONArray("list");
            for (int i = 0; i < array.length(); i++) {
                GameType entity = new GameType();
                JSONObject temp = (JSONObject) array.get(i);
                entity.setId(temp.has("id") ? temp.getString("id") : "");
                entity.setName(temp.has("name") ? temp.getString("name") : "");
                entity.setSort(temp.has("sort") ? temp.getString("sort") : "");
                entity.setFlag(temp.has("flagPath") ? temp.getString("flagPath") : "");
                list.add(entity);
            }
        } catch (Exception e) {
            Log.e("getGameType", e.toString());
            return null;
        }
        return list;
    }

    public static List<GameType> getGameCircleType() {
        List<GameType> list = new ArrayList<GameType>();
        String json = HttpUtils.httpGet(HttpUtils.getGameCircleTypeUrl());
        Log.e("getGameCircleType", json);
        Log.e("getGameCircleType_url", HttpUtils.getGameCircleTypeUrl());
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONArray array = object.getJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                GameType entity = new GameType();
                JSONObject temp = (JSONObject) array.get(i);
                entity.setId(temp.has("id") ? temp.getString("id") : "");
                entity.setName(temp.has("name") ? temp.getString("name") : "");
                entity.setSort(temp.has("sort") ? temp.getString("sort") : "");
                entity.setFlag(temp.has("flagPath") ? temp.getString("flagPath") : "");
                list.add(entity);
            }
        } catch (Exception e) {
            Log.e("getGameCircleType_e", e.toString());
            return null;
        }
        return list;
    }

    /**
     * 游戏圈子列表
     *
     * @param type_id
     * @param member_id
     * @return
     */
    public static List<Game> getGameCircleGameList(String page, String type_id, String member_id) {
        List<Game> list = new ArrayList<Game>();
        String json = HttpUtils.httpGet(HttpUtils.getGameCircleGameUrl(page, type_id, member_id));
        Log.e("getGameCircleGameList", json);
        Log.e("getGameCircleGameList_url", HttpUtils.getGameCircleGameUrl(page, type_id, member_id));
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONArray array = object.getJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                Game game = new Game();
                JSONObject temp = (JSONObject) array.get(i);
                game.setGroup_id(temp.has("group_id") ? temp.getString("group_id") : "");
                game.setGroup_type(temp.has("name") ? temp.getString("name") : "");
                game.setGroup_name(temp.has("group_name") ? temp.getString("group_name") : "");
                game.setAttention_num(temp.has("attention_num") ? temp.getString("attention_num") : "");
                game.setMark(temp.has("mark") ? temp.getString("mark") : "");
                game.setFlagPath(temp.has("flag") ? temp.getString("flag") : "");
                list.add(game);
            }
        } catch (Exception e) {
            Log.e("getGameCircleGameList_e", e.toString());
            return null;
        }
        return list;
    }

    public static Game getGameCircleInfo(String group_id, String member_id, String type_name) {
        String json = HttpUtils.httpGet(HttpUtils.getGameCircleInfoUrl(group_id, member_id, type_name));
        Game game = new Game();
        Log.e("getGameCircleInfoList", json);
        Log.e("getGameCircleInfoList_url", HttpUtils.getGameCircleInfoUrl(group_id, member_id, type_name));
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONObject temp = object.getJSONObject("data");

            game.setGroup_id(temp.has("group_id") ? temp.getString("group_id") : "");
            game.setGroup_type(temp.has("name") ? temp.getString("name") : "");
            game.setGroup_name(temp.has("group_name") ? temp.getString("group_name") : "");
            game.setAttention_num(temp.has("attention_num") ? temp.getString("attention_num") : "");
            game.setMark(temp.has("mark") ? temp.getString("mark") : "");
            game.setFlagPath(temp.has("flag") ? temp.getString("flag") : "");
            game.setDescription(temp.has("description") ? temp.getString("description") : "");
            game.setVideo_num(temp.has("video_num") ? temp.getString("video_num") : "");

        } catch (Exception e) {
            Log.e("getGameCircleInfoList_e", e.toString());
            return null;
        }
        return game;
    }

    /**
     * 分类视屏
     *
     * @param context
     * @param page
     * @return
     */
    public static List<VideoEntity> getVideoTypeList(Context context, String page, String type_id, String type) {
        String json = HttpUtils.httpGet(HttpUtils.getRecommendList(page, type_id, type));
        Log.e("getVideoTypeList_url", HttpUtils.getRecommendList(page, type_id, type));
        Log.e("getVideoTypeList_json", json);
        String result = "";
        String msg = "";
        String data = "";
        List<VideoEntity> list = new ArrayList<VideoEntity>();
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
//            data=object.getString(DATA);
            JSONArray array = object.getJSONObject(DATA).getJSONArray("list");
            for (int i = 0; i < array.length(); i++) {
                VideoEntity vedio = new VideoEntity();
                JSONObject temp = (JSONObject) array.get(i);
                vedio.setId(temp.getString("id"));
                vedio.setTitle_content(temp.getString("title"));
                vedio.setSimg_url(temp.getString("flagPath"));
                vedio.setFlower(temp.getString("flower_count"));
                vedio.setTime(TimeUtils.secToTime(Integer.parseInt(temp.getString("time_length"))));
                vedio.setComment_count(temp.getString("comment_count"));
                vedio.setViewCount(temp.getString("view_count"));
                list.add(vedio);
            }
        } catch (Exception e) {
            Log.e("getVedioList_e", e.toString());
            return null;
        }
        return list;
    }

    /**
     * 更多热门视频
     *
     * @param page
     * @param type
     * @return
     */
    public static List<VideoEntity> getRecommendVideoList(String page, String type, String flag) {
        String json = HttpUtils.httpGet(HttpUtils.getRecommendVideoList(page, type, flag));
        Log.e("getRecommendVideoList_url", HttpUtils.getRecommendVideoList(page, type, flag));
        Log.e("getRecommendVideoList_json", json);
        String result = "";
        String msg = "";
        String data = "";
        List<VideoEntity> list = new ArrayList<VideoEntity>();
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
//            data=object.getString(DATA);
            JSONArray array;
            if (flag.equals("home")) {
                array = object.getJSONArray(DATA);
            } else {
                array = object.getJSONObject(DATA).getJSONArray("list");
            }
            for (int i = 0; i < array.length(); i++) {
                VideoEntity vedio = new VideoEntity();
                JSONObject temp = (JSONObject) array.get(i);
                vedio.setId(temp.has("id") ? temp.getString("id") : "");
                if (flag.equals("home")) {
                    vedio.setTitle_content(temp.has("name") ? temp.getString("name") : "");
                } else {
                    vedio.setTitle_content(temp.has("title") ? temp.getString("title") : "");
                }
                vedio.setSimg_url(temp.has("flagPath") ? temp.getString("flagPath") : "");
                vedio.setFlower(temp.has("flower_count") ? temp.getString("flower_count") : "0");
                vedio.setTime(TimeUtils.secToTime(Integer.parseInt(temp.has("time_length") ? temp.getString("time_length") : "0")));
                vedio.setComment_count(temp.has("comment_count") ? temp.getString("comment_count") : "0");
                vedio.setViewCount(temp.has("view_count") ? temp.getString("view_count") : "0");
                list.add(vedio);
            }
        } catch (Exception e) {
            Log.e("getRecommendVideoList_e", e.toString());
            return null;
        }
        return list;
    }

    /**
     * 首页热门分类更多
     *
     * @param name
     * @param page
     * @param type
     * @return
     */
    public static List<VideoEntity> getMoreHomeHotList(String name, String page, String type) {
        String json = HttpUtils.httpGet(HttpUtils.getMoreHomeHotList(name, page, type));
        Log.e("getMoreHomeHotList_url", HttpUtils.getMoreHomeHotList(name, page, type));
        Log.e("getMoreHomeHotList_json", json);
        String result = "";
        String msg = "";
        String data = "";
        List<VideoEntity> list = new ArrayList<VideoEntity>();
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
//            data=object.getString(DATA);
            JSONArray array = object.getJSONObject("data").getJSONArray("list");
            for (int i = 0; i < array.length(); i++) {
                VideoEntity vedio = new VideoEntity();
                JSONObject temp = (JSONObject) array.get(i);
                vedio.setId(temp.getString("id"));
                vedio.setTitle_content(temp.getString("name"));
                vedio.setSimg_url(temp.getString("flagPath"));
                vedio.setFlower(temp.getString("flower_count"));
                vedio.setTime(TimeUtils.secToTime(Integer.parseInt(temp.getString("time_length"))));
                vedio.setComment_count(temp.getString("comment_count"));
                vedio.setViewCount(temp.getString("view_count"));
                list.add(vedio);
            }
        } catch (Exception e) {
            Log.e("getMoreHomeHotList_e", e.toString());
            return null;
        }
        return list;
    }

    /**
     * 主页首页 首页专栏 更多
     *
     * @param more_mark
     * @param sort
     * @param page
     * @return
     */
    public static List<VideoEntity> getMoreHomeColumnList(String more_mark, String sort, String page) {
        String json = HttpUtils.httpGet(HttpUtils.getMoreHomeColumnUrl(more_mark, sort, page));
        Log.e("getMoreHomeColumnList_url", HttpUtils.getMoreHomeColumnUrl(more_mark, sort, page));
        Log.e("getMoreHomeColumnList_json", json);
        String result = "";
        String msg = "";
        String data = "";
        List<VideoEntity> list = new ArrayList<VideoEntity>();
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            msg = object.getString("msg");
            if (!result.equals(TRUE)) {
                return null;
            }
//            data=object.getString(DATA);
            JSONObject obj = (JSONObject) object.getJSONArray("data").get(0);
            String title = obj.getString("title");

            JSONArray array = obj.getJSONArray("list");
            for (int i = 0; i < array.length(); i++) {
                VideoEntity vedio = new VideoEntity();
                JSONObject temp = (JSONObject) array.get(i);
                vedio.setId(temp.getString("id"));
                vedio.setTitle_content(temp.getString("title"));
                vedio.setSimg_url(temp.getString("flagPath"));
                vedio.setFlower(temp.getString("flower_count"));
                vedio.setTime(TimeUtils.secToTime(Integer.parseInt(temp.getString("time_length"))));
                vedio.setComment_count(temp.getString("comment_count"));
                vedio.setViewCount(temp.getString("view_count"));
                list.add(vedio);
            }
        } catch (Exception e) {
            Log.e("getMoreHomeColumnList_e", e.toString());
            return null;
        }
        return list;
    }

    /**
     * 游戏圈视频列表
     *
     * @param group_id
     * @param member_id
     * @param page
     * @return
     */
    public static List<GameCircleVideoEntity> getGameCircleVideoList(String group_id, String member_id, String page) {
        String json = HttpUtils.httpGet(HttpUtils.getGameCircleVideoListUrl(group_id, member_id, page));
        Log.e("getGameCircleVideoList_url", HttpUtils.getGameCircleVideoListUrl(group_id, member_id, page));
        Log.e("getGameCircleVideoList_json", json);
        String result = "";
        String msg = "";
        String data = "";
        List<GameCircleVideoEntity> list = new ArrayList<GameCircleVideoEntity>();
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
//            data=object.getString(DATA);
            JSONArray array = object.getJSONArray(DATA);
            for (int i = 0; i < array.length(); i++) {
                GameCircleVideoEntity vedio = new GameCircleVideoEntity();
                JSONObject temp = (JSONObject) array.get(i);
                vedio.setAvatar(temp.has("avatar") ? temp.getString("avatar") : "");
                vedio.setMember_id(temp.has("member_id") ? temp.getString("member_id") : "");
                vedio.setNickname(temp.has("nickname") ? temp.getString("nickname") : "");
                vedio.setVideo_name(temp.has("video_name") ? temp.getString("video_name") : "");
                vedio.setVideo_time(temp.has("video_time") ? temp.getString("video_time") : "");
                vedio.setVideo_id(temp.has("video_id") ? temp.getString("video_id") : "");
                vedio.setView_count(temp.has("view_count") ? temp.getString("view_count") : "");
                vedio.setFlower_count(temp.has("flower_count") ? temp.getString("flower_count") : "");
                vedio.setComment_count(temp.has("comment_count") ? temp.getString("comment_count") : "");
                vedio.setTime_length(temp.has("time_length") ? temp.getString("time_length") : "");
                vedio.setFlagPath(temp.has("flagPath") ? temp.getString("flagPath") : "");
                vedio.setAttentionMark(temp.has("attentionMark") ? temp.getString("attentionMark") : "");
                vedio.setCollectionMark(temp.has("collectionMark") ? temp.getString("collectionMark") : "");
                vedio.setFlowerMark(temp.has("flowerMark") ? temp.getString("flowerMark") : "");
                list.add(vedio);
            }
        } catch (Exception e) {
            Log.e("getGameCircleVideoList", e.toString());
            return null;
        }
        return list;
    }

    /**
     * 上传视频列表
     *
     * @param member_id
     * @param page
     * @return
     */
    public static List<VideoEntity> getUploadList(String page, String member_id) {
        String json = HttpUtils.httpGet(HttpUtils.getUploadListUrl(page, member_id));
        Log.e("getUploadList_url", HttpUtils.getUploadListUrl(page, member_id));
        Log.e("getUploadList_json", json);
        String result = "";
        String msg = "";
        String data = "";
        List<VideoEntity> list = new ArrayList<VideoEntity>();
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
//            data=object.getString(DATA);
            JSONArray array = object.getJSONArray(DATA);
            for (int i = 0; i < array.length(); i++) {
                VideoEntity vedio = new VideoEntity();
                JSONObject temp = (JSONObject) array.get(i);
                vedio.setId(temp.has("id") ? temp.getString("id") : "");
                vedio.setTitle_content(temp.has("name") ? temp.getString("name") : "");
                vedio.setSimg_url(temp.has("flagPath") ? temp.getString("flagPath") : "");
                vedio.setFlower(temp.has("flower_count") ? temp.getString("flower_count") : "");
                vedio.setTime(temp.has("time_length") ? TimeUtils.secToTime(Integer.parseInt(temp.getString("time_length"))) : "");
                vedio.setComment(temp.has("comment_count") ? temp.getString("comment_count") : "");
                vedio.setViewCount(temp.has("view_count") ? temp.getString("view_count") : "");
                list.add(vedio);
            }
        } catch (Exception e) {
            Log.e("getUploadList", e.toString());
            return null;
        }
        return list;
    }

    /**
     * 获取游戏类型
     *
     * @return
     */
    public static List<GameType> getGameTypeList() {
        List<GameType> list = new ArrayList<GameType>();
        String json = HttpUtils.httpGet(HttpUtils.getGameTypeList());
        String result = "";
        String msg = "";
        String data = "";
        try {
            if (json.equals("")) {
                return null;
            }

            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }

            JSONArray array = object.getJSONObject(DATA).getJSONArray("list");
            for (int i = 0; i < array.length(); i++) {
                GameType gameType = new GameType();
                JSONObject temp = (JSONObject) array.get(i);
                gameType.setId(temp.getString("id"));
                gameType.setName(temp.getString("name"));
                gameType.setFlag(temp.getString("flag"));
                gameType.setSort(temp.getString("sort"));
                gameType.setHotrank("hotrank");
                list.add(gameType);
            }
        } catch (Exception e) {
            Log.e("getVedioList", e.toString());
            return null;
        }
        return list;
    }

    public static boolean uploadFile(Context context, String id, String url) {
        String json = HttpUtils.postImage(context, HttpUtils.getuploadAvatar(id), url);
        String result = "";
        String msg = "";
        String data = "";
        try {
            if (json.equals("")) {
                return false;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            Log.e("msg", object.getString("msg"));
            if (!result.equals(TRUE)) {
                return false;
            }
        } catch (Exception e) {
            Log.e("uploadFile", e.toString());
            return false;
        }
        return true;
    }

    /**
     * 点赞
     *
     * @param id
     * @return
     */
    public static String giveFlower(String id, String member_id) {
        String json = HttpUtils.httpGet(HttpUtils.getVedioFlowerUrl(id, member_id));
        Log.e("flower", json);
        Log.e("flower_url", HttpUtils.getVedioFlowerUrl(id, member_id));
        String result = "";
        try {
            if (json.equals("")) {
                return "";
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (result.equals(TRUE)) {
                return "s";
            } else {
                JSONObject dataobjcet = object.getJSONObject(DATA);
                if (dataobjcet.has("hadFlower")) {
                    if (dataobjcet.getString("hadFlower").equals(TRUE)) {
                        return "c";
                    }
                }
            }
        } catch (Exception e) {
            Log.e("giveflower", e.toString());
            return "";
        }
        return "f";
    }

    /**
     * 取消点赞
     *
     * @param id
     * @return
     */
    public static String cancelFlower(String id, String member_id) {
        String json = HttpUtils.httpGet(HttpUtils.getVedioCancelFlowerUrl(id, member_id));
        Log.e("cancelFlower", json);
        Log.e("cancelFlower_url", HttpUtils.getVedioCancelFlowerUrl(id, member_id));
        String result = "";
        try {
            if (json.equals("")) {
                return "";
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (result.equals(TRUE)) {
                return "s";
            } else {
                JSONObject dataobjcet = object.getJSONObject(DATA);
            }
        } catch (Exception e) {
            Log.e("cancelFlower", e.toString());
            return "";
        }
        return "f";
    }

    /**
     * 大神点赞
     *
     * @param id
     * @param member_id
     * @return
     */
    public static Boolean submitMasterPrise(String id, String member_id) {
        String json = HttpUtils.httpGet(HttpUtils.submitMasterPriseUrl(id, member_id));
        Log.e("submitMasterPrise", json);
        Log.e("submitMasterPrise_url", HttpUtils.submitMasterPriseUrl(id, member_id));
        String result = "";
        try {
            if (json.equals("")) {
                return false;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (result.equals(TRUE)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.e("submitMasterPrise_e", e.toString());
            return false;
        }
    }

    /**
     * 大神关注
     *
     * @param id
     * @param member_id
     * @return
     */
    public static Boolean submitMasterFocus(String id, String member_id) {
        String json = HttpUtils.httpGet(HttpUtils.submitMasterFocusUrl(id, member_id));
        Log.e("submitMasterFocus", json);
        Log.e("submitMasterFocus_url", HttpUtils.submitMasterFocusUrl(id, member_id));
        String result = "";
        try {
            if (json.equals("")) {
                return false;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (result.equals(TRUE)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.e("submitMasterFocus_e", e.toString());
            return false;
        }
    }

    /**
     * 视频评论点赞
     *
     * @param comment_id
     * @param member_id
     * @return
     */
    public static String submitCommentPrise(String comment_id, String member_id) {
        String json = HttpUtils.httpGet(HttpUtils.getCommentPriseUrl(comment_id, member_id));
        Log.e("submitCommentPrise", json);
        Log.e("submitCommentPrise_url", HttpUtils.getCommentPriseUrl(comment_id, member_id));
        String result = "";
        try {
            if (json.equals("")) {
                return "";
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (result.equals(TRUE)) {
                return "s";
            }
        } catch (Exception e) {
            Log.e("submitCommentPrise", e.toString());
            return "";
        }
        return "";
    }

    /**
     * 游戏圈评论点赞
     *
     * @param review_id
     * @param member_id
     * @return
     */
    public static String submitGameCirclePrise(String review_id, String member_id) {
        String json = HttpUtils.httpGet(HttpUtils.getGameCirclePriseUrl(review_id, member_id));
        Log.e("submitGameCirclePrise", json);
        Log.e("submitGameCirclePrise_url", HttpUtils.getGameCirclePriseUrl(review_id, member_id));
        String result = "";
        try {
            if (json.equals("")) {
                return "";
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (result.equals(TRUE)) {
                return "s";
            }
        } catch (Exception e) {
            Log.e("submitGameCirclePrise_e", e.toString());
            return "";
        }
        return "";
    }

    /**
     * 个人评论点赞
     *
     * @param comment_id
     * @param member_id
     * @return
     */
    public static String submitMasterCommentPrise(String comment_id, String member_id) {
        String json = HttpUtils.httpGet(HttpUtils.getPersonCommentPriseUrl(comment_id, member_id));
        Log.e("submitMasterCommentPrise", json);
        Log.e("submitMasterCommentPrise_url", HttpUtils.getPersonCommentPriseUrl(comment_id, member_id));
        String result = "";
        try {
            if (json.equals("")) {
                return "";
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (result.equals(TRUE)) {
                return "s";
            }
        } catch (Exception e) {
            Log.e("submitMasterCommentPrise_e", e.toString());
            return "";
        }
        return "";
    }

    /**
     * 收藏视频
     *
     * @return
     */
    public static String getCollectVideo(String id, String member_id) {
        String json = HttpUtils.httpGet(HttpUtils.getCollectVideo(id, member_id));
        String result = "";
        String msg = "";
        String data = "";
        try {
            if (json.equals("")) {
                return "";
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (result.equals(TRUE)) {
                return "s";
            } else {
                JSONObject dataobjcet = object.getJSONObject(DATA);
                if (dataobjcet.has("hadCollect")) {
                    if (dataobjcet.getString("hadCollect").equals(TRUE)) {
                        return "c";
                    }
                }
            }

        } catch (Exception e) {
            Log.e("getCollectVideo", e.toString());
            return "";
        }
        return "f";
    }

    /**
     * 取消收藏视频
     *
     * @return
     */
    public static String getCancelCollectVideo(String id, String member_id) {
        String json = HttpUtils.httpGet(HttpUtils.getCancelCollectVideo(id, member_id));
        Log.e("CancelCollectVideo_url", HttpUtils.httpGet(HttpUtils.getCancelCollectVideo(id, member_id)));
        String result = "";
        String msg = "";
        String data = "";
        try {
            if (json.equals("")) {
                return "";
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (result.equals(TRUE)) {
                return "s";
            } else {
                JSONObject dataobjcet = object.getJSONObject(DATA);
            }

        } catch (Exception e) {
            Log.e("getCancelCollectVideo", e.toString());
            return "";
        }
        return "f";
    }

    /**
     * 获取我的收藏列表
     *
     * @param context
     * @param memer_id
     * @param page
     * @return
     */
    public static List<VideoEntity> getCollectList(Context context, String memer_id, String page) {
        String json = HttpUtils.httpGet(HttpUtils.getCollectVideoList(memer_id, page));
        String result = "";
        String msg = "";
        String data = "";
        List<VideoEntity> list = new ArrayList<VideoEntity>();
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONArray array = object.getJSONObject(DATA).getJSONArray("list");
            for (int i = 0; i < array.length(); i++) {
                VideoEntity vedio = new VideoEntity();
                JSONObject temp = (JSONObject) array.get(i);
                vedio.setId(temp.getString("video_id"));
                vedio.setTitle_content(temp.getString("name"));
                vedio.setSimg_url(temp.getString("flagPath"));
                vedio.setFlower(temp.getString("flower_count"));
                vedio.setTime(TimeUtils.secToTime(Integer.parseInt(temp.getString("time_length"))));
                vedio.setComment(temp.getString("comment_count"));
                vedio.setViewCount(temp.getString("view_count"));

                list.add(vedio);
            }
        } catch (Exception e) {
            Log.e("getCollectList", e.toString());
            return null;
        }
        return list;
    }

    /**
     * 获取升级版本号
     *
     * @return
     */
    public static Update getUpdate(Context context) {
        String json = HttpUtils.httpGet(HttpUtils.getUpdateUrl() + VersionUtils.getCurrentVersionCode(context) + "&target=a_sysj");
        String result = "";
        String msg = "";
        String data = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONArray array = object.getJSONArray(DATA);
            JSONObject temp = (JSONObject) array.get(0);
            Update update = new Update();
            update.setBuild(temp.getString("build"));
            update.setChange_log(temp.getString("change_log"));
            update.setUpdate_flag(temp.getString("update_flag"));
            update.setUpdate_url(temp.getString("update_url"));
            update.setVersion_str(temp.getString("version_str"));
            return update;

        } catch (Exception e) {
            Log.e("update", e.toString());
            return null;
        }
    }


    /**
     * 获取 热门游戏 列表
     *
     * @return
     */
    public static List<Game> getHotGameList() {
        List<Game> list = new ArrayList<Game>();
        String json = HttpUtils.httpGet(HttpUtils.getHotGame());
        Log.e("hotGamelist_json", json);
        Log.e("hotGamelist_url", HttpUtils.getHotGame());
        String result = "";
        String msg = "";
        String data = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONArray array = object.getJSONArray(DATA);
            for (int i = 0; i < array.length(); i++) {
                JSONObject temp = (JSONObject) array.get(i);
                Game game = new Game();
                game.setId(temp.getString("game_id"));
                game.setFlagPath(temp.getString("flag"));
                game.setName(temp.getString("gameName"));
                game.setType_name(temp.getString("type_name"));
                game.setTime(temp.getString("time"));
                list.add(game);
            }

        } catch (Exception e) {
            Log.e("", e.toString());
            return null;
        }
        return list;
    }

    /**
     * 获取大神列表
     *
     * @return
     */
    public static List<MasterEntity> getMasterColumnList(String page, String member_id) {
        List<MasterEntity> list = new ArrayList<MasterEntity>();
        String json = HttpUtils.httpGet(HttpUtils.getMasterListUrl(page, member_id));
        Log.e("getMasterColumnList_json", json);
        Log.e("getMasterColumnList_url", HttpUtils.getMasterListUrl(page, member_id));
        String result = "";
        String msg = "";
        String data = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONArray array = object.getJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                JSONObject temp = (JSONObject) array.get(i);
                MasterEntity master = new MasterEntity();
                master.setNickname(temp.has("nickname") ? temp.getString("nickname") : "");
                master.setFlagPath(temp.has("flagPath") ? temp.getString("flagPath") : "");
                master.setManifesto(temp.has("testimonials") ? temp.getString("testimonials") : "");
                master.setMember_id(temp.has("member_id") ? temp.getString("member_id") : "");
                master.setUrl(temp.has("url") ? temp.getString("url") : "");
                master.setMark(temp.has("mark") ? temp.getInt("mark") : 0);
                list.add(master);
            }

        } catch (Exception e) {
            Log.e("getMasterColumnList_e", e.toString());
            return null;
        }
        return list;
    }

    /**
     * 更新用户信息
     *
     * @param member_id
     * @param name
     * @param mobile
     * @param nickName
     * @param sex       0女 1男
     * @param address
     * @return
     */
    public static String getUpdateInfo(String member_id, String name, String mobile, String nickName,
                                       String sex, String address, String like_gametype, String email, String signature, Context context) {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("id", member_id));
        if (!name.equals("")) {
            pairs.add(new BasicNameValuePair("name", name));
        }
        if (!mobile.equals("")) {
            pairs.add(new BasicNameValuePair("mobile", mobile));
        }
        if (!nickName.equals("")) {
            pairs.add(new BasicNameValuePair("nickname", nickName));
        }
        if (!sex.equals("")) {
            pairs.add(new BasicNameValuePair("sex", sex));
        }
        if (!address.equals("")) {
            pairs.add(new BasicNameValuePair("address", address));
        }
        pairs.add(new BasicNameValuePair("signature", signature));

        pairs.add(new BasicNameValuePair("like_gametype", like_gametype));

        if (!email.equals("")) {
            pairs.add(new BasicNameValuePair("email", email));
        }

        Log.e("post", pairs.toString());
        String json = HttpUtils.httpPost(pairs, HttpUtils.getUpdateInfo());
        try {
            JSONObject object = new JSONObject(json);
            String result = object.getString("result");

            if (result.equals("true")) {
                return "s";
            } else {
                ToastUtils.showToast(context, object.getString("msg"));
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }

    /**
     * 更新用户信息
     * QQ
     * mobile
     *
     * @param member_id
     * @param QQ
     * @param mobile
     * @param context
     * @return
     */
    public static String updateQQPhoneInfo(String member_id, String QQ, String mobile, Context context) {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("id", member_id));
        if (!QQ.equals("")) {
            pairs.add(new BasicNameValuePair("QQ", QQ));
        }
        if (!mobile.equals("")) {
            pairs.add(new BasicNameValuePair("mobile", mobile));
        }

        Log.e("post", pairs.toString());
        String json = HttpUtils.httpPost(pairs, HttpUtils.getUpdateInfo());
        try {
            JSONObject object = new JSONObject(json);
            String result = object.getString("result");

            if (result.equals("true")) {
                return "s";
            } else {
                ToastUtils.showToast(context, object.getString("msg"));
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }


    /**
     * 得到accesstoken
     *
     * @return
     */
    public static String getAccessToken(String code) {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("client_id", ExApplication.id));
        pairs.add(new BasicNameValuePair("client_secret", ExApplication.client_secret));
        pairs.add(new BasicNameValuePair("grant_type", "authorization_code"));
        pairs.add(new BasicNameValuePair("code", code));
        pairs.add(new BasicNameValuePair("redirect_uri", ExApplication.url));
        String json = HttpUtils.httpPost(pairs, "https://openapi.youku.com/v2/oauth2/token");
        try {
            JSONObject object = new JSONObject(json);
            String result = object.getString("access_token");
            String refreshToken = object.getString("refresh_token");
//            SharePreferenceUtil.setPreference();
            Log.e("-----", result);
            return result;
        } catch (Exception e) {
            return "";
        }

    }

    /**
     * 第三用户
     *
     * @param context
     * @param openId
     * @param nickname
     * @return
     */
    public static UserEntity getOtherUser(Context context, String openId, String nickname, String sex, String location, String figureurl) {
        UserEntity user = new UserEntity();
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("key", openId));
        pairs.add(new BasicNameValuePair("isOpenId", "1"));
        pairs.add(new BasicNameValuePair("nickname", nickname));
        pairs.add(new BasicNameValuePair("name", nickname));
        pairs.add(new BasicNameValuePair("sex", sex));
        pairs.add(new BasicNameValuePair("location", location));
        pairs.add(new BasicNameValuePair("avatar", figureurl));
        pairs.add(new BasicNameValuePair("regOrigin", "a_sysj"));

        Log.e("post", pairs.toString());

        String json = HttpUtils.httpPost(pairs, HttpUtils.getOtherLogin());
        try {
            JSONObject object = new JSONObject(json);
            String result = object.getString("result");
            String msg = object.getString("msg");
            Log.e("msg", msg);
            System.out.println("result===" + result + "  msg===" + msg);
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONObject data = object.getJSONObject(DATA);
//            JSONObject data=(JSONObject)list.get(0);
//            if (data.toString().equals("false")){
//                return null;
//            }
            user.setImgPath(data.getString("avatar"));
            user.setId(data.getString("id"));
            user.setName(data.getString("name"));
            user.setTitle(data.getString("nickname"));
            user.setAddress(data.has("address") ? data.getString("address") : "");
            user.setGrace(data.getString("degree"));
            user.setLike_gametype(data.getString("like_gametype"));
            user.setRank(data.getString("rank"));
            user.setSex(data.getString("sex"));
            SharePreferenceUtil.setUserEntity(context, json);
        } catch (Exception e) {
            Log.e("getUerInfo", e.toString());
            return null;
        }
        return user;
    }


    /**
     * 获取用户详细信息
     *
     * @param context
     * @param id
     * @return
     */
    public static UserEntity getUserDetailInfo(Context context, String id, String member_id, String flag) {
        UserEntity user = new UserEntity();
        String json = HttpUtils.httpGet(HttpUtils.getUserDetailInfo(id, member_id));
        Log.e("getUserDetailInfo_url---------", HttpUtils.getUserDetailInfo(id, member_id));
        Log.e("------------------------", json);
        String result = "";
        String msg = "";
//        String data = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return null;
            }
            JSONObject data = object.getJSONObject(DATA);
//            JSONObject data=(JSONObject)list.get(0);
//            if (data.toString().equals("false")){
//                return null;
//            }
            user.setAddress(data.has("address") ? data.getString("address") : "");
            user.setImgPath(data.has("avatar") ? data.getString("avatar") : "");
            user.setGrace(data.has("degree") ? data.getString("degree") : "");
            user.setId(data.has("id") ? data.getString("id") : "");
            user.setIsAdmin(data.has("isAdmin") ? data.getString("isAdmin") : "");
            user.setLike_gametype(data.has("like_gametype") ? data.getString("like_gametype") : "");
            user.setMobile(data.has("mobile") ? data.getString("mobile") : "");
            user.setQQ(data.has("qq") ? data.getString("qq") : "");
            user.setName(data.has("name") ? data.getString("name") : "");
            user.setTitle(data.has("nickname") ? data.getString("nickname") : "");
            user.setOpenId(data.has("openid") ? data.getString("openid") : "");
            user.setPassword(data.has("password") ? data.getString("password") : "");
            user.setDegree(data.has("degree") ? data.getInt("degree") : 1);
            user.setRank(data.has("rank") ? data.getString("rank") : "");
            user.setSex(data.has("sex") ? data.getString("sex") : "");
            user.setTime(data.has("time") ? data.getString("time") : "");
            user.setHonour(data.has("title") ? data.getString("title") : "");
            user.setMember_rank(data.has("member_rank") ? data.getInt("member_rank") : 1);
            user.setMember_exp(data.has("member_exp") ? data.getInt("member_exp") : 0);
            user.setNext_exp(data.has("next_exp") ? data.getInt("next_exp") : 0);
            user.setEmail(data.has("email") ? data.getString("email") : "");
            user.setAttention(data.has("attention") ? data.getString("attention") : "0");
            user.setFans(data.has("fans") ? data.getString("fans") : "0");
            user.setMark(data.has("mark") ? data.getInt("mark") : 0);
            user.setMisstion_count(data.has("taskNum") ? data.getString("taskNum") : "0");
            user.setSignature(data.has("signature") ? data.getString("signature") : "");
            JSONArray likeArray = data.has("like") ? data.getJSONArray("like") : null;
            int[] like;
            if (likeArray != null && likeArray.length() > 0) {
                like = new int[likeArray.length()];
                for (int i = 0; i < likeArray.length(); i++) {
                    like[i] = likeArray.getInt(i);
                }
            } else {
                like = null;
            }
            user.setLikeArray(like);
            if (SharePreferenceUtil.getUserEntity(context) == null && flag.equals("persion")) {
                SharePreferenceUtil.setUserEntity(context, json);
            }
        } catch (Exception e) {
            Log.e("getUerInfo", e.toString());
            return null;
        }
        return user;
    }

    /**
     * 获取优酷视频
     *
     * @param id
     * @return
     */
    public static VedioDetail getYouKuDetail(String id) {
        VedioDetail detail = new VedioDetail();
        String json = HttpUtils.httpGet(HttpUtils.youkuVideoInfo(id));
        Log.e("------------------------", json);
        String result = "";
        String msg = "";
//        String data = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            detail.setUrl(object.getString("player"));
            detail.setName(object.getString("title"));
            detail.setDescriptioin(object.getString("description"));
            detail.setLink(object.getString("link"));
            if (object.has("thumbnail")) {
                detail.setFlagPath(object.getString("thumbnail"));
            }
            if (object.has("duration")) {
                detail.setTime(object.getString("duration"));
            }

            return detail;
        } catch (Exception e) {
            Log.e("getDetail", e.toString());
            return null;
        }
    }

    /**
     * 关注玩家
     *
     * @param fans_id    关注者的ID
     * @param befocus_id 被关注者的ID
     * @return
     */
    public static boolean submitFocus(String fans_id, String befocus_id) {
        String json = HttpUtils.httpGet(HttpUtils.submitFocusUrl(fans_id, befocus_id));
        Log.e("submitFocus_json", json);
        Log.e("submitFocus_url", HttpUtils.submitFocusUrl(fans_id, befocus_id));
        String result = "";
        String msg = "";
        try {
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return false;
            }
        } catch (Exception e) {
            Log.e("submitFocus_e", e.toString());
            return false;
        }
        return true;
    }

    /**
     * 加入游戏圈
     *
     * @param group_id
     * @param member_id
     * @return
     */
    public static boolean joinGameCircle(String group_id, String member_id) {
        String json = HttpUtils.httpGet(HttpUtils.joinGameCircleUrl(group_id, member_id));
        Log.e("joinGameCircle_json", json);
        Log.e("joinGameCircle_url", HttpUtils.joinGameCircleUrl(group_id, member_id));
        String result = "";
        try {
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return false;
            }
        } catch (Exception e) {
            Log.e("joinGameCircle_e", e.toString());
            return false;
        }
        return true;
    }

    /**
     * 关注大神
     *
     * @param fans_id
     * @param master_id
     * @return
     */
    public static boolean masterFocus(String fans_id, String master_id) {
        String json = HttpUtils.httpGet(HttpUtils.masterFocusUrl(fans_id, master_id));
        Log.e("submitFocus_json", json);
        Log.e("submitFocus_url", HttpUtils.masterFocusUrl(fans_id, master_id));
        String result = "";
        String msg = "";
        try {
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            if (!result.equals(TRUE)) {
                return false;
            }
        } catch (Exception e) {
            Log.e("submitFocus_e", e.toString());
            return false;
        }
        return true;
    }

    /**
     * 提交视频
     *
     * @param context
     * @param member_id
     * @param video_id
     * @param name
     * @param game_name
     * @param time_length
     * @return
     */
    public static String postVideo(Context context, String member_id, String video_id,
                                   String name, String game_name, String time_length, String match_id, String phone, String qq) {

//        List<NameValuePair> pairs=new ArrayList<NameValuePair>();
//        pairs.add(new BasicNameValuePair("member_id",member_id));
//        pairs.add(new BasicNameValuePair("video_id",video_id));
//        pairs.add(new BasicNameValuePair("video_name",video_name));
//        pairs.add(new BasicNameValuePair("game_name",game_name));
//        pairs.add(new BasicNameValuePair("time_length", time_length));
//        Log.e("postVideo",pairs.toString());
        System.out.println("member_id==" + member_id);
        System.out.println("video_id==" + video_id);
        System.out.println("name==" + name);
        System.out.println("game_name==" + game_name);
        System.out.println("time_length==" + time_length);
        System.out.println("match_id==" + match_id);
        System.out.println("phone==" + phone);
        System.out.println("qq==" + qq);
        String response = HttpUtils.httpPost(member_id, video_id, name, game_name, time_length, match_id, phone, qq, HttpUtils.postVideoInfo());
        System.out.println("response================" + response);

        try {
            JSONObject object = new JSONObject(response);
            String result = object.getString("result");
            String msg = object.getString("msg");
            Log.e("msg", msg);
            if (!result.equals(TRUE)) {
                return "";
            }
        } catch (Exception e) {
            Log.e("postVideo", e.toString());
            return "";
        }
        return response;

    }

    /**
     * 完成任务
     *
     * @param id
     * @return
     */
    public static boolean CompleteMission(String id, Context context) {
        String json = HttpUtils.httpGet(HttpUtils.completeMission(id));
        Log.e("mission", json);
        String result = "";
        try {
            if (json.equals("")) {
                return false;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            String msg = object.getString("msg");
            Log.e("cmoplete_msg", msg);
            if (result.equals(TRUE)) {
                return true;
            }
        } catch (Exception e) {
            Log.e("complete_mission", e.toString());
        }
        return false;
    }

    /**
     * 收藏赛事
     *
     * @param match_id
     * @param member_id
     * @return
     */
    public static boolean CollectMatch(String match_id, String member_id) {
        String json = HttpUtils.httpGet(HttpUtils.submitCollectMatch(member_id, match_id));
        Log.e("collect_json", json);
        Log.e("collect_url", HttpUtils.submitCollectMatch(member_id, match_id));
        String result = "";
        try {
            if (json.equals("")) {
                return false;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            String msg = object.getString("msg");
            Log.e("collect_msg", msg);
            if (result.equals(TRUE)) {
                return true;
            }
        } catch (Exception e) {
            Log.e("collect_exception", e.toString());
        }
        return false;
    }

    /**
     * 获取赛事收藏状态
     *
     * @param match_id
     * @param member_id
     * @return
     */
    public static Map<String, String> getMatchState(String match_id, String member_id) {
        Map<String, String> map = new HashMap<String, String>();
        String json = HttpUtils.httpGet(HttpUtils.getMatchStateUrl(member_id, match_id));
        Log.e("collectstate_json", json);
        Log.e("collectstate_url", HttpUtils.getMatchStateUrl(member_id, match_id));
        String result = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            String msg = object.getString("msg");
            JSONObject object1 = object.getJSONObject("data");
            Log.e("collect_msg", msg);
            if (result.equals(TRUE)) {
                map.put("collectState", "true");
            } else {
                map.put("collectState", "false");
            }
            map.put("likeNum", object1.getString("likeNum"));
            map.put("reviewNum", object1.getString("reviewNum"));
        } catch (Exception e) {
            Log.e("collect_exception", e.toString());
        }
        return map;
    }

    /**
     * 提交赛事评论
     *
     * @param member_id
     * @param match_id
     * @param content
     * @return
     */
    public static boolean submitMatchReview(String member_id, String match_id, String content) {
        String json = HttpUtils.httpGet(HttpUtils.getMatchReviewUrl(member_id, match_id, content));
        Log.e("matchReview_json", json);
        Log.e("matchReview_url", HttpUtils.getMatchReviewUrl(member_id, match_id, content));
        String result = "";
        try {
            if (json.equals("")) {
                return false;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            String msg = object.getString("msg");
            Log.e("collect_msg", msg);
            if (result.equals(TRUE)) {
                return true;
            }
        } catch (Exception e) {
            Log.e("collect_exception", e.toString());
        }
        return false;
    }

    /**
     * 游戏下载量统计
     *
     * @param game_id
     * @return
     */
    public static boolean submitDownloadCount(String game_id) {
        String json = HttpUtils.httpGet(HttpUtils.getSubmitDownloadCountUrl(game_id));
        Log.e("submitDownloadCount_json", json);
        Log.e("submitDownloadCount_url", HttpUtils.getSubmitDownloadCountUrl(game_id));
        String result = "";
        try {
            if (json.equals("")) {
                return false;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            String msg = object.getString("msg");
            Log.e("downloadcount_msg", msg);
            if (result.equals(TRUE)) {
                return true;
            }
        } catch (Exception e) {
            Log.e("downloadcount_exception", e.toString());
        }
        return false;
    }

    /**
     * 取消收藏
     *
     * @param ids
     * @return
     */
    public static boolean cancelCollect(String ids) {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("member_id", ExApplication.MEMBER_ID));
        pairs.add(new BasicNameValuePair("id", ids));
        Log.e("collect_post", pairs.toString());
        String json = HttpUtils.httpPost(pairs, HttpUtils.cancelCollect(ids));
        Log.e("cancelCollect", json);
        String result = "";
        try {
            if (json.equals("")) {
                return false;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            String msg = object.getString("msg");
            Log.e("cancelCollect_msg", msg);
            if (result.equals(TRUE)) {
                return true;
            }
        } catch (Exception e) {
            Log.e("cancelCollect", e.toString());
        }
        return false;
    }

    /**
     * 刷新accesstoken
     *
     * @param context
     * @return
     */
    public static boolean refreshToken(Context context) {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("client_id", ExApplication.id));
        pairs.add(new BasicNameValuePair("client_secret", ExApplication.client_secret));
        pairs.add(new BasicNameValuePair("grant_type", "refresh_token"));
        pairs.add(new BasicNameValuePair("refresh_token", SharePreferenceUtil.getPreference(context, "refresh")));
        String json = HttpUtils.httpPost(pairs, HttpUtils.refreshToken());
        Log.e("refresh", json);
        String token = "";
        String refresh = "";
        try {
            if (json.equals("")) {
                return false;
            }
            JSONObject object = new JSONObject(json);
            token = object.getString("access_token");
            refresh = object.getString("refresh_token");
            SharePreferenceUtil.setPreference(context, "token", token);
            SharePreferenceUtil.setPreference(context, "refresh", refresh);
            return true;
        } catch (Exception e) {
            Log.e("cancelCollect", e.toString());
            return false;
        }
    }

    /**
     * 获取用户上传视频
     *
     * @param context
     * @param id
     * @return
     */
    public static String getUserVideoId(Context context, String id) {
        String videoId = "";
        String json = HttpUtils.httpGet(HttpUtils.getUserVideoId(id));
        Log.e("-----------------------", json);
        String result = "";
        String msg = "";
        try {
            if (json.equals("")) {
                return null;
            }
            JSONObject object = new JSONObject(json);
            result = object.getString("result");
            videoId = object.getString("data");
            System.out.println("result==" + result);
            System.out.println("videoId" + videoId);
        } catch (Exception e) {
            Log.e("getUerInfo", e.toString());
            return null;
        }
        return videoId;
    }

    public static boolean hasValue(JSONObject object, String key) {
        if (object.has(key)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 提交视频信息请求上传凭证
     *
     * @param context
     * @param memberID    用户ID
     * @param videoTitle  视频标题
     * @param videoType   视频类型
     * @param matchID     活动ID
     * @param videoLength 视频时长
     * @param videoWidth  视频宽度
     * @param videoHeight 视频高度
     * @param channel     转码通道ID
     * @return
     */
    public static UploadVideoInfo.BackVideoInfo postVideoEntityBackToken(
            Context context, String memberID, String videoTitle,
            String videoType, String matchID, String videoLength, String videoWidth,
            String videoHeight, String channel) {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("memberID", memberID));
        pairs.add(new BasicNameValuePair("videoTitle", videoTitle));
        pairs.add(new BasicNameValuePair("gameName", videoType));
        pairs.add(new BasicNameValuePair("match_id", matchID));
        pairs.add(new BasicNameValuePair("time_length", videoLength));
        pairs.add(new BasicNameValuePair("width", videoWidth));
        pairs.add(new BasicNameValuePair("height", videoHeight));
        pairs.add(new BasicNameValuePair("channel", channel));
        String response = HttpUtils.httpPostVideoEntityBackToke(pairs,
                HttpUtils.PostVideoEntityBackToke());
        UploadVideoInfo.BackVideoInfo upBackInfo = new UploadVideoInfo.BackVideoInfo();
        try {
            JSONObject object = new JSONObject(response);

            String msg = object.getString("msg");
            String result = object.getString("result");
            if (!result.equals(TRUE)) {// 如果获取token有异常
                // 异常信息
                upBackInfo.setMSG(msg);
                // 获取结果
                upBackInfo.setResult(result);
                return upBackInfo;
            } else {
                upBackInfo.setMSG(msg);
                // 获取结果
                upBackInfo.setResult(result);
                JSONObject data = new JSONObject();
                data = object.getJSONObject(DATA);

                if (data != null) {
                    upBackInfo.setVideoKey(data.getString("videokey"));
                    upBackInfo.setVideoUploadToken(data
                            .getString("videouploadtoken"));

                }
            }

        } catch (Exception e) {
            Log.e("postVideo", e.toString());
            return upBackInfo;
        }

        return upBackInfo;

    }

    /**
     * 提交视频上传结果
     *
     * @param context
     * @param memberID  用户ID
     * @param matchID   活动ID
     * @param videokey  七牛上的视频ID
     * @param isSuccess 0失败 1成功
     * @return
     */
    public static String postVideoUploadResult(Context context,
                                               String memberID, String matchID, String videokey, String isSuccess) {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("member_id", memberID));
        pairs.add(new BasicNameValuePair("match_id", matchID));
        pairs.add(new BasicNameValuePair("videokey", videokey));
        pairs.add(new BasicNameValuePair("is_success", isSuccess));
        String response = HttpUtils.httpPostVideoUpoloadResult(pairs,
                HttpUtils.PostVideoUploadResult());
        try {
            JSONObject object = new JSONObject(response);

            String result = object.getString("result");

            if (!result.equals(TRUE)) {
                return "";
            }
        } catch (Exception e) {
            Log.e("postVideo", e.toString());
            return null;
        }
        return response;
    }


    // 提交上传封面
    public static String uploadVideoCoverImage(Context context,
                                               String videokey, String url) {
        String json = HttpUtils.postImage(context, videokey,
                HttpUtils.getuploadVideoCoverImage(videokey), url);

        try {
            if (json.equals("")) {

                return "";
            }

        } catch (Exception e) {
            Log.e("uploadFile", e.toString());
            return "";
        }
        return json;
    }

    /**
     * 获取后台ROOT提示权限是否开启
     *
     * @param channelName 渠道名称
     * @return
     */

    public static boolean getRootNotify(String channelName, Context mContext) {
        /**
         * 增加弹出框数据
         *
         * http://apps.ifeimo.com/home/sys/addSysAlert.html?alert_name=
         * yingyongbao
         *
         * 弹出框列表数据 http://apps.ifeimo.com/home/sys/sysAlert.html?alert_name=
         * yingyongbao
         *
         * 修改弹出框数据 http://apps.ifeimo.com/home/sys/upSysAlert.html?alert_name=
         * yingyongbao
         *
         */
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        // 判断本地的root开关是否已经开启
        boolean isRootNotify = sp.getBoolean("getRootNotifyFor" + channelName,
                false);
        if (isRootNotify) {

            return true;
        } else {
            // 获取后台root开关
            String url = "http://apps.ifeimo.com/home/sys/sysAlert.html?alert_name=";
            String json = HttpUtils.httpGet(url + channelName);
            try {
                JSONObject object = new JSONObject(json);
                object = new JSONObject(json);
                String result = object.getString("result");

                if (result.equals("true")) {

                    JSONArray arrayList = object.getJSONArray("data");
                    JSONObject temp = (JSONObject) arrayList.get(0);
                    String display = (String) temp.get("display");

                    if (display.equals("1")) {// 1为开启，0为不开启

                        // 修改本地root提示开关
                        sp.edit()
                                .putBoolean("getRootNotifyFor" + channelName,
                                        true).commit();
                        return true;
                    } else {

                        return false;
                    }
                }
            } catch (JSONException e) {

                e.printStackTrace();

                return true;
            }
            return true;
        }
    }

}


