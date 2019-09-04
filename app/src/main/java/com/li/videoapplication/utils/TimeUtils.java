package com.li.videoapplication.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2014/9/23.
 */
public class TimeUtils {

    // a integer to xx:xx:xx
    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    /**
     * 后台读取的10位时间戳转换成时间字符串*
     */
    public static String timestampToDate(String unixDate) {
        SimpleDateFormat fm1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SimpleDateFormat fm2 = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        long unixLong = 0;
        String date1 = "";
        String date2 = "";
        try {
            unixLong = Long.parseLong(unixDate) * 1000;
        } catch (Exception ex) {
            System.out.println("String转换Long错误，请确认数据可以转换！");
        }
        try {
            date1 = fm1.format(new Date(unixLong));
            date2 = fm2.format(new Date(unixLong));
        } catch (Exception ex) {
            System.out.println("String转换Date错误，请确认数据可以转换！");
        }
        return date2;
    }


    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    /**
     * 判断程序距离上次打开是否间隔1天
     *
     * @param lastOpenTime
     * @param currentTime
     * @return
     */
    public static boolean isFirstDaysOpen(String lastOpenTime, String currentTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int day = 24 * 60 * 60 * 1000;
        try {
            Date date1 = simpleDateFormat.parse(lastOpenTime);
            Date date2 = simpleDateFormat.parse(currentTime);
            if (date2.getTime() / day - date1.getTime() / day >= 1) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断刷新每日任务
     *
     * @param currentTime
     * @return
     */
    public static boolean isDaysMissionUptate(String lastCompleteTime, String currentTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int day = 24 * 60 * 60 * 1000;
        try {
            Date date1 = simpleDateFormat.parse(currentTime);
            Date date2 = simpleDateFormat.parse(lastCompleteTime);
            if (date1.getTime() / day - date2.getTime() / day >= 1) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
