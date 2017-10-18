package com.shuishou.malatang.http;

import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuishou.malatang.InstantValue;
import com.shuishou.malatang.R;
import com.shuishou.malatang.bean.Desk;
import com.shuishou.malatang.bean.Dish;
import com.shuishou.malatang.bean.HttpResult;
import com.shuishou.malatang.bean.Indent;
import com.shuishou.malatang.ui.MainActivity;
import com.shuishou.malatang.utils.CommonTool;
import com.yanzhenjie.nohttp.FileBinary;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.download.DownloadQueue;
import com.yanzhenjie.nohttp.download.DownloadRequest;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Administrator on 2017/6/9.
 */

public class HttpOperator {

    private String logTag = "HttpOperation";


    private MainActivity mainActivity;
//    private ArrayList<String> listDishPictures = new ArrayList<>();
    private static final int WHAT_VALUE_QUERYDESK = 4;
    private static final int WHAT_VALUE_QUERYCONFIRMCODE = 6;

    private Gson gson = new Gson();

    private OnResponseListener responseListener =  new OnResponseListener<JSONObject>() {
        @Override
        public void onStart(int what) {
        }

        @Override
        public void onSucceed(int what, Response<JSONObject> response) {
            switch (what){
                case WHAT_VALUE_QUERYDESK :
                    doResponseQueryDesk(response);
                    break;
                case WHAT_VALUE_QUERYCONFIRMCODE:
                    doResponseQueryConfirmCode(response);
                    break;
                default:
            }
        }

        @Override
        public void onFailed(int what, Response<JSONObject> response) {
            Log.e("Http failed", "what = "+ what + "\nresponse = "+ response.get());
            String msg = "";
            switch (what){
                case WHAT_VALUE_QUERYCONFIRMCODE :
                    msg = "Failed to load Confirm Code. Please restart app!";
                    break;
                case WHAT_VALUE_QUERYDESK :
                    msg = "Failed to load Desk data. Please restart app!";
                    break;
            }
            new AlertDialog.Builder(mainActivity)
                    .setIcon(R.drawable.error)
                    .setTitle("WRONG")
                    .setMessage(msg)
                    .setNegativeButton("OK", null)
                    .create().show();
        }

        @Override
        public void onFinish(int what) {
        }
    };

    private RequestQueue requestQueue = NoHttp.newRequestQueue();

    public HttpOperator(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    private void sendErrorMessageToToast(String sMsg){
        mainActivity.getToastHandler().sendMessage(CommonTool.buildMessage(MainActivity.TOASTHANDLERWHAT_ERRORMESSAGE,sMsg));
    }

    private void doResponseQueryConfirmCode(Response<JSONObject> response){
        if (response.getException() != null){
            Log.e(logTag, "doResponseQueryMenu: " + response.getException().getMessage() );
            sendErrorMessageToToast("Http:doResponseQueryMenu: " + response.getException().getMessage());
            return;
        }
        HttpResult<String> result = gson.fromJson(response.get().toString(), new TypeToken<HttpResult<String>>(){}.getType());
        if (result.success){
            mainActivity.setConfirmCode(result.data);
        } else {
            Log.e(logTag, "doResponseQueryMenu: get FALSE for query confirm code");
        }
    }

    private void doResponseQueryDesk(Response<JSONObject> response){
        if (response.getException() != null){
            Log.e(logTag, "doResponseQueryDesk: " + response.getException().getMessage() );
            sendErrorMessageToToast("Http:doResponseQueryDesk: " + response.getException().getMessage());
            return;
        }
        HttpResult<ArrayList<Desk>> result = gson.fromJson(response.get().toString(), new TypeToken<HttpResult<ArrayList<Desk>>>(){}.getType());
        if (result.success){
            ArrayList<Desk> desks = result.data;
            Collections.sort(desks, new Comparator<Desk>() {
                @Override
                public int compare(Desk o1, Desk o2) {
                    return o1.getId() - o2.getId();
                }
            });
            mainActivity.setDesk(result.data);
            mainActivity.persistDesk();
        } else {
            new AlertDialog.Builder(mainActivity)
                    .setIcon(R.drawable.error)
                    .setTitle("WRONG")
                    .setMessage("Failed to load Desk data. Please restart app!")
                    .setNegativeButton("OK", null)
                    .create().show();
        }
    }


    //load desk
    public void loadDeskData(){
        mainActivity.getProgressDlgHandler().sendMessage(CommonTool.buildMessage(MainActivity.PROGRESSDLGHANDLER_MSGWHAT_STARTLOADDATA,
                "start loading table data ..."));
        Request<JSONObject> deskRequest = NoHttp.createJsonObjectRequest(InstantValue.URL_TOMCAT + "/common/getdesks");
        requestQueue.add(WHAT_VALUE_QUERYDESK, deskRequest, responseListener);
    }

    /**
     * check the desk if available for making order.
     * @param deskName
     * @return "AVAILABLE": order is available; "OCCUPIED": there is an order already on this desk; other result for exception;
     */
    public String checkDeskStatus(String deskName){
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(InstantValue.URL_TOMCAT + "/indent/queryindent", RequestMethod.GET);
        request.add("deskname", deskName);
        request.add("status", "Unpaid");
        Response<JSONObject> response = NoHttp.startRequestSync(request);
        if (response.getException() != null){
            return response.getException().getMessage();
        }
        if (response.get() == null) {
            Log.e(logTag, "Error occur while check desk available for making order. response.get() is null.");
            return "Error occur while check desk available for making order. response.get() is null";
        }
        HttpResult<ArrayList<Indent>> result = gson.fromJson(response.get().toString(), new TypeToken<HttpResult<ArrayList<Indent>>>(){}.getType());
        if (result.data == null || result.data.isEmpty()){
            return InstantValue.CHECKDESK4MAKEORDER_AVAILABLE;
        } else {
            return InstantValue.CHECKDESK4MAKEORDER_OCCUPIED;
        }
    }

    /**
     * first check the CONFIRM CODE, if it is right, make order
     * @param orders
     * @param deskid
     */
    public HttpResult<Integer> makeOrder(String code, String orders, int deskid, int customerAmount){
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(InstantValue.URL_TOMCAT + "/indent/makeindent", RequestMethod.POST);
        request.add("confirmCode", code);
        request.add("indents", orders);
        request.add("deskid", deskid);
        request.add("customerAmount", customerAmount);
        Response<JSONObject> response = NoHttp.startRequestSync(request);

        if (response.getException() != null){
            HttpResult<Integer> result = new HttpResult<>();
            result.result = response.getException().getMessage();
            return result;
        }
        if (response.get() == null) {
            Log.e(logTag, "Error occur while make order. response.get() is null.");
            HttpResult<Integer> result = new HttpResult<>();
            result.result = "Error occur while make order. response.get() is null";
            return result;
        }
        HttpResult<Integer> result = gson.fromJson(response.get().toString(), new TypeToken<HttpResult<Integer>>(){}.getType());
        return result;
    }

    public HttpResult<Integer> addDishToOrder(int deskid, String orders){
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(InstantValue.URL_TOMCAT + "/indent/adddishtoindent", RequestMethod.POST);
        request.add("indents", orders);
        request.add("deskid", deskid);

        Response<JSONObject> response = NoHttp.startRequestSync(request);
        if (response.getException() != null){
            HttpResult<Integer> result = new HttpResult<>();
            result.result = response.getException().getMessage();
            return result;
        }
        if (response.get() == null) {
            Log.e(logTag, "Error occur while add dish to order. response.get() is null.");
            HttpResult<Integer> result = new HttpResult<>();
            result.result = "Error occur while add dish to order. response.get() is null";
            return result;

        }
        HttpResult<Integer> result = gson.fromJson(response.get().toString(), new TypeToken<HttpResult<Integer>>(){}.getType());
        return result;
    }

    public void uploadErrorLog(File file, String machineCode) throws FileNotFoundException {
        int key = 0;// the key of filelist;
        UploadErrorLogListener listener = new UploadErrorLogListener(mainActivity);
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(InstantValue.URL_TOMCAT + "/common/uploaderrorlog", RequestMethod.POST);
        FileBinary bin1 = new FileBinary(file);
        request.add("logfile", bin1);
        request.add("machineCode", machineCode);
        listener.addFiletoList(key, file.getAbsolutePath());
        requestQueue.add(key, request, listener);
    }

    public Dish getDishByNameSync(String dishName){
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(InstantValue.URL_TOMCAT + "/menu/querydishbyname", RequestMethod.POST);
        request.add("dishName", dishName);
        Response<JSONObject> response = NoHttp.startRequestSync(request);
        if (response.getException() != null ){
            Log.e(logTag, response.getException().getMessage());
            return null;
        }
        if (response.get() == null){
            Log.e(logTag, "Error occur while get dish by name " + dishName + ". response.get() is null.");
            return null;
        }
        HttpResult<Dish> result = gson.fromJson(response.get().toString(), new TypeToken<HttpResult<Dish>>(){}.getType());
        return result.data;
    }

    public void queryConfirmCode(){
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(InstantValue.URL_TOMCAT + "/common/getconfirmcode", RequestMethod.GET);
        requestQueue.add(WHAT_VALUE_QUERYCONFIRMCODE, request, responseListener);
    }
}
