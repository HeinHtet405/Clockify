package com.koekoetech.clockify.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TimeEntryRecord implements Parcelable {

     @SerializedName("id")
     @Expose
     private String id;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("projectId")
    @Expose
    private String projectId;

    @SerializedName("timeInterval")
    @Expose
    private TimeInterval timeInterval;

    public TimeEntryRecord() {
    }

    protected TimeEntryRecord(Parcel in) {
        id = in.readString();
        description = in.readString();
        projectId = in.readString();
        timeInterval = in.readParcelable(TimeInterval.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(description);
        dest.writeString(projectId);
        dest.writeParcelable(timeInterval, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TimeEntryRecord> CREATOR = new Creator<TimeEntryRecord>() {
        @Override
        public TimeEntryRecord createFromParcel(Parcel in) {
            return new TimeEntryRecord(in);
        }

        @Override
        public TimeEntryRecord[] newArray(int size) {
            return new TimeEntryRecord[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public TimeInterval getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(TimeInterval timeInterval) {
        this.timeInterval = timeInterval;
    }

    @NonNull
    @Override
    public String toString() {
        return "TimeEntryRecord{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", projectId='" + projectId + '\'' +
                ", timeInterval=" + timeInterval +
                '}';
    }
}
