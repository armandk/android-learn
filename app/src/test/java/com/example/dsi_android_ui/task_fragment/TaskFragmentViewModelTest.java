package com.example.dsi_android_ui.task_fragment;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.example.dsi_android_ui.helper.TestDataHelper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.dsi_android_ui.utils.ConstantsKt.FILTERS_MINE;
import static com.example.dsi_android_ui.utils.ConstantsKt.SORT_BY_DUE_DATE;
import static com.example.dsi_android_ui.utils.ConstantsKt.STATUS_HIGH;
import static com.example.dsi_android_ui.utils.ConstantsKt.STATUS_LOW;
import static com.example.dsi_android_ui.utils.ConstantsKt.STATUS_MEDIUM;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

/**
 * The type Task fragment view model test.
 */
@RunWith(RobolectricTestRunner.class)
public class TaskFragmentViewModelTest {

    /**
     * The Instant executor rule.
     */
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private static TaskFragmentViewModel target;
    private final TestDataHelper testDataHelper = new TestDataHelper();
    @Mock
    private static Observer<Integer> filterCountObserver;

    /**
     * The Task list observer.
     */
    @Mock
    static Observer<List<Task>> taskListObserver;

    /**
     * Sets up.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        target = new TaskFragmentViewModel();
        target.responseHandler(Valid.json);
        target.setUserId(1);
        target.getObservableTasks().observeForever(taskListObserver);
        target.getObservableFilterCount().observeForever(filterCountObserver);
    }

    /**
     * Unit test for {@link TaskFragmentViewModel#notifyTaskCompleted(String)}
     * (Task)} (String)}
     */
    @Test
    public void notifyingTaskCompleted_completedValueShouldBeTrue_returnNothing() {
        assertFalse(target.completed.getValue());
        target.notifyTaskCompleted("Updated tasks Successfully");
        assertTrue(target.completed.getValue());
    }

    /**
     * Unit test for {@link TaskFragmentViewModel#notifyAssignedToMe(String)}
     * (String)} (Task)} (String)}
     */

    @Test
    public void notifyingTaskAssignedToMe_assignedValueShouldBeTrue_returnNothing() {
        assertFalse(target.assigned.getValue());
        target.notifyAssignedToMe("Updated tasks Successfully");
        assertTrue(target.assigned.getValue());
    }

    /**
     * Unit test for {@link TaskFragmentViewModel#countMyTasks()}
     */
    @Test
    public void givenNothing_whenCountMyTasks_returnMyTaskCount() {
        // actual call
        int actual = target.countMyTasks();
        // verify
        assertEquals(2, actual);
    }

    /**
     * Unit test for {@link TaskFragmentViewModel#filterTasks(List)} \
     */
    @Test
    public void givenFiltersMine_whenFilterTasks_filterTasks() {
        List<String> filters = new ArrayList<>();
        filters.add(FILTERS_MINE);
        // actual call
        target.filterTasks(filters);
        // verify
        verify(taskListObserver, atLeast(1)).onChanged(target.getFilteredTasks());
    }

    /**
     * Unit test for {@link TaskFragmentViewModel#filterTasks(List)}
     */
    @Test
    public void givenFiltersPriorityLow_whenFilterTasks_filterTasks() {
        List<String> filters = new ArrayList<>();
        filters.add(STATUS_LOW);
        // actual call
        target.filterTasks(filters);
        // verify
        verify(taskListObserver, atLeast(1)).onChanged(target.getFilteredTasks());
    }

    /**
     * Unit test for {@link TaskFragmentViewModel#filterTasks(List)}
     */
    @Test
    public void givenFiltersPriorityMedium_whenFilterTasks_filterTasks() {
        List<String> filters = new ArrayList<>();
        filters.add(STATUS_MEDIUM);
        // actual call
        target.filterTasks(filters);
        // verify
        verify(taskListObserver, atLeast(1)).onChanged(target.getFilteredTasks());
    }

    /**
     * Unit test for {@link TaskFragmentViewModel#filterTasks(List)}
     */
    @Test
    public void givenFiltersPriorityHigh_whenFilterTasks_filterTasks() {
        List<String> filters = new ArrayList<>();
        filters.add(STATUS_HIGH);
        // actual call
        target.filterTasks(filters);
        // verify
        verify(taskListObserver, atLeast(1)).onChanged(target.getFilteredTasks());
    }

    /**
     * Unit test for {@link TaskFragmentViewModel#filterTasks(List)}
     */
    @Test
    public void givenSortByDueDate_whenFilterTasks_sortTasks() {
        List<String> filters = new ArrayList<>();
        filters.add(SORT_BY_DUE_DATE);

        // actual call
        target.filterTasks(filters);
        // verify
        verify(taskListObserver, atLeast(1)).onChanged(target.getFilteredTasks());
    }

    /**
     * Unit test for {@link TaskFragmentViewModel#getAppliedFilters()}
     */
    @Test
    public void givenNothing_whenGetAppliedFilters_returnSelectedFilters() {
        // Mock
        Map<String, List<String>> expected = testDataHelper.generateFilters(1);
        target.applyFilters(expected);
        // actual call
        Map<String, List<String>> actual = target.getAppliedFilters();
        // verify
        assertEquals(expected.toString(), actual.toString());

    }

    /**
     * Unit test for {@link TaskFragmentViewModel#getAllTaskType()}
     */
    @Test
    public void givenNothing_whenGetAllTaskType_returnTaskTypeStringArray() {
        // Mock data
        Map<Integer, String> taskTypes = testDataHelper.generateTaskType();
        String[] expected = Arrays.copyOf(taskTypes.values().toArray(), taskTypes.values().size(), String[].class);
        // actual call
        String[] actual = target.getAllTaskType();
        // verify result
        assertArrayEquals(expected, actual);
    }

    /**
     * Unit test for {@link TaskFragmentViewModel#getAllTaskStatus()}
     */
    @Test
    public void givenNothing_whenGetAllTaskStatus_returnTaskStatusStringArray() {
        // Mock data
        Map<Integer, String> statuses = testDataHelper.generateTaskStatus();
        String[] expected = Arrays.copyOf(statuses.values().toArray(), statuses.values().size(), String[].class);
        // actual call
        String[] actual = target.getAllTaskStatus();
        // verify result
        assertArrayEquals(expected, actual);
    }

    /**
     * Unit test for {@link TaskFragmentViewModel#getAllTaskPriority()}
     */
    @Test
    public void givenNothing_whenGetAllTaskPriority_returnTaskPriorityStringArray() {
        // Mock data
        Map<Integer, String> priorities = testDataHelper.generateTaskPriority();
        String[] expected = Arrays.copyOf(priorities.values().toArray(), priorities.values().size(), String[].class);
        // actual call
        String[] actual = target.getAllTaskPriority();
        // verify result
        assertArrayEquals(expected, actual);
    }

    /**
     * Unit test for {@link TaskFragmentViewModel#applyFilters(Map)}
     */
    @Test
    public void givenTaskTypeFilters_whenApplyFilters_updateFilterCountTo1() {
        // actual call
        target.applyFilters(testDataHelper.generateFilters(1));
        // verify
        verify(filterCountObserver).onChanged(1);
    }

    /**
     * Unit test for {@link TaskFragmentViewModel#applyFilters(Map)}
     */
    @Test
    public void givenStatusFilters_whenApplyFilters_updateFilterCountTo1() {
        // actual call
        target.applyFilters(testDataHelper.generateFilters(2));
        // verify
        verify(filterCountObserver).onChanged(1);
    }

    /**
     * Unit test for {@link TaskFragmentViewModel#applyFilters(Map)}
     */
    @Test
    public void givenPriorityFilters_whenApplyFilters_updateFilterCountTo1() {
        // actual call
        target.applyFilters(testDataHelper.generateFilters(3));
        // verify
        verify(filterCountObserver).onChanged(1);
    }

    /**
     * Unit test for {@link TaskFragmentViewModel#applyFilters(Map)}
     */
    @Test
    public void givenAssigneeFilters_whenApplyFilters_updateFilterCountTo1() {
        // actual call
        target.applyFilters(testDataHelper.generateFilters(4));
        // verify
        verify(filterCountObserver).onChanged(1);
    }

    /**
     * Unit test for {@link TaskFragmentViewModel#applyFilters(Map)}
     */
    @Test
    public void givenDueDateFilters_whenApplyFilters_updateFilterCountTo1() {
        // actual call
        target.applyFilters(testDataHelper.generateFilters(5));
        // verify
        verify(filterCountObserver).onChanged(1);
    }

    /**
     * Unit test for {@link TaskFragmentViewModel#getTaskPriorities()}
     */
    @Test
    public void givenNothing_whenGetTaskPriorities_returnTaskPriorities() {
        // Mock data
        Map<Integer, String> expected = testDataHelper.generateTaskPriority();
        // actual call
        Map<Integer, String> actual = target.getTaskPriorities();
        // verify result
        assertEquals(expected.toString(), actual.toString());
    }

    /**
     * Unit test for {@link TaskFragmentViewModel#getTaskStatus()}
     */
    @Test
    public void givenNothing_whenGetTaskStatus_returnTaskStatus() {
        // Mock data
        Map<Integer, String> expected = testDataHelper.generateTaskStatus();
        // actual call
        Map<Integer, String> actual = target.getTaskStatus();
        // verify result
        assertEquals(expected.toString(), actual.toString());
    }

    /**
     * Unit test for {@link TaskFragmentViewModel#getTaskDueTimes()}
     */
    @Test
    public void givenNothing_whenGetTaskDueTimes_returnTaskDueTimes() {
        // Mock data
        Map<Integer, String> expected = testDataHelper.generateTaskDueTimes();
        // actual call
        Map<Integer, String> actual = target.getTaskDueTimes();
        // verify result
        assertEquals(expected.toString(), actual.toString());
    }

    /**
     * Unit test for {@link TaskFragmentViewModel#getTaskTypes()}
     */
    @Test
    public void givenNothing_whenGetTaskTypes_returnTaskTypes() {
        // Mock data
        Map<Integer, String> expected = testDataHelper.generateTaskType();
        // actual call
        Map<Integer, String> actual = target.getTaskTypes();
        // verify result
        assertEquals(expected.toString(), actual.toString());
    }

    /**
     * Unit test for {@link TaskFragmentViewModel#applyFilters(Map)}
     */
    @Test
    public void givenFilterByStatusAndPriority_whenApplyFilters_applyFiltersInTaskList() {
        // actual call
        target.applyFilters(Objects.requireNonNull(testDataHelper.generateFilters(6)));
        // verify
        verify(filterCountObserver).onChanged(2);
        verify(taskListObserver, atLeast(1)).onChanged(target.getFilteredTasks());
    }

    /**
     * Unit test for {@link TaskFragmentViewModel#applyFilters(Map)}
     */
    @Test
    public void givenSortByTaskType_whenApplyFilters_sortTasksByTaskType() {
        // actual call
        target.applyFilters(Objects.requireNonNull(testDataHelper.generateFilters(7)));
        // verify
        verify(filterCountObserver).onChanged(1);
        verify(taskListObserver, atLeast(1)).onChanged(target.getFilteredTasks());
    }

    /**
     * Unit test for {@link TaskFragmentViewModel#applyFilters(Map)}
     */
    @Test
    public void givenSortByStatus_whenApplyFilters_sortTasksByStatus() {
        // actual call
        target.applyFilters(Objects.requireNonNull(testDataHelper.generateFilters(8)));
        // verify
        verify(filterCountObserver).onChanged(1);
        verify(taskListObserver, atLeast(1)).onChanged(target.getFilteredTasks());
    }

    /**
     * Unit test for {@link TaskFragmentViewModel#applyFilters(Map)}
     */
    @Test
    public void givenSortByPriority_whenApplyFilters_sortTasksByTaskPriority() {
        // actual call
        target.applyFilters(Objects.requireNonNull(testDataHelper.generateFilters(9)));
        // verify
        verify(filterCountObserver).onChanged(1);
        verify(taskListObserver, atLeast(1)).onChanged(target.getFilteredTasks());
    }

    /**
     * Unit test for {@link TaskFragmentViewModel#applyFilters(Map)}
     */
    @Test
    public void givenFilterByTypeUserAndSortByDueDate_whenApplyFilters_ApplyFiltersAndSort() {
        // actual call
        target.applyFilters(Objects.requireNonNull(testDataHelper.generateFilters(10)));
        // verify
        verify(filterCountObserver).onChanged(3);
        verify(taskListObserver, atLeast(1)).onChanged(target.getFilteredTasks());
    }

    /**
     * Unit test for {@link TaskFragmentViewModel#applyFilters(Map)}
     */
    @Test
    public void givenAllFilterCategoryAndSortByPriority_whenApplyFilters_ApplyFiltersAndSort() {
        // actual call
        target.applyFilters(Objects.requireNonNull(testDataHelper.generateFilters(11)));
        // verify
        verify(filterCountObserver).onChanged(6);
        verify(taskListObserver, atLeast(1)).onChanged(target.getFilteredTasks());
    }

    /**
     * Unit test for
     * {@link TaskFragmentViewModel#getSelectedFilterValues(String[], List)}
     */
    @Test
    public void givenAllPrioritiesAndSelectedPriorities_whenGetSelectedFilterValues_returnIndexOfSelectedPriorities() {
        // Mock data
        Map<Integer, String> priorities = testDataHelper.generateTaskPriority();
        String[] expected = Arrays.copyOf(priorities.values().toArray(), priorities.values().size(), String[].class);
        // actual call
        List<Integer> actual = target.getSelectedFilterValues(expected, Arrays.asList(STATUS_HIGH, STATUS_LOW));
        // verify
        assertEquals(2, actual.size());
        assertEquals(0, (int) actual.get(0));
        assertEquals(2, (int) actual.get(1));
    }
}