package com.shuishou.malatang.ui;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.shuishou.malatang.InstantValue;
import com.shuishou.malatang.R;
import com.shuishou.malatang.bean.Desk;
import com.shuishou.malatang.bean.Dish;
import com.shuishou.malatang.bean.HttpResult;
import com.shuishou.malatang.db.DBOperator;
import com.shuishou.malatang.http.HttpOperator;
import com.shuishou.malatang.io.CrashHandler;
import com.shuishou.malatang.io.IOOperator;
import com.shuishou.malatang.utils.CommonTool;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.NoHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Dish dish;
    private ArrayList<Desk> desks;
    private ArrayList<DeskIcon> deskIconList = new ArrayList<>();
    private DeskClickListener deskClickListener = new DeskClickListener();
    private String confirmCode;

    private HttpOperator httpOperator;
    private DBOperator dbOperator;
    private TextView tvRefreshData;
    private TextView tvServerURL;
    private TextView tvDishName;
    private TextView tvUploadErrorLog;
    private Button btnMakeOrder;
    private Button btnGetWeight;
    private TextView tvPrice;
    private EditText txtWeight;
    private TableLayout deskAreaLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        tvRefreshData = (TextView)findViewById(R.id.drawermenu_refreshdata);
        tvServerURL = (TextView)findViewById(R.id.drawermenu_serverurl);
        tvUploadErrorLog = (TextView)findViewById(R.id.drawermenu_uploaderrorlog);
        tvDishName = (TextView)findViewById(R.id.drawmenu_dishname);
        btnMakeOrder = (Button)findViewById(R.id.btnMakeOrder);
        btnGetWeight = (Button)findViewById(R.id.btnGetWeight);
        txtWeight = (EditText)findViewById(R.id.txtWeight);
        deskAreaLayout = (TableLayout)findViewById(R.id.deskAreaLayout);
        tvPrice = (TextView)findViewById(R.id.tvPrice);
        Button btnTest = (Button)findViewById(R.id.btnTest);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = 1/0;
            }
        });
        tvUploadErrorLog.setOnClickListener(this);
        tvRefreshData.setOnClickListener(this);
        tvDishName.setOnClickListener(this);
        tvServerURL.setOnClickListener(this);
        btnGetWeight.setOnClickListener(this);
        btnMakeOrder.setOnClickListener(this);
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
                    tvPrice.setText(InstantValue.PRICE + String.format(InstantValue.FORMAT_DOUBLE_2DECIMAL, calculatePrice(Integer.parseInt(s.toString()))));
                }
            }
        });
        //init tool class, NoHttp
        NoHttp.initialize(this);
        Logger.setDebug(true);
        Logger.setTag("malatang:nohttp");

        InstantValue.URL_TOMCAT = IOOperator.loadServerURL();
        httpOperator = new HttpOperator(this);
        dbOperator = new DBOperator(this);

        //read local database to memory
        desks = dbOperator.queryDesks();
        httpOperator.queryConfirmCode();
        loadDish();
        buildDesks();
    }

    private double calculatePrice(int weight){
        return dish.getPrice() * weight;
    }

    private void loadDish(){
        new Thread(){
            @Override
            public void run() {
                if (InstantValue.URL_TOMCAT != null)
                    dish = httpOperator.getDishByNameSync(IOOperator.loadDishName());
            }
        }.start();
    }

    private void buildDesks(){
        if (desks == null || desks.isEmpty())
            return;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        TableRow.LayoutParams trlp = new TableRow.LayoutParams();
        trlp.topMargin = 10;
        trlp.leftMargin = 10;
        int width = (screenWidth - InstantValue.DESKCELLAMOUNTPERROW * trlp.leftMargin)/InstantValue.DESKCELLAMOUNTPERROW;
        int height = width;
        TableRow tr = null;
        for (int i = 0; i < desks.size(); i++) {
            if (i % InstantValue.DESKCELLAMOUNTPERROW == 0){
                tr = new TableRow(this);
                deskAreaLayout.addView(tr);
            }
            DeskIcon di = new DeskIcon(this, desks.get(i), width, height);
            deskIconList.add(di);
            tr.addView(di, trlp);
        }
    }

    public Handler getProgressDlgHandler(){
        return progressDlgHandler;
    }

    private void onStartOrder(){
    }

    public void setDish(Dish dish){
        this.dish = dish;
        IOOperator.saveDishName(dish.getEnglishName());
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

    public void startProgressDialog(String title, String message){
        progressDlg = ProgressDialog.show(this, title, message);
    }

    @Override
    public void onClick(View v) {
        if (tvUploadErrorLog == v){
            onUploadErrorLog();
        } else if (v == tvRefreshData){
            onRefreshData();
        } else if (v == tvServerURL){
            SaveServerURLDialog dlg = new SaveServerURLDialog(MainActivity.this);
            dlg.showDialog();
        } else if (v == btnGetWeight){

        } else if (v == btnMakeOrder){
            doMakeOrder();
        } else if (v == tvDishName){
            SaveDishDialog dlg = new SaveDishDialog(MainActivity.this);
            dlg.showDialog();
        }
    }

    private void doMakeOrder(){
        if (txtWeight.getText() == null || txtWeight.getText().length() == 0){
            Toast.makeText(this, "请输入重量!", Toast.LENGTH_SHORT).show();
            return;
        }
        DeskIcon choosedDeskIcon = null;
        for (DeskIcon di: deskIconList) {
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
            } else {
                handler.sendMessage(CommonTool.buildMessage(MESSAGEWHAT_ERRORDIALOG,
                        "Something wrong happened while making order! \n\nError message : " + result.result));
            }
        }
    }

    private JSONArray generateOrderJson() throws JSONException {
        JSONArray ja = new JSONArray();
        JSONObject jo = new JSONObject();
        jo.put("id", dish.getId());
        jo.put("amount", Integer.parseInt(txtWeight.getText().toString()));
//            jo.put("addtionalRequirements", cf.getAdditionalRequirements());
        ja.put(jo);
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
    }

    public void onFinishMakeOrder(String title, String message){
        //clear desks
        for (DeskIcon di: deskIconList) {
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

    /**
     * 1. compare logfile's date, if too old delete it, then load all left log files and zip them
     * 2. use http protocol to upload
     * 3. load finish successfully, then delete this file.
     */
    private void onUploadErrorLog(){
        File logdir = new File(InstantValue.ERRORLOGPATH);
        if (logdir.exists() && logdir.isDirectory()) {
            File[] files = logdir.listFiles();
            if (files != null && files.length > 0) {
                //delete the old log files, we just upload them in 30 days, the logfile's name has the format crash-2017-10-05-17-53-25-1507226005123
                Calendar c = Calendar.getInstance();
                for (File file : files) {
                    String filename = file.getName();
                    String[] times = filename.split("-");
                    if (times.length<3)
                        continue;//wrong log file, jump up to avoid exception here
                    c.set(Calendar.YEAR, Integer.parseInt(times[1]));
                    c.set(Calendar.MONTH, Integer.parseInt(times[2]));
                    c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(times[3]));
                    if ((new Date().getTime() - c.getTime().getTime()) / (24 * 60 * 60 * 1000) > 30) {
                        file.delete();
                    }
                }
            }
            files = logdir.listFiles();
            if (files != null && files.length > 0) {
                String zipfilename = InstantValue.ERRORLOGPATH + "/logs-" +System.currentTimeMillis()+ ".zip";
                //zip log files
                try {
                    BufferedInputStream origin = null;
                    FileOutputStream dest = new FileOutputStream(zipfilename);
                    ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
                    byte data[] = new byte[2048];
                    // get a list of files from current directory
                    for (int i=0; i < files.length; i++) {
                        FileInputStream fi = new FileInputStream(files[i]);
                        origin = new BufferedInputStream(fi, 2048);
                        ZipEntry entry = new ZipEntry(files[i].getName());
                        out.putNextEntry(entry);
                        int count;
                        while((count = origin.read(data, 0, 2048)) != -1) {
                            out.write(data, 0, count);
                        }
                        origin.close();
                    }
                    out.close();
                    //delete log files
                    for(File file : files){
                        file.delete();
                    }

                } catch(Exception e) {
                    CrashHandler.getInstance().handleException(e, false);
                    CommonTool.popupWarnDialog(this, -1,"Error", "error while zip log files");
                    return;
                }
                File logzip = new File(zipfilename);
                //get MAC address as the unique id
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wInfo = wifiManager.getConnectionInfo();
                String macAddress = wInfo.getMacAddress();

                startProgressDialog("upload", "prepare to upload error log files");
                try {
                    httpOperator.uploadErrorLog(logzip, macAddress);
                } catch (FileNotFoundException e) {
                    CrashHandler.getInstance().handleException(e, false);
                    progressDlgHandler.sendMessage(CommonTool.buildMessage(PROGRESSDLGHANDLER_MSGWHAT_DISMISSDIALOG));
                }
            } else {
                CommonTool.popupToast(this,"There is not error log now.", Toast.LENGTH_LONG);
            }
        }
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

    class DeskIcon extends android.support.v7.widget.AppCompatTextView{
        private Desk desk;
        private boolean choosed;
        public DeskIcon(Context context, Desk desk, int width, int height){
            super(context);
            this.desk = desk;
            initDeskUI(width, height);
        }

        private void initDeskUI(int width, int height){
            setTextSize(18);
            setTextColor(Color.BLACK);
            setBackgroundColor(Color.LTGRAY);
            setText(desk.getName());
            setHeight(height);
            setWidth(width);
            setOnClickListener(deskClickListener);
            setEllipsize(TextUtils.TruncateAt.END);
        }

        public void setChoosed(boolean b){
            choosed = b;
            if (b){
                this.setBackgroundColor(Color.GREEN);
            } else {
                this.setBackgroundColor(Color.LTGRAY);
            }
        }

        public boolean isChoosed(){
            return choosed;
        }

        public Desk getDesk() {
            return desk;
        }
    }

    class DeskClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if (v.getClass().getName().equals(DeskIcon.class.getName())){
                for(DeskIcon di : deskIconList){
                    di.setChoosed(false);
                }
                ((DeskIcon)v).setChoosed(true);
            }
        }
    }
}
