package com.example.mikep.weatherapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    private final String APIKEY = "&APPID=97850b9e2e9e9ad84ac07ab9dcf61648";
    private final int GET_WEATHER_DAY = 58485;
    private final int GET_WEATHER_WEEK = 58486;
    private final String WEATHER_DAY = "weather?";
    private final String WEATHER_WEEK = "forecast?";

    private RestfulClient client;
    private HandlerThread connectionHandlerThread;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location currentLocation;


    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private WeatherViewAdapter adapter;
    private ArrayList<WeatherDayInfo> defaultInfo;
    private ArrayList<WeatherDayInfo> info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        defaultInfo = new ArrayList<>();
        info = new ArrayList<>();
        adapter = new WeatherViewAdapter(defaultInfo);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


        connectionHandlerThread = new HandlerThread("WeatherAPI");
        connectionHandlerThread.start();

        initRestfulClient();
        setLocationInfo();

    }


    @Override
    protected void onStop() {
        super.onStop();
        connectionHandlerThread.quitSafely();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 111:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                            getApplicationContext().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    }
                    else {
                        System.exit(0);
                    }
                }
                else {
                    Log.d("PermissionRequest", "Failed");}
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        double latitude = 0;
        double longitude = 0;
        while (currentLocation == null){
            currentLocation = getLocationInfo();
            if(currentLocation != null){
                latitude = currentLocation.getLatitude();
                longitude = currentLocation.getLongitude();
            }
        }
        locationManager.removeUpdates(locationListener);
        try {
            Message dayMsg = new Message();
            dayMsg.what = GET_WEATHER_DAY;
            Message weekMsg = new Message();
            weekMsg.what = GET_WEATHER_WEEK;
            client.Get(client.getBaseAddress() + WEATHER_DAY + "lat="+latitude +"&lon="+ longitude + APIKEY, dayMsg);
            client.Get(client.getBaseAddress() + WEATHER_WEEK + "lat="+latitude +"&lon="+ longitude + APIKEY, weekMsg);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }


    private void initRestfulClient(){
        client = new RestfulClient(this, connectionHandlerThread.getLooper(), "http://api.openweathermap.org/data/2.5/") {
            @Override
            protected void onGetFinished(Message weatherMessage, JSONObject returnedData) {

                switch (weatherMessage.what){
                    case GET_WEATHER_DAY:
                        info.add(new WeatherDayInfo(returnedData));
                        break;
                    case GET_WEATHER_WEEK:
                        WeatherInfoParser temp = new WeatherInfoParser(returnedData);
                        info.addAll(temp.getWeatherWeek());
                        adapter.replaceDataSet(info);
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
    }


    private void checkPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},111);
        } else {
            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    private Location getLocationInfo(){
        checkPermission();
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    private void setLocationInfo(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

}
