package com.example.dsi_android_ui.ui.add_task

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.dsi_android_ui.models.Task
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*
import kotlin.collections.ArrayList

@RunWith(RobolectricTestRunner::class)
class AddNewTaskViewModelUnitTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    private lateinit var repository: AddNewTaskRepository
    private lateinit var target: AddNewTaskViewModel
    private lateinit var payload: JSONObject
    private lateinit var task: Task
    private lateinit var listener: VolleyRequestListener

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        target = AddNewTaskViewModel(repository)
        listener = object : VolleyRequestListener {
            override fun onError(message: String) {}
            override fun onResponse(response: String) {}
        }

        task = Task()
        task.userId = 1
        task.setDueDate(Date())
        task.description = "Desc"
        task.typeId = 1
        task.products = ArrayList()
        task.priorityId = 1
        task.dueTimeId = 3
        payload = JSONObject(Task.parseToJsonString(task))
    }

    /**
     * Unit test for **[AddNewTaskViewModel.createTask]**
     */
    @Test
    fun `given listener when createTask called then call postTaskRequest`() {
        //Mockk
        every { repository.createTask(any(), listener) } returns Unit
        //actual call
        target.createTask(task, listener)
        //verify
        verify(atLeast = 1) { repository.createTask(any(), listener) }
    }

    /**
     * Unit test for **[AddNewTaskViewModel.editTask]**
     */
    @Test
    fun `given listener when editTask called then call putTaskRequest`() {
        //Mockk
        every { repository.updateTask(any(), listener) } returns Unit
        //actual call
        target.editTask(task, listener)
        //verify
        verify(atLeast = 1) { repository.updateTask(any(), listener) }
    }

}