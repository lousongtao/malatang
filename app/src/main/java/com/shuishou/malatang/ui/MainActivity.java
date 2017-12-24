package com.shuishou.malatang.ui;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.shuishou.malatang.InstantValue;
import com.shuishou.malatang.R;
import com.shuishou.malatang.bean.ChoosedFood;
import com.shuishou.malatang.bean.Desk;
import com.shuishou.malatang.bean.Dish;
import com.shuishou.malatang.bean.HttpResult;
import com.shuishou.malatang.db.DBOperator;
import com.shuishou.malatang.http.HttpOperator;
import com.shuishou.malatang.io.IOOperator;
import com.shuishou.malatang.utils.CommonTool;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.NoHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import pl.brightinventions.slf4android.LoggerConfiguration;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static final org.slf4j.Logger LOG = LoggerFactory.getLogger(MainActivity.class.getSimpleName());

    private Dish dish;
    private ArrayList<Desk> desks;
    private ArrayList<DeskCell> deskCellList = new ArrayList<>();

    private String confirmCode;

    private HttpOperator httpOperator;
    private DBOperator dbOperator;

    private ChoosedFoodAdapter choosedFoodAdapter;
    private ArrayList<ChoosedFood> choosedFoodList = new ArrayList<>();

    private TextView tvRefreshData;
    private TextView tvServerURL;
    private TextView tvDishName;
    private TextView tvUploadErrorLog;
    private TextView tvExit;
    private Button btnMakeOrder;
    private Button btnAddToList;
    private Button btnGetWeight;
    private TextView tvPrice;
    private EditText txtWeight;
    private TableLayout deskAreaLayout;
    private SwitchCompat btnNochilli;
    private SwitchCompat btnLittlechilli;
    private SwitchCompat btnMiddlechilli;
    private SwitchCompat btnMorechilli;
    private SwitchCompat btnAddPeanut;
    private SwitchCompat btnAddOnion;
    private SwitchCompat btnAddSesame;
    private SwitchCompat btnAddCaraway;
    private RadioButton rbNo1;
    private RadioButton rbNo2;
    private RadioButton rbNo3;
    private RadioButton rbNo4;
    private RadioButton rbNo5;
    private RadioButton rbNo6;
    private RadioButton rbNo7;
    private EditText txNoManual;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ListView lvChoosedFood = (ListView) findViewById(R.id.list_choosedfood);
        choosedFoodAdapter = new ChoosedFoodAdapter(this, R.layout.choosedfood_layout, choosedFoodList);
        lvChoosedFood.setAdapter(choosedFoodAdapter);
        tvRefreshData = (TextView)findViewById(R.id.drawermenu_refreshdata);
        tvServerURL = (TextView)findViewById(R.id.drawermenu_serverurl);
        tvUploadErrorLog = (TextView)findViewById(R.id.drawermenu_uploaderrorlog);
        tvExit = (TextView)findViewById(R.id.drawermenu_exit);
        tvDishName = (TextView)findViewById(R.id.drawmenu_dishname);
        btnMakeOrder = (Button)findViewById(R.id.btnMakeOrder);
        btnAddToList = (Button)findViewById(R.id.btnAddToList);
        btnGetWeight = (Button)findViewById(R.id.btnGetWeight);
        txtWeight = (EditText)findViewById(R.id.txtWeight);
        deskAreaLayout = (TableLayout)findViewById(R.id.deskAreaLayout);
        tvPrice = (TextView)findViewById(R.id.tvPrice);
        btnNochilli = (SwitchCompat)findViewById(R.id.btnNochilli);
        btnLittlechilli = (SwitchCompat)findViewById(R.id.btnLittlechilli);
        btnMiddlechilli = (SwitchCompat)findViewById(R.id.btnMiddlechilli);
        btnMorechilli = (SwitchCompat)findViewById(R.id.btnMorechilli);
        btnAddPeanut = (SwitchCompat)findViewById(R.id.btnAddPeanut);
        btnAddOnion = (SwitchCompat)findViewById(R.id.btnAddOnion);
        btnAddSesame = (SwitchCompat) findViewById(R.id.btnAddSesame);
        btnAddCaraway = (SwitchCompat) findViewById(R.id.btnAddCaraway);
        rbNo1 = (RadioButton)findViewById(R.id.rb1);
        rbNo2 = (RadioButton)findViewById(R.id.rb2);
        rbNo3 = (RadioButton)findViewById(R.id.rb3);
        rbNo4 = (RadioButton)findViewById(R.id.rb4);
        rbNo5 = (RadioButton)findViewById(R.id.rb5);
        rbNo6 = (RadioButton)findViewById(R.id.rb6);
        rbNo7 = (RadioButton)findViewById(R.id.rb7);
        txNoManual = (EditText)findViewById(R.id.txtOtherNo);

        tvUploadErrorLog.setOnClickListener(this);
        btnAddToList.setOnClickListener(this);
        tvRefreshData.setOnClickListener(this);
        tvExit.setOnClickListener(this);
        tvDishName.setOnClickListener(this);
        tvServerURL.setOnClickListener(this);
        btnGetWeight.setOnClickListener(this);
        btnMakeOrder.setOnClickListener(this);
        btnNochilli.setOnClickListener(this);
        btnLittlechilli.setOnClickListener(this);
        btnMiddlechilli.setOnClickListener(this);
        btnMorechilli.setOnClickListener(this);

        txtWeight.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || s.length() == 0){
                    tvPrice.setText(InstantValue.NULLSTRING);
                } else if (dish != null) {
                    tvPrice.setText(InstantValue.PRICE + String.format(InstantValue.FORMAT_DOUBLE_2DECIMAL, calculatePrice(Double.parseDouble(s.toString()))));
                }
            }
        });
        //init tool class, NoHttp
        NoHttp.initialize(this);
        Logger.setDebug(true);
        Logger.setTag("malatang:nohttp");

        InstantValue.URL_TOMCAT = IOOperator.loadServerURL(InstantValue.FILE_SERVERURL);
        httpOperator = new HttpOperator(this);
        dbOperator = new DBOperator(this);

        //read local database to memory
        desks = dbOperator.queryDesks();
        loadConfirmCode();

        loadDish();
        buildDesks();
    }

    private double calculatePrice(double weight){
        return dish.getPrice() * weight;
    }

    private void loadConfirmCode(){
        if (InstantValue.URL_TOMCAT != null && InstantValue.URL_TOMCAT.length() > 0)
            httpOperator.queryConfirmCode();
    }
    private void loadDish(){
        new Thread(){
            @Override
            public void run() {
                if (InstantValue.URL_TOMCAT != null && InstantValue.URL_TOMCAT.length() > 0)
                    dish = httpOperator.getDishByNameSync(IOOperator.loadDishName(InstantValue.FILE_DISHNAME));
            }
        }.start();
    }

    public void buildDesks(){
        deskAreaLayout.removeAllViews();
        if (desks == null || desks.isEmpty())
            return;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        TableRow.LayoutParams trlp = new TableRow.LayoutParams();
        trlp.topMargin = 5;
        trlp.leftMargin = 5;
//        int width = (screenWidth - InstantValue.DESKCELLAMOUNTPERROW * trlp.leftMargin)/InstantValue.DESKCELLAMOUNTPERROW;
        TableRow tr = null;
        for (int i = 0; i < desks.size(); i++) {
            if (i % InstantValue.DESKCELLAMOUNTPERROW == 0){
                tr = new TableRow(this);
                deskAreaLayout.addView(tr);
            }
            DeskCell di = new DeskCell(this, desks.get(i), InstantValue.DESKCELLWIDTH, InstantValue.DESKCELLHEIGHT);
            deskCellList.add(di);
            tr.addView(di, trlp);
        }
    }

    public DBOperator getDbOperator(){
        return dbOperator;
    }

    public HttpOperator getHttpOperator(){
        return httpOperator;
    }

    public void persistDesk(){
        dbOperator.clearDesk();
        dbOperator.saveObjectsByCascade(desks);
    }

    @Override
    public void onClick(View v) {
        if (tvUploadErrorLog == v){
            IOOperator.onUploadErrorLog(this);
        } else if (v == tvRefreshData){
            onRefreshData();
        } else if (v == tvServerURL){
            SaveServerURLDialog dlg = new SaveServerURLDialog(MainActivity.this);
            dlg.showDialog();
        } else if (v == tvExit){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirm")
                    .setIcon(R.drawable.info)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.this.finish();
                        }
                    })
                    .setNegativeButton("No", null);
            builder.create().show();
        } else if (v == btnGetWeight){

        } else if (v == btnAddToList){
            doAddToList();
        } else if (v == btnMakeOrder){
            doMakeOrder();
        } else if (v == tvDishName){
            SaveDishDialog dlg = new SaveDishDialog(MainActivity.this);
            dlg.showDialog();
        } else if (v == btnNochilli){
            btnNochilli.setChecked(true);
            btnLittlechilli.setChecked(false);
            btnMiddlechilli.setChecked(false);
            btnMorechilli.setChecked(false);
        } else if (v == btnLittlechilli){
            btnNochilli.setChecked(false);
            btnLittlechilli.setChecked(true);
            btnMiddlechilli.setChecked(false);
            btnMorechilli.setChecked(false);
        } else if (v == btnMiddlechilli){
            btnNochilli.setChecked(false);
            btnLittlechilli.setChecked(false);
            btnMiddlechilli.setChecked(true);
            btnMorechilli.setChecked(false);
        } else if (v == btnMorechilli){
            btnNochilli.setChecked(false);
            btnLittlechilli.setChecked(false);
            btnMiddlechilli.setChecked(false);
            btnMorechilli.setChecked(true);
        }
    }

    private void doAddToList(){
        if (txtWeight.getText() == null || txtWeight.getText().length() == 0){
            Toast.makeText(this, "请输入重量!", Toast.LENGTH_SHORT).show();
            return;
        }
        String no = getNo();
        for(ChoosedFood cf : choosedFoodList){
            if (cf.getNo().equals(no)){
                Toast.makeText(this, "该编号已经存在!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        StringBuffer sb = new StringBuffer();
        if (btnNochilli.isChecked())
            sb.append("清汤 ");
        if (btnLittlechilli.isChecked())
            sb.append("微辣 ");
        if (btnMiddlechilli.isChecked())
            sb.append("中辣 ");
        if (btnMorechilli.isChecked())
            sb.append("重辣 ");
        if (!btnAddPeanut.isChecked())
            sb.append("不加花生 ");
        if (!btnAddOnion.isChecked())
            sb.append("不加葱花 ");
        if (!btnAddSesame.isChecked())
            sb.append("不加芝麻 ");
        if (!btnAddCaraway.isChecked())
            sb.append("不加香菜 ");
        double price = dish.getPrice() * Double.parseDouble(txtWeight.getText().toString());
        sb.append(InstantValue.DOLLARSPACE + price);
        ChoosedFood cf = new ChoosedFood(no, price, Double.parseDouble(txtWeight.getText().toString()), sb.toString(), true);
        choosedFoodList.add(cf);
        choosedFoodAdapter.notifyDataSetChanged();
        txtWeight.setText(InstantValue.NULLSTRING);
        txNoManual.setText(InstantValue.NULLSTRING);
        btnLittlechilli.setChecked(true);
        btnAddCaraway.setChecked(true);
        btnAddOnion.setChecked(true);
        btnAddSesame.setChecked(true);
        btnAddPeanut.setChecked(true);
    }

    private void doMakeOrder(){
        if (choosedFoodList.isEmpty()){
            Toast.makeText(this, "当前列表为空!", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean hasNew = false;
        for(ChoosedFood cf : choosedFoodList){
            if (cf.isNew()){
                hasNew = true;
                break;
            }
        }
        if (!hasNew){
            Toast.makeText(this, "没有新单, 下单动作取消!", Toast.LENGTH_SHORT).show();
            return;
        }
        DeskCell choosedDeskIcon = null;
        for (DeskCell di: deskCellList) {
            if (di.isChoosed()){
                choosedDeskIcon = di;
                break;
            }
        }
        if (choosedDeskIcon == null){
            Toast.makeText(this, "请选择餐桌!", Toast.LENGTH_SHORT).show();
            return;
        }
        final Desk choosedDesk = choosedDeskIcon.getDesk();

        new Thread(){
            @Override
            public void run() {
                String deskStatus = httpOperator.checkDeskStatus(choosedDesk.getName());
                if (InstantValue.CHECKDESK4MAKEORDER_OCCUPIED.equals(deskStatus)){
                    handler.sendMessage(CommonTool.buildMessage(MESSAGEWHAT_ASKTOADDDISHINORDER, choosedDesk.getId()));
                } else if (InstantValue.CHECKDESK4MAKEORDER_AVAILABLE.equals(deskStatus)){
                    makeNewOrder(choosedDesk.getId());
                } else {
                    handler.sendMessage(CommonTool.buildMessage(MESSAGEWHAT_ERRORDIALOG, deskStatus));
                }
            }
        }.start();
    }

    //this function must be call in a non-UI thread
    private void makeNewOrder(int deskid){
        JSONArray os = null;
        try {
            os = generateOrderJson();
        } catch (JSONException e) {
            handler.sendMessage(CommonTool.buildMessage(MESSAGEWHAT_ERRORDIALOG,
                    "There are error to build JSON Object, please restart APP!"));
            return;
        }

        if (os != null){
            HttpResult<Integer> result = httpOperator.makeOrder(confirmCode, os.toString(), deskid, 0);
            if (result.success){
                handler.sendMessage(CommonTool.buildMessage(MESSAGEWHAT_MAKEORDERSUCCESS, result.data));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        choosedFoodList.clear();
                        choosedFoodAdapter.notifyDataSetChanged();
                    }
                });
            } else {
                handler.sendMessage(CommonTool.buildMessage(MESSAGEWHAT_ERRORDIALOG,
                        "Something wrong happened while making order! \n\nError message : " + result.result));
            }
        }
    }

    private JSONArray generateOrderJson() throws JSONException {
        JSONArray ja = new JSONArray();
        for (int i = 0; i < choosedFoodList.size(); i++) {
            ChoosedFood cf = choosedFoodList.get(i);
            if (!cf.isNew())
                continue;
            JSONObject jo = new JSONObject();
            jo.put("id", dish.getId());
            jo.put("amount", "1");
            jo.put("weight", String.valueOf(cf.getWeight()));
            jo.put("additionalRequirements", "No" + cf.getNo()+ " " + cf.getRequirement());
            ja.put(jo);
        }

        return ja;
    }

    private void addDishToOrder(final int deskid){
        JSONArray os = null;
        try {
            os = generateOrderJson();
        } catch (JSONException e) {
            Toast.makeText(MainActivity.this, "There are error to build JSON Object, please !", Toast.LENGTH_SHORT).show();
            return;
        }

        if (os != null){
            final String oss = os.toString();
            new Thread(){
                @Override
                public void run() {
                    HttpResult<Integer> result = httpOperator.addDishToOrder(deskid,oss);
                    if (result.success){
                        handler.sendMessage(CommonTool.buildMessage(MESSAGEWHAT_ADDDISHSUCCESS));
                    } else {
                        handler.sendMessage(CommonTool.buildMessage(MESSAGEWHAT_ERRORDIALOG,
                                "Something wrong happened while add dishes! \n\nError message : " + result.result));
                    }
                }
            }.start();
        }
        choosedFoodList.clear();
        choosedFoodAdapter.notifyDataSetChanged();
    }

    public void onFinishMakeOrder(String title, String message){
        //clear desks
        for (DeskCell di: deskCellList) {
            di.setChoosed(false);
        }
        tvPrice.setText(InstantValue.NULLSTRING);
        txtWeight.setText(InstantValue.NULLSTRING);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(R.drawable.info);
        builder.setNegativeButton("OK", null);
        builder.create().show();
    }

    private void onRefreshData(){
        dbOperator.deleteAllData(Desk.class);
        httpOperator.loadDeskData();
        CommonTool.popupWarnDialog(this, R.drawable.info, "成功", "数据同步成功.");
    }

    public void removeChoosedFoodFromList(ChoosedFood cf){
        choosedFoodList.remove(cf);
        choosedFoodAdapter.notifyDataSetChanged();
    }

    public void setChoosedFoodList(ArrayList<ChoosedFood> list){
        choosedFoodList.clear();
        choosedFoodList.addAll(list);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                choosedFoodAdapter.notifyDataSetChanged();
            }
        });
    }

    private String getNo(){
        if (txNoManual.getText() != null && txNoManual.getText().toString().trim().length() > 0)
            return txNoManual.getText().toString();
        if (rbNo1.isChecked())
            return "1";
        if (rbNo2.isChecked())
            return "2";
        if (rbNo3.isChecked())
            return "3";
        if (rbNo4.isChecked())
            return "4";
        if (rbNo5.isChecked())
            return "5";
        if (rbNo6.isChecked())
            return "6";
        if (rbNo7.isChecked())
            return "7";
        return "";
    }

    public ArrayList<DeskCell> getDeskCellList() {
        return deskCellList;
    }

    public void setDeskCellList(ArrayList<DeskCell> deskCellList) {
        this.deskCellList = deskCellList;
    }

    public void setDesk(ArrayList<Desk> desks){
        this.desks = desks;
    }

    public Handler getToastHandler(){
        return toastHandler;
    }

    public ArrayList<Desk> getDesks() {
        return desks;
    }

    public String getConfirmCode() {
        return confirmCode;
    }

    public void setDish(Dish dish){
        this.dish = dish;
        IOOperator.saveDishName(InstantValue.FILE_DISHNAME, dish.getFirstLanguageName());
    }

    public Dish getDish() {
        return dish;
    }

    public void setConfirmCode(String confirmCode) {
        this.confirmCode = confirmCode;
    }

    //屏蔽实体按键BACK
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //屏蔽recent task 按键, some pad devices are different with the virtual device, such as Sumsung Tab E
    @Override
    protected void onPause() {
        super.onPause();
        ActivityManager activityManager = (ActivityManager) getApplicationContext() .getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        ActivityManager activityManager = (ActivityManager) getApplicationContext() .getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    /**
     * stop for Sumsung's Recent Task button
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!hasFocus) {
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
    }

    public static final int TOASTHANDLERWHAT_ERRORMESSAGE = 0;
    private Handler toastHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == TOASTHANDLERWHAT_ERRORMESSAGE){
                Toast.makeText(MainActivity.this,msg.obj != null ? msg.obj.toString() : "", Toast.LENGTH_LONG).show();
            }
        }
    };

    public Handler getProgressDlgHandler(){
        return progressDlgHandler;
    }

    public void startProgressDialog(String title, String message){
        progressDlg = ProgressDialog.show(this, title, message);
    }

    public static final int PROGRESSDLGHANDLER_MSGWHAT_DISMISSDIALOG = 0;
    public static final int PROGRESSDLGHANDLER_MSGWHAT_SHOWPROGRESS = 1;
    public static final int PROGRESSDLGHANDLER_MSGWHAT_STARTLOADDATA = 3;
    private ProgressDialog progressDlg;
    private Handler progressDlgHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == PROGRESSDLGHANDLER_MSGWHAT_DISMISSDIALOG) {
                if (progressDlg != null)
                    progressDlg.dismiss();
            } else if (msg.what == PROGRESSDLGHANDLER_MSGWHAT_SHOWPROGRESS){
                if (progressDlg != null){
                    progressDlg.setMessage(msg.obj != null ? msg.obj.toString() : "");
                }
            } else if (msg.what == PROGRESSDLGHANDLER_MSGWHAT_STARTLOADDATA){
                if (progressDlg != null){
                    progressDlg.setMessage(msg.obj != null ? msg.obj.toString() : "");
                }
            }
        }
    };

    private final static int MESSAGEWHAT_MAKEORDERSUCCESS=3;
    private final static int MESSAGEWHAT_ADDDISHSUCCESS=4;
    private final static int MESSAGEWHAT_ASKTOADDDISHINORDER=5;
    private final static int MESSAGEWHAT_ERRORTOAST=8;
    private final static int MESSAGEWHAT_ERRORDIALOG=9;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            dealHandlerMessage(msg);
            super.handleMessage(msg);
        }
    };

    private void dealHandlerMessage(Message msg){
        switch (msg.what){
            case MESSAGEWHAT_ERRORTOAST :
                Toast.makeText(this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                break;
            case MESSAGEWHAT_ERRORDIALOG:
                CommonTool.popupWarnDialog(this, R.drawable.error, "WRONG", msg.obj.toString());
                break;
            case MESSAGEWHAT_MAKEORDERSUCCESS:
                onFinishMakeOrder("SUCCESS", "已成功开桌, 订单编号: " + msg.obj);
                break;
            case MESSAGEWHAT_ASKTOADDDISHINORDER:
                addDishToOrder(Integer.parseInt(msg.obj.toString()));
                break;
            case MESSAGEWHAT_ADDDISHSUCCESS:
                onFinishMakeOrder("SUCCESS", "加菜已完成");
                break;
        }
    }



}
