package com.koekoetech.clockify.adapters;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.koekoetech.clockify.R;
import com.koekoetech.clockify.models.TimeEntryWrapper;

import java.util.ArrayList;

public class TimeEntryRecordRVAdapter extends BaseQuickAdapter<TimeEntryWrapper, BaseViewHolder> {

    public TimeEntryRecordRVAdapter() {
        super(R.layout.item_time_entry_record, new ArrayList<>());
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, TimeEntryWrapper item) {
        AppCompatTextView tvCategoryName = helper.getView(R.id.item_ter_title);
        tvCategoryName.setText(item.getDate());
        RecyclerView rvRecord = helper.getView(R.id.item_ter_rv);
        rvRecord.setNestedScrollingEnabled(false);
        RecordRVAdapter recordRVAdapter = new RecordRVAdapter();
        rvRecord.setAdapter(recordRVAdapter);
        recordRVAdapter.replaceData(item.getTimeEntryRecordList());
    }
}
