package com.udev.mesi.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class APIFormat {
    // Date
    private static final String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
    public static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat(dateTimeFormat);
    private static final String dateFormat = "yyyy-MM-dd";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(dateFormat);

    public static boolean isValidDateTime(String dateTime) {
        try {
            DATETIME_FORMAT.parse(dateTime);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean isValidDate(String date) {
        try {
            DATE_FORMAT.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    // Email
    public static boolean isValidEmail(String email) {
        email = email.trim();
        return (email.length() < 51 && email.contains("@") && email.indexOf("@") > 0 && email.contains(".") && email.indexOf(".") > 1 && email.length() > 3);
    }

    // String
    public static boolean isValidString(String password, int minLength, int maxLength) {
        password = password.trim();
        return (password.length() <= maxLength && password.length() >= minLength);
    }

    // Gender
    public static boolean isValidGender(String gender) {
        return (gender.length() == 1 && (gender.toUpperCase().toCharArray()[0] == 'M' || gender.toUpperCase().toCharArray()[0] == 'F'));
    }
}
