package com.example.aladhantimes.data;

public class Meta {
    private double latitude;
    private double longitude;
    private String timezone;
    private Method method;
    private String latitudeAdjustmentMethod;
    private String midnightMode;
    private String school;
    private Offset offset;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getTimezone() {
        return timezone;
    }

    public Method getMethod() {
        return method;
    }

    public String getLatitudeAdjustmentMethod() {
        return latitudeAdjustmentMethod;
    }

    public String getMidnightMode() {
        return midnightMode;
    }

    public String getSchool() {
        return school;
    }

    public Offset getOffset() {
        return offset;
    }
}
