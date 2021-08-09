package com.example.dsi_android_ui.task_products

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.example.dsi_android_ui.BaseFragment
import com.example.dsi_android_ui.R
import com.example.dsi_android_ui.databinding.FragmentTaskProductListBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class TaskProductListFragment : BaseFragment() {
    private lateinit var binding: FragmentTaskProductListBinding
    private val viewModel by activityViewModels<TaskProductListViewModel>()
    private var bottomNav: BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        bottomNav = requireActivity().findViewById(R.id.bottom_navigation)
        bottomNav?.visibility = View.GONE
        viewModel.wasSelectedProductsModified = false
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, b: Bundle?
    ): View {
        binding = FragmentTaskProductListBinding
            .inflate(inflater, container, false)
        initRecyclerViewAdapter()
        initMenu()
        initFooter()
        initStateObserver()
        return binding.root
    }

    private fun initStateObserver() {
        viewModel.selectedProducts.observe(viewLifecycleOwner, {
            binding.footerButton[0].isEnabled =
                it.isNotEmpty() or viewModel.wasSelectedProductsModified
            binding.addProductsText.isVisible = it.isEmpty()
        })
    }

    private fun initFooter() {
        binding.footerButton[0].isEnabled = viewModel.wasSelectedProductsModified
        binding.footerButton
            .setPrimaryActionClickListener { navigateOut() }
    }

    private fun initMenu() {
        binding.taskProductToolbar
            .setNavigationOnClickListener { cancelAction() }
        binding.taskProductToolbar
            .setOnMenuItemClickListener { navigateToProductSelector() }
    }

    private fun navigateOut() {
        requireActivity().onBackPressed()
    }

    private fun cancelAction() {
        viewModel.cancelModifications()
        navigateOut()
    }

    private fun initRecyclerViewAdapter() {
        val adapter = TaskProductListAdapter(deleteCallback())
        binding.taskProductList.adapter = adapter
        viewModel.selectedProducts.observe(viewLifecycleOwner, {
            Log.d("PRODUCT_SELECTED", "TPLF#initRecyclerView $it")
            adapter.submitList(it.toList())
        })
    }

    private fun deleteCallback() = DeleteCallback { viewModel.deselect(it) }

    private fun navigateToProductSelector(): Boolean {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, TaskProductSelectionFragment())
        transaction.addToBackStack(null)
        transaction.commit()
        return true
    }
}