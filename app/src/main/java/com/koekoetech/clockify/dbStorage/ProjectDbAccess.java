package com.koekoetech.clockify.dbStorage;

import com.koekoetech.clockify.models.Project;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class ProjectDbAccess {

    private Realm mRealm;

    public ProjectDbAccess(Realm realm) {
        this.mRealm = realm;
    }

    public void insertUpdateProject(Project project) {
        mRealm.executeTransaction(realm -> realm.copyToRealmOrUpdate(project));
    }

    public List<Project> getAllProjectList() {
        RealmResults<Project> projectRealmResults = mRealm.where(Project.class).findAll();
        List<Project> projectList = new ArrayList<>();
        for (Project project : projectRealmResults) {
            projectList.add(mRealm.copyFromRealm(project));
        }
        return projectList;
    }

    public Project getProject(String projectId) {
        return mRealm.where(Project.class).equalTo(Project.FIELD_PROJECT_ID, projectId).findFirst();
    }

}
