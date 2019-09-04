package com.li.videoapplication.entity;

import java.util.List;

/**
 * Created by feimoyuangong on 2015/6/5.
 * 主页 首页专栏
 */
public class HomeColumnEntity {
    private String title = "";//专栏名称
    private String icon_pic = "";//专栏图标
    private String more_mark = "";//（专栏）更多

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon_pic() {
        return icon_pic;
    }

    public void setIcon_pic(String icon_pic) {
        this.icon_pic = icon_pic;
    }

    public String getMore_mark() {
        return more_mark;
    }

    public void setMore_mark(String more_mark) {
        this.more_mark = more_mark;
    }

    private List<VideoEntity> columnList;//专栏的 内容列表

    public List<VideoEntity> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<VideoEntity> columnList) {
        this.columnList = columnList;
    }


}
