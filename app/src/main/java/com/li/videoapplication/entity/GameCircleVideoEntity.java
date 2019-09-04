package com.li.videoapplication.entity;

/**
 * Created by feimoyuangong on 2015/7/4.
 */
public class GameCircleVideoEntity {

    private String avatar;
    private String nickname;
    private String video_name;
    private String video_time;
    private String view_count;
    private String flower_count;
    private String comment_count;
    private String time_length;
    private String flagPath;
    private String attentionMark; //视频作者关注标志
    private String collectionMark; //视频收藏标志
    private String flowerMark;   //视频点赞标志
    private String member_id;   //视频点赞标志
    private String video_id;

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getVideo_name() {
        return video_name;
    }

    public void setVideo_name(String video_name) {
        this.video_name = video_name;
    }

    public String getVideo_time() {
        return video_time;
    }

    public void setVideo_time(String video_time) {
        this.video_time = video_time;
    }

    public String getView_count() {
        return view_count;
    }

    public void setView_count(String view_count) {
        this.view_count = view_count;
    }

    public String getFlower_count() {
        return flower_count;
    }

    public void setFlower_count(String flower_count) {
        this.flower_count = flower_count;
    }

    public String getComment_count() {
        return comment_count;
    }

    public void setComment_count(String comment_count) {
        this.comment_count = comment_count;
    }

    public String getTime_length() {
        return time_length;
    }

    public void setTime_length(String time_length) {
        this.time_length = time_length;
    }

    public String getFlagPath() {
        return flagPath;
    }

    public void setFlagPath(String flagPath) {
        this.flagPath = flagPath;
    }

    public String getAttentionMark() {
        return attentionMark;
    }

    public void setAttentionMark(String attentionMark) {
        this.attentionMark = attentionMark;
    }

    public String getCollectionMark() {
        return collectionMark;
    }

    public void setCollectionMark(String collectionMark) {
        this.collectionMark = collectionMark;
    }

    public String getFlowerMark() {
        return flowerMark;
    }

    public void setFlowerMark(String flowerMark) {
        this.flowerMark = flowerMark;
    }
}
