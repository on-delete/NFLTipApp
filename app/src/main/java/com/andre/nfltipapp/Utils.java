package com.andre.nfltipapp;

import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Utils {

    @Nullable
    private static Calendar getESTTimeFromGameTime(String gameTime){
        try {
            DateFormat sdf = new SimpleDateFormat("y-M-d h:m:s a", Locale.US);
            Calendar targetCalendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"));
            sdf.setTimeZone(TimeZone.getTimeZone("EST"));
            Date targetDate = sdf.parse(gameTime);
            targetCalendar.setTime(targetDate);
            return targetCalendar;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getGameDay(String gameDate){
        Calendar gameTimeEST = getESTTimeFromGameTime(gameDate);
        return getWeekdayName(gameTimeEST.get(Calendar.DAY_OF_WEEK)) + " - " + gameTimeEST.get(Calendar.DAY_OF_MONTH) + "." + (gameTimeEST.get(Calendar.MONTH) + 1) + "." + gameTimeEST.get(Calendar.YEAR);
    }

    public static String getGameTime(String gameDate){
        Calendar gameTimeEST = getESTTimeFromGameTime(gameDate);
        return (gameTimeEST.get(Calendar.HOUR_OF_DAY) < 10 ? ("0" + gameTimeEST.get(Calendar.HOUR_OF_DAY)) : gameTimeEST.get(Calendar.HOUR_OF_DAY)) + ":" + (gameTimeEST.get(Calendar.MINUTE) < 10 ? ("0" + gameTimeEST.get(Calendar.MINUTE)) : gameTimeEST.get(Calendar.MINUTE));
    }

    public static boolean isPredictionTimeOver(String gameTime, int offset){
        Calendar gameTimeEST = getESTTimeFromGameTime(gameTime);
        Calendar rightNow = Calendar.getInstance();
        if(gameTimeEST != null){
            gameTimeEST.add(Calendar.MINUTE, offset);
            return rightNow.getTimeInMillis() > gameTimeEST.getTimeInMillis();
        }
        else{
            return false;
        }
    }

    @VisibleForTesting
    static String getWeekdayName(int i)
    {
        switch (i)
        {
            case Calendar.MONDAY:
                return "Mo";
            case Calendar.TUESDAY:
                return "Di";
            case Calendar.WEDNESDAY:
                return "Mi";
            case Calendar.THURSDAY:
                return "Do";
            case Calendar.FRIDAY:
                return "Fr";
            case Calendar.SATURDAY:
                return "Sa";
            case Calendar.SUNDAY:
                return "So";
            default:
                return Integer.toString (i);
        }
    }

}
