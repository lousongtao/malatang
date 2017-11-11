package com.shuishou.malatang.bean;

/**
 * Created by Administrator on 2017/11/2.
 */

public class ChoosedFood {
    private String no;
    private double price;
    private double weight;
    private String requirement;
    private boolean isNew = false;

//    public ChoosedFood(String no, double price, double weight, String requirement){
//        this.no = no;
//        this.price = price;
//        this.weight = weight;
//        this.requirement = requirement;
//    }

    public ChoosedFood(String no, double price, double weight, String requirement, boolean isNew){
        this.no = no;
        this.price = price;
        this.weight = weight;
        this.requirement = requirement;
        this.isNew = isNew;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
