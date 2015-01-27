/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xt.core.utils;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import junit.framework.TestCase;

/**
 *
 * @author Albert
 */
public class DateUtilsTest extends TestCase {
    
    public DateUtilsTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

//    /**
//     * Test of convertCalendarToTimestamp method, of class DateUtils.
//     */
//    public void testConvertCalendarToTimestamp() {
//        System.out.println("convertCalendarToTimestamp");
//        Calendar date = null;
//        Timestamp expResult = null;
//        Timestamp result = DateUtils.convertCalendarToTimestamp(date);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of convertUtilDateToSqlDate method, of class DateUtils.
//     */
//    public void testConvertUtilDateToSqlDate() {
//        System.out.println("convertUtilDateToSqlDate");
//        Date date = null;
//        java.sql.Date expResult = null;
//        java.sql.Date result = DateUtils.convertUtilDateToSqlDate(date);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of convertSqlDateToUtilDate method, of class DateUtils.
//     */
//    public void testConvertSqlDateToUtilDate() {
//        System.out.println("convertSqlDateToUtilDate");
//        java.sql.Date date = null;
//        Date expResult = null;
//        Date result = DateUtils.convertSqlDateToUtilDate(date);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of convertTimestampToCalendar method, of class DateUtils.
//     */
//    public void testConvertTimestampToCalendar() {
//        System.out.println("convertTimestampToCalendar");
//        Timestamp ts = null;
//        Calendar expResult = null;
//        Calendar result = DateUtils.convertTimestampToCalendar(ts);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of parseCalendar method, of class DateUtils.
//     */
//    public void testParseCalendar_String() {
//        System.out.println("parseCalendar");
//        String dateStr = "";
//        Calendar expResult = null;
//        Calendar result = DateUtils.parseCalendar(dateStr);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of parseCalendar method, of class DateUtils.
//     */
//    public void testParseCalendar_String_String() {
//        System.out.println("parseCalendar");
//        String dateStr = "";
//        String dateFormat = "";
//        Calendar expResult = null;
//        Calendar result = DateUtils.parseCalendar(dateStr, dateFormat);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of toDateTimeStr method, of class DateUtils.
     */
    public void testToDateTimeStr() {
        System.out.println("toDateTimeStr");
        Calendar date = DateUtils.parseCalendar("2011-10-01 13:22:22", "yyyy-MM-dd HH:mm:ss");
        String expResult = "2011-10-01 13:22:22";
        String result = DateUtils.toDateTimeStr(date);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

//    /**
//     * Test of toDateStr method, of class DateUtils.
//     */
//    public void testToDateStr_Calendar() {
//        System.out.println("toDateStr");
//        Calendar date = null;
//        String expResult = "";
//        String result = DateUtils.toDateStr(date);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of toDateStr method, of class DateUtils.
//     */
//    public void testToDateStr_Calendar_String() {
//        System.out.println("toDateStr");
//        Calendar date = null;
//        String dateFormat = "";
//        String expResult = "";
//        String result = DateUtils.toDateStr(date, dateFormat);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of calendarMinus method, of class DateUtils.
//     */
//    public void testCalendarMinus() {
//        System.out.println("calendarMinus");
//        Calendar d1 = null;
//        Calendar d2 = null;
//        int expResult = 0;
//        int result = DateUtils.calendarMinus(d1, d2);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of parseDate method, of class DateUtils.
//     */
//    public void testParseDate() throws Exception {
//        System.out.println("parseDate");
//        String dateStr = "";
//        String format = "";
//        Date expResult = null;
//        Date result = DateUtils.parseDate(dateStr, format);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}
