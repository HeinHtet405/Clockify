package com.koekoetech.clockify.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kal.rackmonthpicker.RackMonthPicker;
import com.koekoetech.clockify.R;
import com.koekoetech.clockify.activities.base.NetPagingRVActivity;
import com.koekoetech.clockify.adapters.TimeEntryRecordRVAdapter;
import com.koekoetech.clockify.dbStorage.CategoryDbAccess;
import com.koekoetech.clockify.dbStorage.ProjectDbAccess;
import com.koekoetech.clockify.dbStorage.TimeEntryDbAccess;
import com.koekoetech.clockify.dbStorage.TimeEntryListDbAccess;
import com.koekoetech.clockify.dialogs.CategoryCreateDialog;
import com.koekoetech.clockify.dialogs.CategoryListBottomSheet;
import com.koekoetech.clockify.dialogs.ProfileDialog;
import com.koekoetech.clockify.helpers.AppProgressDialogHelper;
import com.koekoetech.clockify.helpers.DateHelper;
import com.koekoetech.clockify.helpers.HolidayHelper;
import com.koekoetech.clockify.helpers.MyConstant;
import com.koekoetech.clockify.helpers.NetAdapterConfigImpl;
import com.koekoetech.clockify.helpers.SharePreferenceHelper;
import com.koekoetech.clockify.interfaces.NetAdapterConfig;
import com.koekoetech.clockify.models.Category;
import com.koekoetech.clockify.models.Project;
import com.koekoetech.clockify.models.TimeEntry;
import com.koekoetech.clockify.models.TimeEntryRecord;
import com.koekoetech.clockify.models.TimeEntryWrapper;
import com.koekoetech.clockify.models.UserInfo;
import com.koekoetech.clockify.rest.RestClient;
import com.koekoetech.clockify.rest.RetrofitCallbackHelper;
import com.koekoetech.clockify.rest.endpoints.TimeEntryEndpoints;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.Realm;
import needle.Needle;
import needle.UiRelatedTask;
import retrofit2.Call;

public class MainActivity extends NetPagingRVActivity<List<TimeEntryRecord>, TimeEntryWrapper, TimeEntryRecordRVAdapter> implements CategoryListBottomSheet.OnClickCategory, DatePickerDialog.OnDateSetListener, ProfileDialog.OnClickMonth {

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

    @BindView(R.id.activity_main_btn_month)
    MaterialButton btnMonth;

    @BindString(R.string.app_name)
    String appName;

    @BindString(R.string.lbl_category_title)
    String categoryTitle;

    @BindView(R.id.activity_main_fab_add)
    FloatingActionButton fabAdd;

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

    private SharePreferenceHelper sharePreferenceHelper;

    private int dateType = 0;

    private final ArrayList<String> finalDaySelectedList = new ArrayList<>();

    private boolean selectMonth = false;

    private ProjectDbAccess projectDbAccess;

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
        sharePreferenceHelper = SharePreferenceHelper.getHelper(this);
        userInfo = sharePreferenceHelper.getUserInformation();
        apiKey = sharePreferenceHelper.getApiKey();
        projectDbAccess = new ProjectDbAccess(mRealm);

        Glide.with(this)
                .load(userInfo.getProfilePicture())
                .placeholder(R.drawable.img_logo)
                .apply(RequestOptions.circleCropTransform())
                .into(ivProfile);

        playingDateButton();

        fabAdd.setImageResource(R.drawable.ic_timer);
        fabAdd.getDrawable().mutate().setTint(ContextCompat.getColor(this, R.color.colorWhite));
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
        profileDialog.setOnClickListener(this);
    }

    @OnClick(R.id.toolbar_ivRefresh)
    public void clickRefreshProject() {
        if (!projectDbAccess.getAllProjectList().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Refresh Project Data", Toast.LENGTH_SHORT).show();
            projectDbAccess.deleteAllProjects();
            getProjectData();
        }
    }

    private void getProjectData() {
        userInfo = sharePreferenceHelper.getUserInformation();
        Call<List<Project>> projectCall = RestClient.getProjectEndpoints(sharePreferenceHelper.getApiKey()).getProjectList(userInfo.getActiveWorkspace(), 100);
        projectCall.enqueue(new RetrofitCallbackHelper<List<Project>>() {
            @Override
            protected void onSuccess(List<Project> data, int responseCode) {
                appProgressDialogHelper.dismiss();
                for (Project project : data) {
                    projectDbAccess.insertUpdateProject(project);
                }
            }

            @Override
            protected void onFailure(Throwable t, int responseCode, int resultCode) {
                appProgressDialogHelper.dismiss();
                t.printStackTrace();
            }
        });
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

    @OnClick(R.id.activity_main_btn_month)
    public void clickBtnMonth() {
        new RackMonthPicker(this)
                .setLocale(Locale.ENGLISH)
                .setPositiveButton((month, startDate, endDate, year, monthLabel) -> {
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.MONTH, month - 1);
                    cal.set(Calendar.YEAR, year);
                    SimpleDateFormat df = new SimpleDateFormat("MMM-yyyy", Locale.UK);
                    btnMonth.setText(df.format(cal.getTime()));
                    filterDayProcess(month, year, endDate);
                    selectMonth = true;
                })
                .setNegativeButton(Dialog::dismiss).show();
    }

    private void filterDayProcess(int month, int year, int endDate) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.YEAR, year);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        SimpleDateFormat weekOfDayFormat = new SimpleDateFormat("E", Locale.UK);
        ArrayList<String> weekDayList = new ArrayList<>();
        ArrayList<String> weekEndList = new ArrayList<>();
        for (int i = 0; i < endDate; i++) {
            cal.set(Calendar.DAY_OF_MONTH, i + 1);
            String dayOfWeek = weekOfDayFormat.format(cal.getTime());
            if (TextUtils.equals("Sun", dayOfWeek) ||
                    TextUtils.equals("Sat", dayOfWeek)) {
                weekEndList.add(df.format(cal.getTime()));
                Log.i("hein", "filterDayProcess: " + weekEndList);
            } else {
                weekDayList.add(df.format(cal.getTime()));
            }
        }
        if (year == 2020) {
            weekDayList.removeAll(HolidayHelper.holiday2020list());
        }
        finalDaySelectedList.addAll(weekDayList);
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
                    if (dateType == 0) {
                        processTimeEntry(0);
                    } else {
                        if (selectMonth) {
                            processTimeEntry(1);
                        } else {
                            Toast.makeText(this, "Please selected your schedule month", Toast.LENGTH_SHORT).show();
                        }
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

    private void processTimeEntry(int dateType) {
        if (dateType == 0) {
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
            }
        } else {
            appProgressDialogHelper.show();
            Needle.onBackgroundThread().execute(new UiRelatedTask<Void>() {
                @Override
                protected Void doWork() {
                    Realm mRealm = Realm.getDefaultInstance();
                    TimeEntryListDbAccess timeEntryListDbAccess = new TimeEntryListDbAccess(mRealm);
                    CategoryDbAccess categoryDbAccess = new CategoryDbAccess(mRealm);
                    String categoryId = categoryDbAccess.getFirstCategory().getId();
                    List<TimeEntry> getScheduleList = getScheduleListData(categoryId, timeEntryListDbAccess);
                    if (!getScheduleList.isEmpty()) {
                        for (String selectedDay : finalDaySelectedList) {
                            for (TimeEntry timeEntry : getScheduleList) {
                                String startTime = DateHelper.localTimeToServerTime(timeEntry.getStartTime());
                                String endTime = DateHelper.localTimeToServerTime(timeEntry.getEndTime());
                                String completeStartDate = selectedDay + "T" + startTime + "Z";
                                String completeEndDate = selectedDay + "T" + endTime + "Z";

                                // Server send time entry model
                                TimeEntry createTime = new TimeEntry();
                                createTime.setStartTime(completeStartDate);
                                createTime.setBillable(timeEntry.isBillable());
                                createTime.setDescription(timeEntry.getDescription());
                                createTime.setProjectId(timeEntry.getProjectId());
                                createTime.setEndTime(completeEndDate);

                                // Send to server
                                postTimeEntryAsync(createTime);
                            }
                        }
                    }
                    return null;
                }

                @Override
                protected void thenDoUiRelatedWork(Void aVoid) {
                    appProgressDialogHelper.dismiss();
                    onRefresh();
                }
            });
        }
    }

    private void postTimeEntryAsync(TimeEntry timeEntry) {
        Call<TimeEntry> timeEntryCall = RestClient.getTimeEntryEndpoints(apiKey).postTimeEntry(userInfo.getActiveWorkspace(), timeEntry);
        try {
            timeEntryCall.execute();
        }catch (Exception e) {
            Log.e("hein", "postOneMonthEntry: fail!", e);
        }
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

    private List<TimeEntry> getScheduleListData(String id, TimeEntryListDbAccess timeEntryListDbAccess) {
        List<TimeEntry> timeEntryList = timeEntryListDbAccess.getAllTimeEntryList();
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

    @Override
    public void onClickMonthType(MaterialCheckBox checkBox) {
        if (checkBox.isChecked()) {
            sharePreferenceHelper.setDateType(true);
        } else {
            sharePreferenceHelper.setDateType(false);
        }
        playingDateButton();
    }

    private void playingDateButton() {
        if (sharePreferenceHelper.getDateType()) {
            dateType = 1;
            btnDay.setVisibility(View.GONE);
            btnMonth.setVisibility(View.VISIBLE);
        } else {
            dateType = 0;
            btnDay.setVisibility(View.VISIBLE);
            btnMonth.setVisibility(View.GONE);
        }
    }
}
