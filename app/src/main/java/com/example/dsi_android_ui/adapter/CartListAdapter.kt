package com.example.dsi_android_ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.dsi_android_ui.databinding.CartListItemBinding
import com.example.dsi_android_ui.models.CartItem
import java.util.*

class CartListAdapter(
        private var cartList: List<CartItem>,
        private val onItemSelected: (Boolean, CartItem) -> Unit,
        private val isEditMode: Boolean
) : RecyclerView.Adapter<CartListAdapter.CartViewHolder>() {
    class CartViewHolder(private var binding: CartListItemBinding, private val isEditMode: Boolean) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CartItem, onItemSelected: (Boolean, CartItem) -> Unit) {
            val tmpString = item.product_name ?: ""
            val c = tmpString.trim { it <= ' ' }
            val words = c.split(" ").toTypedArray()
            var result = ""
            for (w in words) {
                result += (if (w.length > 1) w.substring(0, 1).toUpperCase(Locale.US) + w.substring(1, w.length).toLowerCase(Locale.US) else w) + " "
            }
            binding.productName.text = result
            binding.productCount.text = item.count.toString()
            ("Gtin "+ item.sku).also { binding.productSku.text = it }
            binding.checkbox.isChecked = item.isSelected
            binding.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                item.isSelected = isChecked
                onItemSelected(isChecked, item)
            }
            if(isEditMode) {
                binding.checkbox.visibility = View.VISIBLE
            }else{
                binding.checkbox.visibility = View.GONE
            }
            val list = ArrayList<String>()
            for (i in 1 until item.count + 1) {
                list.add(i.toString())
            }
            binding.spinner.adapter = ArrayAdapter(binding.spinner.context, android.R.layout.simple_spinner_item, list)
            binding.spinner.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    item.selectedCount = list.get(position).toInt()
                    //binding.productCount.text = ((item.count - item.selectedCount).toString())
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
            var indexOf = list.indexOf(item.selectedCount.toString())
            if(indexOf == -1) indexOf = 0
            binding.spinner.setSelection(indexOf)

            if (item.isSelected) {
                binding.cartLayout.setBackgroundColor(Color.parseColor("#e6f9ff"))
                binding.spinnerLayout.visibility = View.VISIBLE
            } else {
                binding.cartLayout.setBackgroundColor(Color.WHITE)
                binding.spinnerLayout.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CartListItemBinding.inflate(inflater, parent, false)
        return CartViewHolder(binding,isEditMode)
    }


    override fun getItemCount(): Int {
        return cartList.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartList[position], onItemSelected)
    }

    fun updateData(data: List<CartItem>) {
        cartList = data
        notifyDataSetChanged()
    }

}