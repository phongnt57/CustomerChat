package com.phongnt.phong.chattingtostranger.utils;

import java.util.Date;

/**
 * Created by phong on 9/17/2015.
 */
public class TimeUtils {
    Date date;
    String months[] = {
            "Jan", "Feb", "Mar", "Apr",
            "May", "Jun", "Jul", "Aug",
            "Sep", "Oct", "Nov", "Dec"};
    String day[] = {
            "Mon","Tue","Wed","Thus","Fri","Sat","Sun"
    };

    public TimeUtils(Date date) {
        this.date = date;
    }
    public String getDay(){
        return date.getDate() +"/"+(date.getMonth()+1) +"/"+(date.getYear()+1900);
    }
    public String getTime(){
        return day[date.getDay()] +" "+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
    }
}
