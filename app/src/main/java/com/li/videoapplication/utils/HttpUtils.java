package com.li.videoapplication.utils;

import android.content.Context;
import android.util.Log;

import com.li.videoapplication.activity.ExApplication;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by li on 2014/9/22.
 */
public class HttpUtils {

    public static String BASE_URL = "http://apps.ifeimo.com/home";
//    public static String BASE_URL="http://192.168.28.9/sysj_php/home";
//    public static final String BASE_URL="http://nw.sysj.com/home";


    /**
     * 自动获取礼包详情
     *
     * @param id
     * @return
     */
    public static String getAutoGift(String id, String member_id) {
        return BASE_URL + "/member/PackageInfo.html?id=" + id + "&member_id=" + member_id;
    }

    // 上传封面接口
    public static final String getuploadVideoCoverImage(String videoKey) {
        return "http://apps.ifeimo.com/home/video/uploadPicQiniu.html?videokey="
                + videoKey;
    }

    /**
     * 我的礼包列表
     *
     * @param page
     * @return
     */
    public static String getMyGift(String page) {
        return BASE_URL + "/member/getMyPackageList.html?member_id=" + ExApplication.MEMBER_ID + "&page=" + page;
    }


    /**
     * 我的消息列表
     *
     * @param type_id   1表示系统消息，2视频消息（含空间信息）
     * @param member_id
     * @param page
     * @return
     */
    public static String getMyMessage(String type_id, String member_id, String page) {
        return BASE_URL + "/member/msgList.html?type_id=" + type_id + "&member_id=" + member_id + "&page=" + page;
    }

    /**
     * 刷新token
     *
     * @return
     */
    public static final String refreshToken() {
        return "https://openapi.youku.com/v2/oauth2/token";
    }

    /**
     * 取消收藏
     *
     * @param ids
     * @return
     */
    public static String cancelCollect(String ids) {
        return BASE_URL + "/video/cancelCollect.html";
    }

    /**
     * 上传视频信息
     *
     * @return
     */
    public static final String postVideoInfo() {
        return BASE_URL + "/video/publishVideo.html";
    }

    /**
     * 单条视频基本信息
     *
     * @return
     */
    public static final String youkuVideoInfo(String id) {
        return "https://openapi.youku.com/v2/videos/show_basic.json?client_id=" + ExApplication.id + "&video_id=" + id;
    }

    /**
     * 第三方登陆
     *
     * @return
     */
    public static final String getOtherLogin() {
        return BASE_URL + "/member/login.html";
    }

    /**
     * 手机号登录
     *
     * @return
     */
    public static final String getPhoneLogin(String phoneNum) {
        return BASE_URL + "/member/login.html?key=" + phoneNum;
    }

    /**
     * 获取用户资料
     *
     * @param id
     * @return
     */
    public static final String getUserDetailInfo(String id, String member_id) {
        return BASE_URL + "/member/detailNew.html?id=" + id + "&member_id=" + member_id;
    }


    /**
     * 修改用户资料
     *
     * @return
     */
    public static final String getUpdateInfo() {
        return BASE_URL + "/member/finishMemberInfo.html";
    }

    /**
     * 获取(热门)游戏列表
     *
     * @return
     */
    public static final String getHotGame() {
        return BASE_URL + "/game/hotGame.html";
    }

    /**
     * 获取大神列表
     *
     * @return
     */
    public static final String getMasterListUrl(String page, String member_id) {
        return BASE_URL + "/topic/topicList.html?page=" + page + "&member_id=" + member_id;
    }

    /**
     * 关注玩家
     *
     * @param fans_id    关注者id
     * @param befocus_id 被关注者id
     * @return
     */
    public static final String submitFocusUrl(String fans_id, String befocus_id) {
        return BASE_URL + "/member/attention.html?id=" + fans_id + "&member_id=" + befocus_id;
    }

    /**
     * 加入游戏圈接口
     *
     * @param group_id
     * @param member_id
     * @return
     */
    public static final String joinGameCircleUrl(String group_id, String member_id) {
        return BASE_URL + "/game/attention.html?group_id=" + group_id + "&member_id=" + member_id;
    }

    /**
     * 大神关注
     *
     * @param fans_id
     * @param master_id
     * @return
     */
    public static final String masterFocusUrl(String fans_id, String master_id) {
        return BASE_URL + "/topic/subscribe.html?member_id=" + fans_id + "&id=" + master_id;
    }


    /**
     * 版本验证升级
     *
     * @return
     */
    public static final String getUpdateUrl() {
        return BASE_URL + "/index/updateVersion.html?build=";
    }

    /**
     * 收藏视屏
     *
     * @param member_id
     * @param id
     * @return
     */
    public static final String getCollectVideo(String id, String member_id) {
        return BASE_URL + "/video/collect.html?id=" + id + "&member_id=" + member_id;
    }

    /**
     * 取消收藏视频
     *
     * @param member_id
     * @param id
     * @return
     */
    public static final String getCancelCollectVideo(String id, String member_id) {
        return BASE_URL + "/video/cancelCollect.html?id=" + id + "&member_id=" + member_id;
    }

    /**
     * 收藏赛事
     *
     * @param member_id
     * @param match_id
     * @return
     */
    public static final String submitCollectMatch(String member_id, String match_id) {
        return BASE_URL + "/member/matchLike.html?match_id=" + match_id + "&member_id=" + member_id;
    }

    /**
     * 赛事收藏状态
     *
     * @param member_id
     * @param match_id
     * @return
     */
    public static final String getMatchStateUrl(String member_id, String match_id) {
        return BASE_URL + "/member/matchMark.html?match_id=" + match_id + "&member_id=" + member_id;
    }

    /**
     * 赛事评论
     *
     * @param member_id
     * @param match_id
     * @param content
     * @return
     */
    public static final String getMatchReviewUrl(String member_id, String match_id, String content) {
        return BASE_URL + "/member/matchReview.html?match_id=" + match_id + "&member_id=" + member_id + "&content=" + content;
    }

    /**
     * 会员收藏列表
     *
     * @param id
     * @param page
     * @return
     */
    public static final String getCollectVideoList(String id, String page) {
        return BASE_URL + "/member/collectVideoList.html?member_id=" + id + "&page=" + page;
    }

    /**
     * 上传头像
     *
     * @param id
     * @return
     */
    public static final String getuploadAvatar(String id) {
        return BASE_URL + "/member/uploadAvatar.html?id=" + id;
    }


    /**
     * 游戏类型(分类)列表
     *
     * @return
     */
    public static final String getGameTypeList() {
        return BASE_URL + "/game/typeList.html";
    }

    /**
     * 游戏圈子类型接口
     *
     * @return
     */
    public static final String getGameCircleTypeUrl() {
        return BASE_URL + "/game/groupType.html";
    }

    /**
     * 游戏圈子列表接口
     *
     * @param type_id
     * @param member_id
     * @return
     */
    public static final String getGameCircleGameUrl(String page, String type_id, String member_id) {
        return BASE_URL + "/game/groupList.html?type_id=" + type_id + "&member_id=" + member_id + "&page=" + page;
    }

    /**
     * 游戏圈子详情
     *
     * @param group_id
     * @param member_id
     * @param type_name 圈子类型名
     * @return
     */
    public static final String getGameCircleInfoUrl(String group_id, String member_id, String type_name) {
        return BASE_URL + "/game/groupDetail.html?group_id=" + group_id + "&member_id=" + member_id + "&type_name=" + type_name;
    }

    /**
     * 小编荐
     *
     * @return
     */
    public static final String getShowPointList(String page) {
        return BASE_URL + "/recommend/editorRecommend.html?page=" + page;
    }

    /**
     * 用户上传视频列表（云端视频）
     *
     * @param page
     * @return
     */
    public static final String getColudVideoUrl(String page) {
        return BASE_URL + "/video/authorVideoList.html?page=" + page + "&member_id=" + ExApplication.MEMBER_ID;
    }

    /**
     * 返回视界专栏视频二级列表
     *
     * @return
     */
    public static final String getHomeColumnListUrl(String page, String mark) {
        return BASE_URL + "/recommend/topicList.html?page=" + page + "&mark=" + mark;
    }

    /**
     * 下载量统计接口
     *
     * @param game_id
     * @return
     */
    public static final String getSubmitDownloadCountUrl(String game_id) {
        return BASE_URL + "/game/downloadNum.html?id=" + game_id;
    }

    /**
     * 根据提交的类型获取视频列表
     *
     * @param page
     * @param type_id
     * @return
     */
    public static final String getRecommendList(String page, String type_id, String type) {
//        return BASE_URL+"/video/recommendList.html?page="+page+"&type_id="+type_id;
        return BASE_URL + "/video/list.html?page=" + page + "&sort=" + type + "&type_id=" + type_id;
    }

    /**
     * 更多热门视频、精彩推荐
     *
     * @param page
     * @param type
     * @return
     */
    public static final String getRecommendVideoList(String page, String type, String flag) {
        if (flag.equals("home")) {
            return BASE_URL + "/video/homeDaily.html?page=" + page + "&sort=" + type;
        } else {
            return BASE_URL + "/video/likeVideoList.html?member_id=" + ExApplication.MEMBER_ID + "&page=" + page + "&sort=" + type;
        }
    }

    /**
     * 首页热门分类更多
     *
     * @param name
     * @param page
     * @param type
     * @return
     */
    public static final String getMoreHomeHotList(String name, String page, String type) {
        return BASE_URL + "/search/searchVideo.html?name=" + name + "&sort=" + type + "&page=" + page;
    }

    /**
     * 游戏圈子视频列表接口
     *
     * @param group_id
     * @param member_id
     * @param page
     * @return
     */
    public static final String getGameCircleVideoListUrl(String group_id, String member_id, String page) {
        return BASE_URL + "/game/groupVideoList.html?group_id=" + group_id + "&member_id=" + member_id + "&page=" + page;
    }

    /**
     * 用户上传（视频）列表
     *
     * @param page
     * @param member_id
     * @return
     */
    public static final String getUploadListUrl(String page, String member_id) {
        return BASE_URL + "/Video/authorVideoList.html?member_id=" + member_id + "&page=" + page;
    }

    /**
     * 视频类型列表
     *
     * @return
     */
    public static final String getVideoTypeList() {
        return BASE_URL + "/video/typeList.html";
    }

    /**
     * 领取礼包
     *
     * @param member_id
     * @param id
     * @return
     */
    public static final String getGiftUrl(String member_id, String id) {
        return BASE_URL + "/member/claimPackage.html?member_id=" + member_id + "&id=" + id;
    }

    /**
     * 提交（二级）评论 接口
     * V1.1.6
     *
     * @return
     */
    public static final String submitCommentUrl() {
        return BASE_URL + "/video/doComment116.html";
    }

    /**
     * 个人空间评论
     *
     * @return
     */
    public static final String submitPersonCommentUrl() {
        return BASE_URL + "/member/memberReview.html";
    }

    /**
     * 圈子评论接口
     *
     * @return
     */
    public static final String submitGameCircleCommentUrl() {
        return BASE_URL + "/game/groupReview.html";
    }

    /**
     * 视频下载次数统计
     *
     * @param id
     * @return
     */
    public static final String getDownLoadUrl(String id) {
        return BASE_URL + "/video/downLoad.html?id=" + id;
    }

    /**
     * 视频点赞
     *
     * @param id
     * @return
     */
    public static final String getVedioFlowerUrl(String id, String member_id) {
        return BASE_URL + "/video/flower.html?id=" + id + "&member_id=" + member_id;
    }

    /**
     * 取消视频点赞
     *
     * @param id
     * @return
     */
    public static final String getVedioCancelFlowerUrl(String id, String member_id) {
        return BASE_URL + "/video/cancelFlower.html?id=" + id + "&member_id=" + member_id;
    }

    /**
     * 大神点赞接口
     *
     * @param id        玩家id
     * @param member_id 大神ID
     * @return
     */
    public static final String submitMasterPriseUrl(String id, String member_id) {
        return BASE_URL + "/member/like.html?id=" + id + "&member_id=" + member_id;
    }

    /**
     * 大神关注接口
     *
     * @param id
     * @param member_id
     * @return
     */
    public static final String submitMasterFocusUrl(String id, String member_id) {
        return BASE_URL + "/member/attention.html?id=" + id + "&member_id=" + member_id;
    }

    /**
     * 视频评论点赞
     *
     * @param comment_id 点赞对象的评论id
     * @param member_id  点赞对象的玩家id
     * @return
     */
    public static String getCommentPriseUrl(String comment_id, String member_id) {
        return BASE_URL + "/video/commentLike.html?comment_id=" + comment_id + "&member_id=" + member_id;
    }

    /**
     * 游戏圈子评论点赞接口
     *
     * @param review_id
     * @param member_id
     * @return
     */
    public static String getGameCirclePriseUrl(String review_id, String member_id) {
        return BASE_URL + "/game/reviewLike.html?review_id=" + review_id + "&member_id=" + member_id;
    }

    /**
     * 个人留言评论点赞
     *
     * @param comment_id
     * @param member_id
     * @return
     */
    public static String getPersonCommentPriseUrl(String comment_id, String member_id) {
        return BASE_URL + "/member/reviewLike.html?id=" + comment_id + "&member_id=" + member_id;
    }

    /**
     * 获取关键字
     * <p>
     * v1.1.4接口
     *
     * @return
     */
    public static final String getKeyWordUrlByPage() {
        return BASE_URL + "/search/keyWordList.html?page=1";
    }

    /**
     * 获取关键字
     * <p>
     * v1.1.5接口
     *
     * @return
     */
    public static final String getKeyWordUrlByNum() {
        return BASE_URL + "/search/keyWordListNew.html?num=9";
    }


    /**
     * 用户登录的路径
     *
     * @param key 手机号
     * @return
     */
    public static final String getLoginUrl(String key, String password) {
        return BASE_URL + "/member/loginNew.html?key=" + key + "&password=" + password;
    }

    /**
     * 用户注册路径
     *
     * @param key 手机号或邮箱
     * @return
     */
    public static final String getRegisterUrl(String key, String password) {
        return BASE_URL + "/member/registerNew.html?key=" + key + "&password=" + password + "&origin=a_sysj";
    }

    /**
     * 直接修改密码
     *
     * @param uid    用户id
     * @param oldpwd 原密码
     * @param newpwd 新密码
     * @return
     */
    public static final String directModifyPsdUrl(String uid, String oldpwd, String newpwd) {
        return BASE_URL + "/member/pwdModify?uid=" + uid + "&oldpwd=" + oldpwd + "&newpwd=" + newpwd;
    }

    /**
     * 验证修改密码
     *
     * @param uid
     * @param newpwd
     * @return
     */
    public static String verifyModifyPsdUrl(String uid, String newpwd) {
        return BASE_URL + "/member/pwdForget?uid=" + uid + "&newpwd=" + newpwd;
    }

    /**
     * 邮箱找回密码
     *
     * @param email
     * @return
     */
    public static String findPassWordUrl(String email) {
        return BASE_URL + "/member/findForEmail?email=" + email;
    }

    /**
     * 视频搜索的地址
     *
     * @param key
     * @param type
     * @param page
     * @return
     */
    public static final String getSearchVideoUrl(String key, String type, String page) {
        return BASE_URL + "/search/searchVideo.html?name=" + key + "&sort=" + type + "&page=" + page;
    }

    /**
     * 获取搜索联想词地址
     *
     * @return
     */
    public static final String getSearchKeyWord() {
        return BASE_URL + "/search/associate.html";
    }


    public static final String getGameCircleSearchKey(String keyWord) {
        return BASE_URL + "/search/groupSearch.html?keyWord=" + keyWord;
    }

    /**
     * 商务合作的路径
     *
     * @return
     */
    public static final String getBussinessUrl() {
        return BASE_URL + "/index/business.html";
    }

    /**
     * 返回默认礼包列表
     *
     * @param page
     * @return
     */
    public static final String getPackageList(String memerId, String page) {
        if (memerId.equals("")) {
            return BASE_URL + "/member/packageList.html?page=" + page;
        } else {
            return BASE_URL + "/member/packageList.html?page=" + page + "&member_id=" + memerId;
        }
    }

    /**
     * 返回默认任务列表
     * <p>
     * V1.1.4
     *
     * @return
     */
    public static final String getTaskList(String page) {
        if (!ExApplication.MEMBER_ID.equals("")) {
            return BASE_URL + "/member/taskListNew.html?page=" + page + "&member_id=" + ExApplication.MEMBER_ID;
        }
        return BASE_URL + "/member/taskListNew.html?page=" + page;
    }

    /**
     * 返回默认任务列表
     * <p>
     * V1.1.5，新增接受任务
     *
     * @return
     */
    public static final String getTaskList115(String page) {
        if (!ExApplication.MEMBER_ID.equals("")) {
            return BASE_URL + "/member/taskList115.html?member_id=" + ExApplication.MEMBER_ID + "&page=" + page;
        }
        return BASE_URL + "/member/taskList115.html?page=" + page;
    }

    /**
     * 接受任务
     * <p>
     *
     * @return {
     * "result": true,
     * "data": [],
     * "msg": "接受任务成功"
     * }
     */
    public static final String acceptTask(String task_id) {
        return BASE_URL + "/member/acceptTask.html?member_id=" + ExApplication.MEMBER_ID + "&task_id=" + task_id;
    }

    /**
     * 完成任务
     * V1.1.4
     *
     * @param id
     * @return
     */
    public static String completeMission(String id) {
        return BASE_URL + "/member/doTask.html?member_id=" + ExApplication.MEMBER_ID + "&task_id=" + id;
    }


    /**
     * 做任务方法接口
     * <p>
     * v1.1.5
     *
     * @return {
     * "result": true,
     * "data": [],
     * "msg": "任务操作成功"
     * }
     */
    public static final String doTask115(String task_id) {
        return BASE_URL + "/member/doTask115.html?member_id=" + ExApplication.MEMBER_ID + "&task_id=" + task_id;
    }

    /**
     * 做任务方法接口
     * <p>
     * v1.1.7
     *
     * @return {
     * "result": true,
     * "data": [],
     * "msg": "任务操作成功"
     * }
     */
    public static final String doTask117(String task_id) {
        return BASE_URL + "/member/doTask117.html?member_id=" + ExApplication.MEMBER_ID + "&task_id=" + task_id;
    }


    /**
     * 判断是否完成任务接口
     * <p>
     *
     * @return {
     * "result": true,
     * "data": [],
     * "msg": "任务完成成功"
     * }
     */
    public static final String finishTaskNew(String task_id) {
        return BASE_URL + "/member/finishTaskNew.html?member_id=" + ExApplication.MEMBER_ID + "&task_id=" + task_id;
    }

    /**
     * 领取任务奖励
     * <p>
     *
     * @param task_id
     * @return {
     * "result": true,
     * "data": [],
     * "msg": "领取经验成功"
     * }
     */
    public static final String getTaskReward(String task_id) {
        return BASE_URL + "/member/getExp.html?member_id=" + ExApplication.MEMBER_ID + "&task_id=" + task_id;
    }

    /**
     * 未完成任务个数接口
     *
     * @return {
     * "result": true,
     * "data": {
     * "itemsCount": 9
     * },
     * "msg": "获取数据成功"
     * }
     */
    public static final String getTaskUnDoNum() {
        return BASE_URL + "/member/taskUnDoNum?member_id=" + ExApplication.MEMBER_ID;
    }

    /**
     * 返回活动页面三个数据列表（活动、赛事、任务）
     *
     * @param page
     * @return
     */
    public static final String getActivityFrameList(String page) {
        if (!ExApplication.MEMBER_ID.equals("")) {
            return BASE_URL + "/member/taskList.html?page=" + page + "&member_id=" + ExApplication.MEMBER_ID + "&flag=activity";
        }
        return BASE_URL + "/member/taskList.html?page=" + page + "&flag=activity";
    }

    /**
     * 返回活动中心路径
     *
     * @param page
     * @return
     */
    public static final String getMoreActivityUrl(String page) {
        return BASE_URL + "/focuse/getFocuseList.html?page=" + page;
    }

    /**
     * 活动详情
     *
     * @param id
     * @return
     */
    public static final String getActivityDetailUrl(String id) {
        return BASE_URL + "/recommend/activityDetail.html?id=" + id;
    }

    /**
     * 活动信息
     * <p>
     * V1.1.5
     *
     * @param match_id
     * @return
     */
    public static final String getMatchInfoUrl(String match_id) {
        if (!ExApplication.MEMBER_ID.equals("")) {
            return BASE_URL + "/Match/matchInfo.html?match_id=" + match_id + "&member_id=" + ExApplication.MEMBER_ID;
        }
        return BASE_URL + "/Match/matchInfo.html?match_id=" + match_id;
    }

    /**
     * 活动信息 中 对应赛事的参赛视频列表信息
     * <p>
     * V1.1.5
     *
     * @param match_id
     * @return
     */
    public static final String getMatchVideoListUrl(String match_id, String page) {
        return BASE_URL + "/Match/matchVideoList.html?match_id=" + match_id + "&page=" + page;
    }

    /**
     * 返回赛事中心路径(旧接口)
     *
     * @param page
     * @return
     */
    public static final String getMoreMatchUrl(String page) {
        return BASE_URL + "/member/getMatchList.html?page=" + page;
    }

    /**
     * 返回 主页活动 热门活动 路径
     * V1.1.5
     *
     * @param page
     * @return
     */
    public static final String getHotMatchUrl(String page) {
        return BASE_URL + "/Match/getMatchList.html?page=" + page;
    }

    /**
     * 返回 主页活动 我的活动 路径
     * V1.1.5
     *
     * @param
     * @return
     */
    public static final String getMyMatchUrl() {
        return BASE_URL + "/Match/myMatchList.html?member_id=" + ExApplication.MEMBER_ID;
    }

    /**
     * 返回 参加赛事活动 路径
     * V1.1.5
     *
     * @param match_id
     * @return
     */
    public static final String getJoinMatchUrl(String match_id) {
        return BASE_URL + "/match/joinMatch.html?member_id=" + ExApplication.MEMBER_ID + "&match_id=" + match_id;
    }


    /**
     * 搜索礼包的路径
     *
     * @param name
     * @param page
     * @return
     */
    public static final String getSearchGiftUrl(String name, String page) {
        return BASE_URL + "/search/searchPackage.html?name=" + name + "&page=" + page;
    }


    /**
     * 返回搜索到的任务
     *
     * @param name
     * @param page
     * @return
     */
    public static final String getSearchMisssionUrl(String name, String page) {
        return BASE_URL + "/search/searchTask.html?name=" + name + "&page=" + page;
    }

    /**
     * 搜索用户昵称
     *
     * @param nickname
     * @return
     */
    public static final String getFindUser(String nickname, String page) {
        return BASE_URL + "/search/searchMember.html?name=" + nickname + "&page=" + page;
    }

    /**
     * 达人榜接口
     *
     * @param member_id
     * @param flag
     * @param page
     * @return
     */
    public static final String getExpertUrl(String member_id, String flag, String page) {
        return BASE_URL + "/member/daRenListNew.html?member_id=" + member_id + "&flag=" + flag + "&page=" + page;
    }

    /**
     * 关注列表
     *
     * @param member_id
     * @param page
     * @return
     */
    public static final String getMyAttentionUrl(String member_id, String page) {
        return BASE_URL + "/member/personalAttention.html?member_id=" + member_id + "&page=" + page;
    }

    /**
     * 粉丝列表
     *
     * @param member_id
     * @param page
     * @return
     */
    public static final String getMyFansUrl(String member_id, String page) {
        return BASE_URL + "/member/personalFans.html?member_id=" + member_id + "&page=" + page;
    }

    /**
     * 个人排名
     *
     * @return
     */
    public static final String getExpertRankUrl() {
        return BASE_URL + "/member/myDaRenList.html?member_id=" + ExApplication.MEMBER_ID;
    }

    /**
     * 游戏圈子玩家列表接口
     *
     * @param group_id
     * @param member_id
     * @param page
     * @return
     */
    public static final String getGameCircleUserUrl(String group_id, String member_id, String page) {
        return BASE_URL + "/game/groupMemberList.html?group_id=" + group_id + "&member_id=" + member_id + "&page=" + page;
    }


    /**
     * 关于我们
     *
     * @return
     */
    public static final String getAboutUsUrl() {
        return BASE_URL + "/index/aboutUs.html";
    }

    /**
     * 反馈的地址
     *
     * @return
     */
    public static final String getFeekbackUrl() {
        return BASE_URL + "/index/feedback.html";
    }

    /**
     * 获取验证码接口
     *
     * @return
     */
    public static final String getPhoneCodeUrl() {
        return BASE_URL + "/member/msgRequestNew.html";
    }

    /**
     * 手机注册接口
     *
     * @return
     */
    public static final String getPhoneRegisterUrl() {
        return BASE_URL + "/member/verifyCodeNew.html";
    }

    /**
     * 返回精彩推荐的地址
     * <p>
     * 1、有选择喜欢类型，获取数据成功
     * 2、没有选择喜欢类型，获取数据成功
     * <p>
     * V1.1.5
     *
     * @param
     * @return
     */
    public static final String getFavorRecommendUrl() {
        if (!ExApplication.MEMBER_ID.equals("")) {
            return BASE_URL + "/video/likeVideoList115.html?member_id=" + ExApplication.MEMBER_ID;
        }
        return BASE_URL + "/video/likeVideoList115.html";
    }

    /**
     * 大神详情头部信息接口
     *
     * @param id
     * @param member_id
     * @return
     */
    public static String getMasterHeadInfoUrl(String id, String member_id) {
        return BASE_URL + "/topic/specialTopic.html?id=" + id + "&member_id=" + member_id;
    }

    /**
     * 获取更多热门推荐视频的地址
     *
     * @return
     */
    public static String getMoreRecommendUrl(String page) {
        return BASE_URL + "/video/homeDaily.html?page=" + page;
    }

    /**
     * 返回主页 首页专栏 的地址
     * <p>
     * 热门视频
     * 视界专栏 （舞大大学堂/小小舞玩新游/阿沫爱品评）
     * 热门游戏、热门分类
     * <p>
     * page从1开始计算
     * <p>
     * V1.1.6
     *
     * @return
     */
    public static String getHomeColumnUrl(String page) {
        return BASE_URL + "/index/appIndexList.html?page=" + page;
    }

    /**
     * 主页首页 首页专栏 更多 地址
     * <p>
     * 热门视频
     * 视界专栏 （舞大大学堂/小小舞玩新游/阿沫爱品评）
     * 热门游戏、热门分类
     * <p>
     * page从1开始计算
     * <p>
     * V1.1.6
     * sort空为time，page空为1
     *
     * @param more_mark
     * @param sort
     * @param page
     * @return
     */
    public static String getMoreHomeColumnUrl(String more_mark, String sort, String page) {
        return BASE_URL + "/index/appIndexMore.html?more_mark=" + more_mark + "&sort=" + sort + "&page=" + page;
    }

    /**
     * 轻松一刻接口
     *
     * @param page
     * @return
     */
    public static String getRelaxeVideoUrl(String page) {
        return BASE_URL + "/video/relaxedMoment.html?page=" + page;
    }

    /**
     * 推荐ing
     *
     * @param page
     * @return
     */
    public static String getRecommendingUrl(String page) {
        return BASE_URL + "/recommend/recommendList.html?page=" + page;
    }

    /**
     * 启动页 图片（广告位） 路径
     *
     * @return
     */
    public static String getLaunchImageUrl(String time) {
        return BASE_URL + "/index/launchImage.html?target=a_sysj&time=" + time;
    }

    /**
     * 主页 首页广告
     *
     * @return
     */
    public static String getHomeAdUrl() {
        return BASE_URL + "/recommend/homeHd.html";
    }

    /**
     * 获取广告位的路径
     *
     * @return
     */
    public static String getAdVedioUrl() {
        return BASE_URL + "/focuse/list.html";
    }

    /**
     * 玩家秀 滚动广告位 路径
     *
     * @return
     */
    public static String getAdPlayerShowUrl() {
        return BASE_URL + "/recommend/playerShow.html";
    }

    /**
     * Banner广告位 路径
     *
     * @return
     */
    public static String getBannerAdUrl(String situation) {
        return BASE_URL + "/video/uplaodImage.html?situation=" + situation;
    }

    /**
     * 获取礼包的路径
     *
     * @return
     */
    public static String getGiftUrl() {
        return BASE_URL + "/member/packageList.html";
    }

    /**
     * 视频详情的路径
     *
     * @param id
     * @return
     */
    public static String getVedioUrl(String id, String member_id) {
        return BASE_URL + "/video/detail.html?id=" + id + "&member_id=" + member_id;
    }

    /**
     * 玩家秀视频列表
     *
     * @return
     */
    public static String getPlayerShowUrl(String page) {
        return BASE_URL + "/recommend/playerShowList.html?page=" + page;
    }

    /**
     * 大神详情页大神更新接口
     *
     * @param id
     * @param page
     * @return
     */
    public static String getMasterUpdateUrl(String id, String page) {
        return BASE_URL + "/topic/topicVideoList.html?id=" + id + "&page=" + page;
    }

    /**
     * 发现视频列表接口
     *
     * @return
     */
    public static String getDiscoverVideoUrl() {
        return BASE_URL + "/video/everyoneLook.html";
    }

    /**
     * 下一个视频接口
     *
     * @param id
     * @return
     */
    public static String getNextVideoUrl(String id) {
        return BASE_URL + "/video/playNext.html?id=" + id;
    }

    /**
     * 获取 评论列表 的路径
     *
     * @param id
     * @param page
     * @return
     */
    public static String getCommentUrl(String id, String page) {
        return BASE_URL + "/video/commentList.html?id=" + id + "&page=" + page + "&member_id=" + ExApplication.MEMBER_ID;
    }

    /**
     * 个人空间评论
     *
     * @param id
     * @param page
     * @return
     */
    public static String getPersonCommentUrl(String member_id, String page, String id) {
        return BASE_URL + "/member/memberReviewList.html?member_id=" + member_id + "&page=" + page + "&id=" + id;
    }

    /**
     * 游戏圈子评论列表
     *
     * @param group_id
     * @param member_id
     * @param page
     * @return
     */
    public static String getGameCircleCommentUrl(String group_id, String member_id, String page) {
        return BASE_URL + "/game/reviewList.html?group_id=" + group_id + "&member_id=" + member_id + "&page=" + page;
    }

    /**
     * 获取用户上传的视频ID
     *
     * @param id
     * @return
     */
    public static final String getUserVideoId(String id) {
        return BASE_URL + "/Video/getVidById.html?member_id=" + id;
    }

    /**
     * get请求
     *
     * @param url
     * @return
     */
    public static String httpGet(String url) {
        String str = "";
        try {
            HttpGet request = new HttpGet(url);
            HttpClient httpClient = new DefaultHttpClient();////生成一个http客户端对象
            HttpResponse response = httpClient.execute(request);//http客户端执行请求，得到http响应
            if (response.getStatusLine().getStatusCode() == 200) {
                str = EntityUtils.toString(response.getEntity());//response.getEntity()  从http响应中得到元素
            }
        } catch (Exception e) {
            Log.e("httpGet", e.toString());
            str = "";
        }
        Log.e("HttpGet_Response", str);
        return str;
    }

    /**
     * 获取搜索联想关键词的get请求
     *
     * @param url
     * @param keyWord
     * @return
     */
    public static String[] httpGet(String url, String keyWord) {
        String str = "";
        String[] word = {};
        HttpGet request = new HttpGet(url + "?keyWord=" + keyWord);
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                str = EntityUtils.toString(response.getEntity());
                JSONObject object = new JSONObject(str);
                JSONArray array = object.getJSONArray("data");
                word = new String[array.length()];
                for (int i = 0; i < array.length(); i++) {
                    object = array.getJSONObject(i);
                    word[i] = object.getString("name");
                }

            }
        } catch (Exception e) {
            Log.e("httpGet", e.toString());
            str = "";
        }
        Log.e("HttpGet_Response", str);
        return word;
    }

    /**
     * post请求
     *
     * @param param  提交字段
     * @param params 路径
     * @return
     */
    public static String httpPost(List<NameValuePair> param, String... params) {
        String rs = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();////生成一个http客户端对象
            HttpParams httpparams = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpparams, 5000);//设置连接超时时间
            HttpConnectionParams.setSoTimeout(httpparams, 5000);//设置请求超时
            HttpPost httpPost = new HttpPost(params[0]);//客户端向服务器发送请求,返回一个响应对象
            httpPost.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
            HttpResponse response = new DefaultHttpClient().execute(httpPost);
//            HttpResponse response2 = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                rs = EntityUtils.toString(response.getEntity());
            }

        } catch (Exception e) {
            Log.e("httpPost", e.toString());
            rs = "";
        }
        Log.e("HttpPost_Response", rs);
        return rs;

    }

    /**
     * 提交用户上传视频信息的Post请求
     *
     * @param member_id        用户id
     * @param video_id         视频id
     * @param video_name       视频标题
     * @param game_name        游戏名
     * @param time_length      视频时长
     * @param postVideoInfoUrl post请求地址
     * @return
     */
    public static String httpPost(String member_id, String video_id,
                                  String video_name, String game_name, String time_length, String match_id, String phone, String qq, String postVideoInfoUrl) {
        String rs = "";
        HttpPost httpPost = new HttpPost(postVideoInfoUrl);
        HttpClient httpClient = new DefaultHttpClient();
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("member_id", member_id));
        pairs.add(new BasicNameValuePair("video_id", video_id));
        pairs.add(new BasicNameValuePair("name", video_name));
        pairs.add(new BasicNameValuePair("game_name", game_name));
        pairs.add(new BasicNameValuePair("time_length", time_length));
        if (match_id != null) {
            pairs.add(new BasicNameValuePair("match_id", match_id));
            pairs.add(new BasicNameValuePair("phone", phone));
            pairs.add(new BasicNameValuePair("qq", qq));
        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
            HttpResponse response = httpClient.execute(httpPost);
            System.out.println(response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == 200) {
                rs = EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            e.printStackTrace();
            rs = "";
        }
        return rs;
    }

    /**
     * post请求
     *
     * @param params 路径
     * @return
     */
    public static String httpPost(String name, String descripe, String time, String id, String fileUrl, String... params) {
        String rs = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();////生成一个http客户端对象
            HttpParams httpparams = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpparams, 20000);//设置连接超时时间
            HttpConnectionParams.setSoTimeout(httpparams, 600000);//设置请求超时
            HttpPost post = new HttpPost(params[0]);//客户端向服务器发送请求,返回一个响应对象

            MultipartEntity entity = new MultipartEntity();
//            entity.addPart("data",new StringBody(content));

            if (!fileUrl.equals("")) {
                File file = new File(fileUrl);
                Log.e("Img", fileUrl);
                entity.addPart("files", new FileBody(file));
            }

            entity.addPart("member_id", new StringBody(ExApplication.MEMBER_ID));
            entity.addPart("name", new StringBody(name));
            entity.addPart("url", new StringBody(id));
            entity.addPart("descriptoin", new StringBody(descripe));
            entity.addPart("time_length", new StringBody(time));
            post.setEntity(entity);
//            post.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
            HttpResponse response = new DefaultHttpClient().execute(post);
            if (response.getStatusLine().getStatusCode() == 200) {
                rs = EntityUtils.toString(response.getEntity());
            }

        } catch (Exception e) {
            Log.e("httpPost", e.toString());
            rs = "";
        }
        Log.e("HttpPost_Response", rs);
        return rs;

    }


    /**
     * 上传文件
     *
     * @param context
     * @return
     */
    public static String postImage(Context context, String httpUrl, String fileUrl) {

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(httpUrl);

            MultipartEntity entity = new MultipartEntity();
//            entity.addPart("data",new StringBody(content));

            if (!fileUrl.equals("")) {
                File file = new File(fileUrl);
                Log.e("headImg", fileUrl);
                entity.addPart("upFile", new FileBody(file));
            }
            postRequest.setEntity(entity);
            HttpResponse response = httpClient.execute(postRequest);
            if (response.getStatusLine().getStatusCode() == 200) {
                String res = EntityUtils.toString(response.getEntity());
                Log.e("response", res);
//               return EncryptUtil.decrypt(EntityUtils.toString(response.getEntity()));
                return res;
            }

        } catch (Exception e) {
            Log.e("response_exception", e.toString());
            return "";
        }
        return "";
    }

    // 请求上传凭证接口
    public static final String PostVideoEntityBackToke() {
        return "http://apps.ifeimo.com/home/video/doVideoMark202.html";
    }

    // 请求上传凭证
    public static String httpPostVideoEntityBackToke(List<NameValuePair> pairs, String postUrl) {
        String rs = "";
        HttpPost httpPost = new HttpPost(postUrl);
        HttpClient httpClient = new DefaultHttpClient();
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                rs = EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            e.printStackTrace();
            rs = "";
        }
        return rs;
    }


    //提交上传结果接口
    public static final String PostVideoUploadResult() {
        return "http://apps.ifeimo.com/home/video/qiniuTokenPass202.html";
    }

    /**
     * @param context
     * @param httpUrl    接口
     * @param memberID   用户
     * @param videokey   七牛上的视频id
     * @param timeLength 时长
     * @param isSuccess  是否上传成功(1为上成功，0为上传失败)
     * @param fileUrl    封面在本地的位置
     * @return
     */
    public static String httpPostVideoUpoloadResult(Context context, String httpUrl,
                                                    String memberID, String videokey, String timeLength,
                                                    String isSuccess, String fileUrl) {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(httpUrl);
            MultipartEntity entity = new MultipartEntity();
            entity.addPart("member_id",
                    new StringBody(memberID, Charset.forName("utf-8")));
            entity.addPart("videokey",
                    new StringBody(videokey, Charset.forName("utf-8")));
            entity.addPart("time_length",
                    new StringBody(timeLength, Charset.forName("utf-8")));
            entity.addPart("is_success",
                    new StringBody(isSuccess, Charset.forName("utf-8")));

            if (!fileUrl.equals("")) {
                File file = new File(fileUrl);
                Log.e("headImg", fileUrl);
                entity.addPart("flag", new FileBody(file));
            }
            postRequest.setEntity(entity);
            HttpResponse response = httpClient.execute(postRequest);
            if (response.getStatusLine().getStatusCode() == 200) {
                String res = EntityUtils.toString(response.getEntity());
                Log.e("response", res);
                return res;
            }
        } catch (Exception e) {
            Log.e("response_exception", e.toString());
            return "";
        }
        return "";
    }


    // 请求上传结果
    public static String httpPostVideoUpoloadResult(List<NameValuePair> pairs,
                                                    String postUrl) {
        String rs = "";
        HttpPost httpPost = new HttpPost(postUrl);
        HttpClient httpClient = new DefaultHttpClient();
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                rs = EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            e.printStackTrace();
            rs = "";
        }
        return rs;
    }


    /**
     * 上传视频封面
     *
     * @return
     */
    public static final String getuploadVideoCover(String videoKey) {
        return "http://apps.ifeimo.com/home/video/uploadPicQiniu.html?videokey=" + videoKey;
    }

    /**
     * 上传文件
     *
     * @param context
     * @return
     */
    public static String postImage(Context context, String videokey, String httpUrl, String fileUrl) {

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(httpUrl);

            MultipartEntity entity = new MultipartEntity();
//            entity.addPart("data",new StringBody(content));

            if (!fileUrl.equals("")) {
                File file = new File(fileUrl);
                Log.e("headImg", fileUrl);
                entity.addPart("upFile", new FileBody(file));
            }
            postRequest.setEntity(entity);
            HttpResponse response = httpClient.execute(postRequest);
            if (response.getStatusLine().getStatusCode() == 200) {
                String res = EntityUtils.toString(response.getEntity());
                Log.e("response", res);
//               return EncryptUtil.decrypt(EntityUtils.toString(response.getEntity()));
                return res;
            }

        } catch (Exception e) {
            Log.e("response_exception", e.toString());
            return "";
        }
        return "";
    }

}
