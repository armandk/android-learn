package com.example.dsi_android_ui;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.SearchRecentSuggestions;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dsi_android_ui.adapter.MyListItemAdapter;
import com.example.dsi_android_ui.models.Product;
import com.example.dsi_android_ui.provider.RecentProductSearchProvider;
import com.example.dsi_android_ui.search_department.ProductOverview;
import com.example.dsi_android_ui.search_department.ProductOverviewList;
import com.example.dsi_android_ui.search_product.SearchProductModel;
import com.example.dsi_android_ui.search_product.SearchServiceViewModel;
import com.example.dsi_android_ui.search_product.SearchViewModelFactory;
import com.example.dsi_android_ui.ui.browse_by_location.BrowseByNestedLocation;
import com.example.dsi_android_ui.utils.GenericResponse;
import com.sap.cloud.mobile.fiori.search.FioriSearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends BaseFragment {

    private static final String TAG = "SearchFragment";
    private FioriSearchView searchView;
    private SearchServiceViewModel searchServiceViewModel;
    private SearchRecentSuggestions searchRecentSuggestions;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView productListView;
    private MyListItemAdapter productListAdapter;
    private LinearLayout buttonsLayout;
    private Bundle args = new Bundle();
    private ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();


    private ImageView searchImageButton;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        searchView = view.findViewById(R.id.search);
        productListView = view.findViewById(R.id.product_list);
        buttonsLayout = view.findViewById(R.id.button_layout);
        searchRecentSuggestions = new SearchRecentSuggestions(getContext(),
                RecentProductSearchProvider.AUTHORITY, RecentProductSearchProvider.MODE);
        // Browse by Department button functionality
        View departmentBtn = view.findViewById(R.id.department_btn);
        View browseByLocation = view.findViewById(R.id.by_location_btn);
        departmentBtn.setOnClickListener(v -> {
            productListAdapter.setData(Collections.emptyList());
            searchServiceViewModel.productOverviewsLiveData.setValue(new GenericResponse<>(
                    GenericResponse.State.SUCCESS,
                    null,
                    null
            ));
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, DepartmentFragment.newInstance("", ""));
            transaction.addToBackStack(null);
            transaction.commit();
        });

        browseByLocation.setOnClickListener(v -> {
            productListAdapter.setData(Collections.emptyList());
            searchServiceViewModel.productOverviewsLiveData.setValue(new GenericResponse<>(
                    GenericResponse.State.SUCCESS,
                    null,
                    null
            ));
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, BrowseByNestedLocation.newInstance());
            transaction.addToBackStack(null);
            transaction.commit();
        });
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        AutoCompleteTextView searchText = searchView.findViewById(R.id.search_src_text);
        searchText.setThreshold(0);

        searchImageButton = searchView.findViewById(R.id.search_mag_icon);

        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(getContext(),
                android.R.layout.simple_list_item_1,
                null,
                new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1},
                new int[]{android.R.id.text1});

        searchView.setSuggestionsAdapter(adapter);
        searchView.setOnQueryTextListener(new AutoSuggestListener());
        searchView.setQuery("", false);
        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {

            CharSequence query = searchView.getQuery();
            if (hasFocus && null != query && (query.toString().trim().contentEquals(""))) {
                Log.d(TAG, "onCreate: setOnQueryTextFocusChangeListener: Loading recent suggestions...");
                loadRecentSuggestions(query.toString().trim());
            }
            if (hasFocus) {
                setSearchImageButtonToBackButton();
            } else {
                searchView.setQuery("", true);
                searchImageButton.setImageResource(R.drawable.ic_search);
                searchImageButton.setOnClickListener(null);

            }
        });
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = searchView.getSuggestionsAdapter().getCursor();
                cursor.moveToPosition(position);
                String text = cursor.getString(1);
                searchView.setQuery(text, false);
                return true;
            }
        });

        productListView.setLayoutManager(new LinearLayoutManager(getContext()));
        productListAdapter = new MyListItemAdapter(getContext(), new ArrayList<>()) {
            @Override
            public void recyclerviewClick(Object obj) {
                ProductOverview item = (ProductOverview) obj;
                goToProductDetailsScreen(item.getProductName());
            }
        };

        productListView.setAdapter(productListAdapter);

        searchServiceViewModel = new ViewModelProvider(this, new SearchViewModelFactory()).get(SearchServiceViewModel.class);

        searchServiceViewModel.productNamesLiveData.observe(getViewLifecycleOwner(),
                this::handleQuerySearch);
        searchServiceViewModel.productOverviewsLiveData.observe(getViewLifecycleOwner(), this::handleQuerySubmit);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                SearchProductModel searchProductModel = searchServiceViewModel.productNamesLiveData.getValue();
                List<String> productNames = searchProductModel.getProductNames();
                String item = productNames.get(position);
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = searchView.getSuggestionsAdapter().getCursor();
                cursor.moveToPosition(position);
                String item = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));

                goToProductDetailsScreen(item);
                return true;
            }
        });
        return view;
    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        if (!TextUtils.isEmpty(title))
            builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("ok", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void handleQuerySubmit(GenericResponse<ProductOverviewList> genericResponse) {
        GenericResponse.State state = genericResponse.getState();
        if (state == GenericResponse.State.LOADING) {
            showProgressLoading(null);
            return;
        }
        if (state == GenericResponse.State.FAILURE) {
            hideProgressDialog();
            Toast.makeText(getContext(), genericResponse.getError().getErrorMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        ProductOverviewList response = genericResponse.getResponse();
        List<ProductOverview> productList = Collections.emptyList();
        if (response == null || response.getProductList() == null || response.getProductList().isEmpty()) {
            buttonsLayout.setVisibility(View.VISIBLE);
            if (searchView.getQuery().toString().trim().length() != 0)
                showAlertDialog(null, "Product not found");
        } else {
            productList = response.getProductList();
            buttonsLayout.setVisibility(View.GONE);
        }
        productListAdapter.setData(productList);
        hideProgressDialog();

    }

    private void goToProductDetailsScreen(String item) {
        hideKeyboard(searchView);
        searchRecentSuggestions.saveRecentQuery(item, null);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        Product product = new Product(item, "","");
        args.putParcelable("product", product);
        productDetailsFragment.setArguments(args);
        transaction.replace(R.id.container, productDetailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void handleQuerySearch(SearchProductModel searchProductModel) {
        Log.d(TAG, "handleQuerySearch: product name live data " + searchProductModel);
        final MatrixCursor cursor = new MatrixCursor(new String[]{
                BaseColumns._ID,                         // necessary for adapter
                SearchManager.SUGGEST_COLUMN_TEXT_1      // the full search term
        });
        if (searchProductModel != null && searchProductModel.getProductNames() != null) {
            Log.d(TAG, "handleQuerySearch: product name count : " + searchProductModel.getProductNames().size());
            int count = 0;
            for (String productName : searchProductModel.getProductNames()) {
                Object[] row = new Object[]{count++, productName};
                cursor.addRow(row);
            }
        }
        searchView.getSuggestionsAdapter().swapCursor(cursor);
        setSearchImageButtonToBackButton();
    }

    private Cursor getRecentSuggestions(final String query) {
        Uri.Builder uriBuilder = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT)
                .authority(RecentProductSearchProvider.AUTHORITY);
        uriBuilder.appendPath(SearchManager.SUGGEST_URI_PATH_QUERY);
        ContentResolver contentResolver = getActivity().getContentResolver();
        return contentResolver.query(uriBuilder.build(), null, " ?",
                new String[]{query}, null);
    }

    private void loadRecentSuggestions(final String query) {
        Cursor cursor = getRecentSuggestions(query);
        Log.d(TAG, "loadRecentSuggestions: got recent suggestions for query " + query);
        if (null != cursor) {
            final MatrixCursor cursor2 = new MatrixCursor(new String[]{
                    BaseColumns._ID,                         // necessary for adapter
                    SearchManager.SUGGEST_COLUMN_TEXT_1      // the full search term
            });
            if (cursor.moveToFirst()) {
                int count = 0;
                do {
                    String string = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
                    Object[] row = new Object[]{count++, string};
                    cursor2.addRow(row);
                } while (cursor.moveToNext());

            }

            searchView.getSuggestionsAdapter().swapCursor(cursor2);
        }
    }

    class AutoSuggestListener implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String query) {

            searchServiceViewModel.productSearchByKeySubmit(query);

            return false;
        }

        @Override
        public boolean onQueryTextChange(String query) {
            Log.d(TAG, "onQueryTextChange: query : " + query);
            if (searchView.getQuery().toString().trim().length() != 0) {
                setSearchImageButtonToBackButton();
            } else {
                searchImageButton.setImageResource(R.drawable.ic_search);
                searchImageButton.setOnClickListener(null);
            }
            if (null != query && query.length() > 2) {
                searchServiceViewModel.productSearchByKey(query);
            } else {
                loadRecentSuggestions(query);
            }

            return true;
        }
    }

    private void setSearchImageButtonToBackButton() {
        searchImageButton.setImageResource(R.drawable.back_button_24);
        searchImageButton.setOnClickListener(v1 -> {
            productListAdapter.setData(Collections.emptyList());
            productListAdapter.notifyDataSetChanged();
            buttonsLayout.setVisibility(View.VISIBLE);
            searchView.setQuery("", false);
            searchView.clearFocus();
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}