package com.koekoetech.clockify.adapters;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.koekoetech.clockify.R;
import com.koekoetech.clockify.dbStorage.ProjectDbAccess;
import com.koekoetech.clockify.helpers.DateHelper;
import com.koekoetech.clockify.models.Project;
import com.koekoetech.clockify.models.TimeEntryRecord;

import java.util.ArrayList;

import io.realm.Realm;

public class RecordRVAdapter extends BaseQuickAdapter<TimeEntryRecord, BaseViewHolder> {

    private ProjectDbAccess projectDbAccess;

    public RecordRVAdapter() {
        super(R.layout.item_record, new ArrayList<>());
        projectDbAccess = new ProjectDbAccess(Realm.getDefaultInstance());
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, TimeEntryRecord item) {
        AppCompatTextView tvWork = helper.getView(R.id.item_record_work);
        AppCompatTextView tvProject = helper.getView(R.id.item_record_project);
        AppCompatTextView tvStart = helper.getView(R.id.item_record_start);
        AppCompatTextView tvEnd = helper.getView(R.id.item_record_end);
        Project project = projectDbAccess.getProject(item.getProjectId());
        tvWork.setText(item.getDescription());
        tvStart.setText(DateHelper.serverFormatToLocalTime(item.getTimeInterval().getStartTime()));
        tvEnd.setText(DateHelper.serverFormatToLocalTime(item.getTimeInterval().getEndTime()));
        if (project != null) {
            tvProject.setText(project.getName());
            tvProject.setTextColor(Color.parseColor(project.getColor()));
        }
    }
}
