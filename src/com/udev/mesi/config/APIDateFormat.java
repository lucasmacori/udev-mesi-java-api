package com.udev.mesi.config;

import java.text.SimpleDateFormat;

public class APIDateFormat {
    private static final String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
    public static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat(dateTimeFormat);
    private static final String dateFormat = "yyyy-MM-dd";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(dateFormat);
}
