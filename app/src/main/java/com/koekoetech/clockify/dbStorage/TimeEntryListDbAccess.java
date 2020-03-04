package com.koekoetech.clockify.dbStorage;

import com.koekoetech.clockify.models.TimeEntry;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Hein Htet Aung on 04/03/2020 13:37.
 */
public class TimeEntryListDbAccess {

    private Realm mRealm;

    public TimeEntryListDbAccess(Realm realm) {
        this.mRealm = realm;
    }

    public List<TimeEntry> getAllTimeEntryList() {
        RealmResults<TimeEntry> timeEntryRealmResults = mRealm.where(TimeEntry.class).findAll();
        List<TimeEntry> timeEntryList = new ArrayList<>();
        for (TimeEntry timeEntry : timeEntryRealmResults) {
            timeEntryList.add(mRealm.copyFromRealm(timeEntry));
        }
        return timeEntryList;
    }

}
