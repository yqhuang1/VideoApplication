package com.li.videoapplication.entity;

/**
 * 评论列表数据
 * Created by li on 2014/8/18.
 */
public class CommentEntity {

    private String imgPath = "";
    private String name = "";//进行评论的玩家昵称
    private String time = "";//评论的时间
    private String time_x = "";//评论的时间戳
    private String content = "";//评论的内容
    private String honour = "";
    private int level = 1;
    private String itemsCount = "";//视频总的评论数
    private String memberId = "";//被评论的用户id
    private String comment_id = "";//评论的id
    private String review_id = "";//个人评论id
    private String like = "0";//评论被点赞数
    private String mark = "0";//登录用户是否点过赞（0表示未点过赞，1表示点过赞）。
    private String likeMark = "0";//个人信息页评论点赞标志（0表示未点过赞，1表示点过赞）。
    private String likeNum = "0";//个人信息页评论点赞次数

    public String getLikeMark() {
        return likeMark;
    }

    public void setLikeMark(String likeMark) {
        this.likeMark = likeMark;
    }

    public String getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(String likeNum) {
        this.likeNum = likeNum;
    }

    public String getReview_id() {
        return review_id;
    }

    public void setReview_id(String review_id) {
        this.review_id = review_id;
    }

    public String getItemsCount() {
        return itemsCount;
    }

    public void setItemsCount(String itemsCount) {
        this.itemsCount = itemsCount;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String menberId) {
        this.memberId = menberId;
    }

    public String getHonour() {
        return honour;
    }

    public void setHonour(String honour) {
        this.honour = honour;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime_x() {
        return time_x;
    }

    public void setTime_x(String time_x) {
        this.time_x = time_x;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
