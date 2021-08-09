package com.example.dsi_android_ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dsi_android_ui.R
import com.example.dsi_android_ui.databinding.ItemSelectableProductRowBinding
import com.example.dsi_android_ui.search_product.ProductVariant
import com.example.dsi_android_ui.utils.MAX_PREVIEW_LIST_LIMIT

class ProductVariantListAdapter(
        private val productList: List<ProductVariant>, private val isPreview: Boolean,
        private val onRowClick: (ProductVariant) -> Unit
) : RecyclerView.Adapter<ProductVariantListAdapter.ProductRowViewHolder>() {

    class ProductRowViewHolder(
            private val binding: ItemSelectableProductRowBinding,
            private val onRowClick: (Int) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener { onRowClick(adapterPosition) }
        }

        fun bind(item: ProductVariant) {
            binding.productByLocationRowName.text = item.productName
            binding.productByLocationRowSku.text =
                itemView.context.getString(R.string.product_by_location_Gtin, item.gtin)
            binding.productRowStockCount.text = item.count.toString()

            if (item.count == 0) {
                binding.productRowStockCount.setTextColor(Color.RED)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductRowViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSelectableProductRowBinding.inflate(inflater, parent, false)

        binding.productByLocationCheckbox.visibility = GONE
        binding.productRowStockSelectionContainer.visibility = GONE

        return ProductRowViewHolder(binding) {
            onRowClick(getItem(it))
        }
    }

    private fun getItem(position: Int): ProductVariant {
        return productList[position]
    }

    override fun getItemCount(): Int {
        return if (isPreview && productList.size > MAX_PREVIEW_LIST_LIMIT) MAX_PREVIEW_LIST_LIMIT else productList.size

    }

    override fun onBindViewHolder(holder: ProductRowViewHolder, position: Int) {
        holder.bind(productList[position])
    }

}