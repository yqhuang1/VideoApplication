package com.li.videoapplication.entity;

/**
 * Created by li on 2014/8/15.
 */
public class MissionEntity {


    private String imgPath = "";
    private String title = "";
    private String content = "";
    private String gift = "";

    private String id = "";
    private String type_id = "";
    private String game_id = "";
    private String description = "";
    private String reward = "";
    private String add_exp = "";
    private String add_money = "";
    private String starttime = "";
    private String endtime = "";
    private String addtime = "";
    private String taskTypeName = "";
    private String taskTimeLength = "0";
    private String lastCompleteTime = "";
    private String task_flag = "0";//是否完成任务；1任务已完成，0任务进行中
    private String is_accept = "0";//是否接受任务；1已接受，0没接受
    private String is_get = "0";//是否领取；1已领取，0没领取
    private String num = "0";//任务总需完成数
    private String flaging = "0";//任务已执行数

    private String status_txt;//现在状态；分四种："接受任务","进行中","领取奖励","已领取"

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getFlaging() {
        return flaging;
    }

    public void setFlaging(String flaging) {
        this.flaging = flaging;
    }

    public String getIs_accept() {
        return is_accept;
    }

    public void setIs_accept(String is_accept) {
        this.is_accept = is_accept;
    }

    public String getIs_get() {
        return is_get;
    }

    public void setIs_get(String is_get) {
        this.is_get = is_get;
    }

    public String getStatus_txt() {
        return status_txt;
    }

    public void setStatus_txt(String status_txt) {
        this.status_txt = status_txt;
    }

    public String getTask_flag() {
        return task_flag;
    }

    public void setTask_flag(String task_flag) {
        this.task_flag = task_flag;
    }

    public String getLastCompleteTime() {
        return lastCompleteTime;
    }

    public void setLastCompleteTime(String lastCompleteTime) {
        this.lastCompleteTime = lastCompleteTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getGame_id() {
        return game_id;
    }

    public void setGame_id(String game_id) {
        this.game_id = game_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getAdd_exp() {
        return add_exp;
    }

    public void setAdd_exp(String add_exp) {
        this.add_exp = add_exp;
    }

    public String getAdd_money() {
        return add_money;
    }

    public void setAdd_money(String add_money) {
        this.add_money = add_money;
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

    public String getTaskTypeName() {
        return taskTypeName;
    }

    public void setTaskTypeName(String taskTypeName) {
        this.taskTypeName = taskTypeName;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGift() {
        return gift;
    }

    public void setGift(String gift) {
        this.gift = gift;
    }

    public String getTaskTimeLength() {
        return taskTimeLength;
    }

    public void setTaskTimeLength(String taskTimeLength) {
        this.taskTimeLength = taskTimeLength;
    }

    @Override
    public String toString() {
        return "MissionEntity{" +
                "imgPath='" + imgPath + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", gift='" + gift + '\'' +
                ", id='" + id + '\'' +
                ", type_id='" + type_id + '\'' +
                ", game_id='" + game_id + '\'' +
                ", description='" + description + '\'' +
                ", reward='" + reward + '\'' +
                ", add_exp='" + add_exp + '\'' +
                ", add_money='" + add_money + '\'' +
                ", starttime='" + starttime + '\'' +
                ", endtime='" + endtime + '\'' +
                ", addtime='" + addtime + '\'' +
                ", taskTypeName='" + taskTypeName + '\'' +
                ", taskTimeLength='" + taskTimeLength + '\'' +
                ", lastCompleteTime='" + lastCompleteTime + '\'' +
                ", task_flag='" + task_flag + '\'' +
                ", is_accept='" + is_accept + '\'' +
                ", is_get='" + is_get + '\'' +
                ", num='" + num + '\'' +
                ", flaging='" + flaging + '\'' +
                ", status_txt='" + status_txt + '\'' +
                '}';
    }
}
