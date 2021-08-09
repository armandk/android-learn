package com.example.dsi_android_ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dsi_android_ui.R;
import com.example.dsi_android_ui.databinding.TaskListItemBinding;
import com.example.dsi_android_ui.models.User;
import com.example.dsi_android_ui.task_fragment.Task;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public abstract class MyTaskAdapter extends RecyclerView.Adapter<MyTaskAdapter.ViewHolder> implements RecyclerviewOnClickListener {

    private final List<Task> tasks;
    private final User user = new User();
    private Context context = null;

    protected MyTaskAdapter(List<Task> tasks) {
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        TaskListItemBinding binding = TaskListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        context = parent.getContext();
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Task task = tasks.get(position);
        String title = String.format(Locale.getDefault(), "Task # %d: %s", task.getId(), task.getType());
        holder.binding.type.setText(title);
        holder.binding.description.setText(task.getDescription());
        holder.binding.priority.setText(task.getPriority());
        holder.binding.priority.setTextColor(context.getColor(getColor(task.getPriority())));
        holder.binding.remaining.setText(task.getRemaining());
        getDaysVisibility(task, holder);
        holder.binding.status.setText(task.getStatus());
        holder.binding.status.setTextColor(context.getColor(getColor(task.getStatus())));
        setAssignation(task.getUserId(), holder);
        holder.binding.contentLayout.setOnClickListener(v -> recyclerviewClick(task));
    }

    private void setAssignation(Integer userId, ViewHolder holder) {
        String username = getUserName(userId);
        holder.binding.user.setText(username);
        if (userId == 0) {
            holder.binding.user.setTextColor(context.getColor(R.color.blue_text));
        } else {
            holder.binding.user.setTextColor(context.getColor(R.color.black_text));
        }

    }

    private String getUserName(Integer userId) {
        switch (userId) {
            case 0:
                return "Unassigned";
            case 1:
                return user.getName();
            default:
                return "User " + userId;
        }
    }

    private void getDaysVisibility(Task task, ViewHolder holder) {
        if (task.getRemaining().isEmpty()) {
            holder.binding.remaining.setText(R.string.overdue);
            holder.binding.remaining.setTextColor(context.getColor(R.color.red_text));
        } else {
            holder.binding.remaining.setTextColor(context.getColor(R.color.black_text));
        }

        if ("Complete".equalsIgnoreCase(task.getStatus())) {
            holder.binding.remaining.setVisibility(View.GONE);
        } else {
            holder.binding.remaining.setVisibility(View.VISIBLE);
        }

        if (task.getStatus().equals(context.getResources().getString(R.string.complete))) {
            holder.binding.status.setVisibility(View.VISIBLE);
        } else {
            holder.binding.status.setVisibility(View.GONE);
        }

    }

    private int getColor(String key) {
        switch (key.toUpperCase()) {
            case "HIGH":
            case "OVERDUE":
                return R.color.red_text;
            case "MEDIUM":
            case "OPEN":
                return R.color.blue_text;
            case "COMPLETE":
                return R.color.green_text;
            default:
                return R.color.black_text;
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setData(List<Task> productList) {
        this.tasks.clear();
        this.tasks.addAll(productList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TaskListItemBinding binding;

        public ViewHolder(TaskListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }
}