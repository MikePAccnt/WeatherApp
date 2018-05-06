package com.example.mikep.weatherapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    private WeatherPanel topPanel;
    private WeatherPanel botPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //topPanel = (WeatherPanel) getFragmentManager().findFragmentById(R.id.topPanel);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


}
