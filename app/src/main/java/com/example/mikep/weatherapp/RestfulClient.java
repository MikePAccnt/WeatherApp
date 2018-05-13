package com.example.mikep.weatherapp;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 */
public abstract class RestfulClient {

    private static final int GET = 6700;
    private static final int POST = 6701;
    private static final int DELETE = 6702;
    public static final int GET_FAIL = 6703;
    public static final int POST_FAIL = 6704;
    public static final int DELETE_FAIL = 6705;

    private String baseAddress;
    private boolean useBaseAddress;
    private Context context;
    private HandlerHelper handlerHelper;
    private Handler connectionHandler;

    public RestfulClient (Context context, Looper looper){
        this.context = context;
        this.handlerHelper = new HandlerHelper();
        this.connectionHandler = new Handler(looper, handlerHelper);
    }

    public RestfulClient (Context context, Looper looper, String baseAddress){
        this.context = context;
        this.baseAddress = baseAddress;
        this.handlerHelper = new HandlerHelper();
        this.connectionHandler = new Handler(looper, handlerHelper);
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
     * Sends a Get request to a RESTful api.
     *
     * @param address The address of the Get request. Appended to the base address
     *                if useBaseAddress is set to true.
     * @param msg A user message that can be checked in the callback method.
     */
    public void Get(String address, Message msg){

        if(useBaseAddress){
            address = baseAddress + address;
            getRequestHelper(address, msg);
        } else {
            getRequestHelper(address, msg);
        }

    }

    private void getRequestHelper(final String address, final Message userMsg){

        Message getMessage = new Message();
        getMessage.what = GET;
        boolean sentError;

        connectionHandler.post(() -> {
            try {
                URL url = new URL(address);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");


                JSONObject data = new JSONObject(readStreamToString(connection.getInputStream()));


                //Not putting data in the message because it could be bigger than what is allowed
                //inside of a Bundle.
                handlerHelper.handleMessage(getMessage, userMsg, data);

            } catch (MalformedURLException e) {
                Log.e("RestfulClient", e.getMessage());
                getFail(getMessage);
            } catch (IOException e) {
                Log.e("RestfulClient", e.getMessage());
                getFail(getMessage);
            }
            catch (JSONException e) {
                Log.e("RestfulClient", e.getMessage());
                getFail(getMessage);
            }
        });

    }

    private void getFail(Message message){
        Message errorMsg = new Message();
        errorMsg.what = GET_FAIL;
        handlerHelper.handleMessage(message, errorMsg, null);
    }

    /**
     * Sends a Post request to a RESTful api.
     *
     * @param address The address of the Post request. Appended to the base address
     *                if useBaseAddress is set to true.
     * @param msg A user message that can be checked in the callback method.
     * @param dataObject Data to send to the Post address
     */
    public void Post(String address, Message msg, JSONObject dataObject){

        if(useBaseAddress){
            address = baseAddress + address;
            postRequestHelper(address, msg, dataObject);
        } else {
            postRequestHelper(address, msg, dataObject);
        }

    }


    private void postRequestHelper(final String address, final Message userMsg, JSONObject dataObject){
        Message postMessage = new Message();
        postMessage.what = POST;

        connectionHandler.post(() -> {
            try {
                URL url = new URL(address);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                sendData(connection,dataObject);

                handlerHelper.handleMessage(postMessage,userMsg, new JSONObject(connection.getResponseMessage()));

            } catch (MalformedURLException e){
                Log.e("RestfulClient", e.getMessage());
                postFail(postMessage);
            } catch (IOException e) {
                Log.e("RestfulClient", e.getMessage());
                postFail(postMessage);
            } catch (JSONException e) {
                Log.e("RestfulClient", e.getMessage());
                postFail(postMessage);
            }
        });
    }

    private void postFail(Message message){
        Message errorMsg = new Message();
        errorMsg.what = POST_FAIL;
        handlerHelper.handleMessage(message, errorMsg, null);
    }

    /**
     * Sends a Delete request to a RESTful api.
     *
     * @param address The address of the Delete request. Appended to the base address
     *                if useBaseAddress is set to true.
     * @param msg A user message that can be checked in the callback method.
     */
    public void Delete(String address, Message msg, JSONObject dataObject){

        if(useBaseAddress){
            address = baseAddress + address;
            deleteRequestHelper(address, msg, dataObject);
        } else {
            deleteRequestHelper(address, msg, dataObject);
        }

    }


    private void deleteRequestHelper(final String address, final Message userMsg, JSONObject dataObject) {
        Message deleteMessage = new Message();
        deleteMessage.what = DELETE;

        connectionHandler.post(() -> {
            try {
                URL url = new URL(address);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("DELETE");
                connection.setDoOutput(true);

                sendData(connection, dataObject);

                handlerHelper.handleMessage(deleteMessage, userMsg, new JSONObject(connection.getResponseMessage()));

            } catch (MalformedURLException e){
                Log.e("RestfulClient", e.getMessage());
                deleteFail(deleteMessage);
            } catch (IOException e) {
                Log.e("RestfulClient", e.getMessage());
                deleteFail(deleteMessage);
            } catch (JSONException e) {
                Log.e("RestfulClient", e.getMessage());
                deleteFail(deleteMessage);
            }
        });
    }

    private void deleteFail(Message message){
        Message errorMsg = new Message();
        errorMsg.what = DELETE_FAIL;
        handlerHelper.handleMessage(message, errorMsg, null);
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

    private void sendData(HttpURLConnection connection, JSONObject data){

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
            outputStreamWriter.write(data.toString());
            outputStreamWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Callback method when a GET call is finished.
     * NOTE: THIS RUNS ON THE UI THREAD
     * @param msg user entered message unless the JSONObject fails to create.
     *            Check with RestfulClient.GET_FAIL
     * @param returnedData
     */
    protected abstract void onGetFinished(Message msg, JSONObject returnedData);

    /**
     * Callback method when a POST call is finished.
     * NOTE: THIS RUNS ON THE UI THREAD
     * @param msg user entered message unless the JSONObject fails to create.
     *            Check with RestfulClient.POST_FAIL
     * @param httpResponseMessage
     */
    protected abstract void onPostFinished(Message msg, JSONObject httpResponseMessage);

    /**
     * Callback method when a DELETE call is finished.
     * NOTE: THIS RUNS ON THE UI THREAD
     * @param msg user entered message unless the JSONObject fails to create.
     *            Check with RestfulClient.DELETE_FAIL
     * @param httpResponseMessage
     */
    protected abstract void onDeleteFinished(Message msg, JSONObject httpResponseMessage);

    private class HandlerHelper implements RestfulCallback {

        @Override
        public void handleMessage(Message msg, Message userMessage, JSONObject data) {
            int status = msg.what;
            Activity currentActivity = (Activity) context;
            if (status == GET) {
                currentActivity.runOnUiThread(() -> onGetFinished(userMessage, data));
            } else if (status == POST){
                currentActivity.runOnUiThread(() -> onPostFinished(userMessage, data));
            } else if(status == DELETE){
                currentActivity.runOnUiThread(() -> onDeleteFinished(userMessage, data));
            }
        }

        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    }

    private interface RestfulCallback extends Handler.Callback{
        public void handleMessage(Message msg1, Message msg2, JSONObject data);
    }

}