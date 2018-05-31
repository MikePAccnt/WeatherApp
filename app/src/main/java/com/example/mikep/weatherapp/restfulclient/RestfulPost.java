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

public class RestfulPost implements RestfulCall{

    protected static final int POST = 6701;
    protected static final int POST_FAIL = 6704;

    private Handler handler;
    private RestfulCallback handlerHelper;
    private final Message postMessage;
    private final Message errorMessage;

    public RestfulPost(Handler handler, RestfulCallback handlerHelper) {
        this.handler = handler;
        this.handlerHelper = handlerHelper;
        postMessage = new Message();
        postMessage.what = POST;
        errorMessage = new Message();
        errorMessage.what = POST_FAIL;
    }


    @Override
    public void requestHelper(String address, Message userMessage, JSONObject outData) {

        handler.post(() -> {
            try {
                URL url = new URL(address);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                sendData(connection,outData);

                handlerHelper.handleMessage(postMessage,userMessage, new JSONObject(connection.getResponseMessage()));

            } catch (MalformedURLException e){
                Log.e("RestfulClient", e.getMessage());
                onRequestFail(postMessage);
            } catch (IOException e) {
                Log.e("RestfulClient", e.getMessage());
                onRequestFail(postMessage);
            } catch (JSONException e) {
                Log.e("RestfulClient", e.getMessage());
                onRequestFail(postMessage);
            }
        });
    }

    @Override
    public void onRequestFail(Message message) {
        handlerHelper.handleMessage(message, errorMessage, null);
    }
}
