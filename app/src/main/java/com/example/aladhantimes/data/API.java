package com.example.aladhantimes.data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface API {
    @GET("v1/timingsByCity")
    Call<PrayerTimesResponse> getPrayerTimes(
            @Query("country") String country,
            @Query("city") String city);
}
