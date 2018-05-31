package com.example.mikep.weatherapp.restfulclient;

import android.os.Message;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

public interface RestfulCall {

    void requestHelper(final String address, final Message userMessage, JSONObject outData);

    void onRequestFail(Message message);

    //These two methods should be moved later on to a different class
    default String readStreamToString(InputStream inputStream) throws IOException {

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

    default void sendData(HttpURLConnection connection, JSONObject data){

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
            outputStreamWriter.write(data.toString());
            outputStreamWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
