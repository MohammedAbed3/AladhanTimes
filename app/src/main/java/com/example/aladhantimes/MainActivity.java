package com.example.aladhantimes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;


import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aladhantimes.data.API;
import com.example.aladhantimes.data.PrayerTimesResponse;
import com.example.aladhantimes.data.RetrofitInstance;
import com.example.aladhantimes.data.Timings;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements PickCountryAndCityNames, PickLocation {
    private FusedLocationProviderClient fusedLocationProviderClient = null;
    private RetrofitInstance retrofitInstance = null;
    private TextView fajr, zuhr, asr, maghrib, isha;
    ProgressBar progressBar;

    Button btn;
    private SharedPreferences sharedPreferences;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.open_county_city_picker);
        fajr = findViewById(R.id.fajr_text);
        zuhr = findViewById(R.id.zuhr_text);
        asr = findViewById(R.id.asr_text);
        maghrib = findViewById(R.id.maghrib_text);
        isha = findViewById(R.id.isha_text);
        progressBar = findViewById(R.id.progress_bar);



        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);







    String lastFajrTime =sharedPreferences.getString("fajr_time", "");
    String lastZuhrTime = sharedPreferences.getString("dhuhr_time", "");
    String lastAsrTime = sharedPreferences.getString("asr_time", "");
    String lastMaghribTime = sharedPreferences.getString("maghrib_time", "");
    String lastIshaTime = sharedPreferences.getString("isha_time", "");

    fajr.setText(lastFajrTime);
    zuhr.setText(lastZuhrTime);
    asr.setText(lastAsrTime);
    maghrib.setText(lastMaghribTime);
    isha.setText(lastIshaTime);




        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        btn.setOnClickListener(view -> {
            openDialog();
        });
    }

    private void openDialog() {
        PickCountryAndCityDialog dialog = new PickCountryAndCityDialog(this, this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        dialog.show(transaction, "tag");
    }

    private void getPrayerTimes(String country, String city) {
        progressBar.setVisibility(View.VISIBLE);
        retrofitInstance = new RetrofitInstance();
        API api = retrofitInstance.getApi();
        Call<PrayerTimesResponse> call = api.getPrayerTimes(country, city);
        call.enqueue(new Callback<PrayerTimesResponse>() {
            @Override
            public void onResponse(Call<PrayerTimesResponse> call, Response<PrayerTimesResponse> response) {
                if (response.isSuccessful()) {
                    PrayerTimesResponse prayerTimesResponse = response.body();
                    assert prayerTimesResponse != null;
                    Timings times = prayerTimesResponse.getData().getTimings();
                    Log.d("TAG", "onResponse: " + prayerTimesResponse.getData());
                    String fajrStr = times.getFajr();
                    String zuhrStr = times.getDhuhr();
                    String asrStr = times.getAsr();
                    String maghribStr = times.getMaghrib();
                    String ishStr = times.getIsha();
                    setupUI(fajrStr, zuhrStr, asrStr, maghribStr, ishStr);
                }
            }

            @Override
            public void onFailure(Call<PrayerTimesResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void getLocationAndSetupPrayerTimes() {
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
////            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
////                @Override
////                public void onSuccess(Location location) {
////                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
////                    try {
////                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
////                        String countryName = addresses.get(0).getCountryName();
////                        String cityName = addresses.get(0).getLocality();
////                        getPrayerTimes(countryName, cityName);
////                    } catch (IOException e) {
////                        throw new RuntimeException(e);
////                    }
////                }
////            });
//            LocationRequest locationRequest = LocationRequest.create();
//            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//            locationRequest.setInterval(10000); // Update location every 10 seconds
//            fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
//                @Override
//                public void onLocationResult(@NonNull LocationResult locationResult) {
//
//                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
//                    for (Location location : locationResult.getLocations()) {
//                        double latitude = location.getLatitude();
//                        double longitude = location.getLongitude();
//                        List<Address> addresses;
//                        try {
//                            addresses = geocoder.getFromLocation(latitude, longitude, 1);
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                        String countryName = addresses.get(0).getCountryName();
//                        String cityName = addresses.get(0).getAdminArea();
////                        Toast.makeText(MainActivity.this,addresses.get(0).toString(),Toast.LENGTH_LONG).show();
//                        getPrayerTimes(countryName, cityName);
//                    }
//                    super.onLocationResult(locationResult);
//                }
//            }, null);
//
//        } else {
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//        }
//    }

    private void getLocationAndSetupPrayerTimes() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Log.d("Location", "Latitude: " + latitude + ", Longitude: " + longitude);

                    // مواصلة بقية العمليات المطلوبة
                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        if (addresses != null && addresses.size() > 0) {
                            String countryName = addresses.get(0).getCountryName();
                            String cityName = addresses.get(0).getAdminArea();
                            getPrayerTimes(countryName, cityName);
                        }else {
                            Toast.makeText(MainActivity.this, "Unable to get location. Please make sure location services are enabled.", Toast.LENGTH_SHORT).show();

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    locationManager.removeUpdates(this);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            };

            // تحديد مزود الموقع المستخدم
            String locationProvider;
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationProvider = LocationManager.NETWORK_PROVIDER;
            } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationProvider = LocationManager.GPS_PROVIDER;
            } else {
                Toast.makeText(MainActivity.this, "Unable to get location. Please make sure location services are enabled.", Toast.LENGTH_SHORT).show();
                // إذا لم يتم تمكين أي مزود موقع
                // يمكنك تنفيذ الإجراءات المناسبة هنا (مثلاً عرض رسالة للمستخدم لتمكين خدمات الموقع)
                return;
            }

            // طلب تحديد الموقع
            locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);

        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }


    @SuppressLint("SetTextI18n")
    private void setupUI(String fajrStr,
                         String dhuhrStr,
                         String asrStr,
                         String maghribStr,
                         String ishaStr) {
        fajr.setText( getHourInPattern12(fajrStr));
        zuhr.setText( getHourInPattern12(dhuhrStr));
        asr.setText( getHourInPattern12(asrStr));
        maghrib.setText( getHourInPattern12(maghribStr));
        isha.setText( getHourInPattern12(ishaStr));
        progressBar.setVisibility(View.GONE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("fajr_time", fajrStr);
        editor.putString("dhuhr_time", dhuhrStr);
        editor.putString("asr_time", asrStr);
        editor.putString("maghrib_time", maghribStr);
        editor.putString("isha_time", ishaStr);
        editor.apply();


    }

    private String getHourInPattern12(String hourInPattern24) {
        if (!hourInPattern24.startsWith("0")) {
            String[] hourFromAPI = hourInPattern24.split(":");
            String bigHour = hourFromAPI[0];
            if (Integer.parseInt(bigHour) > 12) {
                String min = hourFromAPI[1];
                int mHOur = Integer.parseInt(bigHour) - 12;
                if (mHOur == 0) {
                    mHOur = 12;
                    return mHOur + ":" + min;
                } else {
                    return "0"+mHOur + ":" + min;
                }
            }
        }
        else {
            return hourInPattern24;
        }
        return hourInPattern24;
    }

    private boolean checkGPS() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void pickCountryAndCity(String countryName, String cityName) {
        if (checkGPS()) {
            getPrayerTimes(countryName, cityName);
        } else {
            Toast.makeText(this, "من فضلك , فعل ال GPS", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void pickLocation() {
        if (checkGPS()) {
            getLocationAndSetupPrayerTimes();
        } else {
            Toast.makeText(this, "من فضلك , فعل ال GPS", Toast.LENGTH_SHORT).show();
        }
    }


}