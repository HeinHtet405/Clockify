package com.koekoetech.clockify.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.koekoetech.clockify.R;
import com.koekoetech.clockify.adapters.CategoryRVAdapter;
import com.koekoetech.clockify.dbStorage.CategoryDbAccess;
import com.koekoetech.clockify.models.Category;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;

public class CategoryListBottomSheet extends BottomSheetDialogFragment {

    @BindView(R.id.dialog_category_rv)
    RecyclerView rvCategory;

    private Unbinder unbinder;
    private Realm mRealm;
    private int activityId;
    private OnClickCategory listener;

    public CategoryListBottomSheet(int activityId) {
        this.activityId = activityId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_category_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    public void setListener(OnClickCategory listener) {
        this.listener = listener;
    }

    private void init() {
        mRealm = Realm.getDefaultInstance();
        CategoryDbAccess categoryDbAccess = new CategoryDbAccess(mRealm);
        CategoryRVAdapter categoryRVAdapter = new CategoryRVAdapter(activityId);
        categoryRVAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            final Category category = categoryRVAdapter.getData().get(position);
            if (view.getId() == R.id.item_category_remove) {
                categoryDbAccess.deleteCategory(category.getId());
                adapter.remove(position);
                dismiss();
            } else if (view.getId() == R.id.item_category_main) {
                listener.clickCategory(category);
                dismiss();
            }
        });
        rvCategory.setAdapter(categoryRVAdapter);
        categoryRVAdapter.setNewData(categoryDbAccess.getAllCategoryList());
    }

    @Override
    public void onDestroy() {
        mRealm.close();
        super.onDestroy();
        unbinder.unbind();
    }

    public interface OnClickCategory {
        void clickCategory(Category category);
    }

}
