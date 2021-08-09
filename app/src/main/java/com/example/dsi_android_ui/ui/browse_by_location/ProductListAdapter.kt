package com.example.dsi_android_ui.ui.browse_by_location

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dsi_android_ui.R
import com.example.dsi_android_ui.databinding.ItemSelectableProductRowBinding
import com.example.dsi_android_ui.service.ProductModel

class ProductListAdapter(
    private val productList: List<ProductModel>,
    private val onRowClick: (ProductModel) -> Unit
) : RecyclerView.Adapter<ProductListAdapter.ProductRowViewHolder>() {

    class ProductRowViewHolder(
        private val binding: ItemSelectableProductRowBinding,
        private val onRowClick: (Int) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener { onRowClick(adapterPosition) }
        }

        fun bind(item: ProductModel) {
            binding.productByLocationRowName.text = item.product_name
            binding.productByLocationRowSku.text =
                itemView.context.getString(R.string.product_by_location_Gtin, item.gtin)
            binding.productRowStockCount.text = item.count.toString()

            if (item.count == 0) {
                binding.productRowStockCount.setTextColor(Color.RED)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductRowViewHolder {
        val inflater = LayoutInflater.from(parent.context);
        val binding = ItemSelectableProductRowBinding.inflate(inflater, parent, false)


        binding.productByLocationCheckbox.visibility = GONE
        binding.productRowStockSelectionContainer.visibility = GONE
        binding.selectedStock.visibility = GONE

        return ProductRowViewHolder(binding) {
            onRowClick(getItem(it))
        }
    }

    private fun getItem(position: Int): ProductModel {
        return productList[position]
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ProductRowViewHolder, position: Int) {
        holder.bind(productList[position])
    }

}