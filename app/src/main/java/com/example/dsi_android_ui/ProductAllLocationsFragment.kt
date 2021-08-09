package com.example.dsi_android_ui

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dsi_android_ui.adapter.LocationPreviewListAdapter
import com.example.dsi_android_ui.databinding.ProductDetailsRecyclerLayoutBinding
import com.example.dsi_android_ui.search_product.Location
import com.example.dsi_android_ui.search_product.ProductDetails
import com.example.dsi_android_ui.search_product.SearchServiceViewModel
import com.example.dsi_android_ui.search_product.SearchViewModelFactory

class ProductAllLocationsFragment : BaseFragment() {
    private val searchServiceViewModel by viewModels<SearchServiceViewModel> {
        SearchViewModelFactory()
    }
    private var gtin: String = ""
    private var _binding: ProductDetailsRecyclerLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = this.arguments
        if (bundle != null) {
            gtin = bundle.getString("gtin")!!
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = ProductDetailsRecyclerLayoutBinding.inflate(inflater, container, false)
        initViewElements()
        initRecyclerView()
        return binding.root
    }

    private fun initRecyclerView() {
        binding.recyclerList.setHasFixedSize(true)
        binding.recyclerList.addItemDecoration(
                DividerItemDecoration(binding.recyclerList.context, DividerItemDecoration.VERTICAL)
        )
        binding.recyclerList.layoutManager = LinearLayoutManager(context)
    }

    private fun initViewElements() {
        searchServiceViewModel.productDetailsLiveData.observe(
                viewLifecycleOwner, this::productDetailsUpdated
        )
        if (!TextUtils.isEmpty(gtin)) {
            searchServiceViewModel.productSearchBySku(gtin)
        }
        //Toolbar back button
        val navigationIcon = ResourcesCompat.getDrawable(resources,R.drawable.ic_arrow_back_24dp,null)
        binding.toolbar.navigationIcon = navigationIcon
        binding.toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }

    }

    private fun productDetailsUpdated(productDetail: ProductDetails) {
        if (productDetail.productList != null) {
            val productDetails = productDetail.productList
            val locationList = productDetails.getLocations()
            //Toolbar title
            binding.toolbar.title = String.format(
                    resources.getString(R.string.product_all_locations_title), locationList.size
            )
            if (!locationList.isNullOrEmpty()) {
                binding.recyclerList.adapter = LocationPreviewListAdapter(
                        locationList, false, this::onLocationClick
                )
            }
        }
    }

    private fun onLocationClick(selectedLocation: Location) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(
                R.id.container, ViewLocationFragment.newInstance(selectedLocation, gtin)
        )
        transaction.addToBackStack(null)
        transaction.commit()
    }
}