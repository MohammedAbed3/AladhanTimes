package com.example.aladhantimes.data;

public class Gregorian {
    private String date;
    private String format;
    private String day;
    private Weekday weekday;
    private Month month;
    private String year;
    private Designation designation;

    public String getDate() {
        return date;
    }

    public String getFormat() {
        return format;
    }

    public String getDay() {
        return day;
    }

    public Weekday getWeekday() {
        return weekday;
    }

    public Month getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public Designation getDesignation() {
        return designation;
    }
}
