package com.example.dsi_android_ui.search_department;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.dsi_android_ui.network.VolleyConsumer;
import com.example.dsi_android_ui.utils.GenericResponse;
import com.google.gson.Gson;

import org.json.JSONObject;

import static com.example.dsi_android_ui.utils.ConstantsKt.DSI_SERVICE_URL;

public class SearchDepartmentViewModel extends ViewModel {

    private static final String TAG = "SearchDepartmentViewMod";
    public MutableLiveData<GenericResponse<DepartmentModel>> departmentSearchLiveData;
    public MutableLiveData<ProductOverviewList> categorySearchLiveData;

    public SearchDepartmentViewModel() {
        departmentSearchLiveData = new MutableLiveData<>();
        categorySearchLiveData = new MutableLiveData<>();
    }

    protected void sendRequest(Request<?> request) {
        VolleyConsumer.addRequest(request);
    }

    /**
     * @param searchKey String
     */
    public void searchProductInAllDepartment(String searchKey) {
        searchProductInDepartment(searchKey, null);
    }

    public void searchProductInDepartment(String searchKey, String departmentName) {
        if (searchKey == null) {
            searchKey = "";
        }
        if (departmentName == null) {
            departmentName = "";
        }
        departmentSearchLiveData.postValue(new GenericResponse<>(GenericResponse.State.LOADING, null, null));
        searchKey = searchKey.trim();
        String departmentSearchUrl = DSI_SERVICE_URL + "/api/v1/products?q={searchKey}&depName={departmentName}";
        departmentSearchUrl = departmentSearchUrl.replace("{searchKey}", searchKey).replace("{departmentName}", departmentName);
        Log.d(TAG, "URL: " + departmentSearchUrl);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, departmentSearchUrl, null,
                this::departmentSearchHandler,
                error -> {
                    String errorMessage = error.getMessage();
                    if (TextUtils.isEmpty(errorMessage)) {
                        errorMessage = "Server not Responding";
                    }
                    departmentSearchLiveData.postValue(new GenericResponse<>(GenericResponse.State.FAILURE, null, new GenericResponse.Error(501, errorMessage)));
                }
        );

        sendRequest(jsonObjectRequest);
    }

    protected void departmentSearchHandler(JSONObject response) {
        Log.d(TAG, "callSearchByWord: " + response);
        DepartmentModel departmentModel = null;
        if (response != null)
            departmentModel = new Gson().fromJson(response.toString(), DepartmentModel.class);
        departmentSearchLiveData.postValue(new GenericResponse<>(GenericResponse.State.SUCCESS, departmentModel, null));
    }

    public void searchProductInCategory(String searchKey, String departmentName, String category) {
        if (searchKey == null) {
            searchKey = "";
        }
        if (departmentName == null) {
            departmentName = "";
        }
        searchKey = searchKey.trim();
        String departmentSearchUrl = DSI_SERVICE_URL + "/api/v1/products?q={searchKey}&depName={departmentName}&cate={category} ";
        departmentSearchUrl = departmentSearchUrl.replace("{searchKey}", searchKey).replace("{departmentName}", departmentName).replace("{category}", category);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, departmentSearchUrl, null,
                this::categorySearchHandler,
                error -> categorySearchLiveData.postValue(null)
        );

        sendRequest(jsonObjectRequest);
    }

    protected void categorySearchHandler(JSONObject response) {
        Log.d(TAG, "callSearchByWord: " + response);
        ProductOverviewList productOverviewList = null;
        if (response != null) {
            productOverviewList = new Gson().fromJson(response.toString(), ProductOverviewList.class);
        }
        categorySearchLiveData.postValue(productOverviewList);
    }

}
