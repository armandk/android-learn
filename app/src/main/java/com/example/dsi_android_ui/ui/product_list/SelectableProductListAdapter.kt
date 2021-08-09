package com.example.dsi_android_ui.ui.product_list

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.dsi_android_ui.R
import com.example.dsi_android_ui.databinding.ItemSelectableProductRowBinding
import com.example.dsi_android_ui.databinding.ItemSelectableProductListHeaderBinding

class SelectableProductListAdapter(
    private val selectableList: List<SelectableProduct> = emptyList(),
    private val showDividedLists: Boolean = true
) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    private var selectedList: List<SelectableProduct> = emptyList()
    private var nonSelectedList: List<SelectableProduct> = when {
        showDividedLists -> selectableList.toList()
        else -> emptyList()
    }

    val productSelected = MutableLiveData<SelectableProduct>()
    val productDeselected = MutableLiveData<SelectableProduct>()
    val productClicked = MutableLiveData<SelectableProduct>()
    val productStockSelected = MutableLiveData<Pair<Int, SelectableProduct>>()

    companion object {
        const val TYPE_SELECTED_HEADER = 0
        const val TYPE_SELECTED_ITEM = 1
        const val TYPE_ALL_HEADER = 2
        const val TYPE_NON_SELECTED_ITEM = 3
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TYPE_SELECTED_HEADER, TYPE_ALL_HEADER -> {
                val binding =
                    ItemSelectableProductListHeaderBinding.inflate(inflater, parent, false)
                HeaderViewHolder(binding)
            }
            TYPE_SELECTED_ITEM -> {
                val binding = ItemSelectableProductRowBinding.inflate(inflater, parent, false)
                SelectedProductViewHolder(
                    binding,
                    this::onDeselectProduct,
                    this::onClickProduct,
                    this::onSelectStockCount
                )
            }
            TYPE_NON_SELECTED_ITEM -> {
                val binding = ItemSelectableProductRowBinding.inflate(inflater, parent, false)
                NonSelectedProductViewHolder(binding, this::onSelectProduct)
            }
            else -> throw IllegalArgumentException("Invalid type of data $viewType")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                if (position == 0) {
                    holder.bind(R.string.selected)
                } else {
                    holder.bind(R.string.all_items)
                }
            }
            is SelectedProductViewHolder -> {
                holder.bind(getSelectedItem(position))
            }
            is NonSelectedProductViewHolder -> {
                holder.bind(getNonSelectedItem(position))
            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemViewType(position: Int): Int = when {
        showDividedLists -> {
            when {
                position == 0 -> TYPE_SELECTED_HEADER
                position in 1..selectedList.size -> TYPE_SELECTED_ITEM
                position > (selectedList.size + 1) -> TYPE_NON_SELECTED_ITEM
                else -> TYPE_ALL_HEADER
            }
        }
        else -> {
            val possibleSelectedItem = getItem(position)
            when (selectedList.indexOf(possibleSelectedItem)) {
                -1 -> TYPE_NON_SELECTED_ITEM
                else -> TYPE_SELECTED_ITEM
            }
        }
    }

    override fun getItemCount(): Int = when {
        showDividedLists -> selectableList.size + 2
        else -> selectableList.size
    }

    private fun getItem(position: Int): SelectableProduct {
        return selectableList[position]
    }

    private fun getSelectedItem(position: Int): SelectableProduct = when {
        showDividedLists -> selectedList[position - 1]
        else -> getItem(position)
    }

    private fun getNonSelectedItem(position: Int): SelectableProduct = when {
        showDividedLists -> nonSelectedList[position - selectedList.size - 2]
        else -> getItem(position)
    }

    private fun onSelectProduct(position: Int) {
        val previouslyNonSelectedItem = getNonSelectedItem(position)
        productSelected.postValue(previouslyNonSelectedItem)
    }

    private fun onDeselectProduct(position: Int) {
        val previouslySelectedItem = getSelectedItem(position)
        productDeselected.postValue(previouslySelectedItem)
    }

    private fun onClickProduct(position: Int) {
        val selectedItem = getSelectedItem(position)
        productClicked.postValue(selectedItem)
    }

    /**
     * Note: In order to set initial selected items, setSelected must be called
     */
    fun setSelected(newSelectedList: List<SelectableProduct>) {
        selectedList = newSelectedList

        if (showDividedLists) {
            // Add/remove selected item by filtering original list to preserve order
            nonSelectedList = selectableList.filter {
                !selectedList.contains(it)
            }
        }

        notifyDataSetChanged()
    }

    private fun onSelectStockCount(selectedStock: Int, position: Int) {
        val selectedItem = getSelectedItem(position)
        productStockSelected.postValue(Pair(selectedStock, selectedItem))
    }

}
