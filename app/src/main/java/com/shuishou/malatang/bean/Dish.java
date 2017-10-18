package com.shuishou.malatang.bean;

import com.google.gson.annotations.SerializedName;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;
import com.shuishou.malatang.InstantValue;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/12/22.
 */

public class Dish implements Serializable{
    private int id;

    private String chineseName;

    private String englishName;

    private int sequence;

    private double price;

    private String pictureName;

    private boolean isNew = false;

    private boolean isSpecial = false;

    private boolean isSoldOut;

    private int hotLevel;

    private String abbreviation;

    private int chooseMode ;

    private boolean autoMergeWhileChoose = true;


    public Dish(){

    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public int getHotLevel() {
        return hotLevel;
    }

    public void setHotLevel(int hotLevel) {
        this.hotLevel = hotLevel;
    }

    public boolean isSoldOut() {
        return isSoldOut;
    }

    public void setSoldOut(boolean soldOut) {
        isSoldOut = soldOut;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public boolean isSpecial() {
        return isSpecial;
    }

    public void setSpecial(boolean isSpecial) {
        this.isSpecial = isSpecial;
    }

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }


    public int getChooseMode() {
        return chooseMode;
    }

    public void setChooseMode(int chooseMode) {
        this.chooseMode = chooseMode;
    }


    public boolean isAutoMergeWhileChoose() {
        return autoMergeWhileChoose;
    }

    public void setAutoMergeWhileChoose(boolean autoMergeWhileChoose) {
        this.autoMergeWhileChoose = autoMergeWhileChoose;
    }

    @Override
    public String toString() {
        return "Dish [chineseName=" + chineseName + ", englishName=" + englishName + "]";
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Dish other = (Dish) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
