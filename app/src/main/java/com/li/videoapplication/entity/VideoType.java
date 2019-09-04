package com.li.videoapplication.entity;

/** 视频类型
 * Created by li on 2014/10/10.
 */
public class VideoType {
    private String id="";
    private String flagPath="";
    private String name="";
    private String sort="";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getFlagPath() {
        return flagPath;
    }

    public void setFlagPath(String flagPath) {
        this.flagPath = flagPath;
    }

}
