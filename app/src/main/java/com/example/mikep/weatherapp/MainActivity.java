package com.example.mikep.weatherapp;

import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.example.mikep.weatherapp.restfulclient.RestfulClient;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity{

    private final String APIKEY = "&APPID=97850b9e2e9e9ad84ac07ab9dcf61648";
    private final int GET_WEATHER_DAY = 58485;
    private final int GET_WEATHER_WEEK = 58486;
    private final String WEATHER_DAY = "weather?";
    private final String WEATHER_WEEK = "forecast?";

    private RestfulClient client;
    private HandlerThread connectionHandlerThread;
    private double lat;
    private double lon;

    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private WeatherViewAdapter adapter;
    private ArrayList<WeatherDayInfo> defaultInfo;
    private ArrayList<WeatherDayInfo> info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        lat = getIntent().getDoubleExtra(SplashActivity.LATITUDE, 0);
        lon = getIntent().getDoubleExtra(SplashActivity.LONGITUDE, 0);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        defaultInfo = new ArrayList<>();
        info = new ArrayList<>();
        adapter = new WeatherViewAdapter(defaultInfo);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


        connectionHandlerThread = new HandlerThread("WeatherAPI");
        connectionHandlerThread.start();

        initRestfulClient();

    }


    @Override
    protected void onStop() {
        super.onStop();
        connectionHandlerThread.quitSafely();
    }


    @Override
    protected void onStart() {
        super.onStart();

        Message dayMsg = new Message();
        dayMsg.what = GET_WEATHER_DAY;
        Message weekMsg = new Message();
        weekMsg.what = GET_WEATHER_WEEK;
        client.Get(WEATHER_DAY + "lat="+ lat +"&lon="+ lon + APIKEY, dayMsg);
        client.Get( WEATHER_WEEK + "lat="+ lat +"&lon="+ lon + APIKEY, weekMsg);
    }


    private void initRestfulClient(){
        client = new RestfulClient(this, connectionHandlerThread.getLooper(), "http://api.openweathermap.org/data/2.5/") {
            @Override
            protected void onGetFinished(Message weatherMessage, JSONObject returnedData) {

                switch (weatherMessage.what){
                    case GET_WEATHER_DAY:
                        info.add(new WeatherDayInfo(returnedData));
                        TextView cityName = findViewById(R.id.cityText);
                        cityName.setText(new WeatherDayInfo(returnedData).getCityName());
                        break;
                    case GET_WEATHER_WEEK:
                        WeatherInfoParser temp = new WeatherInfoParser(returnedData);
                        info.addAll(temp.getWeatherWeek());
                        adapter.replaceDataSet(info);
                        break;
                    case RestfulClient.GET_FAIL:
                        Log.d("GET Finished", "GET failed");
                        break;
                }

            }

            @Override
            protected void onPostFinished(Message msg, JSONObject returnedData) {

            }

            @Override
            protected void onDeleteFinished(Message msg, JSONObject returnedData) {

            }

        };
        client.setUseBaseAddress(true);
    }

}
