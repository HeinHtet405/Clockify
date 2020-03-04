package com.koekoetech.clockify.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.koekoetech.clockify.R;
import com.koekoetech.clockify.activities.base.BaseActivity;
import com.koekoetech.clockify.dbStorage.CategoryDbAccess;
import com.koekoetech.clockify.dbStorage.TimeEntryDbAccess;
import com.koekoetech.clockify.dialogs.CategoryCreateDialog;
import com.koekoetech.clockify.dialogs.CategoryListBottomSheet;
import com.koekoetech.clockify.dialogs.ProjectListBottomSheet;
import com.koekoetech.clockify.helpers.DateHelper;
import com.koekoetech.clockify.helpers.MyConstant;
import com.koekoetech.clockify.helpers.UuidGeneratorHelper;
import com.koekoetech.clockify.models.Category;
import com.koekoetech.clockify.models.Project;
import com.koekoetech.clockify.models.TimeEntry;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.Realm;

public class ScheduleCreateEditActivity extends BaseActivity implements ProjectListBottomSheet.OnClickProject, TimePickerDialog.OnTimeSetListener, CategoryListBottomSheet.OnClickCategory {

    @BindView(R.id.activity_sce_tv_category)
    AppCompatTextView tvCategory;

    @BindView(R.id.activity_sce_tv_project)
    AppCompatTextView tvProject;

    @BindView(R.id.activity_sce_tv_start)
    AppCompatTextView tvStart;

    @BindView(R.id.activity_sce_tv_end)
    AppCompatTextView tvEnd;

    @BindView(R.id.activity_sce_et_work)
    AppCompatEditText etWork;

    @BindView(R.id.activity_sce_fab_save)
    FloatingActionButton fabSave;

    private Unbinder unbinder;
    private Realm mRealm;
    private TimeEntryDbAccess timeEntryDbAccess;
    private TimePickerDialog tpd;
    private boolean checkStartTime = true;
    private String categoryName, projectName,
            startTime, endTime, workDesc;
    private Project projectData;
    private Category categoryData;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_schedule_create_edit;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar(true);
        unbinder = ButterKnife.bind(this);
        setupToolbarText("Create Schedule");
        init();
    }

    private void init() {
        mRealm = Realm.getDefaultInstance();
        CategoryDbAccess categoryDbAccess = new CategoryDbAccess(mRealm);
        timeEntryDbAccess = new TimeEntryDbAccess(mRealm);

        if (categoryDbAccess.getAllCategoryList().isEmpty()) {
            CategoryCreateDialog categoryCreateDialog = new CategoryCreateDialog(MyConstant.SCHEDULE_CREATE_EDIT);
            categoryCreateDialog.show(getSupportFragmentManager());
        }

        fabSave.setImageResource(R.drawable.ic_save);
        fabSave.getDrawable().mutate().setTint(ContextCompat.getColor(this, R.color.colorWhite));
    }

    @OnClick(R.id.activity_sce_tv_category)
    public void clickCategoryList() {
        CategoryListBottomSheet categoryListBottomSheet = new CategoryListBottomSheet(MyConstant.SCHEDULE_CREATE_EDIT);
        categoryListBottomSheet.setListener(this);
        categoryListBottomSheet.show(getSupportFragmentManager(), categoryListBottomSheet.getTag());
    }

    @OnClick(R.id.activity_sce_tv_project)
    public void clickProjectList() {
        ProjectListBottomSheet projectListBottomSheet = new ProjectListBottomSheet();
        projectListBottomSheet.setListener(this);
        projectListBottomSheet.show(getSupportFragmentManager(), projectListBottomSheet.getTag());
    }

    @OnClick(R.id.activity_sce_tv_start)
    public void clickStartTime() {
        checkStartTime = true;
        final String startTime = tvStart.getText().toString();
        Date selectedStartTime = null;
        if (!TextUtils.isEmpty(startTime) && !TextUtils.equals(startTime,getString(R.string.lbl_start_hint))){
            selectedStartTime = DateHelper.getDateFromString(startTime,MyConstant.PATTERN_HMS_DASH);
        }
        getTimeDialog(selectedStartTime);
    }

    @OnClick(R.id.activity_sce_tv_end)
    public void clickEndTime() {
        checkStartTime = false;
        final String endTime = tvEnd.getText().toString();
        Date selectedEndTime = null;
        if (!TextUtils.isEmpty(endTime) && !TextUtils.equals(endTime,getString(R.string.lbl_end_hint))){
            selectedEndTime = DateHelper.getDateFromString(endTime,MyConstant.PATTERN_HMS_DASH);
        }
        getTimeDialog(selectedEndTime);
    }

    @OnClick(R.id.activity_sce_fab_save)
    public void clickFabSave() {
        workDesc = Objects.requireNonNull(etWork.getText()).toString();
        if (checkValidation()) {
            TimeEntry createTimeEntry = new TimeEntry();
            createTimeEntry.setId(UuidGeneratorHelper.getGenerateUUID());
            createTimeEntry.setCategoryId(categoryData.getId());
            createTimeEntry.setProjectId(projectData.getId());
            createTimeEntry.setBillable(true);
            createTimeEntry.setStartTime(startTime);
            createTimeEntry.setEndTime(endTime);
            createTimeEntry.setDescription(workDesc);
            timeEntryDbAccess.insertUpdateTimeEntry(createTimeEntry);
            Toast.makeText(this, "Create time entry successfully.", Toast.LENGTH_SHORT).show();
        }
    }

    public void getTimeDialog(@Nullable Date initialTime) {
        Calendar now = Calendar.getInstance();

        if (initialTime != null){
            now.setTime(initialTime);
        }

        /*
        It is recommended to always create a new instance whenever you need to show a Dialog.
        The sample app is reusing them because it is useful when looking for regressions
        during testing
         */
        if (tpd == null) {
            tpd = TimePickerDialog.newInstance(
                    ScheduleCreateEditActivity.this,
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    true
            );
        } else {
            tpd.initialize(
                    ScheduleCreateEditActivity.this,
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    now.get(Calendar.SECOND),
                    true
            );
        }
        tpd.setThemeDark(false);
        tpd.vibrate(true);
        tpd.dismissOnPause(true);
        tpd.enableSeconds(false);
        tpd.setVersion(TimePickerDialog.Version.VERSION_2);
        tpd.setAccentColor(ContextCompat.getColor(this, R.color.colorAccent));
        tpd.setOnCancelListener(dialogInterface -> tpd = null);
        tpd.show(getSupportFragmentManager(), tpd.getTag());
    }

    @Override
    protected void onResume() {
        super.onResume();
        TimePickerDialog tpd = (TimePickerDialog) getSupportFragmentManager().findFragmentByTag("Timepickerdialog");
        if (tpd != null) tpd.setOnTimeSetListener(this);
    }

    @Override
    protected void onDestroy() {
        mRealm.close();
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void clickProject(Project project) {
        projectData = project;
        projectName = project.getName();
        tvProject.setText(project.getName());
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String secondString = second < 10 ? "0" + second : "" + second;
        String time = hourString + ":" + minuteString + ":" + secondString;
        if (checkStartTime) {
            startTime = time;
            tvStart.setText(time);
        } else {
            endTime = time;
            tvEnd.setText(time);
        }
        tpd = null;
    }

    @Override
    public void clickCategory(Category category) {
        categoryData = category;
        categoryName = category.getName();
        tvCategory.setText(category.getName());
    }

    public boolean checkValidation() {
        if (!TextUtils.isEmpty(categoryName) && !TextUtils.isEmpty(projectName) &&
                !TextUtils.isEmpty(startTime) && !TextUtils.isEmpty(endTime) && !TextUtils.isEmpty(workDesc)) {
            return true;
        } else {
            Toast.makeText(this, "Please fill require data.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
