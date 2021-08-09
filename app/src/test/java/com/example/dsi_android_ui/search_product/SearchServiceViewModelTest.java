package com.example.dsi_android_ui.search_product;

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

import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@RunWith(RobolectricTestRunner.class)
public class SearchServiceViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @InjectMocks
    @Spy
    private static SearchServiceViewModel target;
    private final TestDataHelper testDataHelper = new TestDataHelper();

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Unit test for {@link SearchServiceViewModel#productSearchByKey(String)}
     */
    @Test
    public void givenNullAndInvokeErrorResponse_whenProductSearchByKey_updateProductNamesLiveDataWithNull() {
        doAnswer(invocation -> {
            final JsonObjectRequest originalArgument = (JsonObjectRequest) (invocation.getArguments())[0];
            originalArgument.deliverError(new VolleyError("Failed"));
            return null;
        }).when(target).sendRequest(any(JsonObjectRequest.class));
        target.productSearchByKey(null);
        assertNull(target.productNamesLiveData.getValue());
    }

    /**
     * Unit test for {@link SearchServiceViewModel#productSearchByKey(String)}
     */
    @Test
    public void givenNullAndInvokeSuccessResponse_whenProductSearchByKey_updateProductNamesLiveDataWithResponseData() {
        doAnswer(invocation -> {
            String jsonString = new Gson().toJson(testDataHelper.generateSearchProductModel());
            target.productSearchByKeyHandler(new JSONObject(jsonString));
            return null;
        }).when(target).sendRequest(any(JsonObjectRequest.class));
        target.productSearchByKey(null);
        assertEquals(testDataHelper.generateSearchProductModel().getProductNames().size(), Objects.requireNonNull(target.productNamesLiveData.getValue()).getProductNames().size());
    }

    /**
     * Unit test for {@link SearchServiceViewModel#productSearchByKeySubmit(String)}
     */
    @Test
    public void givenNullAndInvokeErrorResponse_whenProductSearchByKeySubmit_updateProductOverviewsLiveDataWithNull() {
        doAnswer(invocation -> {
            final JsonObjectRequest originalArgument = (JsonObjectRequest) (invocation.getArguments())[0];
            originalArgument.deliverError(new VolleyError("Failed"));
            return null;
        }).when(target).sendRequest(any(JsonObjectRequest.class));
        target.productSearchByKeySubmit(null);
        assertEquals(GenericResponse.State.FAILURE, Objects.requireNonNull(target.productOverviewsLiveData.getValue()).getState());
    }

    /**
     * Unit test for {@link SearchServiceViewModel#productSearchByKeySubmit(String)}
     */
    @Test
    public void givenNullAndInvokeSuccessResponse_whenProductSearchByKeySubmit_updateProductOverviewsLiveDataWithResponseData() {
        doAnswer(invocation -> {
            String jsonString = new Gson().toJson(testDataHelper.generateProductOverviewList());
            target.productSearchByKeySubmitHandler(new JSONObject(jsonString));
            return null;
        }).when(target).sendRequest(any(JsonObjectRequest.class));
        target.productSearchByKeySubmit(null);
        assertEquals(GenericResponse.State.SUCCESS, Objects.requireNonNull(target.productOverviewsLiveData.getValue()).getState());
    }

    /**
     * Unit test for {@link SearchServiceViewModel#productSearchByName(String)}
     */
    @Test
    public void givenProductNameAndInvokeErrorResponse_whenProductSearchByName_updateProductDetailsLiveDataWithNull() {
        doAnswer(invocation -> {
            final JsonObjectRequest originalArgument = (JsonObjectRequest) (invocation.getArguments())[0];
            originalArgument.deliverError(new VolleyError("Failed"));
            return null;
        }).when(target).sendRequest(any(JsonObjectRequest.class));
        target.productSearchByName("name");
        assertNull(target.productDetailsLiveData.getValue());
    }

    /**
     * Unit test for {@link SearchServiceViewModel#productSearchBySku(String)}
     */
    @Test
    public void givenSkuAndInvokeSuccessResponse_whenProductSearchBySku_updateProductDetailsLiveDataWithResponseData() {
        doAnswer(invocation -> {
            String jsonString = new Gson().toJson(testDataHelper.generateProductDetails());
            target.productSearchBySkuOrNameHandler(new JSONObject(jsonString));
            return null;
        }).when(target).sendRequest(any(JsonObjectRequest.class));
        target.productSearchBySku("sku");
        assertEquals(testDataHelper.generateProductDetails().toString(), Objects.requireNonNull(target.productDetailsLiveData.getValue()).toString());
    }

    /**
     * Unit test for {@link SearchServiceViewModel#filterCurrentProductFromVariants(List, String)}
     */
    @Test
    public void givenVariantListAndEmptyGtin_whenFilterCurrentProductFromVariants_returnVariantListAsIs() {
        List<ProductVariant> variants = testDataHelper.generateProductVariants();

        List<ProductVariant> filteredVariants = target.filterCurrentProductFromVariants(variants,"");
        assertEquals(filteredVariants.size(), variants.size());
        assertTrue(filteredVariants.containsAll(variants));
    }

    /**
     * Unit test for {@link SearchServiceViewModel#filterCurrentProductFromVariants(List, String)}
     */
    @Test
    public void givenVariantListAndGtin3_whenFilterCurrentProductFromVariants_returnVariantListAsIs() {
        List<ProductVariant> variants = testDataHelper.generateProductVariants();
        ProductVariant variant = variants.get(2);

        List<ProductVariant> filteredVariants = target.filterCurrentProductFromVariants(variants, Objects.requireNonNull(variant.getGtin()));
        assertEquals(variants.size() -1, filteredVariants.size());
        assertFalse(filteredVariants.contains(variant));
    }

}