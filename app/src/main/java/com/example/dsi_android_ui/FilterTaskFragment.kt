package com.example.dsi_android_ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dsi_android_ui.databinding.FilterTaskFragmentBinding
import com.example.dsi_android_ui.task_fragment.TaskFragmentViewModel
import com.example.dsi_android_ui.task_fragment.TaskFragmentViewModelFactory
import com.example.dsi_android_ui.utils.DateHelper
import com.example.dsi_android_ui.utils.FILTERS_ASSIGNEE
import com.example.dsi_android_ui.utils.FILTERS_DUE_DATE
import com.example.dsi_android_ui.utils.FILTERS_MINE
import com.example.dsi_android_ui.utils.FILTERS_PRIORITY
import com.example.dsi_android_ui.utils.FILTERS_STATUS
import com.example.dsi_android_ui.utils.FILTERS_TYPE
import com.example.dsi_android_ui.utils.FILTER_DUE_DATE_ERROR_MESSAGE
import com.example.dsi_android_ui.utils.SORT_CATEGORY
import com.example.dsi_android_ui.utils.SORT_CATEGORY_VALUES
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class FilterTaskFragment : Fragment() {

    private val dateHelper = DateHelper()

    companion object {
        fun newInstance() = FilterTaskFragment()
    }

    private lateinit var viewModel: TaskFragmentViewModel
    private var _binding: FilterTaskFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FilterTaskFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        initViewElements()
        return view

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val provider = TaskFragmentViewModelFactory()
        viewModel =
            ViewModelProvider(requireActivity(), provider).get(TaskFragmentViewModel::class.java)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initViewElements() {

        binding.filterTaskType.setValueOptions(viewModel.allTaskType)
        binding.filterTaskStatus.setValueOptions(viewModel.allTaskStatus)
        binding.filterTaskPriority.setValueOptions(viewModel.allTaskPriority)
        binding.sortTaskCategory.valueOptions = SORT_CATEGORY_VALUES

        //TODO: Automate like others once User data is available
        binding.filterTaskAssignee.setValueOptions(Array(1) { FILTERS_MINE })

        /*TODO: Remove this function once the taskTypes metadata is corrected in the API
        *  or once all the Task types in the API are needed to be enabled*/
        binding.filterTaskType.setValueOptions(Array(1) { "Product Movement" })

        initFilterTaskButtonListeners()
        applyFilters()
    }

    private fun applyFilters() {
        val filters = viewModel.appliedFilters
        if (filters.isNotEmpty()) {
            if (filters.containsKey(FILTERS_TYPE))
                binding.filterTaskType.value =
                    viewModel.getSelectedFilterValues(
                            binding.filterTaskType.valueOptions,
                            filters[FILTERS_TYPE]
                    )
            if (filters.containsKey(FILTERS_STATUS))
                binding.filterTaskStatus.value =
                    viewModel.getSelectedFilterValues(
                            binding.filterTaskStatus.valueOptions,
                            filters[FILTERS_STATUS]
                    )
            if (filters.containsKey(FILTERS_PRIORITY))
                binding.filterTaskPriority.value =
                    viewModel.getSelectedFilterValues(
                            binding.filterTaskPriority.valueOptions,
                            filters[FILTERS_PRIORITY]
                    )
            if (filters.containsKey(FILTERS_ASSIGNEE))
                binding.filterTaskAssignee.value =
                    viewModel.getSelectedFilterValues(
                            binding.filterTaskAssignee.valueOptions,
                            filters[FILTERS_ASSIGNEE]
                    )
            if (filters.containsKey(FILTERS_DUE_DATE))
                setSelectedDates(filters[FILTERS_DUE_DATE])
            val sortValue = getSelectedSortValue(
                    binding.sortTaskCategory.valueOptions, filters[SORT_CATEGORY]
            )
            if (filters.containsKey(SORT_CATEGORY) && sortValue >= 0)
                binding.sortTaskCategory.value = sortValue
        }
    }

    private fun getSelectedSortValue(valueOptions: Array<String>, filters: List<String>?): Int {
        var sortIndex = -1
        if (null != filters && filters.size == 1) {
            valueOptions.indexOf(filters[0]).let { sortIndex = it }
        }
        return sortIndex
    }

    private fun setSelectedDates(filters: List<String>?) {
        if (filters != null && filters.size == 2) {
            binding.filterDueDateUnselected.visibility = View.GONE
            binding.filterDueDateSelected.visibility = View.VISIBLE

            binding.filterTaskDueDateStart.value = dateHelper.convertToDate(filters[0])
            binding.filterTaskDueDateEnd.value = dateHelper.convertToDate(filters[1])
        }
    }

    private fun initFilterTaskButtonListeners() {
        binding.filterApplyBtn.setOnClickListener {
            if (!isFilterValidationError()) {
                onClickApply()
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.remove(this)
                transaction.commit()
            }
        }
        binding.filterDueDateUnselected.setOnClickListener {
            binding.filterDueDateUnselected.visibility = View.GONE
            binding.filterDueDateSelected.visibility = View.VISIBLE

        }
        binding.filterDueDateClear.setOnClickListener {
            binding.filterDueDateUnselected.visibility = View.VISIBLE
            binding.filterDueDateSelected.visibility = View.GONE
            binding.filterTaskDueDateStart.value = Date()
            binding.filterTaskDueDateEnd.value = Date()
            binding.filterTaskDueDateEnd.isErrorEnabled = false
        }
        binding.closeBtn.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.remove(this)
            transaction.commit()
        }
    }

    private fun onClickApply() {
        val filters = HashMap<String, List<String>>()
        filters[FILTERS_TYPE] =
            getSelectedFilterChips(
                    binding.filterTaskType.valueOptions,
                    binding.filterTaskType.value
            )
        filters[FILTERS_STATUS] =
            getSelectedFilterChips(
                    binding.filterTaskStatus.valueOptions,
                    binding.filterTaskStatus.value
            )
        filters[FILTERS_PRIORITY] =
            getSelectedFilterChips(
                    binding.filterTaskPriority.valueOptions,
                    binding.filterTaskPriority.value
            )
        filters[FILTERS_ASSIGNEE] =
            getSelectedFilterChips(
                    binding.filterTaskAssignee.valueOptions,
                    binding.filterTaskAssignee.value
            )
        filters[FILTERS_DUE_DATE] = getSelectedDueDateFilter()
        filters[SORT_CATEGORY] =
            getSelectedChoiceChip(
                    binding.sortTaskCategory.valueOptions,
                    binding.sortTaskCategory.value
            )
        viewModel.applyFilters(filters)
    }

    private fun getSelectedChoiceChip(
            valueOptions: Array<String>,
            selection: Int
    ): ArrayList<String> {
        val selectedValueOptions = ArrayList<String>()
        if (selection >= 0)
            selectedValueOptions.add(valueOptions[selection])
        return selectedValueOptions
    }

    private fun getSelectedFilterChips(
            valueOptions: Array<String>?,
            selections: MutableList<Int>
    ): ArrayList<String> {
        val selectedValueOptions = ArrayList<String>()
        if (valueOptions != null) {
            for (selection in selections) {
                selectedValueOptions.add(valueOptions[selection])
            }
        }
        return selectedValueOptions
    }

    private fun getSelectedDueDateFilter(): ArrayList<String> {
        val selectedDueDateRange = ArrayList<String>()
        if (binding.filterDueDateSelected.visibility == View.VISIBLE) {
            selectedDueDateRange.add(binding.filterTaskDueDateStart.value?.let {
                dateHelper.convertToString(
                        it
                )
            }
                    .toString())
            selectedDueDateRange.add(binding.filterTaskDueDateEnd.value?.let {
                dateHelper.convertToString(
                        it
                )
            }
                    .toString())
        }
        return selectedDueDateRange
    }

    private fun isFilterValidationError(): Boolean {
        binding.filterTaskDueDateEnd.isErrorEnabled =
            binding.filterTaskDueDateStart.value!! > binding.filterTaskDueDateEnd.value
        binding.filterTaskDueDateEnd.error = FILTER_DUE_DATE_ERROR_MESSAGE
        return binding.filterTaskDueDateEnd.isErrorEnabled

    }
}