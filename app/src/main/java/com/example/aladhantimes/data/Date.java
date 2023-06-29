package com.example.aladhantimes.data;

public class Date {
    private String readable;
    private String timestamp;
    private Hijri hijri;
    private Gregorian gregorian;

    public String getReadable() {
        return readable;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Hijri getHijri() {
        return hijri;
    }

    public Gregorian getGregorian() {
        return gregorian;
    }
}
