package com.li.videoapplication.entity;

/**
 * Created by li on 2014/8/18.
 */
public class UserEntity {

    private String imgPath = "";
    /**
     * 用户昵称
     */
    private String title = "";
    private String introduce = "";
    private String time = "";
    private String like_gametype = "";
    private String isAdmin = "";
    /**
     * 排名
     */
    private String grace = "";
    private String id = "";
    private String address = "";
    private String mobile = "";//手机
    private String QQ = "";//QQ
    private String name = "";
    private int degree = 1;//玩家等级
    private String rank = "";
    private String sex = ""; //1男 2女
    private String uploadVideoCount = "";
    private String openId = "";
    private String password = "";
    private String honour = "";//荣耀、称号
    private int member_rank = 1;
    private int member_exp = 0;//玩家当前总经验
    private int next_exp = 0;//玩家下一级总经验
    private String email = "";
    private String isTelpass = "";//是否绑定了手机
    private String isMailpass = "";//是否绑定了邮箱

    private String attention = "";//关注数
    private String fans = "";//粉丝数
    private int mark = 0;//是否关注标记，0表示未关注用户，1表示已关注

    private String misstion_count = ""; //未完成任务数
    private String msgNum = "";//消息总数
    private String signature = "";//个性签名

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getMisstion_count() {
        return misstion_count;
    }

    public void setMisstion_count(String misstion_count) {
        this.misstion_count = misstion_count;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public String getAttention() {
        return attention;
    }

    public void setAttention(String attention) {
        this.attention = attention;
    }

    public String getFans() {
        return fans;
    }

    public void setFans(String fans) {
        this.fans = fans;
    }

    private int[] likeArray = new int[4];

    public int[] getLikeArray() {
        return likeArray;
    }

    public void setLikeArray(int[] likeArray) {
        this.likeArray = likeArray;
    }

    public String getIsMailpass() {
        return isMailpass;
    }

    public void setIsMailpass(String isMailpass) {
        this.isMailpass = isMailpass;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIsTelpass() {
        return isTelpass;
    }

    public void setIsTelpass(String isTelpass) {
        this.isTelpass = isTelpass;
    }

    public String getHonour() {
        return honour;
    }

    public void setHonour(String honour) {
        this.honour = honour;
    }

    public int getMember_rank() {
        return member_rank;
    }

    public void setMember_rank(int member_rank) {
        this.member_rank = member_rank;
    }

    public int getMember_exp() {
        return member_exp;
    }

    public void setMember_exp(int member_exp) {
        this.member_exp = member_exp;
    }

    public int getNext_exp() {
        return next_exp;
    }

    public void setNext_exp(int next_exp) {
        this.next_exp = next_exp;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getQQ() {
        return QQ;
    }

    public void setQQ(String QQ) {
        this.QQ = QQ;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getUploadVideoCount() {
        return uploadVideoCount;
    }

    public void setUploadVideoCount(String uploadVideoCount) {
        this.uploadVideoCount = uploadVideoCount;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getGrace() {
        return grace;
    }

    public void setGrace(String grace) {
        this.grace = grace;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLike_gametype() {
        return like_gametype;
    }

    public void setLike_gametype(String like_gametype) {
        this.like_gametype = like_gametype;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }
}
