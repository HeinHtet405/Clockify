package com.koekoetech.clockify.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.koekoetech.clockify.R;
import com.koekoetech.clockify.activities.base.NetPagingRVActivity;
import com.koekoetech.clockify.adapters.TimeEntryRecordRVAdapter;
import com.koekoetech.clockify.dbStorage.CategoryDbAccess;
import com.koekoetech.clockify.dbStorage.TimeEntryDbAccess;
import com.koekoetech.clockify.dialogs.CategoryCreateDialog;
import com.koekoetech.clockify.dialogs.CategoryListBottomSheet;
import com.koekoetech.clockify.dialogs.ProfileDialog;
import com.koekoetech.clockify.helpers.AppProgressDialogHelper;
import com.koekoetech.clockify.helpers.DateHelper;
import com.koekoetech.clockify.helpers.MyConstant;
import com.koekoetech.clockify.helpers.NetAdapterConfigImpl;
import com.koekoetech.clockify.helpers.SharePreferenceHelper;
import com.koekoetech.clockify.interfaces.NetAdapterConfig;
import com.koekoetech.clockify.models.Category;
import com.koekoetech.clockify.models.TimeEntry;
import com.koekoetech.clockify.models.TimeEntryRecord;
import com.koekoetech.clockify.models.TimeEntryWrapper;
import com.koekoetech.clockify.models.UserInfo;
import com.koekoetech.clockify.rest.RestClient;
import com.koekoetech.clockify.rest.RetrofitCallbackHelper;
import com.koekoetech.clockify.rest.endpoints.TimeEntryEndpoints;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.Realm;
import retrofit2.Call;

public class MainActivity extends NetPagingRVActivity<List<TimeEntryRecord>, TimeEntryWrapper, TimeEntryRecordRVAdapter> implements CategoryListBottomSheet.OnClickCategory, DatePickerDialog.OnDateSetListener {

    @BindView(R.id.activity_main_rv)
    RecyclerView rvTimeEntry;

    @BindView(R.id.activity_main_srl)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.toolbar_ivProfile)
    AppCompatImageView ivProfile;

    @BindView(R.id.activity_main_btn_category)
    MaterialButton btnCategory;

    @BindView(R.id.activity_main_btn_day)
    MaterialButton btnDay;

    @BindView(R.id.activity_main_btn_add)
    MaterialButton btnAdd;

    @BindString(R.string.app_name)
    String appName;

    @BindString(R.string.lbl_category_title)
    String categoryTitle;

    private Unbinder unbinder;
    private Realm mRealm;
    private NetAdapterConfig adapterConfig;
    private TimeEntryDbAccess timeEntryDbAccess;
    private TimeEntryRecordRVAdapter timeEntryRecordRVAdapter;
    private UserInfo userInfo;
    private String apiKey;
    private Category getCategory;
    private DatePickerDialog dpd;
    private String getPickDate = "";
    private AppProgressDialogHelper appProgressDialogHelper;
    private CategoryDbAccess categoryDbAccess;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void setupContents(Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this);
        setupToolbarText(appName);
        init();
    }

    @Override
    protected boolean isCacheExist() {
        return false;
    }

    @NonNull
    @Override
    protected RecyclerView getRecyclerView() {
        return rvTimeEntry;
    }

    @NonNull
    @Override
    protected TimeEntryRecordRVAdapter getRecyclerAdapter() {
        return timeEntryRecordRVAdapter;
    }

    @NonNull
    @Override
    protected SwipeRefreshLayout getSwipeRefresh() {
        return swipeRefreshLayout;
    }

    @Override
    protected List<TimeEntryWrapper> onDataReceived(List<TimeEntryRecord> data) {
        HashMap<String, List<TimeEntryRecord>> timeEntryRecordMap = new HashMap<>();
        for (TimeEntryRecord ter : data) {
            final String startDate = ter.getTimeInterval().getStartTime();
            String formatDateString = DateHelper.serverFormatToLocal(startDate);
            if (!TextUtils.isEmpty(formatDateString)) {
                List<TimeEntryRecord> groupedEntryList = timeEntryRecordMap.get(formatDateString);
                if (groupedEntryList == null) {
                    groupedEntryList = new ArrayList<>();
                }
                groupedEntryList.add(ter);
                timeEntryRecordMap.put(formatDateString, groupedEntryList);
            }
        }

        List<TimeEntryWrapper> recordList = new ArrayList<>();
        for (String startDate : timeEntryRecordMap.keySet()) {
            TimeEntryWrapper timeEntryWrapper = new TimeEntryWrapper();
            timeEntryWrapper.setDate(startDate);
            timeEntryWrapper.setTimeEntryRecordList(timeEntryRecordMap.get(startDate));
            recordList.add(timeEntryWrapper);
        }
        Collections.sort(recordList, (r1, r2) -> {
            Date date1 = DateHelper.getDateFromString(r1.getDate(), MyConstant.PATTERN_DMY_DASH);
            Date date2 = DateHelper.getDateFromString(r2.getDate(), MyConstant.PATTERN_DMY_DASH);
            return date2.compareTo(date1);
        });
        return recordList;
    }

    @NonNull
    @Override
    protected Call<List<TimeEntryRecord>> getRetrofitCall(int currentPage) {
        TimeEntryEndpoints timeEntryEndpoints = RestClient.getTimeEntryEndpoints(apiKey);
        return timeEntryEndpoints.getTimeEntryRecordList(userInfo.getActiveWorkspace(), userInfo.getId(), currentPage);
    }

    @NonNull
    @Override
    protected NetAdapterConfig getAdapterConfig() {
        return adapterConfig;
    }

    @Override
    protected void onSwipeRefreshed() {
        // do nothing
    }

    private void init() {
        mRealm = Realm.getDefaultInstance();
        categoryDbAccess = new CategoryDbAccess(mRealm);
        timeEntryDbAccess = new TimeEntryDbAccess(mRealm);
        adapterConfig = new NetAdapterConfigImpl();
        appProgressDialogHelper = new AppProgressDialogHelper(this);
        appProgressDialogHelper.setMessage("Please wait...");
        timeEntryRecordRVAdapter = new TimeEntryRecordRVAdapter();
        SharePreferenceHelper sharePreferenceHelper = SharePreferenceHelper.getHelper(this);
        userInfo = sharePreferenceHelper.getUserInformation();
        apiKey = sharePreferenceHelper.getApiKey();

        Glide.with(this)
                .load(userInfo.getProfilePicture())
                .placeholder(R.drawable.img_logo)
                .apply(RequestOptions.circleCropTransform())
                .into(ivProfile);
    }

    @OnClick(R.id.activity_main_fab_add)
    public void clickFabAdd() {
        Intent intent = new Intent(MainActivity.this, ScheduleListActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.toolbar_ivProfile)
    public void clickProfile() {
        ProfileDialog profileDialog = new ProfileDialog(userInfo);
        profileDialog.show(getSupportFragmentManager());
    }

    @OnClick(R.id.activity_main_btn_category)
    public void clickBtnCategory() {
        CategoryListBottomSheet categoryListBottomSheet = new CategoryListBottomSheet(MyConstant.MAIN);
        categoryListBottomSheet.setListener(this);
        categoryListBottomSheet.show(getSupportFragmentManager(), categoryListBottomSheet.getTag());
    }

    @OnClick(R.id.activity_main_btn_day)
    public void clickBtnDay() {
        getDateFromPicker();
    }

    private void getDateFromPicker() {
        Calendar now = Calendar.getInstance();

        if (dpd == null) {
            dpd = DatePickerDialog.newInstance(
                    MainActivity.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
        } else {
            dpd.initialize(
                    MainActivity.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
        }
        dpd.setThemeDark(false);
        dpd.vibrate(true);
        dpd.dismissOnPause(true);
        dpd.showYearPickerFirst(false);
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.setAccentColor(ContextCompat.getColor(this, R.color.colorAccent));
        dpd.setMaxDate(now);

        dpd.setOnCancelListener(dialog -> dpd = null);
        dpd.show(getSupportFragmentManager(), "Datepickerdialog");
    }

    @OnClick(R.id.activity_main_btn_add)
    public void clickBtnAdd() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Confirm Submit")
                .setMessage("Are you sure to submit this schedule to Clockify?")
                .setPositiveButton("Submit", (dialog, which) -> {
                    String categoryId = getCategory.getId();

                    if (TextUtils.isEmpty(getPickDate)) {
                        getPickDate = DateHelper.getTodayDate();
                    }

                    int checkSize = 0;
                    List<TimeEntry> getScheduleList = getScheduleData(categoryId);
                    if (!getScheduleList.isEmpty()) {
                        int scheduleSize = getScheduleList.size();
                        for (TimeEntry timeEntry : getScheduleList) {
                            String startTime = DateHelper.localTimeToServerTime(timeEntry.getStartTime());
                            String endTime = DateHelper.localTimeToServerTime(timeEntry.getEndTime());
                            String completeStartDate = getPickDate + "T" + startTime + "Z";
                            String completeEndDate = getPickDate + "T" + endTime + "Z";

                            // Server send time entry model
                            TimeEntry createTime = new TimeEntry();
                            createTime.setStartTime(completeStartDate);
                            createTime.setBillable(timeEntry.isBillable());
                            createTime.setDescription(timeEntry.getDescription());
                            createTime.setProjectId(timeEntry.getProjectId());
                            createTime.setEndTime(completeEndDate);

                            // Send to server
                            postTimeEntry(createTime);

                            checkSize += 1;
                        }

                        if (checkSize == scheduleSize) {
                            onRefresh();
                        }

                    } else {
                        Toast.makeText(this, "You need to fill your schedule.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create();
        alertDialog.setOnShowListener(dialog -> {
            Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        });
        alertDialog.show();
    }

    private void postTimeEntry(TimeEntry timeEntry) {
        appProgressDialogHelper.show();
        Call<TimeEntry> timeEntryCall = RestClient.getTimeEntryEndpoints(apiKey).postTimeEntry(userInfo.getActiveWorkspace(), timeEntry);
        timeEntryCall.enqueue(new RetrofitCallbackHelper<TimeEntry>() {
            @Override
            protected void onSuccess(TimeEntry data, int responseCode) {
                appProgressDialogHelper.dismiss();
                Toast.makeText(MainActivity.this, "Grate...complete your daily job.", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onFailure(Throwable t, int responseCode, int resultCode) {
                t.printStackTrace();
                appProgressDialogHelper.dismiss();
                Toast.makeText(MainActivity.this, "Fail to sent data", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private List<TimeEntry> getScheduleData(String id) {
        List<TimeEntry> timeEntryList = timeEntryDbAccess.getAllTimeEntryList();
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

        List<TimeEntry> getTimeEntryByCategory = new ArrayList<>();
        for (String categoryId : timeEntryMap.keySet()) {
            if (categoryId.equals(id)) {
                getTimeEntryByCategory = timeEntryMap.get(categoryId);
            }
        }
        return getTimeEntryByCategory;
    }

    @Override
    public void onResume() {
        super.onResume();
        getCategory = categoryDbAccess.getFirstCategory();
        if (getCategory == null) {
            btnCategory.setText(categoryTitle);
            CategoryCreateDialog categoryCreateDialog = new CategoryCreateDialog(MyConstant.MAIN);
            categoryCreateDialog.show(getSupportFragmentManager());
        } else {
            btnCategory.setText(getCategory.getName());
        }
        DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag("Datepickerdialog");
        if (dpd != null) dpd.setOnDateSetListener(this);
    }

    @Override
    protected void onDestroy() {
        mRealm.close();
        super.onDestroy();
        unbinder.unbind();
        dpd = null;
    }

    @Override
    public void clickCategory(Category category) {
        getCategory = category;
        btnCategory.setText(category.getName());
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        getPickDate = DateHelper.formatDate(calendar.getTime(), MyConstant.PATTERN_DMY_SERVER_DASH);
        btnDay.setText(getPickDate);
        dpd = null;
    }
}
