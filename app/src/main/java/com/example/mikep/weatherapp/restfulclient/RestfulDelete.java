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

public class RestfulDelete implements RestfulCall {

    protected static final int DELETE = 6702;
    protected static final int DELETE_FAIL = 6705;

    private Handler handler;
    private RestfulCallback handlerHelper;
    private final Message deleteMessage;
    private final Message errorMessage;

    public RestfulDelete(Handler handler, RestfulCallback handlerHelper) {
        this.handler = handler;
        this.handlerHelper = handlerHelper;
        deleteMessage = new Message();
        deleteMessage.what = DELETE;
        errorMessage = new Message();
        errorMessage.what = DELETE_FAIL;
    }

    @Override
    public void requestHelper(String address, Message userMessage, JSONObject outData) {

        handler.post(() -> {
            try {
                URL url = new URL(address);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("DELETE");
                connection.setDoOutput(true);

                sendData(connection, outData);

                handlerHelper.handleMessage(deleteMessage, userMessage, new JSONObject(connection.getResponseMessage()));

            } catch (MalformedURLException e){
                Log.e("RestfulClient", e.getMessage());
                onRequestFail(deleteMessage);
            } catch (IOException e) {
                Log.e("RestfulClient", e.getMessage());
                onRequestFail(deleteMessage);
            } catch (JSONException e) {
                Log.e("RestfulClient", e.getMessage());
                onRequestFail(deleteMessage);
            }
        });
    }

    @Override
    public void onRequestFail(Message message) {
        handlerHelper.handleMessage(message, errorMessage, null);
    }
}
