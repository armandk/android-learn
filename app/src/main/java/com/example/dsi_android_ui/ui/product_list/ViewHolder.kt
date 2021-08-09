package com.example.dsi_android_ui.ui.product_list

import android.graphics.Color
import android.view.View
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.dsi_android_ui.R
import com.example.dsi_android_ui.databinding.ItemSelectableProductListHeaderBinding
import com.example.dsi_android_ui.databinding.ItemSelectableProductRowBinding

abstract class BaseViewHolder<T>(viewBinding: ViewBinding) :
    RecyclerView.ViewHolder(viewBinding.root) {
    abstract fun bind(item: T)
}

class HeaderViewHolder(val binding: ItemSelectableProductListHeaderBinding) :
    BaseViewHolder<Int>(binding) {
    override fun bind(item: Int) {
        binding.selectableProductListHeader.text = itemView.context.getString(item)
    }
}

open class NonSelectedProductViewHolder(
    val binding: ItemSelectableProductRowBinding,
    onClickCheckbox: (position: Int) -> Unit
) : BaseViewHolder<SelectableProduct>(binding) {
    init {
        binding.productByLocationCheckbox.setOnClickListener {
            onClickCheckbox(adapterPosition)
        }
    }

    override fun bind(item: SelectableProduct) {
        val product = item.product

        binding.productByLocationRowName.text = product.product_name
        binding.productByLocationRowSku.text =
            itemView.context.getString(R.string.product_by_location_Gtin, product.gtin)
        binding.productRowStockCount.text = product.count.toString()

        if (product.count == 0) {
            binding.productRowStockCount.setTextColor(Color.RED)
        }

        binding.productByLocationCheckbox.isChecked = false
        binding.productRowStockSelectionContainer.visibility = View.GONE
    }
}

class SelectedProductViewHolder(
    binding: ItemSelectableProductRowBinding,
    onClickCheckbox: (position: Int) -> Unit,
    private val onClickProduct: (position: Int) -> Unit,
    private val onSelectStockCount: (selectedCount: Int, position: Int) -> Unit
) : NonSelectedProductViewHolder(binding, onClickCheckbox) {
    private val mutableStockSelection = mutableListOf<Int>()
    private val stockSelectionAdapter =
        ArrayAdapter(itemView.context, R.layout.dropdown_item_list, mutableStockSelection)

    init {
        itemView.setOnClickListener { onClickProduct(adapterPosition) }

        binding.selectedStock.setAdapter(stockSelectionAdapter)
    }

    override fun bind(item: SelectableProduct) {
        super.bind(item)

        // TODO - custom to SelectProductsFragment fragment
        val numOfLocations = item.product.Locations.size

        if (numOfLocations > 1) {
            binding.productRowStockSelectionContainer.visibility = View.GONE
            binding.productRowRightIcon.visibility = View.VISIBLE

            binding.productRowStockCount.text = item.selectedCount.toString()
        } else {
            binding.productRowStockSelectionContainer.visibility = View.VISIBLE
            binding.productRowRightIcon.visibility = View.GONE

            binding.selectedStock.setText(item.selectedCount.toString(), false)
            binding.selectedStock.setOnItemClickListener { _, _, position, _ ->
                val value = stockSelectionAdapter.getItem(position)
                onSelectStockCount(value!!, adapterPosition)
            }

            // NOTE: a selected product's total count should never be zero
            updateStockSelectionBasedOnCount(item.product.count)
        }

        binding.productByLocationCheckbox.isChecked = true
    }

    private fun updateStockSelectionBasedOnCount(maxCount: Int) {
        val newPossibleStockSelection = List(maxCount) { index -> index + 1 }

        mutableStockSelection.clear()
        mutableStockSelection.addAll(newPossibleStockSelection)

        stockSelectionAdapter.notifyDataSetChanged()
    }
}
