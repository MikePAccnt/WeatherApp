package com.example.mikep.weatherapp;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class RestfulClient {

    public static int GET = 6700;
    public static int POST = 6701;
    public static int DELETE = 6702;

    private String baseAddress;
    private boolean useBaseAddress;
    private Context context;
    private JSONObject data;
    private Handler connectionHandler;
    private Handler.Callback callBackHandler;
    private HandlerThread connectionThread;

    public RestfulClient (Context context, Handler.Callback callBackHandler){
        this.context = context;
        this.callBackHandler = callBackHandler;

        connectionThread = new HandlerThread("RestfulClient");
        connectionThread.start();
        connectionHandler = new Handler(connectionThread.getLooper());
    }

    public RestfulClient (Context context, Handler.Callback callBackHandler, String baseAddress){
        this.context = context;
        this.baseAddress = baseAddress;
        this.callBackHandler = callBackHandler;

        connectionThread = new HandlerThread("RestfulClient");
        connectionThread.start();
        connectionHandler = new Handler(connectionThread.getLooper());
    }

    /**
     * Set the baseAdress of the RESTful api
     * @param baseAddress Base Address of a RESTful api
     */
    public void setBaseAddress(String baseAddress){
        this.baseAddress = baseAddress;
    }

    public String getBaseAddress(){
        return this.baseAddress;
    }

    /**
     * Make the baseAddress used for all api calls.
     * @param set
     */
    public void setUseBaseAddress(boolean set){
        this.useBaseAddress = set;
    }

    /**
     * Sends a Get request to a RESTful api and returns the data received from the request.
     *
     * @param address The address of the Get request
     * @throws MalformedURLException Will be thrown if the Get request fails
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void Get(String address) throws MalformedURLException{

        try {
            if(useBaseAddress){
                address = baseAddress + address;
                getRequestHelper(address);
            } else {
                getRequestHelper(address);
            }
        } catch (UncheckedIOException e) {
            MalformedURLException ex = new MalformedURLException();
            ex.setStackTrace(e.getStackTrace());
            throw ex;
        }

    }

    /**
     * Accessor for the most recent data from a GET call. To be used in the.
     * Only use this in the Handler.Callback or else the data received may not be correct.
     * @return The data from the most recent GET call to the current API.
     */
    public JSONObject getData(){
        return this.data;
    }

    private String readStreamToString(InputStream inputStream) throws IOException {

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null){
            stringBuilder.append(line);
        }
        reader.close();

        return stringBuilder.toString();

    }

    private void getRequestHelper(final String address) throws UncheckedIOException{

        connectionHandler.post(() -> {
            try {
                URL url = new URL(address);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                //Not putting data in the message because it could be bigger than what it allowed
                //inside of a Bundle.
                data = new JSONObject(readStreamToString(connection.getInputStream()));
                Message getMessage = new Message();
                getMessage.what = GET;
                callBackHandler.handleMessage(getMessage); //SOMETIMES causes android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.


            } catch (MalformedURLException e) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    throw new UncheckedIOException(e);
                } else {
                    e.addSuppressed(new MalformedURLException());
                }
            } catch (IOException e) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    throw new UncheckedIOException(e);
                } else {
                    e.addSuppressed(new IOException());
                }
            }
            catch (JSONException e) {
                Log.e("RestfulClient", e.getMessage());
            }
        });

    }

}
