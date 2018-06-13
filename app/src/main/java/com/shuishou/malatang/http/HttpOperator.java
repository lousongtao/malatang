package com.shuishou.malatang.http;

import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shuishou.malatang.InstantValue;
import com.shuishou.malatang.R;
import com.shuishou.malatang.bean.ChoosedFood;
import com.shuishou.malatang.bean.Desk;
import com.shuishou.malatang.bean.Dish;
import com.shuishou.malatang.bean.HttpResult;
import com.shuishou.malatang.bean.Indent;
import com.shuishou.malatang.bean.IndentDetail;
import com.shuishou.malatang.ui.MainActivity;
import com.shuishou.malatang.utils.CommonTool;
import com.yanzhenjie.nohttp.FileBinary;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/6/9.
 */

public class HttpOperator {

    private final String logTag = "HttpOperation";


    private MainActivity mainActivity;
//    private static final int WHAT_VALUE_QUERYINDENTBYDESK = 1;
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
//                case WHAT_VALUE_QUERYINDENTBYDESK:
//                    doResponseQueryIndentByDesk(response);
//                    break;
                default:
            }
        }

        @Override
        public void onFailed(int what, Response<JSONObject> response) {
            Log.e("Http failed", "what = "+ what + "\nresponse = "+ response.get());
            MainActivity.LOG.error("Http failed, what = "+ what + "\nresponse = "+ response.get());
            String msg = "";
            switch (what){
                case WHAT_VALUE_QUERYCONFIRMCODE :
                    msg = "Failed to load Confirm Code. Please restart app!";
                    break;
                case WHAT_VALUE_QUERYDESK :
                    msg = "Failed to load Desk data. Please restart app!";
                    break;
//                case WHAT_VALUE_QUERYINDENTBYDESK:
//                    msg = "Failed to load indent by desk, Please restart app!";
//                    break;
            }
            CommonTool.popupWarnDialog(mainActivity, R.drawable.error, "WRONG", msg);
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
            Log.e(logTag, "doResponseQueryConfirmCode: " + response.getException().getMessage() );
            MainActivity.LOG.error("Exception occur in doResponseQueryConfirmCode: " + response.getException().getMessage());
            sendErrorMessageToToast("Http:doResponseQueryConfirmCode: " + response.getException().getMessage());
            return;
        }
        HttpResult<HashMap> result = gson.fromJson(response.get().toString(), new TypeToken<HttpResult<HashMap>>(){}.getType());
        if (result.success){
            mainActivity.setConfirmCode(result.data.get(InstantValue.CONFIGS_CONFIRMCODE).toString());
        } else {
            Log.e(logTag, "doResponseQueryConfirmCode: get FALSE for query confirm code");
            MainActivity.LOG.error("doResponseQueryConfirmCode: get FALSE for query confirm code");
        }
    }

    private void doResponseQueryDesk(Response<JSONObject> response){
        if (response.getException() != null){
            Log.e(logTag, "doResponseQueryDesk: " + response.getException().getMessage() );
            MainActivity.LOG.error("Exception occur in doResponseQueryDesk: " + response.getException().getMessage());
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
            mainActivity.buildDesks();
        } else {
            CommonTool.popupWarnDialog(mainActivity, R.drawable.error, "WRONG", "Failed to load Desk data. Please restart app!");
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
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(InstantValue.URL_TOMCAT + "/indent/queryindent", RequestMethod.POST);
        request.add("deskname", deskName);
        request.add("status", "Unpaid");
//        request.setContentType("UTF-8");
        Response<JSONObject> response = NoHttp.startRequestSync(request);
        if (response.getException() != null){
            return response.getException().getMessage();
        }
        if (response.get() == null) {
            Log.e(logTag, "Error occur while check desk available for making order. response.get() is null.");
            MainActivity.LOG.error("Error occur while check desk available for making order. response.get() is null.");
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
            MainActivity.LOG.error("Error occur while make order. response.get() is null.");
            HttpResult<Integer> result = new HttpResult<>();
            result.result = "Error occur while make order. response.get() is null";
            return result;
        }
        return gson.fromJson(response.get().toString(), new TypeToken<HttpResult<Integer>>(){}.getType());
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
            MainActivity.LOG.error("Error occur while add dish to order. response.get() is null.");
            HttpResult<Integer> result = new HttpResult<>();
            result.result = "Error occur while add dish to order. response.get() is null";
            return result;

        }
        return gson.fromJson(response.get().toString(), new TypeToken<HttpResult<Integer>>(){}.getType());
    }

    public void uploadErrorLog(File file, String machineCode) {
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
            MainActivity.LOG.error("Exception occur in getDishByNameSync "+response.getException().getMessage());
            return null;
        }
        if (response.get() == null){
            Log.e(logTag, "Error occur while get dish by name " + dishName + ". response.get() is null.");
            MainActivity.LOG.error("Error occur while get dish by name " + dishName + ". response.get() is null.");
            return null;
        }
        HttpResult<Dish> result = gson.fromJson(response.get().toString(), new TypeToken<HttpResult<Dish>>(){}.getType());
        return result.data;
    }

    public void queryConfirmCode(){
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(InstantValue.URL_TOMCAT + "/common/queryconfigmap", RequestMethod.GET);
        requestQueue.add(WHAT_VALUE_QUERYCONFIRMCODE, request, responseListener);
    }

    public void queryIndentForDesk(String deskName){
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(InstantValue.URL_TOMCAT + "/indent/queryindent", RequestMethod.POST);
        request.add("deskname", deskName);
        request.add("status", "Unpaid");
        Response<JSONObject> response = NoHttp.startRequestSync(request);
        if (response.getException() != null){
            Log.e(logTag, "doResponseQueryIndentByDesk: " + response.getException().getMessage() );
            MainActivity.LOG.error("Exception occur in doResponseQueryIndentByDesk: " + response.getException().getMessage());
            sendErrorMessageToToast("Http:doResponseQueryIndentByDesk: " + response.getException().getMessage());
            return;
        }
        HttpResult<ArrayList<Indent>> result = gson.fromJson(response.get().toString(), new TypeToken<HttpResult<ArrayList<Indent>>>(){}.getType());
        if (result.success){
            if (result.data == null || result.data.isEmpty()){
                mainActivity.setChoosedFoodList(new ArrayList<ChoosedFood>());
            } else if (result.data.size() > 1){
                CommonTool.popupWarnDialog(mainActivity, R.drawable.error, "WRONG",
                        "Find more indents on this desk. There should have dirty data. Please report to manager!");
            } else {
                Indent indent = result.data.get(0);
                ArrayList<IndentDetail> details = indent.items;
                for (int i = 0; i < details.size(); i++) {
                    IndentDetail detail = details.get(i);
                    if (detail.dishId == mainActivity.getDish().getId()){
                        ChoosedFood cf = new ChoosedFood("", 0, 0, detail.additionalRequirements, false);
                        mainActivity.addChoosedFoodToList(cf);
                    }
                }

            }
        } else {
            CommonTool.popupWarnDialog(mainActivity, R.drawable.error, "WRONG", "Failed to load indent by this desk. Please restart app!");
        }
    }

}
