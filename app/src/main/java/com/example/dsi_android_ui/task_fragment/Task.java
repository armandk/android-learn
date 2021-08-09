package com.example.dsi_android_ui.task_fragment;

import com.example.dsi_android_ui.models.ProductInTask;

import java.util.Date;
import java.util.List;

public class Task {
    private Integer id;
    private String description;
    private Integer activeId;
    private Boolean active;
    private String type;
    private Integer typeId;
    private String status;
    private Integer statusId;
    private String priority;
    private Integer priorityId;
    private Date dueDate;
    private Integer dueTimeId;
    private String remaining;
    private Integer userId;
    private List<ProductInTask> products;

    public com.example.dsi_android_ui.models.Task parseToTaskModel() {
        com.example.dsi_android_ui.models.Task modelTask = new com.example.dsi_android_ui.models.Task();
        modelTask.setTypeId(this.typeId);
        modelTask.setDescription(this.description);
        modelTask.setId(((long) this.getId()));
        modelTask.setStatusId(this.getStatusId());
        modelTask.setDueTimeId(this.getDueTimeId());
        modelTask.setUserId(this.getUserId());
        modelTask.setPriorityId(this.getPriorityId());
        modelTask.setDueDate(this.getDueDate());
        modelTask.setActive(this.getActiveId());
        modelTask.setProducts(this.getProducts());
        return modelTask;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getRemaining() {
        return remaining;
    }

    public void setRemaining(String remaining) {
        this.remaining = remaining;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<ProductInTask> getProducts() {
        return products;
    }

    public void setProducts(List<ProductInTask> products) {
        this.products = products;
    }

    public Integer getActiveId() {
        return activeId;
    }

    public void setActiveId(Integer activeId) {
        this.activeId = activeId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public Integer getPriorityId() {
        return priorityId;
    }

    public void setPriorityId(Integer priorityId) {
        this.priorityId = priorityId;
    }

    public Integer getDueTimeId() {
        return dueTimeId;
    }

    public void setDueTimeId(Integer dueTimeId) {
        this.dueTimeId = dueTimeId;
    }
}
