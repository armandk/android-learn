package com.example.dsi_android_ui;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dsi_android_ui.adapter.MyListAdapter;
import com.example.dsi_android_ui.search_department.DepartmentModel;
import com.example.dsi_android_ui.search_department.SearchDepartmentViewModel;
import com.example.dsi_android_ui.search_department.SearchDepartmentViewModelFactory;
import com.example.dsi_android_ui.utils.GenericResponse;
import com.sap.cloud.mobile.fiori.search.FioriSearchView;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DepartmentCategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DepartmentCategoryFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FioriSearchView searchView;
    SearchDepartmentViewModel departmentViewModel=null;
    // TODO: Rename and change types of parameters
    private String departmentName;
    private String searchKey;
    private boolean initialLoad = true;

    public DepartmentCategoryFragment() {
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
    public static DepartmentCategoryFragment newInstance(String param1, String param2) {
        DepartmentCategoryFragment fragment = new DepartmentCategoryFragment();
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
            departmentName = getArguments().getString(ARG_PARAM1);
            searchKey = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_department, container, false);
        RecyclerView recyclerView= view.findViewById(R.id.department_list);
        TextView listTitle=view.findViewById(R.id.list_title);
        searchView = view.findViewById(R.id.search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(getContext(),
                android.R.layout.simple_list_item_1,
                null,
                new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1},
                new int[]{android.R.id.text1});

        searchView.setSuggestionsAdapter(adapter);
        searchView.setOnQueryTextListener(new AutoSuggestListener());
        departmentViewModel =new ViewModelProvider(this, new SearchDepartmentViewModelFactory()).get(SearchDepartmentViewModel.class);
        departmentViewModel.departmentSearchLiveData.observe(getViewLifecycleOwner(), genericResponse -> {

            GenericResponse.State state = genericResponse.getState();
            if(state == GenericResponse.State.LOADING && initialLoad){
                showProgressLoading(null);
                initialLoad=false;
                return;
            }
            if(state == GenericResponse.State.FAILURE){
                hideProgressDialog();
                Toast.makeText(getContext(),genericResponse.getError().getErrorMessage(),Toast.LENGTH_SHORT).show();
                return;
            }
            HashMap<String, Integer> result = new HashMap<>();
            DepartmentModel departmentModel = genericResponse.getResponse();
            if(departmentModel != null && departmentModel.getResult() != null){
                result = departmentModel.getResult();
            }

                listTitle.setText("Categories("+result.size()+")  -  "+departmentName);
                MyListAdapter myListAdapter = new MyListAdapter(getActivity(), result) {
                    @Override
                    public void recyclerviewClick(Object position) {

                        if(position!=null){
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.container, DepartmentCategoryProductFragment.newInstance(departmentName,((ItemObject)position).getItemname(), searchView.getQuery().toString().trim()));
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                    }
                };
                recyclerView.setHasFixedSize(true);
                recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(myListAdapter);
                hideProgressDialog();

        });
        departmentViewModel.searchProductInDepartment(searchKey,departmentName);
        searchView.setQuery(searchKey,false);
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


    private View getTextView(String categoryName, int count) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.list_item, null);
        ((TextView) view.findViewById(R.id.dep_name)).setText(categoryName);
        ((TextView) view.findViewById(R.id.dep_count)).setText(count + " ");
        view.setTag(categoryName);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TEST","Tag "+v.getTag()+departmentName);

                if(v.getTag()!=null) {
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, DepartmentCategoryProductFragment.newInstance(departmentName,v.getTag().toString(), searchView.getQuery().toString().trim()));
                    transaction.addToBackStack(null);
                    transaction.commit();
                }

            }
        });
        return view;

    }

    class AutoSuggestListener implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String query) {
            // searchView.setQuery(query, Boolean.TRUE);
            //query is sku

            departmentViewModel.searchProductInDepartment(query.trim(),departmentName);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String query) {

            departmentViewModel.searchProductInDepartment(query.trim(),departmentName);
            return true;
        }
    }
}