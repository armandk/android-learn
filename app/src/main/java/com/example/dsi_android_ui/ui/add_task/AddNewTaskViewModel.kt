package com.example.dsi_android_ui.ui.add_task

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dsi_android_ui.models.ProductInTask
import com.example.dsi_android_ui.models.Task
import com.example.dsi_android_ui.models.User
import org.json.JSONObject

class AddNewTaskViewModel(private val repository: AddNewTaskRepository) : ViewModel() {

    private val _tag = "ADD_TASK_VM"

    private var _user = User(1, "Ram Smith")
    val user: User
        get() = _user

    private var _products = MutableLiveData<MutableList<ProductInTask>>()
    val products: LiveData<MutableList<ProductInTask>>
        get() = _products

    fun clear() {
        _products = MutableLiveData<MutableList<ProductInTask>>()
    }

    fun setProducts(productsInTask: List<ProductInTask>?) {
        _products.value = productsInTask?.toMutableList()
    }

    fun createTask(task: Task, listener: VolleyRequestListener) {
        task.products = products.value
        val jsonTask = parseTaskJSONObject(task)
        Log.i(_tag, "Create Task")
        Log.i(_tag, "Task: ${jsonTask.toString(3)}")
        repository.createTask(jsonTask, listener)
    }

    fun editTask(task: Task, listener: VolleyRequestListener) {
        task.products = products.value
        val jsonTask = parseTaskJSONObject(task)
        Log.i(_tag, "Updating Task")
        Log.i(_tag, "Task: ${jsonTask.toString(3)}")
        repository.updateTask(jsonTask, listener)
    }

    private fun parseTaskJSONObject(task: Task): JSONObject {
        return JSONObject(Task.parseToJsonString(task))
    }


    fun addProductsToTask(product: List<ProductInTask>) {
        _products.value = ArrayList()
        for (it in product) {
            products.value?.add(it)
        }
    }

}