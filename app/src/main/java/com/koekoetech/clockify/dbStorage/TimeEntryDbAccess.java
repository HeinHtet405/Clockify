package com.koekoetech.clockify.dbStorage;

import com.koekoetech.clockify.models.TimeEntry;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class TimeEntryDbAccess {

    private Realm mRealm;

    public TimeEntryDbAccess(Realm realm) {
        this.mRealm = realm;
    }

    public void insertUpdateTimeEntry(TimeEntry timeEntry) {
        mRealm.executeTransaction(realm -> realm.copyToRealmOrUpdate(timeEntry));
    }

    public List<TimeEntry> getAllTimeEntryList() {
        RealmResults<TimeEntry> timeEntryRealmResults = mRealm.where(TimeEntry.class).findAll();
        List<TimeEntry> timeEntryList = new ArrayList<>();
        for (TimeEntry timeEntry : timeEntryRealmResults) {
            timeEntryList.add(mRealm.copyFromRealm(timeEntry));
        }
        return timeEntryList;
    }

    public void deleteTimeEntry(String categoryId) {
        mRealm.executeTransaction(realm -> {
            RealmResults<TimeEntry> timeEntryRealmResults = mRealm.where(TimeEntry.class).equalTo(TimeEntry.FIELD_CATEGORY_ID, categoryId).findAll();
            timeEntryRealmResults.deleteAllFromRealm();
        });
    }
}
