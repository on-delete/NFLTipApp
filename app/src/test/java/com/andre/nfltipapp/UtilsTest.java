package com.andre.nfltipapp;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UtilsTest {

    @Test
    public void isPredictionTimeOver_should_return_true(){
        Calendar testDate = Calendar.getInstance();
        testDate.add(Calendar.DAY_OF_MONTH, -5);

        String testDateString = testDate.get(Calendar.YEAR) + "-" + (testDate.get(Calendar.MONTH) + 1) + "-" + testDate.get(Calendar.DAY_OF_MONTH) + " 08:00:00 PM";
        assertTrue(Utils.isPredictionTimeOver(testDateString, 0));
    }

    @Test
    public void isPredictionTimeOver_should_return_false(){
        Calendar testDate = Calendar.getInstance();
        testDate.add(Calendar.DAY_OF_MONTH, 5);

        String testDateString = testDate.get(Calendar.YEAR) + "-" + (testDate.get(Calendar.MONTH) + 1) + "-" + testDate.get(Calendar.DAY_OF_MONTH) + " 08:00:00 PM";
        assertFalse(Utils.isPredictionTimeOver(testDateString, 0));
    }

    @Test
    public void isPredictionTimeOver_should_return_false_with_offset(){
        Calendar testDate = Calendar.getInstance();
        testDate.add(Calendar.MINUTE, 10);

        String testDateString = testDate.get(Calendar.YEAR) + "-" + (testDate.get(Calendar.MONTH) + 1) + "-" + testDate.get(Calendar.DAY_OF_MONTH) + " " + (testDate.get(Calendar.HOUR) < 10 ? ("0" + testDate.get(Calendar.HOUR)) : testDate.get(Calendar.HOUR)) + ":" + (testDate.get(Calendar.MINUTE) < 10 ? ("0" + testDate.get(Calendar.MINUTE)) : testDate.get(Calendar.MINUTE)) + ":" + (testDate.get(Calendar.SECOND) < 10 ? ("0" + testDate.get(Calendar.SECOND)) : testDate.get(Calendar.SECOND)) + " " + (testDate.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
        assertFalse(Utils.isPredictionTimeOver(testDateString, -30));
    }

    @Test
    public void getGameDay_should_return_day(){
        Calendar testDate = Calendar.getInstance();
        Calendar testDateUS = Calendar.getInstance();
        testDateUS.set(Calendar.HOUR, 1);

        String testDateString = testDateUS.get(Calendar.YEAR) + "-" + (testDateUS.get(Calendar.MONTH) + 1) + "-" + testDateUS.get(Calendar.DAY_OF_MONTH) + " " + (testDateUS.get(Calendar.HOUR) < 10 ? ("0" + testDateUS.get(Calendar.HOUR)) : testDateUS.get(Calendar.HOUR)) + ":" + (testDateUS.get(Calendar.MINUTE) < 10 ? ("0" + testDateUS.get(Calendar.MINUTE)) : testDateUS.get(Calendar.MINUTE)) + ":" + (testDateUS.get(Calendar.SECOND) < 10 ? ("0" + testDateUS.get(Calendar.SECOND)) : testDateUS.get(Calendar.SECOND)) + " " + (testDateUS.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
        String expectedDateString = Utils.getWeekdayName(testDate.get(Calendar.DAY_OF_WEEK)) + " - " + testDate.get(Calendar.DAY_OF_MONTH) + "." + (testDate.get(Calendar.MONTH) + 1) + "." + testDate.get(Calendar.YEAR);

        assertEquals("Dates should be equal", expectedDateString, Utils.getGameDay(testDateString));
    }

    @Test
    public void getGameTime_should_return_time(){
        Calendar testDate = Calendar.getInstance();
        Calendar testDateUS = Calendar.getInstance();
        testDateUS.set(Calendar.HOUR, 1);
        testDateUS.set(Calendar.MINUTE, 0);
        testDateUS.set(Calendar.SECOND, 0);
        testDateUS.set(Calendar.AM_PM, Calendar.PM);

        //Test both timezones are in standard time = +7hrs
        testDate.set(Calendar.MONTH, 0);
        testDate.set(Calendar.DAY_OF_MONTH, 10);
        testDateUS.set(Calendar.MONTH, 0);
        testDateUS.set(Calendar.DAY_OF_MONTH, 10);

        String testDateString = testDateUS.get(Calendar.YEAR) + "-" + (testDateUS.get(Calendar.MONTH) + 1) + "-" + testDateUS.get(Calendar.DAY_OF_MONTH) + " " + (testDateUS.get(Calendar.HOUR) < 10 ? ("0" + testDateUS.get(Calendar.HOUR)) : testDateUS.get(Calendar.HOUR)) + ":" + (testDateUS.get(Calendar.MINUTE) < 10 ? ("0" + testDateUS.get(Calendar.MINUTE)) : testDateUS.get(Calendar.MINUTE)) + ":" + (testDateUS.get(Calendar.SECOND) < 10 ? ("0" + testDateUS.get(Calendar.SECOND)) : testDateUS.get(Calendar.SECOND)) + " " + (testDateUS.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
        String expectedDateString = "19:00";

        assertEquals("Times should be equal", expectedDateString, Utils.getGameTime(testDateString));

        //us timezone is in daylight time = +6hrs
        testDate.set(Calendar.MONTH, 2);
        testDate.set(Calendar.DAY_OF_MONTH, 14);
        testDateUS.set(Calendar.MONTH, 2);
        testDateUS.set(Calendar.DAY_OF_MONTH, 14);

        testDateString = testDateUS.get(Calendar.YEAR) + "-" + (testDateUS.get(Calendar.MONTH) + 1) + "-" + testDateUS.get(Calendar.DAY_OF_MONTH) + " " + (testDateUS.get(Calendar.HOUR) < 10 ? ("0" + testDateUS.get(Calendar.HOUR)) : testDateUS.get(Calendar.HOUR)) + ":" + (testDateUS.get(Calendar.MINUTE) < 10 ? ("0" + testDateUS.get(Calendar.MINUTE)) : testDateUS.get(Calendar.MINUTE)) + ":" + (testDateUS.get(Calendar.SECOND) < 10 ? ("0" + testDateUS.get(Calendar.SECOND)) : testDateUS.get(Calendar.SECOND)) + " " + (testDateUS.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
        expectedDateString = "18:00";

        assertEquals("Times should be equal", expectedDateString, Utils.getGameTime(testDateString));

        //both timezones are in daylight = +7hrs
        testDate.set(Calendar.MONTH, 6);
        testDate.set(Calendar.DAY_OF_MONTH, 14);
        testDateUS.set(Calendar.MONTH, 6);
        testDateUS.set(Calendar.DAY_OF_MONTH, 14);

        testDateString = testDateUS.get(Calendar.YEAR) + "-" + (testDateUS.get(Calendar.MONTH) + 1) + "-" + testDateUS.get(Calendar.DAY_OF_MONTH) + " " + (testDateUS.get(Calendar.HOUR) < 10 ? ("0" + testDateUS.get(Calendar.HOUR)) : testDateUS.get(Calendar.HOUR)) + ":" + (testDateUS.get(Calendar.MINUTE) < 10 ? ("0" + testDateUS.get(Calendar.MINUTE)) : testDateUS.get(Calendar.MINUTE)) + ":" + (testDateUS.get(Calendar.SECOND) < 10 ? ("0" + testDateUS.get(Calendar.SECOND)) : testDateUS.get(Calendar.SECOND)) + " " + (testDateUS.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
        expectedDateString = "19:00";

        assertEquals("Times should be equal", expectedDateString, Utils.getGameTime(testDateString));

        //europe is in standard time again = +6hrs
        testDate.set(Calendar.MONTH, 10);
        testDate.set(Calendar.DAY_OF_MONTH, 1);
        testDateUS.set(Calendar.MONTH, 10);
        testDateUS.set(Calendar.DAY_OF_MONTH, 1);

        testDateString = testDateUS.get(Calendar.YEAR) + "-" + (testDateUS.get(Calendar.MONTH) + 1) + "-" + testDateUS.get(Calendar.DAY_OF_MONTH) + " " + (testDateUS.get(Calendar.HOUR) < 10 ? ("0" + testDateUS.get(Calendar.HOUR)) : testDateUS.get(Calendar.HOUR)) + ":" + (testDateUS.get(Calendar.MINUTE) < 10 ? ("0" + testDateUS.get(Calendar.MINUTE)) : testDateUS.get(Calendar.MINUTE)) + ":" + (testDateUS.get(Calendar.SECOND) < 10 ? ("0" + testDateUS.get(Calendar.SECOND)) : testDateUS.get(Calendar.SECOND)) + " " + (testDateUS.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
        expectedDateString = "18:00";

        assertEquals("Times should be equal", expectedDateString, Utils.getGameTime(testDateString));
    }
}
