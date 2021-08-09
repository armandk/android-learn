package com.example.dsi_android_ui;

import android.app.SearchManager;
import android.content.Context;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.lifecycle.ViewModelProvider;

import com.example.dsi_android_ui.search_product.SearchServiceViewModel;
import com.example.dsi_android_ui.search_product.SearchViewModelFactory;
import com.sap.cloud.mobile.fiori.search.FioriSearchView;


public class SearchableActivity  extends AppCompatActivity {
    private static final String TAG = "SearchableActivity";
    private FioriSearchView searchView;
    private SearchServiceViewModel searchServiceViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchView = findViewById(R.id.search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(getApplicationContext(),
                android.R.layout.simple_list_item_1,
                null,
                new String[] { SearchManager.SUGGEST_COLUMN_TEXT_1 },
                new int[] { android.R.id.text1 });

        searchView.setSuggestionsAdapter(adapter);
        searchView.setOnQueryTextListener(new AutoSuggestListener());

        searchServiceViewModel = new ViewModelProvider(this, new SearchViewModelFactory()).get(SearchServiceViewModel.class);

        searchServiceViewModel.productNamesLiveData.observe(this,
                searchProductModel -> {
                    Log.d(TAG, "onCreate: productname live data "+searchProductModel);
                    final MatrixCursor cursor = new MatrixCursor(new String[]{
                            BaseColumns._ID,                         // necessary for adapter
                            SearchManager.SUGGEST_COLUMN_TEXT_1      // the full search term
                    });
                    if (searchProductModel != null && searchProductModel.getProductNames() != null) {
                        Log.d(TAG, "onCreate: product name count : "+searchProductModel.getProductNames().size());
                        int count = 0;
                        for (String productName : searchProductModel.getProductNames()) {
                            Object[] row = new Object[]{count++, productName};
                            cursor.addRow(row);
                        }
                    }
                    Log.d(TAG, "onCreate: producnt name livedata : cursor count : "+cursor.getCount());
                   // searchView.getSuggestionsAdapter().changeCursor(cursor);
                   searchView.getSuggestionsAdapter().swapCursor(cursor);
                    //this will trigger when we call searchServiceViewModel.callSearchByWord("test")
                });

        searchServiceViewModel.productDetailsLiveData.observe(this,
                productDetails -> {
                    Log.d(TAG, "onCreate: sku search : product details : "+productDetails);
                });
    }

    class AutoSuggestListener implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String query) {
            searchView.setQuery(query, Boolean.TRUE);
            //query is sku

            return true;
        }

        @Override
        public boolean onQueryTextChange(String query) {
            searchServiceViewModel.productSearchByKey(query);
            return true;
        }
    }
}