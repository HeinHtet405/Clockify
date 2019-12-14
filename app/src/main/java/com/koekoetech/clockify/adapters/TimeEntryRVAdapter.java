package com.koekoetech.clockify.adapters;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.koekoetech.clockify.R;
import com.koekoetech.clockify.dbStorage.ProjectDbAccess;
import com.koekoetech.clockify.models.Project;
import com.koekoetech.clockify.models.TimeEntry;

import java.util.ArrayList;

import io.realm.Realm;

public class TimeEntryRVAdapter extends BaseQuickAdapter<TimeEntry, BaseViewHolder> {

    private ProjectDbAccess projectDbAccess;

    public TimeEntryRVAdapter() {
        super(R.layout.item_time_entry, new ArrayList<>());
        projectDbAccess = new ProjectDbAccess(Realm.getDefaultInstance());
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, TimeEntry item) {
        AppCompatTextView tvWork = helper.getView(R.id.item_te_work);
        AppCompatTextView tvProject = helper.getView(R.id.item_te_project);
        AppCompatTextView tvStart = helper.getView(R.id.item_te_start);
        AppCompatTextView tvEnd = helper.getView(R.id.item_te_end);
        tvWork.setText(item.getDescription());
        tvStart.setText(item.getStartTime());
        tvEnd.setText(item.getEndTime());
        Project project = projectDbAccess.getProject(item.getProjectId());
        if (project != null) {
            tvProject.setText(project.getName());
            tvProject.setTextColor(Color.parseColor(project.getColor()));
        }
    }
}
