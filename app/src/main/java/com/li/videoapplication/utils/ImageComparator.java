package com.li.videoapplication.utils;

/**
 * Created by Administrator on 2015/8/10 0010.
 */
import com.fmscreenrecord.video.ImageInfo;

import java.util.Comparator;

public class ImageComparator implements Comparator<ImageInfo> {

    @Override
    public int compare(ImageInfo lhs, ImageInfo rhs) {
        if(lhs.getLastModified() > rhs.getLastModified())
        {
            return -1;
        }else
        {
            return 1;
        }
    }
}
