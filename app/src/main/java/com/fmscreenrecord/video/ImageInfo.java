package com.fmscreenrecord.video;

/**
 * Created by Administrator on 2015/8/10 0010.
 */
public class ImageInfo {


    private String DisplayName;

    private String Path;
    //文件大小
    private String Size;
    //文件最后修改时间
    private long lastModified;

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public String getSize() {
        return Size;
    }

    public void setSize(String size) {
        Size = size;
    }

    public String getPath()
    {
        return Path;
    }

    public void setPath(String path)
    {
        Path = path;
    }

    public String getDisplayName()
    {
        return DisplayName;
    }

    public void setDisplayName(String displayName)
    {
        DisplayName = displayName;
    }

}
