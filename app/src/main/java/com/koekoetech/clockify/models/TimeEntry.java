package com.koekoetech.clockify.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TimeEntry extends RealmObject {

    public static final String FIELD_CATEGORY_ID = "categoryId";

    @PrimaryKey
    private String id;

    private String categoryId;

    @SerializedName("start")
    @Expose
    private String startTime;

    @SerializedName("billable")
    @Expose
    private boolean billable;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("projectId")
    @Expose
    private String projectId;

    @SerializedName("end")
    @Expose
    private String endTime;

    public TimeEntry() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public boolean isBillable() {
        return billable;
    }

    public void setBillable(boolean billable) {
        this.billable = billable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @NonNull
    @Override
    public String toString() {
        return "TimeEntry{" +
                "id='" + id + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", startTime='" + startTime + '\'' +
                ", billable=" + billable +
                ", description='" + description + '\'' +
                ", projectId='" + projectId + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
