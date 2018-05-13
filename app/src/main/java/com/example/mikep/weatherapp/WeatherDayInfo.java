package com.example.mikep.weatherapp;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class WeatherDayInfo implements Parcelable {

    private JSONObject dayInfo;
    private String cityName;
    private String weatherDescription;
    private String weatherId;
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

    public WeatherDayInfo(JSONObject dayInfo) {
        this.dayInfo = dayInfo;
        fillData();
    }

    public String getCityName(){ return cityName; }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getTemperatureF(){return kelvinToFahrenheit(temperature);}

    public double getTemperatureC() {return fahrenheitToCelsius(kelvinToFahrenheit(temperature));}

    public double getTemperature_min() {
        return temperature_min;
    }

    public double getTemperature_max() {
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

    public String getDateAndTime() {
        return dateToString(dateFromUTCTime(time));
    }

    public String getDayOfWeek(){
        return dateToDay(dateFromUTCTime(time));
    }

    public long getSunriseTime() {
        return sunriseTime;
    }

    public long getSunsetTime() {
        return sunsetTime;
    }

    private void fillData(){

        try {
            time = dayInfo.getLong("dt");
            cityName = dayInfo.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject main = dayInfo.getJSONObject("main");
            temperature = main.getDouble("temp");
            temperature_max = main.getDouble("temp_max");
            temperature_min = main.getDouble("temp_min");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject weather = dayInfo.getJSONArray("weather").getJSONObject(0);
            weatherDescription = weather.getString("description");
            weatherId = weather.getString("icon");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject clouds = dayInfo.getJSONObject("clouds");
            cloudPercent = clouds.getDouble("all");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject wind = dayInfo.getJSONObject("wind");
            wind_speed = wind.getDouble("speed");
            wind_direction_deg = wind.getDouble("deg");
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    private double kelvinToFahrenheit(double kelvinTemp){
        return (9.0/5.0) * (kelvinTemp - 273.0) + 32;
    }

    private double fahrenheitToCelsius(double fahrenheitTemp){
        return (5.0/9.0) * (fahrenheitTemp -32.0);
    }

    private double celsiusToFahrenheit(double celsiusTemp){
        return (9.0/5.0) * (celsiusTemp) + 32.0;
    }

    private Date dateFromUTCTime(long UTCTime){

        return new Date(UTCTime * 1000L);

    }

    private String dateToString(Date date){
        Calendar calendar = Calendar.getInstance();
        TimeZone timeZone = calendar.getTimeZone();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd hh:mm a");
        dateFormat.setTimeZone(timeZone);

        return dateFormat.format(date);
    }

    private String dateToDay(Date date){
        Calendar calendar = Calendar.getInstance();
        TimeZone timeZone = calendar.getTimeZone();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
        dateFormat.setTimeZone(timeZone);

        return dateFormat.format(date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(time);
        dest.writeDouble(temperature);
        dest.writeDouble(temperature_max);
        dest.writeDouble(temperature_min);
        dest.writeString(weatherDescription);
        dest.writeString(weatherId);
        dest.writeDouble(cloudPercent);
        dest.writeDouble(wind_speed);
        dest.writeDouble(wind_direction_deg);

    }

    private WeatherDayInfo(Parcel in){
        time = in.readLong();
        temperature = in.readDouble();
        temperature_max = in.readDouble();
        temperature_min = in.readDouble();
        weatherDescription = in.readString();
        weatherId = in.readString();
        cloudPercent = in.readDouble();
        wind_speed = in.readDouble();
        wind_direction_deg = in.readDouble();
    }

    public static final Creator CREATOR = new Creator() {

        @Override
        public Object createFromParcel(Parcel source) {
            return new WeatherDayInfo(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new WeatherDayInfo[size];
        }
    };
}
