package com.andre.nfltipapp;

import android.support.annotation.Nullable;

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
}
