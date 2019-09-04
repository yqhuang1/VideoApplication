package com.li.videoapplication.entity;

/**
 * 离线下载到本地的视频
 * Created by li on 2014/10/13.
 */
public class DownloadVideo {

    /**
     * 判断图片是否被选中
     */
    private String isCheck = "false";

    /**
     * 视频id
     */
    private String video_id = "";

    /**
     * 视频名称（保存的名称）
     */
    private String name = "";
    /**
     * 标题
     */
    private String title = "";
    /**
     * 图片路径
     */
    private String imgUrl = "";
    /**
     * 下载视频的路径
     */
    private String downloadUrl = "";
    /**
     * 存放在本地的路径
     */
    private String playUrl = "";
    /**
     * 视频的qn_key
     */
    private String qn_key = "";
    /**
     * 视频的youku_url
     */
    private String youku_url = "";
    /**
     * 视频的下载状态
     * 下载中：LOADING
     * 下载完成：SUCCESS
     * 下载失败：FAILURE
     */
    private String download_state = "";

    public String getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(String isCheck) {
        this.isCheck = isCheck;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getQn_key() {
        return qn_key;
    }

    public void setQn_key(String qn_key) {
        this.qn_key = qn_key;
    }

    public String getYouku_url() {
        return youku_url;
    }

    public void setYouku_url(String youku_url) {
        this.youku_url = youku_url;
    }

    public String getDownload_state() {
        return download_state;
    }

    public void setDownload_state(String download_state) {
        this.download_state = download_state;
    }

    @Override
    public String toString() {
        return "DownloadVideo{" +
                "isCheck='" + isCheck + '\'' +
                ", video_id='" + video_id + '\'' +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", playUrl='" + playUrl + '\'' +
                ", qn_key='" + qn_key + '\'' +
                ", youku_url='" + youku_url + '\'' +
                ", download_state='" + download_state + '\'' +
                '}';
    }
}
