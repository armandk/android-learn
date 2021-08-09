package com.example.dsi_android_ui.search_product;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @SerializedName("Sku")
    @Expose
    public String sku;
    @SerializedName("Gtin")
    @Expose
    public String gtin;
    @SerializedName("Department")
    @Expose
    public String department;
    @SerializedName("Category")
    @Expose
    public String category;
    @SerializedName("ProductName")
    @Expose
    public String productName;
    @SerializedName("Count")
    @Expose
    public int count;
    @SerializedName("SalePrice")
    @Expose
    public float salePrice;
    @SerializedName("RegPrice")
    @Expose
    public float regPrice;
    @SerializedName("Locations")
    @Expose
    public List<Location> locations = null;
    @SerializedName("StatusIcon")
    @Expose
    public String statusIcon;
    @SerializedName("Status")
    @Expose
    public String status;

    @Override
    public String toString() {
        return "Product{" +
                "sku='" + sku + '\'' +
                ", department='" + department + '\'' +
                ", category='" + category + '\'' +
                ", productName='" + productName + '\'' +
                ", count=" + count +
                ", salePrice=" + salePrice +
                ", regPrice=" + regPrice +
                ", locations=" + locations +
                ", statusIcon='" + statusIcon + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public String getProductName() {  return this.productName; }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public float getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(float salePrice) {
        this.salePrice = salePrice;
    }

    public float getRegPrice() {
        return regPrice;
    }

    public void setRegPrice(float regPrice) {
        this.regPrice = regPrice;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
