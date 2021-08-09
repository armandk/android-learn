package com.example.dsi_android_ui.task_products

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class TaskProductListAdapter(val callback: DeleteCallback): ListAdapter<TaskProduct, TaskProductListViewHolder>(ProductDiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskProductListViewHolder {
        return TaskProductListViewHolder.from(parent,callback)
    }

    override fun onBindViewHolder(holder: TaskProductListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class DeleteCallback (val clickListener: (TaskProduct) -> Unit) {
    fun onClick(product: TaskProduct){
        clickListener(product)
    }
}

class ProductDiffUtilCallback : DiffUtil.ItemCallback<TaskProduct>(){
    override fun areItemsTheSame(oldItem: TaskProduct, newItem: TaskProduct): Boolean {
        return oldItem.productIdentifier == newItem.productIdentifier &&
                oldItem.productName == newItem.productName
    }

    override fun areContentsTheSame(oldItem: TaskProduct, newItem: TaskProduct): Boolean {
        return oldItem == newItem
    }
}
