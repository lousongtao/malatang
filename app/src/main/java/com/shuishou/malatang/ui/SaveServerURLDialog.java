package com.shuishou.malatang.ui;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.shuishou.malatang.InstantValue;
import com.shuishou.malatang.R;
import com.shuishou.malatang.io.IOOperator;


/**
 * Created by Administrator on 2017/7/21.
 */

class SaveServerURLDialog {

    private EditText txtServerURL;
    private MainActivity mainActivity;

    private AlertDialog dlg;

    public SaveServerURLDialog(@NonNull MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        initUI();
    }

    private void initUI(){
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.config_serverurl_layout, null);

        txtServerURL = (EditText) view.findViewById(R.id.txtServerURL);

        loadServerURL();

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
    }

    private void loadServerURL(){
        String url = IOOperator.loadServerURL(InstantValue.FILE_SERVERURL);
        if (url != null)
            txtServerURL.setText(url);
    }

    private void doSaveURL(){
        final String url = txtServerURL.getText().toString();
        if (url == null || url.length() == 0){
            Toast.makeText(mainActivity, "Please input server URL.", Toast.LENGTH_LONG).show();
            return;
        }
        IOOperator.saveServerURL(InstantValue.FILE_SERVERURL, url);
        InstantValue.URL_TOMCAT = url;
        dlg.dismiss();
        mainActivity.popRestartDialog("Save server URL successfully, please restart the app.");
    }

    public void showDialog(){
        dlg.show();
    }

    public void dismiss(){
        dlg.dismiss();
    }
}
