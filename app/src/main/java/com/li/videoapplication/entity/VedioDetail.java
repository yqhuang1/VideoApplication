package com.li.videoapplication.entity;

/**
 * 视频详情类
 * Created by li on 2014/9/23.
 */
public class VedioDetail {

    private String url = "";
    private String id = "";
    private String type_id = "";
    private String game_id = "";
    private String member_id = "";
    private String name = "";
    private String descriptioin = "";
    private String content = "";
    private String view_count = "";
    private String download_count = "";
    private String flower_count = "";
    private String collection_count = "";
    private String comment_count = "";
    private String time = "15";
    private String upload_time = "";
    private String time_length = "";
    private String type_name = "";
    private String flagPath = "";
    private String pic_flsp = "";
    private String gameDownloadUrl = "";
    private String userName = "";
    private String avatar = "";
    private String link = "";
    private String description = "";
    private String qn_key = "";
    private int flower_mark = 0;//0未点赞，1已点赞
    private int collection_mark = 0;//0未收藏，1已收藏
    private int status = 0;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getFlower_mark() {
        return flower_mark;
    }

    public void setFlower_mark(int flower_mark) {
        this.flower_mark = flower_mark;
    }

    public int getCollection_mark() {
        return collection_mark;
    }

    public void setCollection_mark(int collection_mark) {
        this.collection_mark = collection_mark;
    }

    public String getQn_key() {
        return qn_key;
    }

    public void setQn_key(String qn_key) {
        this.qn_key = qn_key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGameDownloadUrl() {
        return gameDownloadUrl;
    }

    public void setGameDownloadUrl(String gameDownloadUrl) {
        this.gameDownloadUrl = gameDownloadUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getGame_id() {
        return game_id;
    }

    public void setGame_id(String game_id) {
        this.game_id = game_id;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescriptioin() {
        return descriptioin;
    }

    public void setDescriptioin(String descriptioin) {
        this.descriptioin = descriptioin;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getView_count() {
        return view_count;
    }

    public void setView_count(String view_count) {
        this.view_count = view_count;
    }

    public String getDownload_count() {
        return download_count;
    }

    public void setDownload_count(String download_count) {
        this.download_count = download_count;
    }

    public String getFlower_count() {
        return flower_count;
    }

    public void setFlower_count(String flower_count) {
        this.flower_count = flower_count;
    }

    public String getCollection_count() {
        return collection_count;
    }

    public void setCollection_count(String collection_count) {
        this.collection_count = collection_count;
    }

    public String getComment_count() {
        return comment_count;
    }

    public void setComment_count(String comment_count) {
        this.comment_count = comment_count;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUpload_time() {
        return upload_time;
    }

    public void setUpload_time(String upload_time) {
        this.upload_time = upload_time;
    }

    public String getTime_length() {
        return time_length;
    }

    public void setTime_length(String time_length) {
        this.time_length = time_length;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public String getPic_flsp() {
        return pic_flsp;
    }

    public void setPic_flsp(String pic_flsp) {
        this.pic_flsp = pic_flsp;
    }

    public String getFlagPath() {
        return flagPath;
    }

    public void setFlagPath(String flagPath) {
        this.flagPath = flagPath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "VedioDetail{" +
                "url='" + url + '\'' +
                ", id='" + id + '\'' +
                ", type_id='" + type_id + '\'' +
                ", game_id='" + game_id + '\'' +
                ", member_id='" + member_id + '\'' +
                ", name='" + name + '\'' +
                ", descriptioin='" + descriptioin + '\'' +
                ", content='" + content + '\'' +
                ", view_count='" + view_count + '\'' +
                ", download_count='" + download_count + '\'' +
                ", flower_count='" + flower_count + '\'' +
                ", collection_count='" + collection_count + '\'' +
                ", comment_count='" + comment_count + '\'' +
                ", time='" + time + '\'' +
                ", upload_time='" + upload_time + '\'' +
                ", time_length='" + time_length + '\'' +
                ", type_name='" + type_name + '\'' +
                ", flagPath='" + flagPath + '\'' +
                ", gameDownloadUrl='" + gameDownloadUrl + '\'' +
                ", userName='" + userName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", link='" + link + '\'' +
                ", description='" + description + '\'' +
                ", qn_key='" + qn_key + '\'' +
                ", flower_mark=" + flower_mark +
                ", collection_mark=" + collection_mark +
                '}';
    }
}
