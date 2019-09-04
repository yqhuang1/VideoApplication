package com.li.videoapplication.entity;

/**
 * Created by Administrator on 2015/9/14 0014.
 * Banner广告类
 */
public class BannerEntity {
    String id = "";
    String title = "";
    String flag = "";
    String url = "";
    String situation = "";
    String flagPath = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getFlagPath() {
        return flagPath;
    }

    public void setFlagPath(String flagPath) {
        this.flagPath = flagPath;
    }

    @Override
    public String toString() {
        return "BannerEntity{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", flag='" + flag + '\'' +
                ", url='" + url + '\'' +
                ", situation='" + situation + '\'' +
                ", flagPath='" + flagPath + '\'' +
                '}';
    }
}
