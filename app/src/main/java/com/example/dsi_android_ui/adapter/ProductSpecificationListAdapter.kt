package com.example.dsi_android_ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dsi_android_ui.databinding.ProductSpecificationListItemBinding
import com.example.dsi_android_ui.search_product.ProductSpecification
import com.example.dsi_android_ui.utils.MAX_PREVIEW_LIST_LIMIT

class ProductSpecificationListAdapter(
        private val specifications: MutableList<ProductSpecification>,
        private val isPreview: Boolean
) :
        RecyclerView.Adapter<ProductSpecificationListAdapter.ViewHolder>() {

    class ViewHolder(val binding: ProductSpecificationListItemBinding) :
            RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
            parent: ViewGroup, viewType: Int
    ): ViewHolder {
        return ViewHolder(
                ProductSpecificationListItemBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val specification = specifications[position]
        val specificationName = specification.name + ":"
        holder.binding.specName.text = specificationName
        holder.binding.specValue.text = specification.value
    }

    override fun getItemCount(): Int {
        return if (isPreview && specifications.size > MAX_PREVIEW_LIST_LIMIT) MAX_PREVIEW_LIST_LIMIT else specifications.size
    }
}
