package com.li.videoapplication.entity;

import java.io.Serializable;

/**
 * Created by feimoyuangong on 2015/2/9.
 * <p/>
 * 活动类
 */
public class MatchEntity implements Serializable {
    private int match_id = -1;//赛事id
    private int match_type = -1;//赛事类型,0表示模板赛事，1表示贴吧赛事
    private String name = "";//赛事名称
    private int game_id = -1;//赛事游戏id
    private int member_id = -1;//楼主id
    private String cover_chart1 = "";//封面1路径
    private String cover_chart2 = "";//封面2路径
    private String starttime = "";//开始时间
    private String endtime = "";//结束时间
    private String addtime = "";//发帖时间
    private String upload_time = "";//上传时间
    private int display = -1;//显示标识，1表示显示，0表示不显示
    private String pic_600_x = "";
    private String pic_110_110 = "";
    private String pic_hd = "";
    private String pic_pld = "";

    private String ios_download = "";
    private String android_download = "";

    private boolean cleckedFlag = false;//选择的标志

    public boolean isCleckedFlag() {
        return cleckedFlag;
    }

    public void setCleckedFlag(boolean cleckedFlag) {
        this.cleckedFlag = cleckedFlag;
    }

    public String getIos_download() {
        return ios_download;
    }

    public void setIos_download(String ios_download) {
        this.ios_download = ios_download;
    }

    public String getAndroid_download() {
        return android_download;
    }

    public void setAndroid_download(String android_download) {
        this.android_download = android_download;
    }

    public String getPic_pld() {
        return pic_pld;
    }

    public void setPic_pld(String pic_pld) {
        this.pic_pld = pic_pld;
    }

    public String getPic_hd() {
        return pic_hd;
    }

    public void setPic_hd(String pic_hd) {
        this.pic_hd = pic_hd;
    }

    public String getPic_110_110() {
        return pic_110_110;
    }

    public void setPic_110_110(String pic_110_110) {
        this.pic_110_110 = pic_110_110;
    }

    public String getPic_600_x() {
        return pic_600_x;
    }

    public String getUpload_time() {
        return upload_time;
    }

    public void setUpload_time(String upload_time) {
        this.upload_time = upload_time;
    }

    public void setPic_600_x(String pic_600_x) {
        this.pic_600_x = pic_600_x;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {

        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    private int height = 0;
    private int width = 0;
    private int mark = 0;//帖子是否过期，0表示未过期，1表示过期
    private int status = 0;//0未参加，1为已参加，2为已上传，3为已审核

    private String description = "";//赛事描述，活动简介
    private String rewards = "";//赛事奖励
    private String url = "";//赛事路径

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMatch_rule() {
        return match_rule;
    }

    public void setMatch_rule(String match_rule) {
        this.match_rule = match_rule;
    }

    public String getGame_icon() {
        return game_icon;
    }

    public void setGame_icon(String game_icon) {
        this.game_icon = game_icon;
    }

    public String getGame_name() {
        return game_name;
    }

    public void setGame_name(String game_name) {
        this.game_name = game_name;
    }

    public String getGame_url() {
        return game_url;
    }

    public void setGame_url(String game_url) {
        this.game_url = game_url;
    }

    private String match_rule = "";//赛事规则
    private String game_icon = "";//赛事游戏图标
    private String game_name = "";//赛事游戏名称
    private String game_url = "";//赛事

    private String winners = "";//活动获奖名单

    public String getWinners() {
        return winners;
    }

    public void setWinners(String winners) {
        this.winners = winners;
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

    public int getMatch_id() {
        return match_id;
    }

    public void setMatch_id(int match_id) {
        this.match_id = match_id;
    }

    public int getMatch_type() {
        return match_type;
    }

    public void setMatch_type(int match_type) {
        this.match_type = match_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGame_id() {
        return game_id;
    }

    public void setGame_id(int game_id) {
        this.game_id = game_id;
    }

    public int getMember_id() {
        return member_id;
    }

    public void setMember_id(int member_id) {
        this.member_id = member_id;
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

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public int getDisplay() {
        return display;
    }

    public void setDisplay(int display) {
        this.display = display;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRewards() {
        return rewards;
    }

    public void setRewards(String rewards) {
        this.rewards = rewards;
    }

    @Override
    public String toString() {
        return "MatchEntity{" +
                "match_id=" + match_id +
                ", match_type=" + match_type +
                ", name='" + name + '\'' +
                ", game_id=" + game_id +
                ", member_id=" + member_id +
                ", cover_chart1='" + cover_chart1 + '\'' +
                ", cover_chart2='" + cover_chart2 + '\'' +
                ", starttime='" + starttime + '\'' +
                ", endtime='" + endtime + '\'' +
                ", addtime='" + addtime + '\'' +
                ", upload_time='" + upload_time + '\'' +
                ", display=" + display +
                ", pic_600_x='" + pic_600_x + '\'' +
                ", pic_110_110='" + pic_110_110 + '\'' +
                ", pic_hd='" + pic_hd + '\'' +
                ", pic_pld='" + pic_pld + '\'' +
                ", ios_download='" + ios_download + '\'' +
                ", android_download='" + android_download + '\'' +
                ", height=" + height +
                ", width=" + width +
                ", mark=" + mark +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", rewards='" + rewards + '\'' +
                ", url='" + url + '\'' +
                ", match_rule='" + match_rule + '\'' +
                ", game_icon='" + game_icon + '\'' +
                ", game_name='" + game_name + '\'' +
                ", game_url='" + game_url + '\'' +
                ", winners='" + winners + '\'' +
                '}';
    }
}
