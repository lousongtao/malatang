package com.shuishou.malatang.ui;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;

import com.shuishou.malatang.bean.Desk;

/**
 * Created by Administrator on 2017/11/8.
 */

public class DeskCell extends android.support.v7.widget.AppCompatTextView{
    private Desk desk;
    private boolean choosed;
    private MainActivity mainActivity;
    private DeskClickListener deskClickListener = new DeskClickListener();
    public DeskCell(MainActivity mainActivity, Desk desk, int width, int height){
        super(mainActivity);
        this.desk = desk;
        this.mainActivity = mainActivity;
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

    class DeskClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if (v.getClass().getName().equals(DeskCell.class.getName())){
                for(DeskCell di : mainActivity.getDeskCellList()){
                    di.setChoosed(false);
                }
                ((DeskCell)v).setChoosed(true);
            }
            //load Dish record for this Desk
            mainActivity.startProgressDialog("", "loading existing order...");
            new Thread(){
                @Override
                public void run() {
                    mainActivity.getHttpOperator().queryIndentForDesk(desk.getName());
                    mainActivity.stopProgressDialog();
                }
            }.start();

        }
    }
}


