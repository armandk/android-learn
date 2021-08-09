package com.example.dsi_android_ui.task_fragment;

import com.example.dsi_android_ui.models.ProductInTask;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class TaskDTO {
    private int id;
    private String description;
    private int active;
    private int typeId;
    private int statusId;
    private int userId;
    private int priorityId;
    private Date dueDate;
    private int dueTimeId;
    private List<ProductInTask> products;

}