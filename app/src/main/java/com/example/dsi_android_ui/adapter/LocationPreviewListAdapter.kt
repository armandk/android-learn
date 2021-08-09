package com.example.dsi_android_ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dsi_android_ui.databinding.LocationListItemBinding
import com.example.dsi_android_ui.search_product.Location
import com.example.dsi_android_ui.utils.MAX_PREVIEW_LIST_LIMIT
import com.example.dsi_android_ui.utils.STATUS_LOW
import com.example.dsi_android_ui.utils.STATUS_MEDIUM
import java.util.*

class LocationPreviewListAdapter(
        private val locationList: MutableList<Location>,
        private val isPreview: Boolean,
        private val onLocationClick: (Location) -> Unit
) :
        RecyclerView.Adapter<LocationPreviewListAdapter.ViewHolder>() {

    class ViewHolder(val binding: LocationListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
            parent: ViewGroup, viewType: Int
    ): ViewHolder {
        return ViewHolder(
                LocationListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val loc = locationList[position]
        val locStr = "${loc.aisle}-${loc.shelf}-${loc.section}-${loc.bin}"
        holder.binding.fullLocation.text = locStr
        holder.binding.productDepartment.text = loc.info
        holder.binding.locationCount.text = loc.count.toString()
        when (loc.status.toLowerCase(Locale.getDefault())) {
            STATUS_LOW.toLowerCase(Locale.getDefault()) -> {
                holder.binding.locationStatus.setTextColor(Color.RED)
                holder.binding.locationStatus.text = loc.status
            }
            STATUS_MEDIUM.toLowerCase(Locale.getDefault()) -> {
                holder.binding.locationStatus.setTextColor(Color.YELLOW)
                holder.binding.locationStatus.text = loc.status
            }
            else -> {
                holder.binding.locationStatus.text = ""
            }
        }
        holder.binding.fullLocation.setOnClickListener { onLocationClick(loc) }
    }

    override fun getItemCount(): Int {
        return if (isPreview && locationList.size > MAX_PREVIEW_LIST_LIMIT) MAX_PREVIEW_LIST_LIMIT else locationList.size
    }
}
