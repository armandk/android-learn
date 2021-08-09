package com.example.dsi_android_ui;

import static com.example.dsi_android_ui.utils.ConstantsKt.FILTERS_MINE;
import static com.example.dsi_android_ui.utils.ConstantsKt.SORT_BY_DUE_DATE;
import static com.example.dsi_android_ui.utils.ConstantsKt.STATUS_HIGH;
import static com.example.dsi_android_ui.utils.ConstantsKt.STATUS_LOW;
import static com.example.dsi_android_ui.utils.ConstantsKt.STATUS_MEDIUM;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dsi_android_ui.adapter.MyTaskAdapter;
import com.example.dsi_android_ui.databinding.FragmentTaskBinding;
import com.example.dsi_android_ui.task_fragment.Task;
import com.example.dsi_android_ui.task_fragment.TaskFragmentViewModel;
import com.example.dsi_android_ui.task_fragment.TaskFragmentViewModelFactory;
import com.example.dsi_android_ui.ui.add_task.AddNewTaskFragment;
import com.sap.cloud.mobile.fiori.formcell.FormCell;

import java.util.LinkedList;
import java.util.List;

public class TaskFragment extends BaseFragment {

    private TaskFragmentViewModel viewModel;
    private FragmentTaskBinding binding;
    private CellListener cellListener;

    public static TaskFragment newInstance() {
        return new TaskFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TaskFragmentViewModelFactory provider = new TaskFragmentViewModelFactory();
        viewModel = new ViewModelProvider(requireActivity(), provider)
                .get(TaskFragmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTaskBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        initViewElements();
        initButtonListeners();
        fetchTasks();
        return view;
    }

    public void fetchTasks() {
        viewModel.getObservableTasks().observe(getViewLifecycleOwner(), this::taskListUpdated);
        viewModel.setUserId(1);
        viewModel.loadTasks();
        showProgressLoading("Fetching tasks");
        viewModel.getObservableFilterCount().observe(getViewLifecycleOwner(), this::filterCountUpdated);
        applyQuickFilters();
    }

    private void filterCountUpdated(Integer count) {
        if (count > 0 && !(viewModel.getAppliedFilters().isEmpty())) {
            String textValue = "(" + count + ")";
            binding.filterCount.setText(textValue);
            binding.filterCount.setVisibility(View.VISIBLE);
        } else {
            binding.filterCount.setVisibility(View.INVISIBLE);
        }
    }

    private void initViewElements() {
        binding.filterCount.setVisibility(View.INVISIBLE);
        cellListener = new CellListener();
        binding.filterFormCell.setCellValueChangeListener(cellListener);
        initRecyclerView();
    }

    private void initButtonListeners() {
        binding.filterBtn.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, new FilterTaskFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        binding.taskFragmentAddTask.setOnClickListener(v -> {
            Log.d("ADD_TASK", "Navigating to ADD_TASK");
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            Fragment prev = getChildFragmentManager().findFragmentByTag("add_task");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            AddNewTaskFragment addTask = new AddNewTaskFragment(null, this);
            addTask.show(ft, "add_task");
        });
    }

    private void initRecyclerView() {
        binding.listview.setHasFixedSize(true);
        binding.listview.addItemDecoration(new DividerItemDecoration(
                binding.listview.getContext(), DividerItemDecoration.VERTICAL));
        binding.listview.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void applyQuickFilters() {
        List<String> filters = viewModel.getQuickFilters();
        if (null != filters && !filters.isEmpty()) {
            binding.filterFormCell.setValue(
                    viewModel.getSelectedFilterValues(binding.filterFormCell.getValueOptions(), filters));
        }
    }

    private void taskListUpdated(List<Task> tasks) {
        if (binding.filterCount.getVisibility() == View.VISIBLE) {
            binding.filterFormCell.setValue(null);
        }
        hideProgressDialog();
        binding.taskCount.setText(String.valueOf(tasks.size()));
        MyTaskAdapter adapter = new MyTaskAdapter(tasks) {
            @Override
            public void recyclerviewClick(Object position) {
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, TaskDetailsFragment.newInstance((Task) position));
                transaction.addToBackStack(null);
                transaction.commit();
            }

        };
        binding.listview.setAdapter(adapter);
        binding.myTaskCount.setText(String.valueOf(viewModel.countMyTasks()));
    }

    private class CellListener extends FormCell.CellValueChangeListener<List<Integer>> {
        @Override
        protected void cellChangeHandler(List<Integer> selection) {
            if (selection == null) return;
            List<String> filters = new LinkedList<>();
            if (selection.contains(0)) {
                binding.filterCount.setVisibility(View.INVISIBLE);
                filters.add(FILTERS_MINE);
            }
            if (selection.contains(1)) {
                binding.filterCount.setVisibility(View.INVISIBLE);
                filters.add(STATUS_HIGH);
            }
            if (selection.contains(2)) {
                binding.filterCount.setVisibility(View.INVISIBLE);
                filters.add(STATUS_MEDIUM);
            }
            if (selection.contains(3)) {
                binding.filterCount.setVisibility(View.INVISIBLE);
                filters.add(STATUS_LOW);
            }
            if (selection.contains(4)) {
                binding.filterCount.setVisibility(View.INVISIBLE);
                filters.add(SORT_BY_DUE_DATE);
            }
            if (binding.filterCount.getVisibility() != View.VISIBLE) {
                viewModel.filterTasks(filters);
            }
        }
    }
}