package com.example.dsi_android_ui.ui.browse_by_location

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.dsi_android_ui.*
import com.example.dsi_android_ui.databinding.BrowseByNestedLocationBinding
import com.example.dsi_android_ui.models.Product
import com.example.dsi_android_ui.service.LocationModel
import com.example.dsi_android_ui.service.ProductModel
import com.example.dsi_android_ui.ui.breadcrumbs.BreadcrumbsAdapter
import com.example.dsi_android_ui.ui.product_list.SelectableProductsViewModel
import com.example.dsi_android_ui.utils.LocationHierarchyLevel


/**
 * A simple [Fragment] subclass.
 * Use the [BrowseByNestedLocation.newInstance] factory method to
 * create an instance of this fragment.
 */
class BrowseByNestedLocation : BaseFragment() {

    private val viewModel by activityViewModels<BrowseByLocationViewModel> {
        BrowseByLocationViewModelFactory()
    }
    private val selectableProductsViewModel: SelectableProductsViewModel by activityViewModels()

    private var hierarchyLevel: LocationHierarchyLevel? = null
    private var breadcrumbList: List<String> = emptyList()
    private var _binding: BrowseByNestedLocationBinding? = null
    private val binding get() = _binding!!
    private var hasRequestedProducts = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            hierarchyLevel = it.getSerializable(ARG_HIERARCHY_LEVEL) as LocationHierarchyLevel
            breadcrumbList = it.getStringArrayList(ARG_BREADCRUMB_LIST)?.toList() as List<String>
        }
        hideKeyboard()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BrowseByNestedLocationBinding.inflate(inflater, container, false)
        viewModel.updateLocation(hierarchyLevel, breadcrumbList)

        // TODO - add to navigation logic
        (activity as MainActivity).changeBottomNavigation(R.id.search)

        bindBasedOnLocationHierarchy()

        showProgressLoading(null)

        if (viewModel.isLocationListSelected) {
            viewModel.getListByHierarchyLevel()
        } else {
            hasRequestedProducts = true
            viewModel.getProductListByLocation()
        }

        viewModel.locationListLiveData.observe(viewLifecycleOwner) { onLocationListChange() }
        viewModel.productListLiveData.observe(viewLifecycleOwner) { onProductListChange() }

        binding.browseByLocationBreadcrumb.adapter =
            BreadcrumbsAdapter(breadcrumbList) { position, _ ->
                onBreadcrumbClicked(position)
            }

        onClickSelectProductsItem()

        return binding.root
    }

    private fun onClickSelectProductsItem() {

        binding.browseByLocationToolbar
            .setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.select_products -> onSelectProductsClick()
                    R.id.add_products_from_cart -> onAddProductSelected(breadcrumbList)
                    else -> true
                }
            }
    }

    private fun onLocationListChange() {
        if (viewModel.isLocationListSelected) {
            renderLocationList()
            hideProgressDialog()
        }
    }

    private fun onProductListChange() {
        if (!viewModel.isLocationListSelected) {
            renderProductList()
            hideProgressDialog()
        }
    }

    private fun bindBasedOnLocationHierarchy() {
        binding.browseByLocationToolbar.inflateMenu(R.menu.navigation_menu_location)
        binding.browseByLocationToolbar.menu.findItem(R.id.select_products).isEnabled = true
        // Initial toggle state
        binding.browseByLocationToggle.check(
            when (viewModel.isLocationListSelected) {
                true -> binding.viewLocationListButton.id
                false -> binding.viewProductListButton.id
            }
        )

        binding.browseByLocationToolbar.let {
            it.title = breadcrumbList.lastOrNull() ?: getString(R.string.browse_by_location_title)
            it.setNavigationOnClickListener {
                activity?.onBackPressed()
            }
        }

        binding.browseByLocationList.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        when (hierarchyLevel) {
            // Last screen
            LocationHierarchyLevel.BIN -> {
                binding.browseByLocationBreadcrumb.visibility = VISIBLE
                binding.browseByLocationToggle.visibility = GONE
                binding.browseByLocationDivider.visibility = VISIBLE

                binding.browseByLocationListHeader.productListHeaderText.typeface =
                    Typeface.DEFAULT_BOLD
                binding.browseByLocationListHeader.productListHeaderProductCount.typeface =
                    Typeface.DEFAULT_BOLD
                binding.browseByLocationToolbar.menu.findItem(R.id.add_products_from_cart).isEnabled =
                    true
            }
            // First screen
            null -> {
                binding.browseByLocationBreadcrumb.visibility = GONE
                binding.browseByLocationToggle.visibility = GONE
                binding.browseByLocationDivider.visibility = GONE
                binding.browseByLocationToolbar.menu.findItem(R.id.add_products_from_cart).isEnabled =
                    false
            }
            else -> {
                binding.browseByLocationBreadcrumb.visibility = VISIBLE
                binding.browseByLocationToggle.visibility = VISIBLE
                binding.browseByLocationDivider.visibility = VISIBLE
                binding.browseByLocationToolbar.menu.findItem(R.id.add_products_from_cart).isEnabled =
                    false
                binding.viewLocationListButton.text = when (hierarchyLevel) {
                    LocationHierarchyLevel.AISLE -> getString(R.string.by_shelf)
                    LocationHierarchyLevel.SHELF -> getString(R.string.by_section)
                    else -> getString(R.string.by_bin)
                }

                binding.browseByLocationToggle.setOnCheckedChangeListener { _, checkedId ->
                    if (checkedId == binding.viewLocationListButton.id) {
                        renderLocationList()

                        viewModel.isLocationListSelected = true
                    } else {
                        if (hasRequestedProducts) {
                            renderProductList()
                        } else {
                            hasRequestedProducts = true
                            showProgressLoading(null)
                            viewModel.getProductListByLocation()
                        }
                        binding.browseByLocationToolbar.menu.findItem(R.id.select_products).isEnabled =
                            true

                        viewModel.isLocationListSelected = false
                    }
                }
            }
        }
    }

    private fun renderLocationList() {
        val locationList = viewModel.locationListLiveData.value!!
        binding.browseByLocationListHeader.root.visibility = GONE
        binding.browseByLocationToolbar.menu.findItem(R.id.select_products).isEnabled = false
        binding.browseByLocationList.adapter =
            BrowseByLocationListAdapter(locationList, this::onLocationRowClick)
    }

    private fun renderProductList() {
        val productList = viewModel.productListLiveData.value!!
        if (hierarchyLevel == LocationHierarchyLevel.BIN) {
            binding.browseByLocationToolbar.menu.findItem(R.id.select_products).isEnabled = true
        }
        binding.browseByLocationListHeader.root.visibility = VISIBLE
        binding.browseByLocationList.adapter =
            ProductListAdapter(productList, this::onProductRowClick)
        binding.browseByLocationListHeader.productListHeaderProductCount.text =
            productList.size.toString()
    }

    private fun onBreadcrumbClicked(position: Int) {
        val newHierarchyLevel = LocationHierarchyLevel.values()[position]
        // TODO - avoid using breadcrumb list to navigate
        val newBreadcrumbList = breadcrumbList.slice(0..position)

        // Reset toggle state
        viewModel.isLocationListSelected = true

        // TODO - navigate based on position
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, newInstance(newHierarchyLevel, newBreadcrumbList))
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun onLocationRowClick(location: LocationModel) {
        // TODO - avoid using breadcrumb list to navigate
        val newBreadcrumbList = breadcrumbList.toMutableList()
        newBreadcrumbList.add(location.name)

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, newInstance(location.type, newBreadcrumbList))
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun onProductRowClick(product: ProductModel) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        val productDetailsFragment = ProductDetailsFragment()
        val bundle = Bundle()
        bundle.putParcelable("product", Product(product.product_name, product.gtin))
        productDetailsFragment.arguments = bundle
        transaction.replace(R.id.container, productDetailsFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun onSelectProductsClick(): Boolean {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        val selectProductsFragment = SelectProductsFragment()
        val productList = viewModel.productListLiveData.value!!
        val bundle = Bundle()

        selectableProductsViewModel.setupSelectableProducts(productList)

        // TODO - add utility for this
        var title = "Aisle ${viewModel.aisle}"
        if (viewModel.shelf.isNotEmpty()) {
            title = "${title} - Section ${viewModel.shelf}"
            if (viewModel.section.isNotEmpty()) {
                title = "${title} - Shelf ${viewModel.section}"
                if (viewModel.bin.isNotEmpty()) {
                    title = "${title} - Bin ${viewModel.bin}"
                }
            }
        }
        title = "${title} (${productList.size.toString()})"

        bundle.putString("title", title)
        selectProductsFragment.arguments = bundle

        transaction.replace(R.id.container, selectProductsFragment)
        transaction.addToBackStack(null)
        transaction.commit()
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun hideKeyboard() {
        val imm =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        // To get the correct window token, lets first get the currently focused view
        var view = requireActivity().currentFocus

        // To get the window token when there is no currently focused view, we have a to create a view
        if (view == null) {
            view = View(activity)
        }

        // hide the keyboard
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
        private const val ARG_HIERARCHY_LEVEL = "hierarchyLevel"
        private const val ARG_BREADCRUMB_LIST = "breadcrumbList"

        @JvmStatic
        fun newInstance(param1: LocationHierarchyLevel, param2: List<String>) =
            BrowseByNestedLocation().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_HIERARCHY_LEVEL, param1)
                    putStringArrayList(ARG_BREADCRUMB_LIST, ArrayList(param2))
                }
            }

        @JvmStatic
        fun newInstance() = BrowseByNestedLocation()
    }

    private fun onAddProductSelected(breadcrumbList: List<String>): Boolean {
        if (breadcrumbList.size == 4) {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            if (transaction != null) {
                transaction.replace(
                    R.id.container,
                    CartFragment.newInstance(
                        0,
                        false,
                        breadcrumbList[0],
                        breadcrumbList[1],
                        breadcrumbList[2],
                        breadcrumbList[3]
                    )
                )
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }

        return true
    }

}

