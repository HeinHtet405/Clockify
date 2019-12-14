package com.koekoetech.clockify.adapters;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.koekoetech.clockify.R;
import com.koekoetech.clockify.models.Schedule;

import java.util.ArrayList;

public class ScheduleRVAdapter extends BaseQuickAdapter<Schedule, BaseViewHolder> {

    public ScheduleRVAdapter() {
        super(R.layout.item_schedule, new ArrayList<>());
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Schedule item) {
        AppCompatTextView tvCategoryName = helper.getView(R.id.item_schedule_title);
        tvCategoryName.setText(item.getCategory().getName());
        RecyclerView rvSchedule = helper.getView(R.id.item_schedule_rv);
        rvSchedule.setNestedScrollingEnabled(false);
        TimeEntryRVAdapter timeEntryRVAdapter = new TimeEntryRVAdapter();
        rvSchedule.setAdapter(timeEntryRVAdapter);
        timeEntryRVAdapter.setNewData(item.getTimeEntryList());
        helper.addOnClickListener(R.id.item_schedule_remove);
    }
}
