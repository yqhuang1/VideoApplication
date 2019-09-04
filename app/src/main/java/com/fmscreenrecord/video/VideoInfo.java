package com.fmscreenrecord.video;

import java.io.Serializable;

public class VideoInfo implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String DisplayName; // 文件名

    private String Path; // 路径

    private int mtime;

    private long duration; // 时长

    private String thumbnail; // 缩略图位置

    private String title;// 标题


    private String gamename;//上传时游戏类型名
    private String upvideotitle;//上传时的游戏标题
    private double precent;//上传时进度

    private String videodescribe;//视频描述

    private String videoPlayChannel;//播放平台（七牛/优酷)


    /**
     * * 手游视界 ****
     */
    // 播放次数
    private String ViewCount;
    // 点赞次数
    private String FlowerCount;
    // 评论数
    private String CommentCount;
    private String TimeLength;

    //游戏类型ID
    private String GameTypeId;
    //视频上传到云端时间
    private String UploadTime;


    // 视频播放链接
    private String VideoURL; // 上传后的视频在服务器上的链接
    private String VideoId; // 数据库中的视频id
    private String videoSource; // rec/ext
    private String videoStation; // loc/ser/uploading
    // 图片链接
    private String imageUrl;
    private int id;

    //视频上传七牛token
    private String Token;
    //视频上传七牛token的时间(毫秒)
    private long TokenTime;

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public long getTokenTime() {
        return TokenTime;
    }

    public void setTokenTime(long tokenTime) {
        TokenTime = tokenTime;
    }


    public int getId() {
        return id;
    }

    public String getUpvideotitle() {
        return upvideotitle;
    }

    public void setUpvideotitle(String upvideotitle) {
        this.upvideotitle = upvideotitle;
    }

    public double getPrecent() {
        return precent;
    }

    public void setPrecent(double precent) {
        this.precent = precent;
    }

    public String getVideoPlayChannel() {
        return videoPlayChannel;
    }

    public void setVideoPlayChannel(String videoPlayChannel) {
        this.videoPlayChannel = videoPlayChannel;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGameTypeId() {
        return GameTypeId;
    }

    public String getUploadTime() {
        return UploadTime;
    }

    public String getGamename() {
        return gamename;
    }

    public void setGamename(String gamename) {
        this.gamename = gamename;
    }

    public String getVideodescribe() {
        return videodescribe;
    }

    public void setVideodescribe(String videodescribe) {
        this.videodescribe = videodescribe;
    }

    public void setUploadTime(String uploadTime) {
        UploadTime = uploadTime;
    }

    public void setGameTypeId(String gameTypeId) {
        GameTypeId = gameTypeId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    private String VideoSize;

    public String getVideoURL() {
        return VideoURL;
    }

    public void setVideoURL(String videoURL) {
        VideoURL = videoURL;
    }

    public int getTime() {
        return mtime;
    }

    public void setTime(int time) {
        mtime = time;
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String path) {
        Path = path;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoId() {
        return VideoId;
    }

    public void setVideoId(String videoId) {
        VideoId = videoId;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getVideoStation() {
        return videoStation;
    }

    public void setVideoStation(String videoStation) {
        this.videoStation = videoStation;
    }

    public String getVideoSource() {
        return videoSource;
    }

    public void setVideoSource(String videoSource) {
        this.videoSource = videoSource;
    }

    public String getVideoSize() {
        return VideoSize;
    }

    public void setVideoSize(String VideoSize) {
        this.VideoSize = VideoSize;
    }

    private int progress = 0;

    public int getVideoUploadProgress() {
        return progress;
    }

    public void setVideoUploadProgress(int progress) {
        this.progress = progress;
    }

    public String getViewCount() {
        return ViewCount;
    }

    public void setViewCount(String viewCount) {
        ViewCount = viewCount;
    }

    public String getFlowerCount() {
        return FlowerCount;
    }

    public void setFlowerCount(String flowerCount) {
        FlowerCount = flowerCount;
    }

    public String getCommentCount() {
        return CommentCount;
    }

    public void setCommentCount(String commentCount) {
        CommentCount = commentCount;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    public String getTimeLength() {
        return TimeLength;
    }

    public void setTimeLength(String timeLength) {
        TimeLength = timeLength;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        VideoInfo other = (VideoInfo) obj;
        if (id != other.id)
            return false;
        return true;
    }

}