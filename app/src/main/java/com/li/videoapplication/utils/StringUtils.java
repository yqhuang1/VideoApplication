package com.li.videoapplication.utils;

/**
 * Created by user on 2014/12/5.
 */
public class StringUtils {

    //播放数过万处理,传入播放数进行转换
    public static String turnViewCount(String count) {
        float viewcount = Float.parseFloat(count);
        if (viewcount > 999999) {
            return String.format("%.1f", viewcount / 1000000) + "百万";
        } else if (viewcount > 9999 && viewcount < 999999) {
            return String.format("%.1f", viewcount / 10000) + "万";
        } else {
            return count;
        }
    }

    //点赞、收藏数如果出现负数情况，设置为零
    public static String adjustCount(String count) {
        float viewcount = Float.parseFloat(count);
        if (viewcount < 0) {
            return "0";
        } else {
            return count;
        }
    }
}
