package com.li.videoapplication.entity;

/**
 * Created by feimoyuangong on 2015/5/12.
 */
public class DiscoverVideoEntity {
    private String flagPath="";
    private String name="";
    private String nickname="";
    private String qn_key="";
    private String url="";
    private String avatar="";
    private String flower_count="";
    private String comment_count="";
    private String id="";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFlagPath() {
        return flagPath;
    }

    public void setFlagPath(String flagPath) {
        this.flagPath = flagPath;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public String getNickName() {
        return nickname;
    }

    public void setNickName(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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
}
