package com.example.dsi_android_ui.location

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dsi_android_ui.R
import com.example.dsi_android_ui.adapter.RecyclerviewOnClickListener
import com.example.dsi_android_ui.service.LocationModel

abstract class MyLocationAdpater(var arrayListOf: List<LocationModel>) : RecyclerView.Adapter<MyLocationAdpater.ViewHolder>(), RecyclerviewOnClickListener {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView

        val contetntView : View
        init {
            // Define click listener for the ViewHolder's View.
            textView = view.findViewById(R.id.item_name)
            contetntView =view.findViewById(R.id.content_layout)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyLocationAdpater.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.loc_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return arrayListOf.size
    }

    fun setData(list: List<LocationModel>){

        arrayListOf =list
        notifyDataSetChanged();
    }

    override fun onBindViewHolder(holder: MyLocationAdpater.ViewHolder, position: Int) {

        holder.textView.setText(arrayListOf.get(position).name)

        holder.contetntView.setOnClickListener { recyclerviewClick(position) }
    }

}
