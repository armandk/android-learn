package com.example.dsi_android_ui.ui.browse_by_location

import android.view.LayoutInflater
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dsi_android_ui.databinding.BrowseByLocationRowBinding
import com.example.dsi_android_ui.service.LocationModel

class BrowseByLocationListAdapter(
        private val locationList: List<LocationModel>,
        private val onRowClick: (LocationModel) -> Unit
) : RecyclerView.Adapter<BrowseByLocationListAdapter.LocationRowViewHolder>() {

    class LocationRowViewHolder(
            private val binding: BrowseByLocationRowBinding,
            private val onRowClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener { onRowClick(adapterPosition) }
        }

        fun bind(item: LocationModel) {
            binding.browseByLocationRowName.text = item.name

            when (item.productCount) {
                0 -> {
                    binding.browseByLocationRowProductCount.visibility = INVISIBLE
                    binding.browseByLocationRowProductCount.text = ""
                }
                else -> {
                    binding.browseByLocationRowProductCount.visibility = VISIBLE
                    binding.browseByLocationRowProductCount.text = item.productCount.toString()
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationRowViewHolder {
        val inflater = LayoutInflater.from(parent.context);
        val binding = BrowseByLocationRowBinding.inflate(inflater, parent, false)

        return LocationRowViewHolder(binding) {
            onRowClick(getItem(it))
        }
    }

    private fun getItem(position: Int): LocationModel {
        return locationList[position]
    }

    override fun getItemCount(): Int {
        return locationList.size
    }

    override fun onBindViewHolder(holder: LocationRowViewHolder, position: Int) {
        holder.bind(locationList[position])
    }

}