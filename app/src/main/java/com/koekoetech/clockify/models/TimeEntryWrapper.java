package com.koekoetech.clockify.models;

import androidx.annotation.NonNull;

import java.util.List;

public class TimeEntryWrapper {

    private String date;
    private List<TimeEntryRecord> timeEntryRecordList;

    public TimeEntryWrapper() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<TimeEntryRecord> getTimeEntryRecordList() {
        return timeEntryRecordList;
    }

    public void setTimeEntryRecordList(List<TimeEntryRecord> timeEntryRecordList) {
        this.timeEntryRecordList = timeEntryRecordList;
    }

    @NonNull
    @Override
    public String toString() {
        return "TimeEntryWrapper{" +
                "date='" + date + '\'' +
                ", timeEntryRecordList=" + timeEntryRecordList +
                '}';
    }
}
