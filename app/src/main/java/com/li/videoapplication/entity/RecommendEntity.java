package com.li.videoapplication.entity;

/**
 * Created by feimoyuangong on 2015/5/15.
 */
public class RecommendEntity {
    private String flagPath="";//封面图路径
    private String title="";//标题
    private String type="";//类型：video视频，activity活动，package礼包
    private String video_id="";//视频id
    private String activity_id="";//活动id
    private String package_id="";//礼包id
    private String url="";//活动路径

    public String getFlagPath() {
        return flagPath;
    }

    public void setFlagPath(String flagPath) {
        this.flagPath = flagPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(String activity_id) {
        this.activity_id = activity_id;
    }

    public String getPackage_id() {
        return package_id;
    }

    public void setPackage_id(String package_id) {
        this.package_id = package_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
