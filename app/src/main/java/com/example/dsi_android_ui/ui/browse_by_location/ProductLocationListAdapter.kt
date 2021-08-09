package com.example.dsi_android_ui.ui.browse_by_location

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dsi_android_ui.R
import com.example.dsi_android_ui.databinding.ConflictResolutionLocationListItemBinding
import com.example.dsi_android_ui.service.LocationDetailsModel
import com.example.dsi_android_ui.service.ProductModel

data class SelectableLocationDetails(
    val LocationDetails: LocationDetailsModel,
    var selectedCount: Int = 1,
)

class ProductLocationListAdapter(
    private val product: ProductModel,
    private val list: List<SelectableLocationDetails>,
    private val selectedList: List<SelectableLocationDetails>,
    private val onRowSelected: (SelectableLocationDetails) -> Unit,
    private val onRowDeselected: (SelectableLocationDetails) -> Unit,
    private val onRowCountChanged: (SelectableLocationDetails, Int) -> Unit,
) : RecyclerView.Adapter<ProductLocationListAdapter.ProductLocationRowViewHolder>() {

    inner class ProductLocationRowViewHolder(
        private val binding: ConflictResolutionLocationListItemBinding,
        private val onSelect: (Int) -> Unit,
        private val onDeselect: (Int) -> Unit,
        private val onChangeRowCount: (Int, Int) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        private val mutableStockSelection = mutableListOf<Int>()
        private val countAdapter =
            ArrayAdapter(itemView.context, R.layout.dropdown_item_list, mutableStockSelection)

        init {
            binding.conflictResolutionRowCountSelection.setAdapter(countAdapter)
        }

        fun bind(selectableLocationDetails: SelectableLocationDetails, isSelected: Boolean) {
            binding.fullLocation.text = selectableLocationDetails.LocationDetails.LocationPath
            binding.productDepartment.text = product.department
            binding.locationCount.text = selectableLocationDetails.LocationDetails.Count.toString()

            binding.checkbox.isChecked = isSelected
            binding.checkbox.setOnCheckedChangeListener { _, _ ->
                if (isSelected) {
                    onDeselect(adapterPosition)
                } else {
                    onSelect(adapterPosition)
                }
            }

            if (isSelected) {
                binding.conflictResolutionRowCountSelectionContainer.visibility = View.VISIBLE
                binding.editLocationRow.setBackgroundColor(Color.parseColor("#E5F0FA"))

                binding.conflictResolutionRowCountSelection.setText(
                    selectableLocationDetails.selectedCount.toString(),
                    false
                )
                binding.conflictResolutionRowCountSelection.setOnItemClickListener { _, _, position, _ ->
                    val value = countAdapter.getItem(position)
                    onChangeRowCount(adapterPosition, value!!)
                }

                updateStockSelectionBasedOnCount(selectableLocationDetails.LocationDetails.Count)
            } else {
                binding.conflictResolutionRowCountSelectionContainer.visibility = View.GONE
                binding.editLocationRow.setBackgroundColor(Color.WHITE)
            }

        }

        private fun updateStockSelectionBasedOnCount(maxCount: Int) {
            val newPossibleStockSelection = List(maxCount) { index -> index + 1 }

            mutableStockSelection.clear()
            mutableStockSelection.addAll(newPossibleStockSelection)

            countAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductLocationRowViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ConflictResolutionLocationListItemBinding.inflate(inflater, parent, false)

        return ProductLocationRowViewHolder(
            binding,
            this::onSelectRow,
            this::onDeselectRow,
            this::onChangeRowCount,
        )
    }

    private fun onSelectRow(position: Int) {
        onRowSelected(getItem(position))
    }

    private fun onDeselectRow(position: Int) {
        onRowDeselected(getItem(position))
    }

    private fun onChangeRowCount(position: Int, newCount: Int) {
        onRowCountChanged(getItem(position), newCount)
    }

    private fun getItem(position: Int): SelectableLocationDetails {
        return list[position]
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ProductLocationRowViewHolder, position: Int) {
        val currentItem = getItem(position)
        val isSelected = selectedList.contains(currentItem)
        holder.bind(getItem(position), isSelected)
    }
}