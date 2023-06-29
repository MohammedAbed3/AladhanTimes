package com.example.aladhantimes;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.hbb20.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PickCountryAndCityDialog extends DialogFragment {
    PickCountryAndCityNames pickCountryAndCityNames;
    PickLocation pickLocation;

    List<String> citiesOfCountry= new ArrayList<>();

    public PickCountryAndCityDialog(PickCountryAndCityNames pickCountryAndCityListener, PickLocation pickLocationListener) {
        pickCountryAndCityNames = pickCountryAndCityListener;
        pickLocation = pickLocationListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        CountryCodePicker ccp = view.findViewById(R.id.country_code_picker);
        Spinner cityPicker = view.findViewById(R.id.city_picker);
        Button confirmSelection = view.findViewById(R.id.confirm_picking);
        Button pickLocationBtn = view.findViewById(R.id.current_location_btn);
        TextView errorText=view.findViewById(R.id.error_text);
//        if (!checkGPS()){
//            pickLocationBtn.setEnabled(false);
//            errorText.setVisibility(View.VISIBLE);
//            errorText.setText("اذا أردت تحديد الأوقات بناءاً علي موقعك ؟ من فضلك فعل الGPS");
//        }
//        else {
//            pickLocationBtn.setEnabled(true);
//            errorText.setVisibility(View.GONE);
//        }
        cityPicker.setVisibility(View.INVISIBLE);
        ccp.setOnCountryChangeListener(() -> {
                    try {
                        cityPicker.setVisibility(View.VISIBLE);
                        confirmSelection.setEnabled(true);
                        citiesOfCountry.clear();
                        if (getCitiesName(ccp.getSelectedCountryName()) != null) {
                            confirmSelection.setEnabled(true);
                            errorText.setVisibility(View.GONE);
                            citiesOfCountry.addAll(Objects.requireNonNull(getCitiesName(ccp.getSelectedCountryName())));
                        }
                        else {
                            confirmSelection.setEnabled(false);
                            cityPicker.setVisibility(View.INVISIBLE);
                            errorText.setVisibility(View.VISIBLE);
                        }
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, citiesOfCountry);
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        cityPicker.setAdapter(arrayAdapter);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });
        confirmSelection.setOnClickListener(v -> {
            String selectedCountry = ccp.getSelectedCountryName();
            String selectedCity=cityPicker.getSelectedItem().toString();
            pickCountryAndCityNames.pickCountryAndCity(selectedCountry,selectedCity);
            onDestroyView();
        });
        pickLocationBtn.setOnClickListener(v->{
            if (!checkGPS()){
                pickLocationBtn.setEnabled(false);
                errorText.setVisibility(View.VISIBLE);
                errorText.setText("اذا أردت تحديد الأوقات بناءاً علي موقعك ؟ من فضلك فعل الGPS");
            }else {
                pickLocation.pickLocation();
                onDestroyView();
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.select_country_city_layout, container, false);

        return v;
    }

    private String getJsonString() {
        AssetManager assetManager = getActivity().getAssets();
        String jsonString = "";
        try {
            InputStream inputStream = assetManager.open("cities.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            jsonString = new String(buffer, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return jsonString;
    }

    private boolean checkGPS() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    private List<String> getCitiesName(String countryName) throws JSONException {
        ArrayList<String> cities = new ArrayList<String>();
        JSONObject jsonObject = new JSONObject(getJsonString());
        if (jsonObject.has(countryName)) {
            JSONArray array = jsonObject.getJSONArray(countryName);
            String city = "";
            for (int i = 0; i <= array.length() - 1; i++) {
                city = array.get(i).toString();
                cities.add(city);
            }
            return cities;
        }else {
            return null;
        }
    }
}

interface PickCountryAndCityNames {
    void pickCountryAndCity(String countryName, String cityName);
}

interface PickLocation {
    void pickLocation();
}