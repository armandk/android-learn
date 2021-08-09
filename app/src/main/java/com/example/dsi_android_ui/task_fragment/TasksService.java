package com.example.dsi_android_ui.task_fragment;

import android.annotation.SuppressLint;

import com.google.gson.Gson;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import lombok.Getter;

import static com.example.dsi_android_ui.utils.ConstantsKt.TASK_SERVICE_URL;

public class TasksService {
    private static final String URL = TASK_SERVICE_URL;
    @Getter
    private final Map<Integer, String> priority = new HashMap<>();
    @Getter
    private final Map<Integer, String> status = new HashMap<>();
    @Getter
    private final Map<Integer, String> type = new HashMap<>();
    @Getter
    private final Map<Integer, String> dueTimes = new HashMap<>();

    public String generateQuery() {
        return URL;
    }

    public String updateQuery(int userId) {
        return URL + "/" + userId;
    }

    public List<Task> tasksFromResponse(String response) {
        readMetadata(response);
        return readTaskList(response);
    }

    private List<Task> readTaskList(String response) {
        List<Task> list = new LinkedList<>();
        TaskDTOList taskDTOList = parseTasks(response);
        if (taskDTOList.getTasks() != null)
            for (TaskDTO task : taskDTOList.getTasks())
                list.add(generateTaskFromDto(task));
        return list;
    }

    private void readMetadata(String response) {
        if (noMetadata()) {
            TasksMetadata metadata = parseMetadata(response);
            if (metadata.getTaskTypes() != null) {
                for (Metadata data : metadata.getTaskTypes()) {
                    if (data.getActive() == 1)
                        type.put(data.getId(), data.getDescription());
                }
                for (Metadata data : metadata.getTaskPriorities()) {
                    if (data.getActive() == 1)
                        priority.put(data.getId(), data.getDescription());
                }
                for (Metadata data : metadata.getTaskStatuses()) {
                    if (data.getActive() == 1)
                        status.put(data.getId(), data.getDescription());
                }
                for (Metadata data : metadata.getDueTimes()) {
                    if (data.getActive() == 1)
                        dueTimes.put(data.getId(), data.getDescription());
                }
            }
        }
    }

    private TaskDTOList parseTasks(String string) {
        TaskDTOList parsed = new Gson().fromJson(string, TaskDTOList.class);
        return parsed == null ? new TaskDTOList() : parsed;
    }

    private TasksMetadata parseMetadata(String string) {
        TasksMetadata parsed = new Gson().fromJson(string, TasksMetadata.class);
        return parsed == null ? new TasksMetadata() : parsed;
    }

    private boolean noMetadata() {
        return type.isEmpty() || status.isEmpty() || priority.isEmpty();
    }

    Task generateTaskFromDto(TaskDTO dto) {
        Boolean active = dto.getActive() != 0;
        String typeString = type.get(dto.getTypeId());
        String priorityString = priority.get(dto.getPriorityId());
        String statusString = status.get(dto.getStatusId());
        String remaining = getRemaining(new Date(), dto.getDueDate());

        Task task = new Task();
        task.setActive(active);
        task.setActiveId(dto.getActive());
        task.setDescription(dto.getDescription());
        task.setDueDate(dto.getDueDate());
        task.setDueTimeId(dto.getDueTimeId());
        task.setId(dto.getId());
        task.setStatus(statusString == null ? "status" : statusString);
        task.setStatusId(dto.getStatusId());
        task.setPriority(priorityString == null ? "priority" : priorityString);
        task.setPriorityId(dto.getPriorityId());
        task.setType(typeString == null ? "type" : typeString);
        task.setTypeId(dto.getTypeId());
        task.setUserId(dto.getUserId());
        task.setDueTimeId(dto.getDueTimeId());
        task.setRemaining(remaining);
        task.setProducts(dto.getProducts());
        return task;
    }

    @SuppressLint("DefaultLocale")
    public String getRemaining(Date current, Date due) {
        long diff = due.getTime() - current.getTime();
        long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        if (diff > 0)
            return String.format("%d day%s left", days, days == 1 ? "" : "s");
        return "";
    }

}
