package com.li.videoapplication.entity;

/**
 * Created by li on 2014/10/20.
 */
public class Game {

    private String id = "";//game_id
    private String flagPath = "";//flag
    private String name = "";//gameName
    private String type_name = "";//游戏类型
    private String time = "";

    //游戏圈子添加字段
    private String group_id = "";//圈子id
    private String group_name = "";//圈子名
    private String group_type = "";//圈子类型
    private String attention_num = "";//加入人数
    private String mark = "0";//加入标记
    private String description = "";//游戏描述
    private String video_num = "0";//游戏视频数

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

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_type() {
        return group_type;
    }

    public void setGroup_type(String group_type) {
        this.group_type = group_type;
    }

    public String getAttention_num() {
        return attention_num;
    }

    public void setAttention_num(String attention_num) {
        this.attention_num = attention_num;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideo_num() {
        return video_num;
    }

    public void setVideo_num(String video_num) {
        this.video_num = video_num;
    }
}
