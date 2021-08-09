package com.example.dsi_android_ui.search_product;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Location implements Serializable {

    @SerializedName("Aisle")
    @Expose
    public String aisle;
    @SerializedName("Shelf")
    @Expose
    public String shelf;
    @SerializedName("Section")
    @Expose
    public String section;
    @SerializedName("Bin")
    @Expose
    public String bin;
    @SerializedName("Count")
    @Expose
    public int count;
    @SerializedName("Min")
    @Expose
    public int min;
    @SerializedName("Max")
    @Expose
    public int max;
    @SerializedName("Status")
    @Expose
    public String status;
    @SerializedName("Info")
    @Expose
    public String info;
    @SerializedName("LocationPath")
    @Expose
    public String locationPath;

    @Override
    public String toString() {
        return "Location{" +
                "aisle='" + aisle + '\'' +
                ", shelf='" + shelf + '\'' +
                ", section='" + section + '\'' +
                ", bin='" + bin + '\'' +
                ", count=" + count +
                ", status='" + status + '\'' +
                ", info='" + info + '\'' +
                ", min=" + min +
                ", max=" + max +
                ", locationPath=" + locationPath +
                '}';
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public String getAisle() {
        return aisle;
    }

    public void setAisle(String aisle) {
        this.aisle = aisle;
    }

    public String getShelf() {
        return shelf;
    }

    public void setShelf(String shelf) {
        this.shelf = shelf;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getLocationPath() {
        return locationPath;
    }

    public void setLocationPath(String locationPath) {
        this.locationPath = locationPath;
    }
}