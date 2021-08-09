package com.example.dsi_android_ui.ui.breadcrumbs

import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.dsi_android_ui.databinding.BreadcrumbItemBinding
import com.example.dsi_android_ui.databinding.BreadcrumbLinkItemBinding

class BreadcrumbsAdapter(
        var breadcrumbItems: List<String> = emptyList(),
        private val onItemClicked: (Int, String) -> Unit
) : RecyclerView.Adapter<BreadcrumbsAdapter.ItemHolder<String>>() {

    companion object {
        private const val TYPE_TEXT = 0
        private const val TYPE_LINK = 1
        private const val TYPE_DIVIDER = 2
    }

    abstract class ItemHolder<T>(viewBinding: ViewBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        abstract fun bind(item: T)
    }

    inner class BreadcrumbItemHolder(private val binding: BreadcrumbItemBinding) : ItemHolder<String>(binding) {
        override fun bind(item: String) {
            binding.breadcrumbItemText.text = item
        }
    }

    inner class BreadcrumbLinkHolder(
            private val binding: BreadcrumbLinkItemBinding,
            private val onItemClick: (Int) -> Unit
    ) : ItemHolder<String>(binding) {
        init {
            binding.breadcrumbLinkItemText.let {
                it.movementMethod = LinkMovementMethod.getInstance()
                it.setOnClickListener { onItemClick(adapterPosition) }
            }
        }

        override fun bind(item: String) {
            binding.breadcrumbLinkItemText.text = item
        }
    }

    inner class BreadcrumbDividerHolder(binding: BreadcrumbItemBinding) : ItemHolder<String>(binding) {
        init {
            binding.breadcrumbItemText.let {
                it.text = "/"
                it.setPadding(10, 0, 10, 0)
            }
        }

        override fun bind(item: String) {}
    }

    private fun getActualItemPosition(viewType: Int, position: Int): Int {
        return when (viewType) {
            TYPE_DIVIDER -> (position - 1) / 2 + 1
            else -> position / 2
        }
    }

    private fun getItem(position: Int): String {
        return breadcrumbItems[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder<String> {
        val inflater = LayoutInflater.from(parent.context);

        return when (viewType) {
            TYPE_TEXT -> {
                val binding = BreadcrumbItemBinding.inflate(inflater, parent, false)
                BreadcrumbItemHolder(binding)
            }
            TYPE_LINK -> {
                val binding = BreadcrumbLinkItemBinding.inflate(inflater, parent, false)
                BreadcrumbLinkHolder(binding) {
                    val actualPosition = getActualItemPosition(viewType, it)
                    onItemClicked(actualPosition, getItem(actualPosition))
                }
            }
            TYPE_DIVIDER -> {
                val binding = BreadcrumbItemBinding.inflate(inflater, parent, false)
                BreadcrumbDividerHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid type of data $viewType")
        }
    }

    override fun onBindViewHolder(holder: ItemHolder<String>, position: Int) {
        val viewType = getItemViewType(position)
        val actualPosition = getActualItemPosition(viewType, position)
        holder.bind(getItem(actualPosition))
    }

    override fun getItemCount(): Int {
        return (breadcrumbItems.size * 2) - 1
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            (position == itemCount - 1) -> TYPE_TEXT
            (position % 2 == 1) -> TYPE_DIVIDER
            else -> TYPE_LINK
        }
    }

}