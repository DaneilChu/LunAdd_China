package com.infinity.lunadd.widget;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by DanielChu on 2017/6/9.
 */
public class TimeUtil  {

    //get Current Time (GMT+8)
    public static Date getCurrentTime(){
        Date currentTime = new Date(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        TimeZone oldZone = cal.getTimeZone();
        TimeZone newZone = TimeZone.getTimeZone("GMT+8:00");
        return changeTimeZone(currentTime,oldZone,newZone);
    }

    public static Date getLastDayTime(){
        Date currentTime = new Date(System.currentTimeMillis()-24*60*60*1000);
        Calendar cal = Calendar.getInstance();
        TimeZone oldZone = cal.getTimeZone();
        TimeZone newZone = TimeZone.getTimeZone("GMT+8:00");
        return changeTimeZone(currentTime,oldZone,newZone);
    }

    private static Date changeTimeZone(Date date, TimeZone oldZone, TimeZone newZone) {
        Date dateTmp = null;
        if (date != null) {
            int timeOffset = oldZone.getRawOffset() - newZone.getRawOffset();
            dateTmp = new Date(date.getTime() - timeOffset);
        }
        return dateTmp;
    }

}
