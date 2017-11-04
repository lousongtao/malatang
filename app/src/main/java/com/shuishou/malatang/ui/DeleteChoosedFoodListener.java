package com.shuishou.malatang.ui;

import android.content.Context;
import android.view.View;

import com.shuishou.malatang.bean.ChoosedFood;

/**
 * Created by Administrator on 2017/11/2.
 */

public class DeleteChoosedFoodListener implements View.OnClickListener {
    private static DeleteChoosedFoodListener instance;
    private MainActivity mainActivity;
    private DeleteChoosedFoodListener(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    public static DeleteChoosedFoodListener getInstance(MainActivity mainActivity){
        if (instance == null)
            instance = new DeleteChoosedFoodListener(mainActivity);
        return instance;
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() instanceof ChoosedFood){
            mainActivity.removeChoosedFoodFromList((ChoosedFood)v.getTag());
        }
    }
}
