package com.example.dsi_android_ui.location

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.example.dsi_android_ui.R
import com.example.dsi_android_ui.service.LocationModel
import kotlin.collections.ArrayList

class LocationListAdapter(private val ctx: Context, private  var data: List<LocationModel>)
    : ArrayAdapter<LocationModel>(ctx, R.layout.loc_list_item, data) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(ctx)
                .inflate(R.layout.loc_list_item, parent, false)
        val textView: TextView = view.findViewById(R.id.item_name)

        val locationModel = data.get(position)
        textView.text = locationModel.name

        return view
    }
    
    fun setData(newData: List<LocationModel>){
        clear()
        addAll(newData)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(chars: CharSequence?): FilterResults {
                val filterList = data
                        .filter { key -> key.name.contains(chars!!, true) }

                return object : FilterResults(){
                    init {
                        values = filterList
                        count = filterList.size;
                    }
                }
            }

            override fun publishResults(p0: CharSequence?, filterResults: FilterResults?) {
                setData(filterResults!!.values as ArrayList<LocationModel>)
            }
        }
    }
}
