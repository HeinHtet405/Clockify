package com.koekoetech.clockify.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.koekoetech.clockify.R;
import com.koekoetech.clockify.adapters.ProjectRVAdapter;
import com.koekoetech.clockify.dbStorage.ProjectDbAccess;
import com.koekoetech.clockify.models.Project;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;

public class ProjectListBottomSheet extends BottomSheetDialogFragment {

    @BindView(R.id.dialog_project_rv)
    RecyclerView rvProject;

    private Realm mRealm;
    private Unbinder unbinder;
    private OnClickProject listener;

    public ProjectListBottomSheet() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_project_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    public void setListener(OnClickProject listener) {
        this.listener = listener;
    }

    private void init() {
        mRealm = Realm.getDefaultInstance();
        ProjectDbAccess projectDbAccess = new ProjectDbAccess(mRealm);
        ProjectRVAdapter projectRVAdapter = new ProjectRVAdapter();
        projectRVAdapter.setOnItemClickListener((adapter, view, position) -> {
            Project project = projectRVAdapter.getItem(position);
            listener.clickProject(project);
            dismiss();
        });
        rvProject.setAdapter(projectRVAdapter);
        projectRVAdapter.setNewData(projectDbAccess.getAllProjectList());
    }

    @Override
    public void onDestroy() {
        mRealm.close();
        super.onDestroy();
        unbinder.unbind();
    }

    public interface OnClickProject {
        void clickProject(Project project);
    }
}
