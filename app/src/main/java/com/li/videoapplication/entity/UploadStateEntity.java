package com.li.videoapplication.entity;

/**
 * Created by Leo on 2015/7/24.
 */
public class UploadStateEntity {

    private String localPath;  //本地视频路径
    private String coludPath;  //云端视频路径
    private String title;   //上传视频标题
    private String gameName;  //上传视频游戏名
    private String videoId;   //上传视频ID
    private int state;  //0未上传，-1中断上传，1上传完成
    private String flagPath;  //上传图片路径
    private double percent;  //上传进度

    public String getColudPath() {
        return coludPath;
    }

    public void setColudPath(String coludPath) {
        this.coludPath = coludPath;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getFlagPath() {
        return flagPath;
    }

    public void setFlagPath(String flagPath) {
        this.flagPath = flagPath;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    @Override
    public String toString() {
        return "UploadStateEntity{" +
                "localPath='" + localPath + '\'' +
                ", coludPath='" + coludPath + '\'' +
                ", title='" + title + '\'' +
                ", gameName='" + gameName + '\'' +
                ", videoId='" + videoId + '\'' +
                ", state=" + state +
                ", flagPath='" + flagPath + '\'' +
                ", percent=" + percent +
                '}';
    }
}
