package com.koekoetech.clockify.adapters;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.koekoetech.clockify.R;
import com.koekoetech.clockify.helpers.MyConstant;
import com.koekoetech.clockify.models.Category;

import java.util.ArrayList;

public class CategoryRVAdapter extends BaseQuickAdapter<Category, BaseViewHolder> {

    private int activityId;

    public CategoryRVAdapter(int activityId) {
        super(R.layout.item_category, new ArrayList<>());
        this.activityId = activityId;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Category item) {
        AppCompatTextView tvName = helper.getView(R.id.item_category_name);
        tvName.setText(item.getName());
        AppCompatImageView ivRemove = helper.getView(R.id.item_category_remove);
        if (activityId == MyConstant.SCHEDULE_LIST) {
            ivRemove.setVisibility(View.VISIBLE);
        } else if (activityId == MyConstant.SCHEDULE_CREATE_EDIT || activityId == MyConstant.MAIN){
            ivRemove.setVisibility(View.GONE);
        }
        helper.addOnClickListener(R.id.item_category_remove, R.id.item_category_main);
    }
}
