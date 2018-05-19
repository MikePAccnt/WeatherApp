package com.example.mikep.weatherapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.HandlerThread;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    public static String LONGITUDE = "SPLASH.ACTIVITY.LONGITUDE";
    public static String LATITUDE = "SPLASH.ACTIVITY.LATITUDE";

    private static final String TAG = "SplashActivity";
    private final int PERMISSION_CODE = 111;
    private double lat = 40.7306;
    private double lon = -73.9867;

    ArrayList<ImageView> imageViews;
    @BindView(R.id.imageOne) ImageView imageView1;
    @BindView(R.id.imageTwo) ImageView imageView2;
    @BindView(R.id.imageThree) ImageView imageView3;
    @BindView(R.id.progressText) TextView progressText;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    private Animation fadeAnimation1;
    private Animation fadeAnimation2;
    private Animation fadeAnimation3;
    private boolean animationFinished = false;
    private boolean wentToSettings = false;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private HandlerThread handlerThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        ButterKnife.bind(this);

        handlerThread = new HandlerThread("Location Handler");
        handlerThread.start();

        lat = 40.7306;
        lon = -73.9867;
        setLocationInfo();
        initAnimation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!animationFinished)
            imageView1.startAnimation(fadeAnimation1);

    }



    @Override
    protected void onRestart() {
        super.onRestart();
        if(animationFinished && wentToSettings){
            //If the user didn't turn on location after being sent to settings
            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                Toast.makeText(getApplicationContext(), "Location Access Required, using default location", Toast.LENGTH_LONG).show();
                goToMainActivity();
            } else {
                ((Runnable) this::getCurrentLocation).run();
            }
        }
    }

    private void goToMainActivity(){
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.putExtra(LATITUDE, lat);
        intent.putExtra(LONGITUDE, lon);
        startActivity(intent);
        finish();
    }

    //This can probably be simplified but for now it works.
    private void initAnimation(){
        imageViews = new ArrayList<>();
        imageViews.add(imageView1);
        imageViews.add(imageView2);
        imageViews.add(imageView3);

        fadeAnimation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_anim);
        fadeAnimation1.setFillAfter(true);
        fadeAnimation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_anim);
        fadeAnimation2.setFillAfter(true);
        fadeAnimation3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_anim);
        fadeAnimation3.setFillAfter(true);

        fadeAnimation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                animation.start();
                imageView1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageView2.startAnimation(fadeAnimation2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fadeAnimation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                animation.start();
                imageView2.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageView3.startAnimation(fadeAnimation3);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fadeAnimation3.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                animation.start();
                imageView3.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animationFinished = true;
                askForPermissions();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    //Only has one permission so the arrayList isn't helpful right now
    //however it helps if future permissions are needed to be added.
    private void askForPermissions(){
        final int PERMISSION_ACCESS_LOCATION = getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

        ArrayList<String> permissions = new ArrayList<>();

        if(PERMISSION_ACCESS_LOCATION == PackageManager.PERMISSION_DENIED){
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        else {
            checkForLocationEnabled();
        }

        if(permissions.size() != 0) {
            requestPermissions(permissions.toArray((new String[permissions.size()])), PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_CODE:
                Arrays.stream(permissions).forEach(s -> Log.d(TAG, "Permission: " + s));
                if(grantResults.length > 0){
                    boolean[] permissionsAccepted = new boolean[grantResults.length];
                    for(int i = 0; i < grantResults.length; i++){
                        permissionsAccepted[i] = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                    }

                    for(int i = 0; i < permissionsAccepted.length; i++){
                        if(!permissionsAccepted[i]){
                            if(permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)){
                                //TODO: Check for stored backup data to display old weather info

                                //If no old weather info, have a default location for weather info
                                Toast.makeText(getApplicationContext(), "Location Access Required, using default location", Toast.LENGTH_LONG).show();
                                goToMainActivity();
                            }
                        } else {
                            //TODO: Fix this. Not being called when permission is accepted.
                            if(permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)){
                                checkForLocationEnabled();
                            }
                        }
                    }
                }
        }
    }

    private void checkForLocationEnabled(){
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            wentToSettings = true;
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
        else {
            getCurrentLocation();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation(){

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener, handlerThread.getLooper());

        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);

        new Thread(() -> {
            //Need to keep trying because accessing lastLocation right after starting updates means
            //it might not have had any locations yet.
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            while(location == null){
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            locationManager.removeUpdates(locationListener);
            handlerThread.quitSafely();

            lat = location.getLatitude();
            lon = location.getLongitude();

            Log.d(TAG, "Lat: " + lat);
            Log.d(TAG, "Lon: " + lon);

            goToMainActivity();
        }).start();

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
