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

        String testDateString = testDate.get(Calendar.YEAR) + "-" + (testDate.get(Calendar.MONTH) + 1) + "-" + testDate.get(Calendar.DAY_OF_MONTH) + " " + (testDate.get(Calendar.HOUR) < 10 ? ("0" + testDate.get(Calendar.HOUR)) : testDate.get(Calendar.HOUR)) + ":" + (testDate.get(Calendar.MINUTE) < 10 ? ("0" + testDate.get(Calendar.MINUTE)) : testDate.get(Calendar.MINUTE)) + ":" + (testDate.get(Calendar.SECOND) < 10 ? ("0" + testDate.get(Calendar.SECOND)) : testDate.get(Calendar.SECOND)) + " " + (testDate.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
        String expectedDateString = Utils.getWeekdayName(testDate.get(Calendar.DAY_OF_WEEK)) + " - " + testDate.get(Calendar.DAY_OF_MONTH) + "." + (testDate.get(Calendar.MONTH) + 1) + "." + testDate.get(Calendar.YEAR);

        assertEquals("Dates should be equal", expectedDateString, Utils.getGameDay(testDateString));
    }

    @Test
    public void getGameTime_should_return_time(){
        Calendar testDate = Calendar.getInstance();
        Calendar testDateUS = Calendar.getInstance();
        testDateUS.add(Calendar.HOUR, -7);

        String testDateString = testDateUS.get(Calendar.YEAR) + "-" + (testDateUS.get(Calendar.MONTH) + 1) + "-" + testDateUS.get(Calendar.DAY_OF_MONTH) + " " + (testDateUS.get(Calendar.HOUR) < 10 ? ("0" + testDateUS.get(Calendar.HOUR)) : testDateUS.get(Calendar.HOUR)) + ":" + (testDateUS.get(Calendar.MINUTE) < 10 ? ("0" + testDateUS.get(Calendar.MINUTE)) : testDateUS.get(Calendar.MINUTE)) + ":" + (testDateUS.get(Calendar.SECOND) < 10 ? ("0" + testDateUS.get(Calendar.SECOND)) : testDateUS.get(Calendar.SECOND)) + " " + (testDateUS.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
        String expectedDateString = (testDate.get(Calendar.HOUR_OF_DAY) < 10 ? ("0" + testDate.get(Calendar.HOUR_OF_DAY)) : testDate.get(Calendar.HOUR_OF_DAY)) + ":" + (testDate.get(Calendar.MINUTE) < 10 ? ("0" + testDate.get(Calendar.MINUTE)) : testDate.get(Calendar.MINUTE));

        assertEquals("Times should be equal", expectedDateString, Utils.getGameTime(testDateString));
    }
}
