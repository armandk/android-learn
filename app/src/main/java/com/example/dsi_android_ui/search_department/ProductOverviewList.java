package com.example.dsi_android_ui.search_department;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;

@Data
public class ProductOverviewList {

    @SerializedName("ProductList")
    @Expose
    private List<ProductOverview> productList = null;

    public List<ProductOverview> getProductList() {
        return productList;
    }

}