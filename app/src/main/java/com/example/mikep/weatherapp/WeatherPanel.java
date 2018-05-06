package com.example.mikep.weatherapp;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.Date;

public class WeatherPanel extends Fragment implements Handler.Callback{

    private final String APIKEY = "&APPID=97850b9e2e9e9ad84ac07ab9dcf61648";
    private RestfulClient client;
    public static Handler handler;
    private WeatherInfoParser weatherInfo;

    private View thisView;

    TextView cityName;
    TextView weatherDescription;
    TextView tempText;
    TextView infoTime;

    @Override
    public void onStart() {
        super.onStart();
        initRestfulClient();
        try {
            client.Get(client.getBaseAddress() + "q=Obetz" + APIKEY);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.weather_panel, container, false);
        initViewElements();
        return thisView;
    }

    private void initViewElements(){
        cityName = thisView.findViewById(R.id.cityName);
        tempText = thisView.findViewById(R.id.tempText);
        infoTime = thisView.findViewById(R.id.infoTime);
    }

    @SuppressLint("HandlerLeak")
    private void initRestfulClient(){
//        handler = new Handler(Looper.getMainLooper()) {
//            @Override
//            public void handleMessage(Message msg) {
//
//
//                super.handleMessage(msg);
//            }
//        };
        client = new RestfulClient(getContext(), this, "http://api.openweathermap.org/data/2.5/weather?");
    }

    private void fillViewItems(){
        cityName.setText(weatherInfo.getCityName());
        tempText.setText(String.valueOf((int)kelvinToFahrenheit(weatherInfo.getTempature())));
        infoTime.setText(dateFromUTCTime(weatherInfo.getTime()));
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

    @Override
    public boolean handleMessage(Message msg) {
        int what = msg.what;

        if(what == RestfulClient.GET){
            JSONObject obj = client.getData();
            weatherInfo = new WeatherInfoParser(obj);
            getActivity().runOnUiThread(this::fillViewItems);

            return true;
        }
        return false;
    }
}
