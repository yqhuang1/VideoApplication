package com.li.videoapplication.entity;

/**
 * Created by Administrator on 2015/9/14 0014.
 */
public class LaunchImgEntity {
    String changetime = "";//更新时间，用来判断海报是否需要更新
    String launch_id = "";
    String title = "";
    String flag = "";
    String starttime = "";
    String endtime = "";
    String alone_id = "";

    public String getChangetime() {
        return changetime;
    }

    public void setChangetime(String changetime) {
        this.changetime = changetime;
    }

    public String getLaunch_id() {
        return launch_id;
    }

    public void setLaunch_id(String launch_id) {
        this.launch_id = launch_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
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

    public String getAlone_id() {
        return alone_id;
    }

    public void setAlone_id(String alone_id) {
        this.alone_id = alone_id;
    }

    @Override
    public String toString() {
        return "LaunchImgEntity{" +
                "changetime='" + changetime + '\'' +
                ", launch_id='" + launch_id + '\'' +
                ", title='" + title + '\'' +
                ", flag='" + flag + '\'' +
                ", starttime='" + starttime + '\'' +
                ", endtime='" + endtime + '\'' +
                ", alone_id='" + alone_id + '\'' +
                '}';
    }

}
