package com.diamondfire.helpbot.util;

import java.time.*;
import java.util.*;

@SuppressWarnings("deprecation")
public class DateUtil {

    public static Date toDate(java.sql.Date sqlDate) {
        return new Date(sqlDate.getDay(), sqlDate.getMonth(), sqlDate.getYear());
    }

    public static Date toDate(LocalDate date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());

        return calendar.getTime();
    }

    public static java.sql.Date toSqlDate(Date date) {
        return new java.sql.Date(date.toInstant().toEpochMilli());
    }


}
