package com.example.dsi_android_ui.ui.add_task

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.dsi_android_ui.BaseFragment
import com.example.dsi_android_ui.R
import com.example.dsi_android_ui.TaskDetailsFragment
import com.example.dsi_android_ui.TaskFragment
import com.example.dsi_android_ui.databinding.AddNewTaskFragmentBinding
import com.example.dsi_android_ui.models.Task
import com.example.dsi_android_ui.task_fragment.TaskFragmentViewModel
import com.example.dsi_android_ui.task_fragment.TasksService
import com.example.dsi_android_ui.task_products.TaskProductListFragment
import com.example.dsi_android_ui.task_products.TaskProductListViewModel
import com.example.dsi_android_ui.ui.base.BaseDialogFragment
import com.example.dsi_android_ui.utils.CustomDialogButtonColor
import com.example.dsi_android_ui.utils.createCustomConfirmDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.sap.cloud.mobile.fiori.formcell.FormCell
import com.sap.cloud.mobile.fiori.formcell.FormCell.CellValueChangeListener
import com.sap.cloud.mobile.fiori.formcell.InlineValidation
import java.text.SimpleDateFormat
import java.util.*


class AddNewTaskFragment(private var taskToEdit: Task? = null, private val fragment: BaseFragment) :
    BaseDialogFragment<AddNewTaskFragmentBinding>() {

    private val _tag = "ADD_TASK"

    private val service = TasksService()
    private var loadingTask: Boolean = false
    private val task = Task()
    private var wasTaskModified: Boolean = false

    private var bottomNav: BottomNavigationView? = null

    private val taskProductListViewModel by activityViewModels<TaskProductListViewModel>()
    private val viewModel: AddNewTaskViewModel by activityViewModels { AddNewTaskViewModelFactory() }
    private val taskViewModel by activityViewModels<TaskFragmentViewModel>()

    override fun getDialogBinding(inflater: LayoutInflater, container: ViewGroup?) =
        AddNewTaskFragmentBinding.inflate(inflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bottomNav = requireActivity().findViewById(R.id.bottom_navigation)
        bottomNav?.visibility = View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.clear()

        taskToEdit?.let {
            loadingTask = true
            binding.addNewTaskEditButton.visibility = View.VISIBLE
            binding.addNewTaskFooterButtons.visibility = View.GONE
        }

        setupToolbar()
        setFooterButtonsOnClickListener()
        setFieldsListeners()
        settingDefaultValues()
        setTasKValesFromTaskToUpdate()
    }

    private fun close() {
        taskViewModel.loadTasks()
        taskProductListViewModel.clear()
        when (fragment) {
            is TaskFragment -> {
                bottomNav?.visibility = View.VISIBLE
                fragment.fetchTasks()
            }
            is TaskDetailsFragment -> {
                fragment.updateTaskDetailScreen(parseTaskFromTaskModel(task))
            }
        }
        dismiss()
    }

    private fun cancelAction() {
        if (fragment is TaskFragment)
            bottomNav?.visibility = View.VISIBLE

        if (wasTaskModified || taskProductListViewModel.wasSelectedProductsModified) {
            createCustomConfirmDialog(
                requireActivity(),
                R.string.changes_will_be_lost,
                null,
                R.string.stay,
                R.string.leave,
                CustomDialogButtonColor.PRIMARY
            ) {
                taskProductListViewModel.clear()
                dismiss()
            }
        } else {
            taskProductListViewModel.clear()
            dismiss()
        }
    }

    private fun settingDefaultValues() {
        Log.d(_tag, "SettingDefaultValues")
        binding.addNewTaskTypeOption.valueOptions =
            taskViewModel.taskTypes.values.filter { value -> value.contains("Movement") }
                .toTypedArray()
        if (taskToEdit == null) {
            binding.addNewTaskTypeOption.value = 0
        } else {
            binding.addNewTaskTypeOption.isEditable = false
        }
        binding.addNewTaskPriorityOption.valueOptions =
            taskViewModel.taskPriorities.values.toTypedArray().reversedArray()
        binding.addNewTaskPriorityOption.value = 0
        binding.addNewTaskAssignee.valueOptions =
            arrayOf("Me (${viewModel.user.name})", "unassigned")
        binding.addNewTaskAssignee.value = 0
        Log.d(_tag, "DEFAULT STATUS: ${task.statusId}")
        val dueTimesArray =
            taskViewModel.taskDueTimes.values.filter { value -> !value.contains("Mid") }
                .toTypedArray()
                .reversedArray()
        binding.addNewTaskDueTime.valueOptions = dueTimesArray
        binding.addNewTaskDueTime.value = 0
        wasTaskModified = false
    }

    private fun setTasKValesFromTaskToUpdate() {
        taskToEdit?.let {
            with(task) {
                id = it.id
                statusId = it.statusId
                active = it.active
                setTaskTypeDisplayValue(it)
                binding.addNewTaskDescription.value = it.description
                description = it.description
                binding.addNewTaskAssignee.value = if (it.userId == 0) 1 else 0
                userId = it.userId
                val priority = taskViewModel.taskPriorities[it.priorityId]
                binding.addNewTaskPriorityOption.value =
                    binding.addNewTaskPriorityOption.valueOptions.indexOf(priority)
                priorityId = it.priorityId
                binding.addNewTaskDueDate.value = it.getDueDateAsDate()
                dueDate = it.dueDate
                val dueTime = taskViewModel.taskDueTimes[it.dueTimeId]
                binding.addNewTaskDueTime.value =
                    binding.addNewTaskDueTime.valueOptions.indexOf(dueTime)
                dueTimeId = it.dueTimeId
                binding.addNewTaskProducts.value = it.products
                products = it.products
                viewModel.setProducts(products)
                products?.let {
                    Log.d(_tag, "Adding products to selected products")
                    taskProductListViewModel.addProductsFromTaskToSelectedToList(it.toList())
                }
                setProductsDisplayValue()
                loadingTask = false
                wasTaskModified = false
            }
        }
    }

    private fun setTaskTypeDisplayValue(t: Task) {
        val type = taskViewModel.taskTypes[t.typeId]
        when (type?.toUpperCase(Locale.ROOT)) {
            "PRODUCT MOVEMENT" -> binding.addNewTaskTypeOption.value =
                binding.addNewTaskTypeOption.valueOptions.indexOf(type)
            "PRODUCT OUT OF STOCK" -> binding.addNewTaskProductOutStockChip.isSelected = true
            "PRODUCT RESTOCK" -> binding.addNewTaskProductRestockChip.isSelected = true
        }
        task.typeId = t.typeId
    }

    private fun setFieldsListeners() {
        binding.addNewTaskScrollView.setOnScrollChangeListener { _, _, _, _, _ -> removeFocusFromDescription() }
        setDescriptionListener()
        setTypeListener()
        setAssigneListener()
        setPriorityListener()
        setDueTimeListener()
        setDueDateListener()

        binding.addNewTaskProducts.setOnClickListener {
            navigateToProductList()
        }

        taskProductListViewModel.productsInTask.observe(viewLifecycleOwner, { list ->
            Log.d(
                _tag,
                "ProductsInTaskObserver#fromTask ${taskProductListViewModel.wasSelectedProductsModified || taskProductListViewModel.productsAddedFromTask}"
            )
            if (taskProductListViewModel.wasSelectedProductsModified || taskProductListViewModel.productsAddedFromTask) {
                viewModel.addProductsToTask(list)
                setProductsDisplayValue()
                if (taskProductListViewModel.wasSelectedProductsModified)
                    showSnackBar(list.isEmpty())
            }
        })
    }

    private fun setDueDateListener() {
        binding.addNewTaskDueDate.cellValueChangeListener =
            object : CellValueChangeListener<Date>() {
                override fun cellChangeHandler(date: Date?) {
                    removeFocusFromDescription()
                    date?.let {
                        val now = Calendar.getInstance()
                        now.set(Calendar.HOUR_OF_DAY, 0)
                        now.set(Calendar.MINUTE, 0)
                        val dateChose = Calendar.getInstance()
                        dateChose.time = it
                        if (dateChose.before(now)) {
                            binding.addNewTaskDueDate.isErrorEnabled = true
                            binding.addNewTaskDueDate.error =
                                "Due date can't be set to a previous date from today"
                        } else {
                            binding.addNewTaskDueDate.isErrorEnabled = false
                            task.setDueDate(it)
                        }
                    }
                }

                override fun updatedDisplayText(date: Date?): CharSequence? {
                    val df = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    return df.format(date ?: Date())
                }
            }
    }

    private fun setDueTimeListener() {
        binding.addNewTaskDueTime.cellValueChangeListener =
            object : CellValueChangeListener<Int>() {
                override fun cellChangeHandler(choice: Int?) {
                    removeFocusFromDescription()
                    choice?.let {
                        if (it >= 0) {
                            wasTaskModified = true
                            val option = binding.addNewTaskDueTime.valueOptions[it]
                            val key =
                                taskViewModel.taskDueTimes.filterValues { value -> value == option }.keys.first()
                            task.dueTimeId = key
                        }
                    }
                }
            }
    }

    private fun setPriorityListener() {
        binding.addNewTaskPriorityOption.cellValueChangeListener =
            object : CellValueChangeListener<Int>() {
                override fun cellChangeHandler(choice: Int?) {
                    removeFocusFromDescription()
                    choice?.let {
                        if (it >= 0) {
                            wasTaskModified = true
                            val option = binding.addNewTaskPriorityOption.valueOptions[it]
                            val key =
                                taskViewModel.taskPriorities.filterValues { value -> value == option }.keys.first()
                            task.priorityId = key
                        }
                    }
                }
            }
    }

    private fun setAssigneListener() {
        binding.addNewTaskAssignee.cellValueChangeListener =
            object : CellValueChangeListener<Int>() {
                override fun cellChangeHandler(choice: Int?) {
                    removeFocusFromDescription()
                    choice?.let {
                        if (it >= 0) {
                            wasTaskModified = true
                            task.userId = if (it == 0) viewModel.user.id else 0
                            task.statusId = if (it == 0) 2 else 1
                        }
                    }
                }
            }
    }

    private fun setTypeListener() {
        binding.addNewTaskTypeOption.cellValueChangeListener =
            object : CellValueChangeListener<Int>() {
                override fun cellChangeHandler(choice: Int?) {
                    removeFocusFromDescription()
                    choice?.let {
                        if (choice >= 0) {
                            wasTaskModified = true
                            val option = binding.addNewTaskTypeOption.valueOptions[choice]
                            val key =
                                taskViewModel.taskTypes.filterValues { it == option }.keys.first()
                            task.typeId = key
                        } else {
                            task.typeId = null
                        }
                    }
                    binding.addNewTaskTypeOption.isErrorEnabled = false
                }
            }
    }

    private fun setDescriptionListener() {
        binding.addNewTaskDescription.cellValueChangeListener =
            object : CellValueChangeListener<CharSequence>() {
                override fun cellChangeHandler(value: CharSequence?) {
                    value?.let {
                        val description = it.toString().trim()
                        if (description.isNotBlank()) {
                            task.description = description
                            wasTaskModified = true
                            binding.addNewTaskDescription.isErrorEnabled = false
                        }
                    }
                }
            }
    }

    private fun showSnackBar(isListEmpty: Boolean) {
        val message =
            if (isListEmpty) R.string.message_products_removed else R.string.message_products_selected
        createCustomSnackbar(
            requireView(), getString(message), 2000, R.drawable.ic_check,
        ).show()
    }

    private fun navigateToProductList() {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        val productListFragment = TaskProductListFragment()
        transaction.replace(R.id.container, productListFragment)
        transaction.addToBackStack("ProductList")
        transaction.commit()
    }

    private fun setProductsDisplayValue() {
        if (viewModel.products.value.isNullOrEmpty()) {
            binding.addNewTaskProducts.setDisplayValue("Select Products")
            binding.addNewTaskProducts.setDisplayValueTextAppearance(R.style.ProductsNotSelectedInput)
            Log.d(_tag, "Setting products display value to: Select Products")
        } else {
            val message = getString(R.string.message_products_selected)
            binding.addNewTaskProducts.setDisplayValue("${viewModel.products.value?.size} $message")
            binding.addNewTaskProducts.setDisplayValueTextAppearance(R.style.ProductsSelectedInput)
            Log.d(
                _tag,
                "Setting products display value to: ${viewModel.products.value?.size} $message"
            )
        }
    }

    private fun setupToolbar() {
        taskToEdit?.let { binding.addNewTaskToolbar.title = getString(R.string.title_edit_task) }
        binding.addNewTaskToolbar.setNavigationOnClickListener { cancelAction() }
        binding.addNewTaskEditButton.setOnClickListener { editTask(it) }
    }

    private fun setFooterButtonsOnClickListener() {
        binding.addNewTaskFooterButtons.setPrimaryActionClickListener { createTask(it) }
        binding.addNewTaskFooterButtons.setSecondaryActionClickListener { dismiss() }
    }

    private fun createTask(view: View) {
        if (validateRequiredFields()) {
            Log.d(_tag, "Creating Task")
            viewModel.createTask(task, volleyResponseHandler(view))
        }
    }

    private fun editTask(view: View) {
        if (validateRequiredFields()) {
            Log.d(_tag, "Updating Task")
            viewModel.editTask(task, volleyResponseHandler(view, true))
        }
    }

    private fun volleyResponseHandler(view: View, isEdition: Boolean = false) =
        object : VolleyRequestListener {
            override fun onError(message: String) {
                Log.d(_tag, "volleyResponseHandler#onError# $message")
                Snackbar.make(view, message, 2000).show()
            }

            override fun onResponse(response: String) {
                Log.d(
                    _tag,
                    "volleyResponseHandler#onResponse# Task $response ${if (isEdition) "Updated" else "Created"}"
                )
                createCustomSnackbar(
                    fragment.requireView(),
                    if (!isEdition) getString(R.string.message_task_created) else getString(R.string.message_task_saved),
                    2000,
                    R.drawable.ic_check,
                    getString(R.string.action_text_undo)
                ) {
                    undoClickListener()
                }.show()
                close()
            }
        }

    private fun undoClickListener() {
        val ft = fragment.parentFragmentManager.beginTransaction()
        val prev = fragment.parentFragmentManager.findFragmentByTag("add_task")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        val dialog = AddNewTaskFragment(task, fragment)
        Log.d(_tag, "undoClickListener#UndoClicked# Navigating to edit task: $task")
        dialog.show(fragment.parentFragmentManager, "add_task")
    }

    private fun validateRequiredFields(): Boolean {
        Log.d(_tag, "Validating required fields")
        var valid = true
        fun activateError(field: InlineValidation) {
            field.isErrorEnabled = true
            field.error = getString(R.string.error_require_message)
            valid = false
        }
        if (binding.addNewTaskDueDate.isErrorEnabled) {
            binding.addNewTaskDueDate.performClick()
            valid = false
        }
        if (binding.addNewTaskTypeOption.value < 0) {
            Log.d(_tag, "Task type NOT selected")
            activateError(binding.addNewTaskTypeOption)
        }
        if (binding.addNewTaskDescription.value.isNullOrBlank()) {
            Log.d(_tag, "Description is empty")
            activateError(binding.addNewTaskDescription)
        }

        return valid

    }

    private fun <T> cellChangeValue() =
        object : FormCell.CellValueChangeListener<T>() {
            override fun cellChangeHandler(value: T?) {
                removeFocusFromDescription()
                value
            }

            override fun updatedDisplayText(value: T?): CharSequence? {
                return if (value is Date) {
                    Log.d(_tag, "DATE_PICKER")
                    val df = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    df.format(value)
                } else {
                    Log.d(_tag, "CHOICE_PICKER")
                    super.updatedDisplayText(value)
                }
            }
        }

    private fun removeFocusFromDescription() {
        if (binding.addNewTaskDescription.hasFocus()) {
            hideKeyboard()
            binding.addNewTaskDescription.clearFocus()
        }
    }


    private fun parseTaskFromTaskModel(taskModel: Task): com.example.dsi_android_ui.task_fragment.Task {
        Log.d("TaskDetails", "parseTaskFromTaskModel#TASK_MODEL: $taskModel")
        val typeString: String = taskViewModel.taskTypes[taskModel.typeId] ?: "type"
        val priorityString: String =
            taskViewModel.taskPriorities[taskModel.priorityId] ?: "priority"
        val statusString: String = taskViewModel.taskStatus[taskModel.statusId] ?: "status"
        val remaining: String = service.getRemaining(Date(), taskModel.getDueDateAsDate())

        val task = com.example.dsi_android_ui.task_fragment.Task()
        task.active = taskModel.active != 0
        task.activeId = taskModel.active
        task.description = taskModel.description
        task.dueDate = taskModel.getDueDateAsDate()
        task.dueTimeId = taskModel.dueTimeId
        if (taskModel.id != null) task.id = taskModel.id!!.toInt()
        task.status = statusString
        task.statusId = taskModel.statusId
        task.priority = priorityString
        task.priorityId = taskModel.priorityId
        task.type = typeString
        task.typeId = taskModel.typeId
        task.userId = taskModel.userId
        task.remaining = remaining
        task.products = taskModel.products
        Log.d("TaskDetails", "parseTaskFromTaskModel#TASK: $task")

        return task
    }

}