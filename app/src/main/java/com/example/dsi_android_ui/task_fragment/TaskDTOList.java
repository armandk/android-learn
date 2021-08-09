package com.example.dsi_android_ui.task_fragment;

import java.util.LinkedList;
import java.util.List;

import lombok.Data;

@Data
public class TaskDTOList {
    private List<TaskDTO> tasks = new LinkedList<>();
}
