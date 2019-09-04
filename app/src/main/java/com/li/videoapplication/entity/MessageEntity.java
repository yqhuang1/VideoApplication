package com.li.videoapplication.entity;

/**
 * Created by feimoyuangong on 2015/6/10.
 */
public class MessageEntity {
    private String member_id; //评论者id
    private String avatar;    //评论者头像
    private String nickname;  //评论者昵称
    private String content;   //评论内容
    private String time;      //评论时间
    private String mark;      //查看标记，0表示已查看，1表示位查看
    private String video_id;  //被评论视频id
    private String flag;      //消息标志，0-视频评论消息，1-个人空间评论消息

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
