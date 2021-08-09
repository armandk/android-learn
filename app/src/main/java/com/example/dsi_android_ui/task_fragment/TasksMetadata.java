package com.example.dsi_android_ui.task_fragment;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

import lombok.Data;

@Data
public class TasksMetadata {
    @SerializedName("task_priorities")
    private List<Metadata> taskPriorities = new LinkedList<>();
    @SerializedName("task_statuses")
    private List<Metadata> taskStatuses = new LinkedList<>();
    @SerializedName("task_types")
    private List<Metadata> taskTypes = new LinkedList<>();
    @SerializedName("task_due_times")
    private List<Metadata> dueTimes = new LinkedList<>();
}
