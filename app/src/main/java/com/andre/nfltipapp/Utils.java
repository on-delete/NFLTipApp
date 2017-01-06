package com.andre.nfltipapp;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Andre on 06.01.2017.
 */

public class Utils {

    private static DateFormat sdf = new SimpleDateFormat("y-M-d h:m:s a", Locale.US);

    public static long getActualGameTimeInMilliSeconds(String gameTime){
        try {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin"));
            sdf.setTimeZone(TimeZone.getTimeZone("EST"));
            Date date = sdf.parse(gameTime);
            cal.setTime(date);
            return cal.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
