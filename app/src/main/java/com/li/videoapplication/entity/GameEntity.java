package com.li.videoapplication.entity;

/**
 * Created by feimoyuangong on 2015/1/20.
 */
public class GameEntity {
    private String game_name="";//游戏名
    private String game_icon="";//游戏图标地址
    private String down_address="";//游戏下载地址
    private int down_count=0;//游戏下载量
    private Double total=0.0;//游戏总分
    private String description="";//游戏简介
    private int game_id=0;

    public int getGame_id() {
        return game_id;
    }

    public void setGame_id(int game_id) {
        this.game_id = game_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return game_name;
    }

    public void setName(String name) {
        this.game_name = name;
    }

    public String getGame_icon() {
        return game_icon;
    }

    public void setGame_icon(String game_icon) {
        this.game_icon = game_icon;
    }

    public String getDown_address() {
        return down_address;
    }

    public void setDown_address(String down_address) {
        this.down_address = down_address;
    }

    public int getDown_count() {
        return down_count;
    }

    public void setDown_count(int down_count) {
        this.down_count = down_count;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
