package com.example.dsi_android_ui.search_department;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.dsi_android_ui.helper.TestDataHelper;
import com.example.dsi_android_ui.utils.GenericResponse;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.robolectric.RobolectricTestRunner;

import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;

@RunWith(RobolectricTestRunner.class)
public class SearchDepartmentViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @InjectMocks
    @Spy
    private static SearchDepartmentViewModel target;
    private final TestDataHelper testDataHelper = new TestDataHelper();

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Unit test for {@link SearchDepartmentViewModel#searchProductInAllDepartment(String)}
     */
    @Test
    public void givenNothing_whenSearchProductInAllDepartment_updateDepartmentSearchLiveDataWithLoading() {
        doNothing().when(target).sendRequest(any());
        target.searchProductInAllDepartment(null);
        assertEquals(GenericResponse.State.LOADING, Objects.requireNonNull(target.departmentSearchLiveData.getValue()).getState());
    }

    /**
     * Unit test for {@link SearchDepartmentViewModel#searchProductInDepartment(String, String)}
     */
    @Test
    public void givenNothingAndInvokeErrorResponse_whenSearchProductInDepartment_updateDepartmentSearchLiveDataWithFailure() {
        doAnswer(invocation -> {
            final JsonObjectRequest originalArgument = (JsonObjectRequest) (invocation.getArguments())[0];
            originalArgument.deliverError(new VolleyError("Failed"));
            return null;
        }).when(target).sendRequest(any(JsonObjectRequest.class));
        target.searchProductInDepartment(null, null);
        assertEquals(GenericResponse.State.FAILURE, Objects.requireNonNull(target.departmentSearchLiveData.getValue()).getState());
    }

    /**
     * Unit test for {@link SearchDepartmentViewModel#searchProductInDepartment(String, String)}
     */
    @Test
    public void givenNothingAndInvokeSuccessResponse_whenSearchProductInDepartment_updateDepartmentSearchLiveDataWithSuccess() {
        doAnswer(invocation -> {
            String jsonString = new Gson().toJson(testDataHelper.generateDepartmentModel());
            target.departmentSearchHandler(new JSONObject(jsonString));
            return null;
        }).when(target).sendRequest(any(JsonObjectRequest.class));
        target.searchProductInDepartment(null, null);
        assertEquals(GenericResponse.State.SUCCESS, Objects.requireNonNull(target.departmentSearchLiveData.getValue()).getState());
    }

    /**
     * Unit test for {@link SearchDepartmentViewModel#searchProductInCategory(String, String, String)}
     */
    @Test
    public void givenNothingAndInvokeSuccessResponse_whenSearchProductInCategory_updateCategorySearchLiveDataWithResponseData() {
        doAnswer(invocation -> {
            String jsonString = new Gson().toJson(testDataHelper.generateProductOverviewList());
            target.categorySearchHandler(new JSONObject(jsonString));
            return null;
        }).when(target).sendRequest(any(JsonObjectRequest.class));
        target.searchProductInCategory(null, null, "");
        assertEquals(Objects.requireNonNull(testDataHelper.generateProductOverviewList()).getProductList().size(), Objects.requireNonNull(target.categorySearchLiveData.getValue()).getProductList().size());
    }

    /**
     * Unit test for {@link SearchDepartmentViewModel#searchProductInCategory(String, String, String)}
     */
    @Test
    public void givenNothingAndInvokeErrorResponse_whenSearchProductInCategory_updateCategorySearchLiveDataWithNull() {
        doAnswer(invocation -> {
            final JsonObjectRequest originalArgument = (JsonObjectRequest) (invocation.getArguments())[0];
            originalArgument.deliverError(new VolleyError("Failed"));
            return null;
        }).when(target).sendRequest(any(JsonObjectRequest.class));
        target.searchProductInCategory(null, null, "");
        assertNull(target.categorySearchLiveData.getValue());
    }
}