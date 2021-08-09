package com.example.dsi_android_ui.task_products

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.get
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.dsi_android_ui.BaseFragment
import com.example.dsi_android_ui.R
import com.example.dsi_android_ui.databinding.FragmentTaskProductSelectionBinding
import com.example.dsi_android_ui.search_department.ProductOverview
import com.example.dsi_android_ui.search_department.ProductOverviewList
import com.example.dsi_android_ui.search_product.SearchServiceViewModel
import com.example.dsi_android_ui.service.ProductModel
import com.example.dsi_android_ui.ui.product_list.SelectableProduct
import com.example.dsi_android_ui.ui.product_list.SelectableProductListAdapter
import com.example.dsi_android_ui.ui.product_list.SelectableProductsViewModel
import com.example.dsi_android_ui.utils.GenericResponse

class TaskSelectableProductsViewModel : SelectableProductsViewModel()

class TaskProductSelectionFragment : BaseFragment() {
    private lateinit var binding: FragmentTaskProductSelectionBinding
    private val selectionViewModel: TaskSelectableProductsViewModel by viewModels()
    private val listViewModel by activityViewModels<TaskProductListViewModel>()

    //TODO: searchViewModel needs refactoring. Violates Law of Demeter or principle of least knowledge. We need a reusable widget instead
    private val searchViewModel by activityViewModels<SearchServiceViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTaskProductSelectionBinding
            .inflate(inflater, container, false)
        initMenu()
        initSearch()
        initFooter()
        setUpSearchObserver()
        setUpStatusObserver()
        return binding.root
    }

    private fun initMenu() {
        binding.taskProductToolbar
            .setNavigationOnClickListener { leave() }
    }

    private fun initSearch() {
        val searchListener = SearchListener(searchViewModel)
        val searchIcon = binding.search.findViewById<ImageView>(R.id.search_mag_icon)
        searchIcon.setOnClickListener {
            searchListener.onQueryTextSubmit(binding.search.query.toString())
        }
        binding.search.setOnQueryTextListener(searchListener)
    }

    private fun initFooter() {
        binding.footer[0].isEnabled = false
        binding.footer.setPrimaryActionClickListener { addProducts() }
        binding.footer.setSecondaryActionClickListener { leave() }
    }

    private fun addProducts() {
        val products = selectionViewModel.selectedProducts.value ?: listOf()
        Log.d("PRODUCT_SELECTED", "TPSF#addProducts $products")
        listViewModel.addAllToSelected(mapToTaskProducts(products))
        leave()
    }

    private fun mapToTaskProducts(products: List<SelectableProduct>) =
        products.map {
            TaskProduct(
                it.product.product_name,
                it.product.gtin,
                it.product.status,
                it.product.count,
                it.selectedCount,
                it.product.locationPath
            )
        }

    private fun leave() {
        clearSearch()
        navigateOut()
    }

    private fun clearSearch() {
        val emptyResponse: GenericResponse<ProductOverviewList> = GenericResponse(
            GenericResponse.State.SUCCESS,
            ProductOverviewList(),
            null
        )
        searchViewModel.productOverviewsLiveData.setValue(emptyResponse)
    }

    private fun navigateOut() {
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun setUpSearchObserver() {
        searchViewModel.productOverviewsLiveData.observe(viewLifecycleOwner, {
            if (it.state == GenericResponse.State.LOADING)
                showProgressLoading("Searching")
            else {
                hideKeyboard()
                hideProgressDialog()
                val products = it.response?.productList ?: listOf()
                selectionViewModel.setupSelectableProducts(
                    mapToProductModels(products)
                )
                instantiateAdapter()
            }
        })
    }

    private fun instantiateAdapter() {
        val adapter = SelectableProductListAdapter(
            selectionViewModel.selectableProducts
        )
        binding.list.adapter = adapter
        adapter.productSelected.observe(viewLifecycleOwner) {
            selectionViewModel.onProductSelected(it)
        }
        adapter.productDeselected.observe(viewLifecycleOwner) {
            selectionViewModel.onProductDeselected(it)
        }
        selectionViewModel.selectedProducts.observe(viewLifecycleOwner) {
            adapter.setSelected(it)
        }
    }

    private fun mapToProductModels(products: List<ProductOverview>) =
        products.map {
            ProductModel(
                it.sku, it.gtin, it.productName, it.locations[0].count,
                "", "", 0F, 0F,
                "", "", "", "", "",
                "", "", it.locations[0].locationPath
            )
        }

    private fun setUpStatusObserver() {
        selectionViewModel.selectedProducts.observe(viewLifecycleOwner) {
            binding.footer[0].isEnabled = it.isNotEmpty()
        }
    }

    private fun hideKeyboard() {
        val imm = requireActivity()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
    }
}

class SearchListener(private val searchViewModel: SearchServiceViewModel) : OnQueryTextListener {
    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let { searchViewModel.productSearchByKeySubmit(it) }; return true
    }

    override fun onQueryTextChange(newText: String?): Boolean = true
}