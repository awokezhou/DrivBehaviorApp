package com.example.awoke.drivbehaviorapp.DataSave;

import java.util.Calendar;
import java.util.TimeZone;

public class NowTime {
    public String Date;
    public String Time;
    public String DateTime;

    public static String getTime(NowTime time) {
        String year, month, day;
        String hour, minut, sec;
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        year = String.valueOf(cal.get(Calendar.YEAR));
        month = String.valueOf(cal.get(Calendar.MONTH)+1);
        day = String.valueOf(cal.get(Calendar.DATE));
        if (cal.get(Calendar.AM_PM) == 0)
            hour = String.valueOf(cal.get(Calendar.HOUR));
        else
            hour = String.valueOf(cal.get(Calendar.HOUR)+12);
        minut = String.valueOf(cal.get(Calendar.MINUTE));
        sec = String.valueOf(cal.get(Calendar.SECOND));
        time.Date = year + "-" + month + "-" + day;
        time.Time = hour + ":" + minut + ":" + sec;
        time.DateTime = year + "/" + month + "/" + day + "-" + hour + ":" + minut + ":" + sec;
        return year + "/" + month + "/" + day + "-" + hour + ":" + minut + ":" + sec;
    }
}
