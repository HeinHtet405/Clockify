package com.koekoetech.clockify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.koekoetech.clockify.R;
import com.koekoetech.clockify.activities.base.BaseActivity;
import com.koekoetech.clockify.adapters.ScheduleRVAdapter;
import com.koekoetech.clockify.dbStorage.CategoryDbAccess;
import com.koekoetech.clockify.dbStorage.TimeEntryDbAccess;
import com.koekoetech.clockify.dialogs.CategoryCreateDialog;
import com.koekoetech.clockify.dialogs.CategoryListBottomSheet;
import com.koekoetech.clockify.helpers.MyConstant;
import com.koekoetech.clockify.models.Schedule;
import com.koekoetech.clockify.models.TimeEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.Realm;

public class ScheduleListActivity extends BaseActivity {

    @BindView(R.id.activity_schedule_rv)
    RecyclerView rvSchedule;

    @BindView(R.id.activity_schedule_fab_add)
    FloatingActionButton fabAdd;

    private Unbinder unbinder;
    private Realm mRealm;
    private ScheduleRVAdapter scheduleRVAdapter;
    private TimeEntryDbAccess timeEntryDbAccess;
    private CategoryDbAccess categoryDbAccess;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_schedule_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar(true);
        unbinder = ButterKnife.bind(this);
        setupToolbarText("Schedule List");
        init();
    }

    @OnClick(R.id.activity_schedule_fab_add)
    public void clickScheduleFabAdd() {
        Intent intent = new Intent(getApplicationContext(), ScheduleCreateEditActivity.class);
        startActivity(intent);
    }

    private void init() {
        mRealm = Realm.getDefaultInstance();
        timeEntryDbAccess = new TimeEntryDbAccess(mRealm);
        categoryDbAccess = new CategoryDbAccess(mRealm);

        scheduleRVAdapter = new ScheduleRVAdapter();
        scheduleRVAdapter.setEmptyView(R.layout.item_empty, rvSchedule);
        scheduleRVAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            final Schedule timeEntry = scheduleRVAdapter.getData().get(position);
            if (view.getId() == R.id.item_schedule_remove) {
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle("Confirm Delete")
                        .setMessage("Are you sure to delete this time entry? This action cannot be undone!")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            timeEntryDbAccess.deleteTimeEntry(timeEntry.getCategory().getId());
                            adapter.remove(position);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .create();
                alertDialog.setOnShowListener(dialog -> {
                    Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_red_light));
                });
                alertDialog.show();
            }
        });
        rvSchedule.setAdapter(scheduleRVAdapter);

        fabAdd.setImageResource(R.drawable.ic_schedule);
        fabAdd.getDrawable().mutate().setTint(ContextCompat.getColor(this, R.color.colorWhite));
    }

    private void getScheduleDataList() {
        List<TimeEntry> timeEntryList = timeEntryDbAccess.getAllTimeEntryList();
        if (!timeEntryList.isEmpty()) {
            HashMap<String, List<TimeEntry>> timeEntryMap = new HashMap<>();
            for (TimeEntry timeEntry : timeEntryList) {
                final String categoryId = timeEntry.getCategoryId();
                List<TimeEntry> groupedEntryList = timeEntryMap.get(categoryId);
                if (groupedEntryList == null) {
                    groupedEntryList = new ArrayList<>();
                }
                groupedEntryList.add(timeEntry);
                timeEntryMap.put(categoryId, groupedEntryList);
            }

            List<Schedule> scheduleList = new ArrayList<>();
            for (String categoryId : timeEntryMap.keySet()) {
                Schedule schedule = new Schedule();
                schedule.setCategory(categoryDbAccess.getCategory(categoryId));
                schedule.setTimeEntryList(timeEntryMap.get(categoryId));
                scheduleList.add(schedule);
            }
            scheduleRVAdapter.setNewData(scheduleList);
        } else {
            Toast.makeText(this, "You need to create your schedule", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), ScheduleCreateEditActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getScheduleDataList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_add) {
            CategoryCreateDialog categoryCreateDialog = new CategoryCreateDialog(MyConstant.SCHEDULE_LIST);
            categoryCreateDialog.show(getSupportFragmentManager());
            return true;
        } else if (item.getItemId() == R.id.menu_category_list) {
            CategoryListBottomSheet categoryListBottomSheet = new CategoryListBottomSheet(MyConstant.SCHEDULE_LIST);
            categoryListBottomSheet.show(getSupportFragmentManager(), categoryListBottomSheet.getTag());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        mRealm.close();
        super.onDestroy();
        unbinder.unbind();
    }
}
