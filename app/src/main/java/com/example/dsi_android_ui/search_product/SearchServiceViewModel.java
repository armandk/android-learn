package com.example.dsi_android_ui.search_product;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.dsi_android_ui.network.VolleyConsumer;
import com.example.dsi_android_ui.search_department.ProductOverviewList;
import com.example.dsi_android_ui.utils.GenericResponse;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static com.example.dsi_android_ui.utils.ConstantsKt.DSI_SERVICE_URL;

public class SearchServiceViewModel extends ViewModel {

    private static final String TAG = "SearchServiceViewModel";
    public static final String PRODUCT_SEARCH_URL = DSI_SERVICE_URL + "/api/v1/products";
    private JsonObjectRequest jsonObjectRequest = null;
    public final MutableLiveData<SearchProductModel> productNamesLiveData;
    public final MutableLiveData<ProductDetails> productDetailsLiveData;
    public final MutableLiveData<GenericResponse<ProductOverviewList>> productOverviewsLiveData;

    public SearchServiceViewModel() {
        productNamesLiveData = new MutableLiveData<>();
        productDetailsLiveData = new MutableLiveData<>();
        productOverviewsLiveData = new MutableLiveData<>();
    }


    protected void sendRequest(JsonObjectRequest jsonObjectRequest) {
        VolleyConsumer.get(jsonObjectRequest);
    }

    public void productSearchByKey(String searchKey) {

        if (jsonObjectRequest != null && !jsonObjectRequest.isCanceled()) {
            jsonObjectRequest.cancel();
        }
        if (searchKey == null) {
            searchKey = "";
        }
        searchKey = searchKey.trim();
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, PRODUCT_SEARCH_URL + "?q=" + searchKey, null,
                this::productSearchByKeyHandler, this::productSearchByKeyError);
        sendRequest(jsonObjectRequest);
    }

    private void productSearchByKeyError(VolleyError volleyError) {
        productNamesLiveData.postValue(null);
    }

    protected void productSearchByKeyHandler(JSONObject response) {
        SearchProductModel searchProductModel = null;
        Log.d(TAG, "callSearchByWord: " + response);
        if (response != null) {
            searchProductModel = new Gson().fromJson(response.toString(), SearchProductModel.class);
        }
        productNamesLiveData.postValue(searchProductModel);
    }

    public void productSearchByKeySubmit(String searchKey) {
        if (jsonObjectRequest != null && !jsonObjectRequest.isCanceled()) {
            jsonObjectRequest.cancel();
        }
        productOverviewsLiveData.postValue(
                new GenericResponse<>(
                        GenericResponse.State.LOADING,
                        null,
                        null
                ));
        if (searchKey == null) {
            searchKey = "";
        }
        searchKey = searchKey.trim();
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, PRODUCT_SEARCH_URL + "?q=" + searchKey + "&res=", null,
                this::productSearchByKeySubmitHandler, this::productSearchByKeySubmitError);
        sendRequest(jsonObjectRequest);
    }

    private void productSearchByKeySubmitError(VolleyError error) {
        String message = error.getMessage();
        if (TextUtils.isEmpty(message)) {
            message = "Server not Responding";
        }
        productOverviewsLiveData.postValue(
                new GenericResponse<>(
                        GenericResponse.State.FAILURE,
                        null,
                        new GenericResponse.Error(0, message)
                ));
    }

    protected void productSearchByKeySubmitHandler(JSONObject response) {
        ProductOverviewList productOverviewList = null;
        Log.d(TAG, "productOverviewList: " + response);
        if (response != null) {
            productOverviewList = new Gson().fromJson(response.toString(), ProductOverviewList.class);
        }
        productOverviewsLiveData.postValue(
                new GenericResponse<>(GenericResponse.State.SUCCESS, productOverviewList, null));
    }

    public void productSearchByName(String productName) {
        productSearchBySkuOrName(productName, null);
    }

    public void productSearchBySku(String sku) {
        productSearchBySkuOrName(null, sku);
    }

    private void productSearchBySkuOrName(String name, String sku) {
        if (jsonObjectRequest != null && !jsonObjectRequest.isCanceled()) {
            jsonObjectRequest.cancel();
        }
        String url;
        if (name != null) {
            url = PRODUCT_SEARCH_URL + "?pName=" + name;
        } else {
            url = PRODUCT_SEARCH_URL + "?sku=" + sku;
        }
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                this::productSearchBySkuOrNameHandler, this::productSearchBySkuOrNameError);
        sendRequest(jsonObjectRequest);
        Log.i(TAG, "ProductSearchBySku: " + productDetailsLiveData.toString());
    }

    private void productSearchBySkuOrNameError(VolleyError volleyError) {
        productDetailsLiveData.postValue(null);
    }

    protected void productSearchBySkuOrNameHandler(JSONObject response) {
        ProductDetails productDetails = null;
        Log.d(TAG, "productSearchBySkuOrName: " + response);
        if (response != null) {
            productDetails = new Gson().fromJson(response.toString(), ProductDetails.class);
        }
        productDetailsLiveData.postValue(productDetails);
    }

    public List<ProductVariant> filterCurrentProductFromVariants (List<ProductVariant> variants, String currentProductGtin) {
        List<ProductVariant> filteredVariants = new LinkedList<>();
        if (!(currentProductGtin.isEmpty())) {
            for (ProductVariant variant: variants) {
                if (!(Objects.requireNonNull(variant.getGtin()).isEmpty()) && !(variant.getGtin().equals(currentProductGtin))) {
                    filteredVariants.add(variant);
                }
            }
        }else{
            filteredVariants.addAll(variants);
        }
        return filteredVariants;
    }
}


















