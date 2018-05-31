package com.example.mikep.weatherapp.restfulclient;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

class RestfulGet implements RestfulCall {

    protected static final int GET = 6700;
    protected static final int GET_FAIL = 6703;

    private final String TAG = "RestfulGet";

    private Handler handler;
    private RestfulCallback handlerHelper;
    private final Message getMessage;
    private final Message errorMessage;

    public RestfulGet(Handler handler, RestfulCallback handlerHelper) {
        this.handler = handler;
        this.handlerHelper = handlerHelper;
        getMessage = new Message();
        getMessage.what = GET;
        errorMessage = new Message();
        errorMessage.what = GET_FAIL;
    }

    @Override
    public void requestHelper(String address, Message userMessage, JSONObject outData) {


        handler.post(() -> {
            try {
                URL url = new URL(address);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");


                JSONObject data = new JSONObject(readStreamToString(connection.getInputStream()));


                //Not putting data in the message because it could be bigger than what is allowed
                //inside of a Bundle.
                handlerHelper.handleMessage(getMessage, userMessage, data);

            } catch (MalformedURLException e) {
                Log.e(TAG, e.getMessage());
                onRequestFail(getMessage);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                onRequestFail(getMessage);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
                onRequestFail(getMessage);
            }
        });
    }

    @Override
    public void onRequestFail(Message message) {
        handlerHelper.handleMessage(message, errorMessage, null);
    }
}
