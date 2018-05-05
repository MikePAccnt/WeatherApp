package com.example.mikep.weatherapp;

import android.content.Context;
import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherInfoParser {

    private JSONObject info;

    private Location location;
    private int weatherId;
    private String weatherDescription;
    private String iconId;
    private double temperature;
    private double temperature_min;
    private double temperature_max;
    private double pressure;
    private double humidityPercent;
    private double pressure_sea;
    private double pressure_ground;
    private double wind_speed;
    private double wind_direction_deg;
    private double cloudPercent;
    private double rain3h;
    private double snow3h;
    private long time;
    private long sunriseTime;
    private long sunsetTime;
    private String cityName;


    public WeatherInfoParser(JSONObject info) {
        this.info = info;

        fillLocationInfo();
        fillWeatherInfo();
        fillMainInfo();
        fillWindInfo();
        fillCloudInfo();
        fillRainInfo();
        fillSnowInfo();
        fillTimeInfo();
        fillSystemInfo();
        fillBaseInfo();

    }

    private void fillLocationInfo(){
        try {
            JSONObject obj = info.getJSONObject("coord");
            location = new Location("");
            location.setLongitude(obj.getDouble("lon"));
            location.setLatitude(obj.getDouble("lat"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fillWeatherInfo(){
        try {
            JSONObject obj = info.getJSONObject("weather");
            weatherId = obj.getInt("id");
            weatherDescription = obj.getString("description");
            iconId = obj.getString("icon");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fillMainInfo(){
        try {
            JSONObject obj = info.getJSONObject("main");
            temperature = obj.getDouble("temp");
            temperature_min = obj.getDouble("temp_min");
            temperature_max = obj.getDouble("temp_max");
            pressure = obj.getDouble("pressure");
            humidityPercent = obj.getDouble("humidity");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fillWindInfo(){
        try {
            JSONObject obj = info.getJSONObject("wind");
            wind_speed = obj.getDouble("speed");
            wind_direction_deg = obj.getDouble("deg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fillCloudInfo(){
        try {
            JSONObject obj = info.getJSONObject("clouds");
            cloudPercent = obj.getDouble("all");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fillRainInfo(){
        try {
            JSONObject obj = info.getJSONObject("rain");
            rain3h = obj.getDouble("3h");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fillSnowInfo(){
        try {
            JSONObject obj = info.getJSONObject("snow");
            snow3h = obj.getDouble("3h");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fillTimeInfo(){
        try {
            time = info.getLong("dt");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fillSystemInfo(){
        try {
            JSONObject obj = info.getJSONObject("sys");
            sunriseTime = obj.getLong("sunrise");
            sunsetTime = obj.getLong("sunset");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fillBaseInfo(){
        try {
            cityName = info.getString("name");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Location getLocation() {
        return location;
    }

    public int getWeatherId() {
        return weatherId;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public String getIconId() {
        return iconId;
    }

    public double getTempature() {
        return temperature;
    }

    public double getTempature_min() {
        return temperature_min;
    }

    public double getTempature_max() {
        return temperature_max;
    }

    public double getPressure() {
        return pressure;
    }

    public double getHumidityPercent() {
        return humidityPercent;
    }

    public double getPressure_sea() {
        return pressure_sea;
    }

    public double getPressure_ground() {
        return pressure_ground;
    }

    public double getWind_speed() {
        return wind_speed;
    }

    public double getWind_direction_deg() {
        return wind_direction_deg;
    }

    public double getCloudPercent() {
        return cloudPercent;
    }

    public double getRain3h() {
        return rain3h;
    }

    public double getSnow3h() {
        return snow3h;
    }

    public long getTime() {
        return time;
    }

    public double getSunriseTime() {
        return sunriseTime;
    }

    public double getSunsetTime() {
        return sunsetTime;
    }

    public String getCityName() {
        return cityName;
    }
}
