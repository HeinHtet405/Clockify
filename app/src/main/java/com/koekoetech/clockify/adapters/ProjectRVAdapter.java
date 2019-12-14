package com.koekoetech.clockify.adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.koekoetech.clockify.R;
import com.koekoetech.clockify.models.Project;

import java.util.ArrayList;

public class ProjectRVAdapter extends BaseQuickAdapter<Project, BaseViewHolder> {

    public ProjectRVAdapter() {
        super(R.layout.item_project, new ArrayList<>());
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void convert(@NonNull BaseViewHolder helper, Project item) {
        CardView cvColor = helper.getView(R.id.item_project_cv_color);
        AppCompatTextView tvProjectName = helper.getView(R.id.item_project_tv_name);

        cvColor.setRadius(40);
        cvColor.setCardBackgroundColor(Color.parseColor(item.getColor()));
        String tvClient = item.getClientName();
        if (!TextUtils.isEmpty(item.getClientName())) {
            tvProjectName.setText(item.getName() + " (" + tvClient + " )");
        } else {
            tvProjectName.setText(item.getName());
        }
    }
}
