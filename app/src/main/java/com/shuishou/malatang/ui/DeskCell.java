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

    /**
     * 1. 设定当前cell为选中状态, 其他的都改为非选中
     * 2. 清空右侧列表
     * 3. 查询该桌子上面已有的麻辣烫记录
     * 4. 根据查询结果, 把数据加入右侧列表.
     */
    class DeskClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if (v.getClass().getName().equals(DeskCell.class.getName())){
                for(DeskCell di : mainActivity.getDeskCellList()){
                    di.setChoosed(false);
                }
                ((DeskCell)v).setChoosed(true);
            }
            mainActivity.removeAllChoosedFoodFromList();
            //load Dish record for this Desk
//            mainActivity.startProgressDialog("", "loading existing order...");
            new Thread(){
                @Override
                public void run() {
                    mainActivity.getHttpOperator().queryIndentForDesk(desk.getName());
//                    mainActivity.stopProgressDialog();
                }
            }.start();

        }
    }
}


