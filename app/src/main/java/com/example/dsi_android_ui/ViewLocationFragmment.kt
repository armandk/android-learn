package com.example.dsi_android_ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.example.dsi_android_ui.search_product.Location
import com.example.dsi_android_ui.search_product.ProductDetails
import com.example.dsi_android_ui.search_product.SearchServiceViewModel
import com.example.dsi_android_ui.search_product.SearchViewModelFactory

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ViewLocationFragment : BaseFragment() {
    private lateinit var searchServiceViewModel: SearchServiceViewModel
    private var location: Location? = null
    private var sku: String? = null
    private val _tag = "ViewLocationFragment"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            location = it.getSerializable(ARG_PARAM1) as Location
            sku = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_view_location, container, false)
        Log.d(_tag, "Load view location fragment")
        // set up back button
        val productToolbar: Toolbar = view.findViewById(R.id.toolbar)
        productToolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
        val application = activity?.application as AppController
        searchServiceViewModel = ViewModelProvider(this, SearchViewModelFactory()).get(SearchServiceViewModel::class.java)
        val nameView: TextView = view.findViewById(R.id.name)
        val aisleView: TextView = view.findViewById(R.id.aisle)
        val shelfView: TextView = view.findViewById(R.id.shelf)
        val sectionView: TextView = view.findViewById(R.id.section)
        val binView: TextView = view.findViewById(R.id.bin)
        val minView: TextView = view.findViewById(R.id.min)
        val maxView: TextView = view.findViewById(R.id.max)
        val totalCountView: TextView = view.findViewById(R.id.total_count)
        val editButton: Button = view.findViewById(R.id.buttonEdit)

        location?.let {
            nameView.text = it.info
            aisleView.text = it.aisle
            shelfView.text = it.shelf
            sectionView.text = it.section
            binView.text = it.bin
            minView.text = it.min.toString()
            maxView.text = it.max.toString()
            totalCountView.text = it.count.toString()

            val title = listOf(it.aisle, it.shelf, it.section, it.bin).joinToString(separator = "-")
            productToolbar.title = title
            searchServiceViewModel.productSearchBySku(sku)
        }
        searchServiceViewModel.productDetailsLiveData.observe(viewLifecycleOwner,
                { productDetail: ProductDetails ->

                    //Get the location array and create a full location path
                    val locationList = productDetail.productList.getLocations()
                    if (!locationList.isNullOrEmpty()) {
                        for (i in locationList.indices) {
                            val loc = locationList[i]
                            location?.let {
                                if (it.aisle.equals(loc.aisle) && it.shelf.equals(loc.shelf) && it.section.equals(loc.section) && it.bin.equals(loc.bin)) {
                                    nameView.text = loc.info
                                    minView.text = loc.min.toString()
                                    maxView.text = loc.max.toString()
                                    totalCountView.text = loc.count.toString()
                                }
                            }
                        }
                    }

                })


        editButton.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            if (transaction != null) {
                val addLocationFragment = AddLocationFragment.newInstance(location, sku!!)
                addLocationFragment.setListener {
                    loc ->
                    run {
                        location?.aisle = loc.aisle
                        location?.section = loc.section
                        location?.shelf = loc.shelf
                        location?.bin = loc.bin
                        location?.min = loc.min
                        location?.max = loc.max
                        location?.count = loc.count
                        location?.info = loc.info

                        searchServiceViewModel.productSearchBySku(sku)
                    }

                }
                transaction.replace(R.id.container, addLocationFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }


        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Location, param2: String?) =
                ViewLocationFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}