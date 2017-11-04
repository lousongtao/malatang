package com.shuishou.malatang.ui;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.shuishou.malatang.InstantValue;
import com.shuishou.malatang.R;
import com.shuishou.malatang.bean.Dish;
import com.shuishou.malatang.io.IOOperator;
import com.shuishou.malatang.utils.CommonTool;


/**
 * Created by Administrator on 2017/7/21.
 */

class SaveDishDialog {

    private EditText txtDishName;
    private MainActivity mainActivity;

    private AlertDialog dlg;

    private final static int MESSAGEWHAT_ERRORDIALOG=9;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            dealHandlerMessage(msg);
            super.handleMessage(msg);
        }
    };
    public SaveDishDialog(@NonNull MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        initUI();
    }

    private void initUI(){
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.config_dishname_layout, null);

        txtDishName = (EditText) view.findViewById(R.id.txtDishName);

        loadDishName();

        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("Configure Dish Name")
                .setIcon(R.drawable.info)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null)
                .setView(view);
        dlg = builder.create();
        dlg.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                //add listener for YES button
                ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doSaveDishName();
                    }
                });
            }
        });
        dlg.setCancelable(false);
        dlg.setCanceledOnTouchOutside(false);
    }

    private void dealHandlerMessage(Message msg){
        switch (msg.what){
            case MESSAGEWHAT_ERRORDIALOG:
                CommonTool.popupWarnDialog(mainActivity, R.drawable.error, "WRONG", msg.obj.toString());
                break;
        }
    }

    private void loadDishName(){
        String name = IOOperator.loadDishName(InstantValue.FILE_DISHNAME);
        if (name != null)
            txtDishName.setText(name);
    }

    private void doSaveDishName(){
        final String name = txtDishName.getText().toString();
        if (name == null || name.length() == 0){
            Toast.makeText(mainActivity, "Please input Dish Name.", Toast.LENGTH_LONG).show();
            return;
        }
        new Thread(){
            @Override
            public void run() {
                Dish dish = mainActivity.getHttpOperator().getDishByNameSync(name);
                if (dish == null){
                    handler.sendMessage(CommonTool.buildMessage(MESSAGEWHAT_ERRORDIALOG, "cannot get dish from server by this name"));
                }else {
                    mainActivity.setDish(dish);
                    dlg.dismiss();
                }
            }
        }.start();

    }

    public void showDialog(){
        dlg.show();
    }

    public void dismiss(){
        dlg.dismiss();
    }
}
