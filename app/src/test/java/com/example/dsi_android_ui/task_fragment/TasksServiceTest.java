package com.example.dsi_android_ui.task_fragment;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.dsi_android_ui.utils.ConstantsKt.TASK_SERVICE_URL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TasksServiceTest {
    private static TasksService service;
    private static int USER_ID = 1;

    @BeforeClass
    public static void setUp() {
        service = new TasksService();
    }

    /**
     * Unit test for {@link TasksService#generateQuery()}
     */
    @Test
    public void givenNothing_whenGenerateQuery_returnUrl() {
        String actual = service.generateQuery();
        assertEquals(TASK_SERVICE_URL, actual);
    }

    /**
     * Unit test for {@link TasksService#updateQuery(int)}
     */
    @Test
    public void givenUserId_whenUpdateQuery_returnUrl() {
        String actual = service.updateQuery(USER_ID);
        assertEquals(TASK_SERVICE_URL + "/" + USER_ID, actual);
    }

    /**
     * Unit test for {@link TasksService#tasksFromResponse(String)}
     */
    @Test
    public void givenEmptyResponseJson_tasksFromResponse_returnEmptyTaskList() {
        // actual call
        List<Task> actual = service.tasksFromResponse("");
        // verify
        assertNotNull(actual);
        assertEquals(0, actual.size());
    }

    /**
     * Unit test for {@link TasksService#tasksFromResponse(String)}
     */
    @Test
    public void givenResponseJson_tasksFromResponse_returnTaskList() {
        // actual call
        List<Task> actual = service.tasksFromResponse(Valid.json);
        // verify
        assertNotNull(actual);
        assertEquals(3, service.getPriority().size());
        assertEquals(4, service.getStatus().size());
        assertEquals(3, service.getType().size());
        assertEquals(4, actual.size());
    }

    /**
     * Unit test for {@link TasksService#generateTaskFromDto(TaskDTO)}
     */
    @Test
    public void givenTaskDTO_whenGenerateTaskFromDTO_returnTask() {
        // Mock
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        calendar.add(Calendar.MINUTE, 1);
        Date dayAfter = calendar.getTime();
        TaskDTO dto = new TaskDTO();
        dto.setActive(1);
        dto.setDescription("desc");
        dto.setDueDate(dayAfter);
        dto.setId(9999);
        dto.setPriorityId(2);
        dto.setStatusId(1);
        dto.setTypeId(1);
        dto.setUserId(6666);
        service.tasksFromResponse(Valid.json);
        // actual call
        Task actual = service.generateTaskFromDto(dto);
        // verify
        assertEquals(true, actual.getActive());
        assertEquals("desc", actual.getDescription());
        assertEquals(dayAfter, actual.getDueDate());
        assertEquals(9999, actual.getId().intValue());
        assertEquals("Medium", actual.getPriority());
        assertEquals("Open", actual.getStatus());
        assertEquals("Product Movement", actual.getType());
        assertEquals(6666, actual.getUserId().intValue());
        assertEquals("1 day left", actual.getRemaining());
    }

    /**
     * Unit test for {@link TasksService#getRemaining(Date, Date)}
     */
    @Test
    public void givenDates_whenGetRemaining_returnNoOfDaysLeft() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        Date dayAfter = calendar.getTime();
        String actual = service.getRemaining(date, dayAfter);
        assertEquals("1 day left", actual);
    }

    /**
     * Unit test for {@link TasksService#getRemaining(Date, Date)}
     */
    @Test
    public void givenDatesDueDateLessThan24Hours_whenGetRemaining_returnZeroDaysLeft() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, 23);
        Date dayAfter = calendar.getTime();
        String actual = service.getRemaining(date, dayAfter);
        assertEquals("0 days left", actual);
    }

    /**
     * Unit test for {@link TasksService#getRemaining(Date, Date)}
     */
    @Test
    public void givenDatesDueDateLessThanToday_whenGetRemaining_returnEmptyString() {
        Date dayBefore = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dayBefore);
        calendar.add(Calendar.HOUR, 23);
        Date date = calendar.getTime();
        String actual = service.getRemaining(date, dayBefore);
        assertEquals("", actual);
    }
}