package com.shuishou.malatang.ui;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.shuishou.malatang.InstantValue;
import com.shuishou.malatang.R;
import com.shuishou.malatang.io.IOOperator;


/**
 * Created by Administrator on 2017/7/21.
 */

class SaveConnectionDialog {

    private EditText txtServerURL;
    private EditText txtBluetoothUUID;
    private EditText txtBluetoothDevice;
    private MainActivity mainActivity;

    private AlertDialog dlg;

    public SaveConnectionDialog(@NonNull MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        initUI();
    }

    private void initUI(){
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.config_connection_layout, null);

        txtServerURL = (EditText) view.findViewById(R.id.txtServerURL);
        txtBluetoothUUID = (EditText) view.findViewById(R.id.txtBluetoothUUID);
        txtBluetoothDevice = (EditText) view.findViewById(R.id.txtBluetoothDevice);
        loadConnection();

        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("Configure Server URL")
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
                        doSaveURL();
                    }
                });
            }
        });
        dlg.setCancelable(false);
        dlg.setCanceledOnTouchOutside(false);
        Window window = dlg.getWindow();
        WindowManager.LayoutParams param = window.getAttributes();
        param.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        param.y = 0;
        window.setAttributes(param);
    }

    private void loadConnection(){
        if (InstantValue.URL_TOMCAT != null)
            txtServerURL.setText(InstantValue.URL_TOMCAT);
        if (InstantValue.BLUETOOTHUUID != null && InstantValue.BLUETOOTHUUID.length() > 0)
            txtBluetoothUUID.setText(InstantValue.BLUETOOTHUUID);
        if (InstantValue.BLUETOOTHDEVICE != null)
            txtBluetoothDevice.setText(InstantValue.BLUETOOTHDEVICE);
    }

    private void doSaveURL(){
        final String url = txtServerURL.getText().toString();
        final String bluetoothUUID = txtBluetoothUUID.getText().toString();
        final String bluetoothDevice = txtBluetoothDevice.getText().toString();
        if (url == null || url.length() == 0){
            Toast.makeText(mainActivity, "Please input server URL.", Toast.LENGTH_LONG).show();
            return;
        }

        IOOperator.saveConnection(InstantValue.FILE_CONNECTION, url, bluetoothDevice, bluetoothUUID);
        InstantValue.URL_TOMCAT = url;
        dlg.dismiss();
        mainActivity.popRestartDialog("Configuration params changed successfully, please restart the app.");
    }

    public void showDialog(){
        dlg.show();
    }

    public void dismiss(){
        dlg.dismiss();
    }
}
