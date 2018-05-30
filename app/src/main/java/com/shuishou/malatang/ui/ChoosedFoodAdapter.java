package com.shuishou.malatang.ui;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.shuishou.malatang.InstantValue;
import com.shuishou.malatang.R;
import com.shuishou.malatang.bean.ChoosedFood;

import java.util.List;

/**
 * Created by Administrator on 2017/11/2.
 */

public class ChoosedFoodAdapter extends ArrayAdapter<ChoosedFood> {
    private MainActivity mainActivity;
    private int resourceId;
    public ChoosedFoodAdapter(@NonNull MainActivity mainActivity, @LayoutRes int resource, @NonNull List<ChoosedFood> objects) {
        super(mainActivity, resource, objects);
        resourceId = resource;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ChoosedFood cf = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView tvHeader = (TextView)view.findViewById(R.id.tvChoosedFoodHeader);
        TextView tvReq = (TextView)view.findViewById(R.id.tvChoosedFoodReqs);
        ImageView imgDelete = (ImageView)view.findViewById(R.id.imgDelete);
        if (cf.isNew()) {
            tvHeader.setText("编号 " + cf.getNo() + ", 重量 " + cf.getWeight() + ", 价格 $" + String.format(InstantValue.FORMAT_DOUBLE_2DECIMAL, cf.getPrice()));
            tvReq.setText(cf.getRequirement());
            imgDelete.setOnClickListener(DeleteChoosedFoodListener.getInstance(mainActivity));
            imgDelete.setTag(cf);
        } else {
            tvHeader.setText("旧单");
            tvReq.setText(cf.getRequirement());
            imgDelete.setVisibility(View.INVISIBLE);
        }
        return view;
    }
}
