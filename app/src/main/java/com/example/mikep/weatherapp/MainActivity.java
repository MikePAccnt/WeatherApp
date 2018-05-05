package com.example.mikep.weatherapp;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    private WeatherPanel topPanel;
    private WeatherPanel botPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        topPanel = (WeatherPanel) getFragmentManager().findFragmentById(R.id.topPanel);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


}
