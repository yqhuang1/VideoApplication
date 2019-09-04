package com.li.videoapplication.entity;

/**
 * Created by feimoyuangong on 2015/2/10.
 */
public class ActivityEntity {

    private int activity_id=-1;//活动id
    private int heat=-1;//活动热度
    private int display=-1;//0表示不显示，1表示显示
    private int mark=-1;//0表示未过期，1表示过期
    private String name="";//活动名称
    private String cover_chart1="";//封面1
    private String cover_chart2="";//封面2
    private String url="";//活动路径
    private String activity_rule="";//活动规则
    private String rewards="";//活动奖励
    private String androidUrl="";//活动游戏下载路径
    private String game="";//活动游戏名
    private String gameFlag="";//活动游戏icon
    private String startTime="";//活动开始时间
    private String endTime="";//活动结束时间

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getActivity_rule() {
        return activity_rule;
    }

    public void setActivity_rule(String activity_rule) {
        this.activity_rule = activity_rule;
    }

    public String getRewards() {
        return rewards;
    }

    public void setRewards(String rewards) {
        this.rewards = rewards;
    }

    public String getAndroidUrl() {
        return androidUrl;
    }

    public void setAndroidUrl(String androidUrl) {
        this.androidUrl = androidUrl;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getGameFlag() {
        return gameFlag;
    }

    public void setGameFlag(String gameFlag) {
        this.gameFlag = gameFlag;
    }

    public int getDisplay() {
        return display;
    }

    public void setDisplay(int display) {
        this.display = display;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public int getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(int activity_id) {
        this.activity_id = activity_id;
    }

    public int getHeat() {
        return heat;
    }

    public void setHeat(int head) {
        this.heat = head;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover_chart1() {
        return cover_chart1;
    }

    public void setCover_chart1(String cover_chart1) {
        this.cover_chart1 = cover_chart1;
    }

    public String getCover_chart2() {
        return cover_chart2;
    }

    public void setCover_chart2(String cover_chart2) {
        this.cover_chart2 = cover_chart2;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
