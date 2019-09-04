package com.li.videoapplication.entity;

import java.util.List;

/**
 * Created by feimoyuangong on 2015/5/23.
 */
public class HomeHotEntity {

    private String Column;//专栏名称 typeName
    private List<VideoEntity> hotList;
    private String columnId;//专栏id type_id

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getColumn() {
        return Column;
    }

    public void setColumn(String column) {
        Column = column;
    }

    public List<VideoEntity> getHotList() {
        return hotList;
    }

    public void setHotList(List<VideoEntity> hotList) {
        this.hotList = hotList;
    }

    @Override
    public String toString() {
        return "HomeHotEntity{" +
                "Column='" + Column + '\'' +
                ", hotList=" + hotList +
                ", columnId='" + columnId + '\'' +
                '}';
    }
}

