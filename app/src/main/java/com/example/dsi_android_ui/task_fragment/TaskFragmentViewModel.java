package com.example.dsi_android_ui.task_fragment;

import android.os.Build;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dsi_android_ui.network.VolleyConsumer;
import com.example.dsi_android_ui.ui.add_task.AddNewTaskRepository;
import com.example.dsi_android_ui.ui.add_task.VolleyRequestListener;
import com.example.dsi_android_ui.utils.DateHelper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import static com.example.dsi_android_ui.utils.ConstantsKt.FILTERS_ASSIGNEE;
import static com.example.dsi_android_ui.utils.ConstantsKt.FILTERS_DUE_DATE;
import static com.example.dsi_android_ui.utils.ConstantsKt.FILTERS_MINE;
import static com.example.dsi_android_ui.utils.ConstantsKt.FILTERS_PRIORITY;
import static com.example.dsi_android_ui.utils.ConstantsKt.FILTERS_STATUS;
import static com.example.dsi_android_ui.utils.ConstantsKt.FILTERS_TYPE;
import static com.example.dsi_android_ui.utils.ConstantsKt.QUICK_FILTERS;
import static com.example.dsi_android_ui.utils.ConstantsKt.SORT_BY_DUE_DATE;
import static com.example.dsi_android_ui.utils.ConstantsKt.SORT_CATEGORY;
import static com.example.dsi_android_ui.utils.ConstantsKt.STATUS_HIGH;
import static com.example.dsi_android_ui.utils.ConstantsKt.STATUS_LOW;
import static com.example.dsi_android_ui.utils.ConstantsKt.STATUS_MEDIUM;

@RequiredArgsConstructor
public class TaskFragmentViewModel extends ViewModel {

    @Getter
    private final MutableLiveData<List<Task>> observableTasks = new MutableLiveData<>();
    @Getter
    private final MutableLiveData<Integer> observableFilterCount = new MutableLiveData<>();
    private final List<Task> allTasks = new LinkedList<>();
    @Getter
    private final List<Task> filteredTasks = new LinkedList<>();
    private final Map<String, List<String>> appliedFilters = new HashMap<>();
    private final Map<String, List<String>> quickFilters = new HashMap<>();
    private int filterCount = 0;
    private final TasksService service = new TasksService();
    public final MutableLiveData<Boolean> completed = new MutableLiveData<>(false);
    public final MutableLiveData<Boolean> assigned = new MutableLiveData<>(false);
    public final MutableLiveData<Boolean> unassigned = new MutableLiveData<>(false);
    // public final MutableLiveData<Boolean> updated = new MutableLiveData<>(false);
    @Setter
    private Integer userId = -1;

    public final MutableLiveData<Boolean> deleted = new MutableLiveData<>(false);

    public Map<String, List<String>> getAppliedFilters() {
        return appliedFilters;
    }

    public List<String> getQuickFilters() {
        return quickFilters.get(QUICK_FILTERS);
    }

    public void clearLiveData() {
        completed.setValue(false);
        assigned.setValue(false);
        unassigned.setValue(false);
        deleted.setValue(false);
    }

    public void loadTasks() {
        Log.i("TASK_LIST", "LOADING TASKS");
        VolleyConsumer.get(service.generateQuery(), this::responseHandler);
        filterCount = 0;
        appliedFilters.clear();
        observableFilterCount.postValue(filterCount);
    }

    public void completeTask(Task task) {
        updateTask(task, new VolleyRequestListener() {
            @Override
            public void onError(@NotNull String message) {
                // TODO: Handle error when calling the API
            }

            @Override
            public void onResponse(@NotNull String response) {
                notifyTaskCompleted(response);
            }
        });
    }

    public void assignTaskToMe(Task task) {
        updateTask(task, new VolleyRequestListener() {
            @Override
            public void onError(@NotNull String message) {
                // TODO: Handle error when calling the API
            }

            @Override
            public void onResponse(@NotNull String response) {
                if (task.getUserId() == 0) {
                    notifyUnassignedTask(response);
                } else {
                    notifyAssignedToMe(response);
                }
            }
        });
    }

    public void updateTask(Task task, VolleyRequestListener listener) {
        AddNewTaskRepository repository = new AddNewTaskRepository();
        String strTask = com.example.dsi_android_ui.models.Task.Companion.parseToJsonString(task.parseToTaskModel());
        try {
            JSONObject jsonObject = new JSONObject(strTask);
            repository.updateTask(jsonObject, listener);
        } catch (JSONException ex) {
            listener.onError(ex.getMessage());
        }

    }

    protected void notifyTaskCompleted(String s) {
        completed.setValue(true);
        // Toast.makeText(getapp)
    }

    protected void notifyAssignedToMe(String s) {
        assigned.setValue(true);
        // Toast.makeText(getapp)
    }

    protected void notifyUnassignedTask(String s) {
        unassigned.setValue(true);
        // Toast.makeText(getapp)
    }

    public int countMyTasks() {
        int count = 0;
        for (Task task : filteredTasks)
            if (userId == task.getUserId())
                count++;
        return count;
    }

    public void filterTasks(List<String> filters) {
        filterCount = 0;
        this.appliedFilters.clear();
        filteredTasks.clear();
        filteredTasks.addAll(allTasks);
        if (includesPriority(filters))
            applyAllPriorityFilters(filters);
        if (filters.contains(FILTERS_MINE))
            filterByUser();
        if (filters.contains(SORT_BY_DUE_DATE))
            sortFiltered();
        observableTasks.setValue(filteredTasks);
        quickFilters.put(QUICK_FILTERS, filters);
    }

    private boolean includesPriority(List<String> filters) {
        return filters.contains(STATUS_HIGH) || filters.contains(STATUS_MEDIUM) || filters.contains(STATUS_LOW);
    }

    private void applyAllPriorityFilters(List<String> filters) {
        if (!filters.isEmpty()) {
            filteredTasks.clear();
            this.filterCount++;
            for (String filter : filters)
                filteredTasks.addAll(filterByPriority(filter));
        }
    }

    private void filterByUser() {
        List<Task> mine = filterByUser(this.filteredTasks);
        filteredTasks.clear();
        filteredTasks.addAll(mine);
    }

    private void sortFiltered() {
        List<Task> ordered = sortByDueDate(this.filteredTasks);
        filteredTasks.clear();
        filteredTasks.addAll(ordered);
    }

    void responseHandler(String response) {
        allTasks.clear();
        allTasks.addAll(service.tasksFromResponse(response));
        Collections.sort(allTasks, (task1, task2) -> task2.getId().compareTo(task1.getId()));

        if (quickFilters.containsKey(QUICK_FILTERS)
                && !Objects.requireNonNull(quickFilters.get(QUICK_FILTERS)).isEmpty()) {
            List<String> filters = quickFilters.get(QUICK_FILTERS);
            filterTasks(filters);
        } else if (!appliedFilters.isEmpty()) {
            applyFilters(appliedFilters);
        } else {
            observableFilterCount.postValue(filterCount);
            filteredTasks.clear();
            filteredTasks.addAll(allTasks);
            observableTasks.postValue(allTasks);
        }
    }

    private List<Task> filterByPriority(String priority) {
        List<Task> filtered = new LinkedList<>();
        if (priority.equalsIgnoreCase(STATUS_HIGH) || priority.equalsIgnoreCase(STATUS_MEDIUM)
                || priority.equalsIgnoreCase(STATUS_LOW))
            for (Task task : allTasks)
                if (priority.equalsIgnoreCase(task.getPriority()))
                    filtered.add(task);
        return filtered;
    }

    private List<Task> filterByUser(List<Task> tasks) {
        List<Task> filtered = new LinkedList<>();
        for (Task task : tasks)
            if (userId == task.getUserId())
                filtered.add(task);
        return filtered;
    }

    private List<Task> sortByDueDate(List<Task> tasks) {
        List<Task> sorted = new LinkedList<>(tasks);
        Collections.sort(sorted,
                (task1, task2) -> Long.compare(task1.getDueDate().getTime(), task2.getDueDate().getTime()));
        return sorted;
    }

    public String[] getAllTaskType() {
        return Arrays.copyOf(service.getType().values().toArray(), service.getType().values().size(), String[].class);
    }

    public String[] getAllTaskStatus() {
        return Arrays.copyOf(service.getStatus().values().toArray(), service.getStatus().values().size(),
                String[].class);
    }

    public String[] getAllTaskPriority() {
        return Arrays.copyOf(service.getPriority().values().toArray(), service.getPriority().values().size(),
                String[].class);
    }

    public void applyFilters(Map<String, List<String>> filters) {
        filteredTasks.clear();
        filterCount = 0;
        quickFilters.clear();
        filteredTasks.addAll(allTasks);
        if (filters.containsKey(FILTERS_PRIORITY))
            applyAllPriorityFilters(Objects.requireNonNull(filters.get(FILTERS_PRIORITY)));
        if (filters.containsKey(FILTERS_TYPE))
            applyAllTaskTypeFilters(Objects.requireNonNull(filters.get(FILTERS_TYPE)));
        if (filters.containsKey(FILTERS_STATUS))
            applyAllStatusFilters(Objects.requireNonNull(filters.get(FILTERS_STATUS)));
        if (filters.containsKey(FILTERS_ASSIGNEE) && !Objects.requireNonNull(filters.get(FILTERS_ASSIGNEE)).isEmpty()) {
            filterCount++;
            filterByUser();
        }
        if (filters.containsKey(FILTERS_DUE_DATE))
            applyDueDateFilter(Objects.requireNonNull(filters.get(FILTERS_DUE_DATE)));
        if (filters.containsKey(SORT_CATEGORY) && (!Objects.requireNonNull(filters.get(SORT_CATEGORY)).isEmpty())
                && Objects.requireNonNull(filters.get(SORT_CATEGORY)).size() == 1) {
            sortByCategory(Objects.requireNonNull(filters.get(SORT_CATEGORY)).get(0));
            filterCount++;
        }
        observableFilterCount.postValue(filterCount);
        observableTasks.postValue(filteredTasks);
        appliedFilters.putAll(filters);
    }

    private void sortByCategory(String sortCategory) {
        switch (sortCategory) {
            case FILTERS_TYPE:
                sortByType();
                break;
            case FILTERS_DUE_DATE:
                sortFiltered();
                break;
            case FILTERS_PRIORITY:
                sortByPriority();
                break;
            case FILTERS_STATUS:
                sortByStatus();
                break;
            default:
                break;
        }

    }

    private void sortByStatus() {
        List<Task> sorted = new ArrayList<>();
        for (String status : getAllTaskStatus()) {
            for (Task task : filteredTasks) {
                if (task.getStatus().equals(status))
                    sorted.add(task);
            }
        }
        filteredTasks.clear();
        filteredTasks.addAll(sorted);
    }

    private void sortByPriority() {
        List<Task> sorted = new ArrayList<>();
        for (String priority : getAllTaskPriority()) {
            for (Task task : filteredTasks) {
                if (task.getPriority().equals(priority))
                    sorted.add(task);
            }
        }
        filteredTasks.clear();
        filteredTasks.addAll(sorted);
    }

    private void sortByType() {
        List<Task> sorted = new LinkedList<>(filteredTasks);
        Collections.sort(sorted, (task1, task2) -> task1.getType().compareToIgnoreCase(task2.getType()));
        filteredTasks.clear();
        filteredTasks.addAll(sorted);
    }

    private void applyDueDateFilter(List<String> filters) {
        if (filters.size() == 2) {
            List<Task> filteredByDueDate = filterByDueDate(this.filteredTasks, filters);
            filteredTasks.clear();
            filteredTasks.addAll(filteredByDueDate);
            filterCount++;
        }
    }

    private void applyAllStatusFilters(List<String> filters) {
        if (!filters.isEmpty()) {
            List<Task> filteredByStatus = filterByTaskStatus(this.filteredTasks, filters);
            filteredTasks.clear();
            filteredTasks.addAll(filteredByStatus);
            filterCount++;
        }
    }

    private void applyAllTaskTypeFilters(List<String> filters) {
        if (!filters.isEmpty()) {
            List<Task> filteredByType = filterByTaskType(this.filteredTasks, filters);
            filteredTasks.clear();
            filteredTasks.addAll(filteredByType);
            filterCount++;
        }
    }

    private List<Task> filterByTaskType(List<Task> taskList, List<String> taskTypes) {
        List<Task> filtered = new LinkedList<>();
        for (Task task : taskList)
            if (taskTypes.contains(task.getType()))
                filtered.add(task);
        return filtered;
    }

    private List<Task> filterByTaskStatus(List<Task> taskList, List<String> status) {
        List<Task> filtered = new LinkedList<>();
        for (Task task : taskList)
            if (status.contains(task.getStatus()))
                filtered.add(task);
        return filtered;
    }

    private List<Task> filterByDueDate(List<Task> taskList, List<String> filters) {
        DateHelper dateHelper = new DateHelper();
        Date startDate = dateHelper.convertToDate(filters.get(0));
        Date endDate = dateHelper.convertToDate(filters.get(1));
        List<Task> filtered = new LinkedList<>();
        if (startDate != null && endDate != null) {
            Calendar start = Calendar.getInstance();
            start.setTime(startDate);
            start.set(Calendar.HOUR_OF_DAY, 0);
            start.set(Calendar.MINUTE, 0);
            start.set(Calendar.SECOND, 1);

            Calendar end = Calendar.getInstance();
            end.setTime(endDate);
            end.set(Calendar.HOUR_OF_DAY, 23);
            end.set(Calendar.MINUTE, 59);
            end.set(Calendar.SECOND, 59);

            for (Task task : taskList) {
                if (start.getTime().compareTo(task.getDueDate()) * task.getDueDate().compareTo(end.getTime()) >= 0)
                    filtered.add(task);
            }
        } else {
            filtered.addAll(taskList);
        }

        return filtered;
    }

    public Map<Integer, String> getTaskTypes() {
        return service.getType();
    }

    public Map<Integer, String> getTaskPriorities() {
        return service.getPriority();
    }

    public Map<Integer, String> getTaskStatus() {
        return service.getStatus();
    }

    public Map<Integer, String> getTaskDueTimes() {
        return service.getDueTimes();
    }

    public List<Integer> getSelectedFilterValues(String[] valueOptions, List<String> filters) {
        List<Integer> filterIndex = new ArrayList<>();
        List<String> valueOptionsList = Arrays.asList(valueOptions);
        if (filters != null) {
            for (String value : filters) {
                if (valueOptionsList.contains(value)) {
                    filterIndex.add(valueOptionsList.indexOf(value));
                }
            }
        }
        return filterIndex;
    }

    protected void deleteTask(String s) {
        deleted.setValue(true);
    }

    public void deleteTask(int taskId) {
        VolleyConsumer.getDelete(service.updateQuery(taskId), this::deleteTask);
    }
}
