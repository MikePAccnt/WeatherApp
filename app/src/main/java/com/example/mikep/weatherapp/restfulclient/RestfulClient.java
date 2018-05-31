package com.example.mikep.weatherapp.restfulclient;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import org.json.JSONObject;

/**
 *
 */
public abstract class RestfulClient {

    private static final int GET = RestfulGet.GET;
    private static final int POST = RestfulPost.POST;
    private static final int DELETE = RestfulDelete.DELETE;
    public static final int GET_FAIL = RestfulGet.GET_FAIL;
    public static final int POST_FAIL = RestfulPost.POST_FAIL;
    public static final int DELETE_FAIL = RestfulDelete.DELETE_FAIL;

    private String baseAddress;
    private boolean useBaseAddress;
    private Context context;
    private HandlerHelper handlerHelper;
    private Handler connectionHandler;

    private RestfulGet restfulGet;
    private RestfulPost restfulPost;
    private RestfulDelete restfulDelete;

    public RestfulClient (Context context, Looper looper){
        this.context = context;
        this.handlerHelper = new HandlerHelper();
        this.connectionHandler = new Handler(looper, handlerHelper);

        restfulGet = new RestfulGet(connectionHandler, handlerHelper);
        restfulPost = new RestfulPost(connectionHandler, handlerHelper);
        restfulDelete = new RestfulDelete(connectionHandler, handlerHelper);
    }

    public RestfulClient (Context context, Looper looper, String baseAddress){
        this.context = context;
        this.baseAddress = baseAddress;
        this.handlerHelper = new HandlerHelper();
        this.connectionHandler = new Handler(looper, handlerHelper);

        restfulGet = new RestfulGet(connectionHandler, handlerHelper);
        restfulPost = new RestfulPost(connectionHandler, handlerHelper);
        restfulDelete = new RestfulDelete(connectionHandler, handlerHelper);
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
            restfulGet.requestHelper(address, msg, null);
        } else {
            restfulGet.requestHelper(address, msg, null);
        }

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
            restfulPost.requestHelper(address, msg, dataObject);
        } else {
            restfulPost.requestHelper(address, msg, dataObject);
        }

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
            restfulDelete.requestHelper(address, msg, dataObject);
        } else {
            restfulDelete.requestHelper(address, msg, dataObject);
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


}