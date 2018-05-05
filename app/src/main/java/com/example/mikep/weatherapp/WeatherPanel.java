package com.example.mikep.weatherapp;

import android.app.Fragment;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.Date;

public class WeatherPanel extends Fragment {

    private final String APIKEY = "&APPID=97850b9e2e9e9ad84ac07ab9dcf61648";
    private RestfulClient client;
    private HandlerThread connectionHandlerThread;
    private Looper looper;
    private WeatherInfoParser weatherInfo;

    private View thisView;

    TextView cityName;
    TextView weatherDescription;
    TextView tempText;
    TextView infoTime;

    @Override
    public void onStart() {
        super.onStart();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.weather_panel, container, false);

        connectionHandlerThread = new HandlerThread("WeatherAPI");
        connectionHandlerThread.start();
        initViewElements();
        initRestfulClient();
        try {
            client.Get(client.getBaseAddress() + "q=Obetz" + APIKEY);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return thisView;
    }

    private void initViewElements(){
        cityName = thisView.findViewById(R.id.cityName);
        tempText = thisView.findViewById(R.id.tempText);
        infoTime = thisView.findViewById(R.id.infoTime);
    }

    private void initRestfulClient(){
        client = new RestfulClient(getContext(), connectionHandlerThread.getLooper(), "http://api.openweathermap.org/data/2.5/weather?") {
            @Override
            protected void onGetFinished() {
                JSONObject object = this.getData();
                weatherInfo = new WeatherInfoParser(object);
                cityName.setText(weatherInfo.getCityName());
                tempText.setText(String.valueOf((int)kelvinToFahrenheit(weatherInfo.getTempature())));
                infoTime.setText(dateFromUTCTime(weatherInfo.getTime()));
            }
        };
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

    private String dateFromUTCTime(long UTCTime){
        Calendar calendar = Calendar.getInstance();
        TimeZone timeZone = calendar.getTimeZone();
        Date currentDate = new Date(UTCTime * 1000L);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd hh:mm a");
        dateFormat.setTimeZone(timeZone);

        return dateFormat.format(currentDate);

    }
}
