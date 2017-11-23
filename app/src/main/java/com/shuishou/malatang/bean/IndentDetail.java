package com.shuishou.malatang.bean;

/**
 * Created by Administrator on 2017/11/8.
 */

public class IndentDetail {
    public int id;
    public int dishId;
    public int amount;
    public double dishPrice;//单个dish价格, 不考虑amount
    public String dishFirstLanguageName;
    public String dishSecondLanguageName;
    public String additionalRequirements;
    public double weight;
}
