package com.example.dsi_android_ui

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dsi_android_ui.adapter.LocationPreviewListAdapter
import com.example.dsi_android_ui.adapter.ProductSpecificationListAdapter
import com.example.dsi_android_ui.adapter.ProductVariantListAdapter
import com.example.dsi_android_ui.models.Product
import com.example.dsi_android_ui.product_details.SelectLocationProductDetails
import com.example.dsi_android_ui.search_product.Location
import com.example.dsi_android_ui.search_product.ProductDetails
import com.example.dsi_android_ui.search_product.ProductSpecification
import com.example.dsi_android_ui.search_product.ProductVariant
import com.example.dsi_android_ui.search_product.SearchServiceViewModel
import com.example.dsi_android_ui.search_product.SearchViewModelFactory
import com.example.dsi_android_ui.service.ProductModel
import com.example.dsi_android_ui.utils.MAX_PREVIEW_LIST_LIMIT
import com.sap.cloud.mobile.fiori.`object`.ObjectHeader
import java.util.*

class ProductDetailsFragment : BaseFragment() {

    private val searchServiceViewModel by viewModels<SearchServiceViewModel> {
        SearchViewModelFactory()
    }
    private var pObjectHeader: ObjectHeader? = null
    private var productToolbar: Toolbar? = null
    private lateinit var locationListView: RecyclerView
    private var pStates: HashMap<Int, Boolean>? = HashMap()
    private val _tag = "ProductDetailsFragment"
    private val idDescription = 4
    private val idHeader = 2
    private val idImage = 1
    private val idSubHeadline = 3
    private lateinit var seeAllLocationsLink: TextView
    private lateinit var seeAllLocationsDivider: View
    private lateinit var specificationListView: RecyclerView
    private lateinit var seeAllSpecificationsLink: TextView
    private lateinit var seeAllSpecificationsDivider: View
    private lateinit var variantListView: RecyclerView
    private lateinit var seeAllVariantsLink: TextView
    private lateinit var seeAllVariantsDivider: View
    private lateinit var productDetailView: View

    private var receivedProduct: Product? = null
    private var pName: String = ""
    private var gtin: String = ""
    private var listOfLocation: MutableList<String> = ArrayList()
    private var listOfCount: MutableList<Int> = ArrayList()
    private lateinit var productDetails: com.example.dsi_android_ui.search_product.Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = this.arguments
        if (bundle != null) {
            receivedProduct = bundle.getParcelable("product")!! // Key
            pName = receivedProduct?.productName ?: ""
            gtin = receivedProduct?.productGtin ?: ""
        }

        pStates = createDefaultStates()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        productDetailView = inflater.inflate(R.layout.fragment_product_details, container, false)
        hideKeyboard(productDetailView)
        productToolbar = productDetailView.findViewById(R.id.toolbar)
        locationListView = productDetailView.findViewById(R.id.location_list)
        pObjectHeader = productDetailView.findViewById(R.id.profileHeader)
        seeAllLocationsLink = productDetailView.findViewById(R.id.location_list_see_all)
        seeAllLocationsDivider = productDetailView.findViewById(R.id.loc_list_divider)
        specificationListView = productDetailView.findViewById(R.id.spec_list)
        seeAllSpecificationsLink = productDetailView.findViewById(R.id.spec_list_see_all)
        seeAllSpecificationsDivider = productDetailView.findViewById(R.id.spec_list_divider)
        variantListView = productDetailView.findViewById(R.id.variant_list)
        seeAllVariantsLink = productDetailView.findViewById(R.id.variant_list_see_all)
        seeAllVariantsDivider = productDetailView.findViewById(R.id.variant_list_divider)
        initToolbar()
        initSeeAllListeners()
        searchServiceViewModel.productDetailsLiveData.observe(viewLifecycleOwner,this::productDetailsUpdated)

        if (!TextUtils.isEmpty(gtin)) {
            searchServiceViewModel.productSearchBySku(gtin)
        } else if (!TextUtils.isEmpty(pName)) {
            searchServiceViewModel.productSearchByName(pName)
        }
        return productDetailView
    }

    private fun productDetailsUpdated(productDetail: ProductDetails) {
        locationListView.removeAllViews()
        specificationListView.removeAllViews()
        variantListView.removeAllViews()
        Log.d(_tag, "onCreate: Product search by productName: $productDetail")
        if (productDetail.productList != null) {
            productDetails = productDetail.productList
            gtin = productDetails.gtin

            //Configure the Header values
            configureObjectHeader(productDetails)

            //Set the warning image if its required
            if (productDetails.getStatus().equals("Low", ignoreCase = true)) {
                            val statusTextView: TextView? = productDetailView.findViewById(R.id.statusText)
                val statusText = "${productDetails.getStatus()} Stock"
                statusTextView?.text = statusText
                            val statusImageView: ImageView? = productDetailView.findViewById(R.id.statusImage)
                val statusImage = ResourcesCompat.getDrawable(resources,R.drawable.ic_warning_black_24dp,null)
                statusImageView?.setImageDrawable(statusImage)
            }

            //Get the location array and create a full location path
            val locationList = productDetails.getLocations()
            listOfCount.clear()
            listOfLocation.clear()
            if (!locationList.isNullOrEmpty()) {
                for (i in locationList.indices) {
                    val loc = locationList[i]
                    val locStr = "${loc.aisle}-${loc.shelf}-${loc.section}-${loc.bin}"
                    listOfLocation.add(locStr)
                    listOfCount.add(loc.count)
                }
            }
            if (!locationList.isNullOrEmpty()) {
                initLocationListRecyclerView(locationList)
            }
            val specifications = productDetail.specifications
            val variants = productDetail.variants
            if (!specifications.isNullOrEmpty()) {
                initSpecificationListRecyclerView(specifications)
            }
            if (!variants.isNullOrEmpty()) {
                val filteredVariants = searchServiceViewModel.filterCurrentProductFromVariants(variants, gtin)
                initVariantListRecyclerView(filteredVariants)
            }
        }
    }

    private fun initSeeAllListeners() {
        seeAllLocationsLink.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            val fragment = ProductAllLocationsFragment()
            val bundle = Bundle()
            bundle.putString("gtin", gtin)
            fragment.arguments = bundle
            transaction.replace(R.id.container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        seeAllSpecificationsLink.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            val fragment = ProductAllSpecificationsFragment()
            val bundle = Bundle()
            bundle.putString("gtin", gtin)
            fragment.arguments = bundle
            transaction.replace(R.id.container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        seeAllVariantsLink.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            val fragment = ProductAllVariantsFragment()
            val bundle = Bundle()
            bundle.putString("gtin", gtin)
            fragment.arguments = bundle
            transaction.replace(R.id.container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
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

    private fun onProductVariantClick(selectedVariant: ProductVariant) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        val productDetailsFragment = ProductDetailsFragment()
        val bundle = Bundle()
        bundle.putParcelable("product",
                selectedVariant.gtin?.let {
                    selectedVariant.productName?.let { it1 ->
                        Product(
                                it1, it
                        )
                    }
                })
        productDetailsFragment.arguments = bundle
        transaction.replace(R.id.container, productDetailsFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun initLocationListRecyclerView(locationList: MutableList<Location>) {
        locationListView.setHasFixedSize(true)
        locationListView.addItemDecoration(
            DividerItemDecoration(locationListView.context, DividerItemDecoration.VERTICAL)
        )
        locationListView.layoutManager = LinearLayoutManager(context)
        locationListView.adapter = LocationPreviewListAdapter(
            locationList, true, this::onLocationClick
        )
        if (locationList.size > MAX_PREVIEW_LIST_LIMIT) {
            seeAllLocationsLink.visibility = View.VISIBLE
            seeAllLocationsDivider.visibility = View.VISIBLE
            seeAllLocationsLink.text = String.format(
                resources.getString(R.string.timeline_preview_see_all), locationList.size
            )
        } else {
            seeAllLocationsLink.visibility = View.GONE
            seeAllLocationsDivider.visibility = View.GONE
        }

    }

    private fun initSpecificationListRecyclerView(specifications: MutableList<ProductSpecification>) {
        specificationListView.setHasFixedSize(true)
        specificationListView.layoutManager = LinearLayoutManager(context)
        specificationListView.adapter = ProductSpecificationListAdapter(
                specifications, true
        )
        if (specifications.size > MAX_PREVIEW_LIST_LIMIT) {
            seeAllSpecificationsLink.visibility = View.VISIBLE
            seeAllSpecificationsDivider.visibility = View.VISIBLE
            seeAllSpecificationsLink.text = String.format(
                    resources.getString(R.string.timeline_preview_see_all), specifications.size
            )
        } else {
            seeAllSpecificationsLink.visibility = View.GONE
            seeAllSpecificationsDivider.visibility = View.GONE
        }

    }

    private fun initVariantListRecyclerView(variants: MutableList<ProductVariant>) {
        variantListView.setHasFixedSize(true)
        variantListView.addItemDecoration(
                DividerItemDecoration(variantListView.context, DividerItemDecoration.VERTICAL)
        )
        variantListView.layoutManager = LinearLayoutManager(context)
        variantListView.adapter = ProductVariantListAdapter(
                variants, true, this::onProductVariantClick
        )
        if (variants.size > MAX_PREVIEW_LIST_LIMIT) {
            seeAllVariantsLink.visibility = View.VISIBLE
            seeAllVariantsDivider.visibility = View.VISIBLE
            seeAllVariantsLink.text = String.format(
                    resources.getString(R.string.timeline_preview_see_all), variants.size
            )
        } else {
            seeAllVariantsLink.visibility = View.GONE
            seeAllVariantsDivider.visibility = View.GONE
        }

    }

    private fun initToolbar() {
        //Toolbar code
        productToolbar?.title = pName
        val navigationIcon = ResourcesCompat.getDrawable(resources,R.drawable.ic_arrow_back_24dp,null)
        productToolbar?.navigationIcon = navigationIcon
        productToolbar?.setNavigationOnClickListener { requireActivity().onBackPressed() }
        productToolbar?.inflateMenu(R.menu.navigation_menu_productdetails)

        productToolbar?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.addLocation -> {

                    AlertDialog.Builder(context).setMessage("Add Location Feature not Implemented")
                        .setPositiveButton("ok") { dialog, _ -> dialog.dismiss() }
                        .create().show()
                    //commented due to api not implemented
                    /* val transaction = activity?.supportFragmentManager?.beginTransaction()
                     if (transaction != null) {
                         transaction.replace(
                                 R.id.container, AddLocationFragment.newInstance(null, gtin)
                         )
                         transaction.addToBackStack(null)
                         transaction.commit()
                     }*/
                }
                R.id.add_to_cart -> {
                    val selectedProduct = ProductModel(
                        sku = productDetails.sku,
                        gtin = productDetails.gtin,
                        product_name = productDetails.productName,
                        count = productDetails.count,
                        department = productDetails.department,
                        category = productDetails.category,
                        sale_price = productDetails.salePrice,
                        reg_price = productDetails.regPrice,
                        status_icon = productDetails.statusIcon,
                        status = productDetails.status,
                        aisle = "",
                        shelf = "",
                        section = "",
                        bin = "",
                        info = "",
                        locationPath = ""
                    )
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.replace(
                        R.id.container,
                        SelectLocationProductDetails.newInstance(
                            selectedProduct,
                            listOfLocation,
                            listOfCount
                        )
                    )
                    transaction.addToBackStack(null)
                    transaction.commit()

                }

            }
            false
        }
    }

    private fun createDefaultStates(): HashMap<Int, Boolean> {
        val states = HashMap<Int, Boolean>()
        states[idImage] = true
        states[idSubHeadline] = true
        states[idDescription] = true
        states[idHeader] = true
        return states
    }

    fun configureObjectHeader(productDetail: com.example.dsi_android_ui.search_product.Product) {
        val it: Iterator<*> = pStates!!.keys.iterator()
        while (it.hasNext()) {
            val id = it.next() as Int
            configureObjectHeader(productDetail, id, pStates!![id]!!)
        }
    }

    fun configureObjectHeader(
        productDetail: com.example.dsi_android_ui.search_product.Product,
        id: Int,
        isChecked: Boolean
    ) {
        when (id) {
            idImage -> if (isChecked) {
                pObjectHeader?.setDetailImage(R.drawable.object_placeholder)
            }
            idSubHeadline -> if (isChecked) {
                pObjectHeader?.subheadline =
                    "$" + productDetail.getSalePrice() + " reg " + productDetail.getRegPrice()
                //Log.i(TAG, "configureObjectHeader: Product name "+productDetail.getSku());
            }
            idHeader -> if (isChecked) {
                pObjectHeader?.headline = getString(
                    R.string.product_by_location_Gtin, productDetail.gtin
                )
            }
            idDescription -> if (isChecked) {
                pObjectHeader?.description = "In Stock : " + productDetail.getCount()
            }
        }
        pObjectHeader?.invalidate()
        pObjectHeader?.requestLayout()
    }

}


