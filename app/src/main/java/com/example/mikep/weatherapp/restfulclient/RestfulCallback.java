package com.example.mikep.weatherapp.restfulclient;

import android.os.Handler;
import android.os.Message;

import org.json.JSONObject;

public interface RestfulCallback extends Handler.Callback {

    void handleMessage(Message msg1, Message msg2, JSONObject returnedData);

}
