package com.example.dsi_android_ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.dsi_android_ui.databinding.FragmentConflictResolutionLocationBinding
import com.example.dsi_android_ui.ui.browse_by_location.ProductLocationListAdapter
import com.example.dsi_android_ui.ui.browse_by_location.SelectableLocationDetails
import com.example.dsi_android_ui.ui.product_list.SelectableProductsViewModel

class ConflictResolutionLocationFragment : BaseFragment() {
    private val selectableProductsViewModel: SelectableProductsViewModel by activityViewModels()

    private var _binding: FragmentConflictResolutionLocationBinding? = null
    private val binding get() = _binding!!

    private lateinit var selectableLocationList: List<SelectableLocationDetails>
    private val selectedList = mutableListOf<SelectableLocationDetails>()
    private var adapter: ProductLocationListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConflictResolutionLocationBinding.inflate(inflater, container, false)

        // NOTE: Assumes previously set
        // currently no single API to return filtered out locations within product details
        val currentSelectableProduct =
            selectableProductsViewModel.currentConflictResolutionProduct!!
        val product = currentSelectableProduct.product
        val previouslySelected = selectableProductsViewModel.getCurrentSelectedConflictResolution()
            .fold(mutableMapOf<String, Int>(), { acc, item ->
                acc[item.LocationPath] = item.Count
                acc
            })

        selectableLocationList = product.Locations.map { locationDetails ->
            val possiblePreviousCount = previouslySelected[locationDetails.LocationPath]
            val newSelectableLocation =
                SelectableLocationDetails(locationDetails, possiblePreviousCount ?: 1)

            if (possiblePreviousCount != null) {
                selectedList.add(newSelectableLocation)
            }

            newSelectableLocation
        }

        binding.toolbar.title = "Locations (" + selectableLocationList.size + ")"
        binding.toolbar.navigationIcon = resources.getDrawable(R.drawable.ic_arrow_back_24dp, null)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.locationList.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        adapter = ProductLocationListAdapter(
            product,
            selectableLocationList,
            selectedList,
            this::onRowSelected,
            this::onRowDeselected,
            this::onSelectedCountChanged
        )
        binding.locationList.adapter = adapter

        binding.cancelButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.btnDone.setOnClickListener {
            selectableProductsViewModel.applyConflictResolution(selectedList.map {
                it.LocationDetails.copy(Count = it.selectedCount)
            })
            requireActivity().onBackPressed()
        }

        return binding.root
    }

    private fun onRowSelected(selectableLocationDetails: SelectableLocationDetails) {
        selectedList.add(selectableLocationDetails)
        onUpdateSelectedList()
    }

    private fun onRowDeselected(selectableLocationDetails: SelectableLocationDetails) {
        selectedList.remove(selectableLocationDetails)
        onUpdateSelectedList()
    }

    private fun onSelectedCountChanged(
        selectableLocationDetails: SelectableLocationDetails,
        newCount: Int
    ) {
        selectableLocationDetails.selectedCount = newCount
        onUpdateSelectedList()
    }

    private fun onUpdateSelectedList() {
        binding.btnDone.isEnabled = selectedList.size > 0
        adapter?.notifyDataSetChanged()
    }
}
