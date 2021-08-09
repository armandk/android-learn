package com.example.dsi_android_ui.task_products

import android.R.layout.simple_list_item_1
import android.content.Context
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.dsi_android_ui.R
import com.example.dsi_android_ui.databinding.ListItemTaskProductBinding

class TaskProductListViewHolder(val binding: ListItemTaskProductBinding, val callback: DeleteCallback) :
    RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup, callback: DeleteCallback): TaskProductListViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ListItemTaskProductBinding
                .inflate(inflater, parent, false)
            return TaskProductListViewHolder(binding,callback)
        }
    }

    fun bind(item: TaskProduct) {
        binding.productNameText.text = item.productName
        binding.countText.setText("${item.selected}")
        binding.identifierText.text = textSku(item)
        binding.button.visibility = View.GONE
        initalizeSpinner(item)
        binding.root.setOnClickListener(onClickListener())
        binding.button.setOnClickListener { callback.onClick(item) }
    }

    private fun textSku(item: TaskProduct) =
        binding.root.context.getString(R.string.sku_text, item.productIdentifier)

    private fun onClickListener() = object : View.OnClickListener {
        override fun onClick(v: View?) {
            binding.button.isVisible = true
            binding.productTaskImage.isVisible = false
        }
    }

    private fun initalizeSpinner(item: TaskProduct) {
        binding.countText.setText(item.selected.toString())
        val array = Array(item.productCount) { i -> i + 1 }
        val adapter = ArrayAdapter(binding.root.context, simple_list_item_1, array)
        binding.countText.setAdapter(adapter)
        binding.countText.threshold = 0
        binding.countText.setOnItemClickListener { _, v, i, _ ->
            binding.countText.setText(binding.countText.adapter.getItem(i).toString())
            item.selected = binding.countText.adapter.getItem(i) as Int
            hideKeyboard(v)
            binding.button.isVisible = false
            binding.productTaskImage.isVisible = true
        }
        binding.countText.setOnKeyListener { view: View, i: Int, keyEvent: KeyEvent ->
            if (keyEvent.equals(KeyEvent.KEYCODE_ENTER))
                hideKeyboard(view)
            false
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(
            view.applicationWindowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

}