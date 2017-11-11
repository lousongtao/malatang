package com.shuishou.malatang.bean;

/**
 * Created by Administrator on 2017/11/8.
 */

public class IndentDetail {
    public int id;
    public int dishId;
    public int amount;
    public double dishPrice;//单个dish价格, 不考虑amount
    public String dishChineseName;
    public String dishEnglishName;
    public String additionalRequirements;
    public double weight;
}
