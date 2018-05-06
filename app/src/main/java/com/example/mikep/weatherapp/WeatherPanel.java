package com.example.mikep.weatherapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.Date;

public class WeatherPanel extends Fragment {

    private static int PANELNUMBER = 0;
    private final String APIKEY = "&APPID=97850b9e2e9e9ad84ac07ab9dcf61648";
    private RestfulClient client;
    private HandlerThread connectionHandlerThread;
    private Looper looper;
    private WeatherInfoParser weatherInfo;
    private int panelNumber;
    private View thisView;
    private LinearLayout newPanelLayout;
    private AlertDialog.Builder changeCityBuilder;

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
        panelNumber = PANELNUMBER++;
        thisView = inflater.inflate(R.layout.weather_panel, container, false);
        thisView.setOnClickListener(new PanelClicked());
        connectionHandlerThread = new HandlerThread("WeatherAPI");
        connectionHandlerThread.start();
        initViewElements();
        initRestfulClient();
        initChangeCityBuilder();
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

    private void initChangeCityBuilder(){
        Activity activity = getActivity();
        View v = activity.getLayoutInflater().inflate(R.layout.new_city, null);

        changeCityBuilder = new AlertDialog.Builder(getContext());
        changeCityBuilder.setView(v);
        TextInputEditText newCity = v.findViewById(R.id.cityInput);
        changeCityBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String city = newCity.getText().toString().trim();
                try {
                    client.Get(client.getBaseAddress() + "q=" + city + APIKEY);
                    ((ViewGroup) v.getParent()).removeView(v);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
        changeCityBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((ViewGroup) v.getParent()).removeView(v);
                dialog.dismiss();
            }
        });
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

    private class PanelClicked implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            changeCityBuilder.show();
        }
    }
}