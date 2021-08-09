package com.example.dsi_android_ui

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.core.view.isEmpty
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dsi_android_ui.adapter.TaskProductDetailsAdapter
import com.example.dsi_android_ui.databinding.FragmentTaskDetailsBinding
import com.example.dsi_android_ui.models.ProductInTask
import com.example.dsi_android_ui.models.User
import com.example.dsi_android_ui.task_fragment.Task
import com.example.dsi_android_ui.task_fragment.TaskFragmentViewModel
import com.example.dsi_android_ui.task_fragment.TaskFragmentViewModelFactory
import com.example.dsi_android_ui.ui.add_task.AddNewTaskFragment
import com.example.dsi_android_ui.utils.createCustomConfirmDialog
import com.example.dsi_android_ui.utils.createCustomSnackbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.sap.cloud.mobile.fiori.`object`.ObjectHeader
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "TaskDetails"

class TaskDetailsFragment : BaseFragment() {

    private val user = User(1, "Ram Smith")
    private lateinit var taskDetails: Task
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var binding: FragmentTaskDetailsBinding
    private val viewModel by activityViewModels<TaskFragmentViewModel> {
        TaskFragmentViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bottomNav = requireActivity().findViewById(R.id.bottom_navigation)
        bottomNav.visibility = View.GONE
    }

    override fun onDestroy() {
        bottomNav.visibility = View.VISIBLE
        viewModel.clearLiveData()
        super.onDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTaskDetailsBinding.inflate(inflater, container, false)

        initRecyclerView()

        // Setting Buttons
        displayingAssignOrCompleteButton()
        settingUpOnClickListeners()

        // Toolbar code
        settingUpToolbar()
        showMenuOptions()

        // ObjectHeader code
        settingUpObjectHeader()

        setupObservables()

        return binding.root
    }

    private fun setupObservables() {
        viewModel.completed.observe(viewLifecycleOwner, {
            if (it) {
                taskDetails.statusId = 4
                binding.taskBtn.isEnabled = false
                createCustomSnackbar(
                    this.requireView(),
                    getString(R.string.task_completed),
                    Snackbar.LENGTH_LONG,
                    R.drawable.ic_check,
                    null
                ).show()
                navigateOut()
            }
        })

        viewModel.assigned.observe(viewLifecycleOwner, {
            if (it) {
                binding.taskBtn.visibility = View.VISIBLE
                binding.taskAssignBtn.visibility = View.GONE
                updateTaskDetailScreen()
                createCustomSnackbar(
                    this.requireView(),
                    getString(R.string.assigned_me),
                    Snackbar.LENGTH_LONG,
                    R.drawable.ic_check,
                    null
                ).show()
            }
        })

        viewModel.unassigned.observe(viewLifecycleOwner, {
            if (it) {
                binding.taskBtn.visibility = View.VISIBLE
                binding.taskAssignBtn.visibility = View.GONE
                updateTaskDetailScreen()
                createCustomSnackbar(
                    this.requireView(),
                    getString(R.string.task_unassigned),
                    Snackbar.LENGTH_LONG,
                    R.drawable.ic_check,
                    null
                ).show()
            }
        })
    }

    fun updateTaskDetailScreen(task: Task? = null) {
        task?.let {
            setTaskDetails(task)
            loadProductData(task.products)
        }
        displayingAssignOrCompleteButton()
        settingUpObjectHeader()
        showMenuOptions()
    }

    private fun displayingAssignOrCompleteButton() {
        if (taskDetails.userId == 0) {
            binding.taskBtn.visibility = View.GONE
            binding.taskAssignBtn.visibility = View.VISIBLE
        } else {
            binding.taskBtn.visibility = View.VISIBLE
            binding.taskAssignBtn.visibility = View.GONE
            if (taskDetails.userId != user.id || taskDetails.status.equals("Complete", true)) {
                binding.taskBtn.isEnabled = false
                binding.taskBtn.setBackgroundColor(Color.GRAY)
            }
        }
    }

    private fun settingUpOnClickListeners() {
        binding.taskBtn.setOnClickListener {
            taskDetails.statusId = 4   // Completed
            viewModel.completeTask(taskDetails)

        }
        binding.taskAssignBtn.setOnClickListener {
            taskDetails.userId = user.id   // Assigned to current user
            taskDetails.statusId = 2       // In Progress
            taskDetails.status = viewModel.taskStatus[2]
            viewModel.assignTaskToMe(taskDetails)
        }
    }

    private fun settingUpToolbar() {
        binding.toolbar.let {
            it.title = "Task # ${taskDetails.id}: ${taskDetails.type}"
            it.navigationIcon = ResourcesCompat.getDrawable(
                requireContext().resources,
                R.drawable.ic_arrow_back_24dp,
                null
            )
            it.setNavigationOnClickListener {
                navigateOut()
            }
        }
    }

    private fun navigateOut() {
        requireActivity().onBackPressed()
    }

    private fun showMenuOptions() {
        if (taskDetails.userId == user.id) {
            createMenu()
        } else {
            binding.toolbar.menu.clear()
        }
    }

    private fun createMenu() {
        with(binding.toolbar) {
            if (menu.isEmpty()) {
                inflateMenu(R.menu.navigation_menu_task)
                val deleteOption = menu.getItem(menu.size() - 1)
                val span = SpannableString(deleteOption.title)
                span.setSpan(ForegroundColorSpan(Color.RED), 0, span.length, 0)
                deleteOption.title = span
                hidingItemsIfTaskCompleted(menu)

                setMenuItemListeners(this)
            }
        }
    }

    private fun hidingItemsIfTaskCompleted(menu: Menu) {
        if (taskDetails.statusId == 4) {
            for (item in menu.children) {
                if (item.itemId != R.id.task_detail_delete_task) {
                    item.isVisible = false
                }
            }
        }
    }

    private fun setMenuItemListeners(toolbar: Toolbar) {
        toolbar.setOnMenuItemClickListener { menu ->
            when (menu.itemId) {
                R.id.task_detail_unassign_task -> {
                    unassignTask()
                    true
                }
                R.id.task_detail_edit_task -> {
                    navigateToEditTask()
                    true
                }
                R.id.task_detail_delete_task -> {
                    createCustomConfirmDialog(
                        requireActivity(),
                        actionButtonLabel = R.string.delete
                    ) {
                        deleteTask(taskDetails.id)
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun unassignTask() {
        taskDetails.userId = 0     // Unassign
        taskDetails.statusId = 1   // Open
        taskDetails.status = viewModel.taskStatus[1]
        viewModel.assignTaskToMe(taskDetails)
    }

    private fun navigateToEditTask() {
        val ft = childFragmentManager.beginTransaction()
        val prev = childFragmentManager.findFragmentByTag("add_task")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        val addTask = AddNewTaskFragment(taskDetails.parseToTaskModel(), this)
        addTask.show(ft, "add_task")
    }

    private fun settingUpObjectHeader() {
        val df = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val dueTime = viewModel.taskDueTimes[taskDetails.dueTimeId] ?: "All day"
        val taskType =
            """Task type : ${taskDetails.type}
              |Due Date: ${taskDetails.dueDate?.let { df.format(it) }}
              |Due Time: $dueTime""".trimMargin()

        with(binding.objectHeader) {
            headline = taskType
            displayAssigneeOnObjectHeader()
            val analyticsView = View.inflate(
                context,
                R.layout.content_object_header_detail, null
            )
            configureDetailText(analyticsView);
            detailView = analyticsView

            setStatusPriorityTextView(this)
            setStatusRemainingStatusTextView(this)
        }
    }

    private fun setStatusPriorityTextView(header: ObjectHeader) {
        header.setStatus(taskDetails.priority, 0)
        val priority = header.getStatusView(0) as TextView
        priority.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        priority.setTextColor(
            ResourcesCompat.getColorStateList(
                resources,
                when (taskDetails.priorityId) {
                    1 -> R.color.light_red_text
                    2 -> R.color.light_blue_text
                    3 -> R.color.light_yellow_text
                    else -> R.color.white
                }, null
            )
        )

        priority.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
    }

    private fun setStatusRemainingStatusTextView(header: ObjectHeader) {
        val status: TextView
        when (taskDetails.status) {
            getString(R.string.complete_text) -> {
                header.setStatus(taskDetails.status, 1)
                status = header.getStatusView(1) as TextView
                status.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.light_green_text,
                        null
                    )
                )
            }
            else -> {
                if (!taskDetails.remaining.isNullOrEmpty()) {
                    header.setStatus(taskDetails.remaining, 1)
                } else {
                    header.setStatus("Overdue", 1)
                }
                status = header.getStatusView(1) as TextView
                status.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.light_red_text,
                        null
                    )
                )
            }
        }
        status.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        status.setPadding(0, 8, 0, 0)
        status.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
    }

    private fun displayAssigneeOnObjectHeader() {
        val assignedTo = when (taskDetails.userId) {
            0 -> "Unassigned"
            1 -> user.name
            else -> "User ${taskDetails.userId}"
        }
        binding.objectHeader.subheadline = "Assigned: $assignedTo"
    }

    private fun deleteTask(id: Int) {
        viewModel.deleteTask(id)
        viewModel.deleted.observe(viewLifecycleOwner, {
            if (it) {
                createCustomSnackbar(
                    requireView(),
                    resources.getString(R.string.task_deleted_message),
                    Snackbar.LENGTH_SHORT,
                    R.drawable.ic_check
                ).show()
                bottomNav.visibility = View.VISIBLE
                transitToTaskDetails()
            }
        })

    }

    private fun transitToTaskDetails() {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, TaskFragment())
        transaction.addToBackStack(null)
        transaction.commit()
        (activity as MainActivity).changeBottomNavigation(R.id.tasks)
    }

    private fun configureDetailText(analyticsView: View?) {
        val detailsText = analyticsView?.findViewById(R.id.details_tv) as TextView
        detailsText.text = taskDetails.description?.let { getSafeSubstring(it, 100) }

    }

    private fun loadProductData(products: List<ProductInTask>?) {
        val myListAdapter: TaskProductDetailsAdapter =
            object : TaskProductDetailsAdapter(activity, taskDetails.products) {
                override fun recyclerviewClick(position: Any) {
//                        Toast.makeText(view.getContext(),"click on item: "+((ProductOverview)position).getProductName(), Toast.LENGTH_LONG).show();
                    //Log.d("TEST", "ProductOverview " + (position as ProductOverview).productName)

                }
            }
        binding.recycleView.adapter = myListAdapter
        binding.recycleView.invalidate()
    }

    private fun getSafeSubstring(s: String, maxLength: Int): String {
        return if (!TextUtils.isEmpty(s) && s.length >= maxLength) s.substring(0, maxLength) else s
    }


    private fun initRecyclerView() {
        if (taskDetails.products.isNullOrEmpty()) {
            binding.taskDetailsNoProductsLabel.visibility = View.VISIBLE
            binding.recycleView.visibility = View.GONE
        } else {
            binding.taskDetailsNoProductsLabel.visibility = View.GONE
            binding.recycleView.visibility = View.VISIBLE
            binding.recycleView.let {
                it.setHasFixedSize(true)
                it.addItemDecoration(
                    DividerItemDecoration(
                        it.context,
                        DividerItemDecoration.VERTICAL
                    )
                )
                it.layoutManager = LinearLayoutManager(context)
                taskDetails.products?.let { products -> loadProductData(products) }
            }
        }
    }

    private fun setTaskDetails(task: Task) {
        taskDetails = task
    }

    companion object {
        @JvmStatic
        fun newInstance(task: Task) =
            TaskDetailsFragment().apply {
                arguments = Bundle().apply {
                    taskDetails = task
                }
            }
    }
}