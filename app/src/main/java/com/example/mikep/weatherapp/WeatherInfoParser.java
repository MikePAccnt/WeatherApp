package com.example.mikep.weatherapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WeatherInfoParser {

    private JSONObject info;
    private ArrayList<WeatherDayInfo> weatherWeek;


    private double longitude;
    private double latitude;
    private String cityName;


    public WeatherInfoParser(JSONObject info) {
        this.info = info;

        fillLocationInfo();
        fillWeatherWeek();
    }

    private void fillLocationInfo(){
        try {
            JSONObject obj = info.getJSONObject("coord");
            longitude = obj.getDouble("lon");
            latitude = obj.getDouble("lat");
            cityName = info.getJSONObject("city").getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fillWeatherWeek(){
        try {
            JSONArray week = info.getJSONArray("list");
            weatherWeek = new ArrayList<>();
            for(int i = 0; i < week.length(); i++){
                weatherWeek.add(new WeatherDayInfo(week.getJSONObject(i)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude(){
        return latitude;
    }

    public String getCityName() {
        return cityName;
    }

    public ArrayList<WeatherDayInfo> getWeatherWeek(){
        return weatherWeek;
    }

}
