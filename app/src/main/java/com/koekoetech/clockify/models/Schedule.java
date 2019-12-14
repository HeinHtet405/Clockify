package com.koekoetech.clockify.models;

import androidx.annotation.NonNull;

import java.util.List;

public class Schedule {

    private Category category;
    private List<TimeEntry> timeEntryList;

    public Schedule() {
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<TimeEntry> getTimeEntryList() {
        return timeEntryList;
    }

    public void setTimeEntryList(List<TimeEntry> timeEntryList) {
        this.timeEntryList = timeEntryList;
    }

    @NonNull
    @Override
    public String toString() {
        return "Schedule{" +
                "category=" + category +
                ", timeEntryList=" + timeEntryList +
                '}';
    }
}
