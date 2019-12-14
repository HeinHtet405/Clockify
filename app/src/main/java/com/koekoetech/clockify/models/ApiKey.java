package com.koekoetech.clockify.models;

import androidx.annotation.NonNull;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ApiKey extends RealmObject {

    @PrimaryKey
    private String id;
    private String key;

    public ApiKey() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @NonNull
    @Override
    public String toString() {
        return "ApiKey{" +
                "id='" + id + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
