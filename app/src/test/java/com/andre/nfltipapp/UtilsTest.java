package com.andre.nfltipapp;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Andre on 21.02.2017.
 */

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
        System.out.println(testDateString);
        assertFalse(Utils.isPredictionTimeOver(testDateString, -30));
    }
}
