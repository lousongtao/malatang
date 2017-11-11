package com.shuishou.malatang.bean;

import com.shuishou.malatang.InstantValue;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/9/25.
 */

public class Indent {
    public int id;
    public String deskname;
    public String startTime;
    public String endTime;
    public byte status;
    public int dailysequence;
    public double totalprice;
    public double paidPrice;
    public byte payWay;
    public int customerAmount;
    public ArrayList<IndentDetail> items;
}
