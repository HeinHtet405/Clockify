package com.koekoetech.clockify.dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.material.button.MaterialButton;
import com.koekoetech.clockify.R;
import com.koekoetech.clockify.activities.ScheduleListActivity;
import com.koekoetech.clockify.dbStorage.CategoryDbAccess;
import com.koekoetech.clockify.dialogs.base.BaseDialogFragment;
import com.koekoetech.clockify.helpers.MyConstant;
import com.koekoetech.clockify.helpers.UuidGeneratorHelper;
import com.koekoetech.clockify.models.Category;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.Realm;

public class CategoryCreateDialog extends BaseDialogFragment {

    private static final String TAG = "CategoryCreateDialog";

    @BindView(R.id.dc_ce_et_name)
    AppCompatEditText etName;

    @BindView(R.id.dc_ce_btn_cancel)
    MaterialButton btnCancel;

    private Unbinder unbinder;
    private Realm mRealm;
    private CategoryDbAccess categoryDbAccess;
    private int checkActivity;

    public CategoryCreateDialog(int checkActivity) {
        this.checkActivity = checkActivity;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_category_create_edit;
    }

    @NonNull
    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    protected void onViewReady(@NonNull View view, @Nullable Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, view);
        init();
    }

    private void init() {
        mRealm = Realm.getDefaultInstance();
        categoryDbAccess = new CategoryDbAccess(mRealm);

        if (checkActivity == MyConstant.SCHEDULE_LIST) {
            btnCancel.setVisibility(View.VISIBLE);
        } else {
            btnCancel.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.dc_ce_btn_save)
    void clickBtnSave() {
        String category = Objects.requireNonNull(etName.getText()).toString();
        if (!TextUtils.isEmpty(category)) {
            Category createCategory = new Category();
            createCategory.setId(UuidGeneratorHelper.getGenerateUUID());
            createCategory.setName(category);
            categoryDbAccess.insertUpdateCategory(createCategory);
            dismiss();
            if (checkActivity == MyConstant.MAIN) {
                Intent intent = new Intent(getContext(), ScheduleListActivity.class);
                startActivity(intent);
            }
        } else {
            Toast.makeText(getActivity(), "Please add category name", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.dc_ce_btn_cancel)
    void clickCancel() {
        dismiss();
    }

    @Override
    public void onDestroy() {
        mRealm.close();
        super.onDestroy();
        unbinder.unbind();
    }
}
