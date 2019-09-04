package com.li.videoapplication.entity;

import java.io.Serializable;

public class VideoEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    private String isCheck = "false";

    private String id = "";
    private String name = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String type_id;

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    private String title = "";
    private String title_content = "";
    //时长
    private String time = "";
    private String upload_time = "";
    private String simg_url = "";
    private String bimg_url = "";
    private String all_content = "";
    private String viewCount = "";
    private String flower = "";
    private String comment = "";
    private String playUrl = "";

    public String getView_count() {
        return view_count;
    }

    public void setView_count(String view_count) {
        this.view_count = view_count;
    }

    private String url;
    private String qn_key;

    public String getQn_key() {
        return qn_key;
    }

    public void setQn_key(String qn_key) {
        this.qn_key = qn_key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 品靓点字段
     *
     * @return
     */
    private String comment_count = "0";
    private String flagPath = "";
    private String flower_count = "0";
    private String view_count = "0";
    private String share_count = "0";

    public String getShare_count() {
        return share_count;
    }

    public void setShare_count(String share_count) {
        this.share_count = share_count;
    }

    public String getUpload_time() {
        return upload_time;
    }

    public void setUpload_time(String upload_time) {
        this.upload_time = upload_time;
    }

    public String getComment_count() {
        return comment_count;
    }

    public void setComment_count(String comment_count) {
        this.comment_count = comment_count;
    }

    public String getFlagPath() {
        return flagPath;
    }

    public void setFlagPath(String flagPath) {
        this.flagPath = flagPath;
    }

    public String getFlower_count() {
        return flower_count;
    }

    public void setFlower_count(String flower_count) {
        this.flower_count = flower_count;
    }

    public String getViewCount() {
        return viewCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle_content() {
        return title_content;
    }

    public void setTitle_content(String title_content) {
        this.title_content = title_content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSimg_url() {
        return simg_url;
    }

    public void setSimg_url(String simg_url) {
        this.simg_url = simg_url;
    }

    public String getBimg_url() {
        return bimg_url;
    }

    public void setBimg_url(String bimg_url) {
        this.bimg_url = bimg_url;
    }

    public String getAll_content() {
        return all_content;
    }

    public void setAll_content(String all_content) {
        this.all_content = all_content;
    }


    public String getFlower() {
        return flower;
    }

    public void setFlower(String flower) {
        this.flower = flower;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(String isCheck) {
        this.isCheck = isCheck;
    }

}
