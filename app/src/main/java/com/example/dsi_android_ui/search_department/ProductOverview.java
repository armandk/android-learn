package com.example.dsi_android_ui.search_department;

import com.example.dsi_android_ui.search_product.Location;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class ProductOverview {
    @SerializedName("product_name")
    @Expose
    private String productName;

    @SerializedName("sku")
    @Expose
    private String sku;
  
    @SerializedName("gtin")
    @Expose
    private String gtin;
  
    @SerializedName("count")
    @Expose
    private int count;

    @SerializedName("locations")
    @Expose
    private List<Location> locations;

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public String getProductName() {
        return productName;
    }


    public void setProductName(String productName) {
        this.productName = productName;
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

    public String getGtin() {
        return gtin;
    }

    public void setGtin(String gtin) {
        this.gtin = gtin;
    }


}