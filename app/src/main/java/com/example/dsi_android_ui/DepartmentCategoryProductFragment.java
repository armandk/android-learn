package com.example.dsi_android_ui;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dsi_android_ui.adapter.MyListItemAdapter;
import com.example.dsi_android_ui.models.Product;
import com.example.dsi_android_ui.search_department.ProductOverview;
import com.example.dsi_android_ui.search_department.SearchDepartmentViewModel;
import com.example.dsi_android_ui.search_department.SearchDepartmentViewModelFactory;
import com.sap.cloud.mobile.fiori.search.FioriSearchView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DepartmentCategoryProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DepartmentCategoryProductFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private FioriSearchView searchView;
    SearchDepartmentViewModel departmentViewModel=null;
    // TODO: Rename and change types of parameters
    private String departmentName;
    private String categoryName;
    private String searchKey;

    private Product product;
    private Bundle args = new Bundle();
    private ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();

    public DepartmentCategoryProductFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DepartmentCategoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DepartmentCategoryProductFragment newInstance(String param1, String param2, String param3) {
        DepartmentCategoryProductFragment fragment = new DepartmentCategoryProductFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            departmentName = getArguments().getString(ARG_PARAM1);
            categoryName = getArguments().getString(ARG_PARAM2);
            searchKey = getArguments().getString(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_department, container, false);
        //LinearLayout listPickerFormCell= view.findViewById(R.id.department_list);
        RecyclerView recyclerView= view.findViewById(R.id.department_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        TextView listTitle=view.findViewById(R.id.list_title);
        searchView = view.findViewById(R.id.search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(true);

        ImageView searchImageButton = searchView.findViewById(R.id.search_mag_icon);
        //searchImageButton.setImageResource(R.drawable.back_button_24);

        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(getContext(),
                android.R.layout.simple_list_item_1,
                null,
                new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1},
                new int[]{android.R.id.text1});

        searchView.setSuggestionsAdapter(adapter);
        searchView.setOnQueryTextListener(new AutoSuggestListener());
        departmentViewModel =new ViewModelProvider(this, new SearchDepartmentViewModelFactory()).get(SearchDepartmentViewModel.class);
        departmentViewModel.categorySearchLiveData.observe(getViewLifecycleOwner(), departmentModel -> {


            if (departmentModel.getProductList()!=null) {
                listTitle.setText(categoryName+"("+departmentModel.getProductList().size()+")");
                MyListItemAdapter myListAdapter = new MyListItemAdapter(getActivity(), departmentModel.getProductList()) {
                    @Override
                    public void recyclerviewClick(Object position) {
//                        Toast.makeText(view.getContext(),"click on item: "+((ProductOverview)position).getProductName(), Toast.LENGTH_LONG).show();

                       navigateToProductDetails(((ProductOverview)position).getProductName(), ((ProductOverview)position).getGtin());
                    }
                };
                recyclerView.setHasFixedSize(true);
                recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(myListAdapter);
                recyclerView.invalidate();
            }
        });
        departmentViewModel.searchProductInCategory(searchKey,departmentName,categoryName);
        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                searchImageButton.setImageResource(R.drawable.back_button_24);
                searchImageButton.setOnClickListener((v1) -> {
                    searchView.clearFocus();
                });
            }
            else {
                searchImageButton.setImageResource(R.drawable.ic_search);
                searchImageButton.setOnClickListener(null);
            }
        });

        searchView.setQuery(searchKey, false);
        Toolbar productToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        //productToolbar.setTitle("Title");
        Drawable navigationIcon = getResources().getDrawable(R.drawable.ic_arrow_back_24dp,null);
        productToolbar.setNavigationIcon(navigationIcon);
        productToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        return view;

    }


    private View getTextView(ProductOverview selectedProduct) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.list_category_item, null);
        ((TextView) view.findViewById(R.id.dep_name)).setText(selectedProduct.getProductName());
        ((TextView) view.findViewById(R.id.dep_count)).setText(selectedProduct.getCount() + " ");
        ((TextView) view.findViewById(R.id.item_sku)).setText("Sku: "+selectedProduct.getSku() );
        view.setTag(selectedProduct.getProductName());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TEST","Tag "+v.getTag()+departmentName);
                navigateToProductDetails(selectedProduct.getProductName(),selectedProduct.getGtin());
            }
        });
        return view;

    }
    private void navigateToProductDetails(String productName, String productGtin){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        product = new Product(productName,productGtin,"");
        args.putParcelable("product",product);
        productDetailsFragment.setArguments(args);
        transaction.replace(R.id.container, productDetailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    class AutoSuggestListener implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String query) {
            // searchView.setQuery(query, Boolean.TRUE);
            //query is sku

            departmentViewModel.searchProductInCategory(query.trim(),departmentName,categoryName);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String query) {

            departmentViewModel.searchProductInCategory(query.trim(),departmentName,categoryName);
            return true;
        }
    }
}