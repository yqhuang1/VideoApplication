package com.li.videoapplication.entity;

/**
 * Created by feimoyuangong on 2015/6/6.
 */
public class MasterEntity {
    private String flagPath;
    private String nickname;
    private String manifesto;//大师宣言
    private String member_id;
    private String url;
    private int mark=0; //关注标记，0为未关注，1为关注

    /**
     * 大师详情页添加字段
     * @return
     */
    private String topic_avatar;//头像
    private String hobby;//爱好
    private String game_career;//游戏生涯
    private String often_game;//常玩游戏
    private String praise;//点赞数
    private String fans;//粉丝数

    public int getLikeMark() {
        return likeMark;
    }

    public void setLikeMark(int likeMark) {
        this.likeMark = likeMark;
    }

    private int likeMark=0; //点赞标记，0为未点赞，1为点赞

    public String getTopic_avatar() {
        return topic_avatar;
    }

    public void setTopic_avatar(String topic_avatar) {
        this.topic_avatar = topic_avatar;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getGame_career() {
        return game_career;
    }

    public void setGame_career(String game_career) {
        this.game_career = game_career;
    }

    public String getOften_game() {
        return often_game;
    }

    public void setOften_game(String often_game) {
        this.often_game = often_game;
    }

    public String getPraise() {
        return praise;
    }

    public void setPraise(String praise) {
        this.praise = praise;
    }

    public String getFans() {
        return fans;
    }

    public void setFans(String fans) {
        this.fans = fans;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFlagPath() {
        return flagPath;
    }

    public void setFlagPath(String flagPath) {
        this.flagPath = flagPath;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getManifesto() {
        return manifesto;
    }

    public void setManifesto(String manifesto) {
        this.manifesto = manifesto;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    @Override
    public String toString() {
        return "MasterEntity{" +
                "flagPath='" + flagPath + '\'' +
                ", nickname='" + nickname + '\'' +
                ", manifesto='" + manifesto + '\'' +
                ", member_id='" + member_id + '\'' +
                ", url='" + url + '\'' +
                ", mark=" + mark +
                ", topic_avatar='" + topic_avatar + '\'' +
                ", hobby='" + hobby + '\'' +
                ", game_career='" + game_career + '\'' +
                ", often_game='" + often_game + '\'' +
                ", praise='" + praise + '\'' +
                ", fans='" + fans + '\'' +
                ", likeMark=" + likeMark +
                '}';
    }
}
