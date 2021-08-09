package com.example.dsi_android_ui.search_product;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;

@Data
public class ProductDetails {

    @SerializedName("product")
    @Expose
    public Product product;
    @SerializedName("specification")
    @Expose
    public List<ProductSpecification> specifications;
    @SerializedName("variations")
    @Expose
    public List<ProductVariant> variants;

    @Override
    public String toString() {
        return "ProductDetails{" +
                "product=" + product +
                ", specification=" + specifications +
                ", variants=" + variants +
                '}';
    }

    public Product getProductList() {
        return product;
    }

    public void setProductList(Product product) {
        this.product = product;
    }
}