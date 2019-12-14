package com.koekoetech.clockify.models;

import androidx.annotation.NonNull;

public class DateCategory {

    private int id;
    private String name;

    public DateCategory(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return "DateCategory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
