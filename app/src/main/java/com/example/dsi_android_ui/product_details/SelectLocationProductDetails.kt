package com.example.dsi_android_ui.product_details

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import com.example.dsi_android_ui.BaseFragment
import com.example.dsi_android_ui.ProductDetailsFragment
import com.example.dsi_android_ui.R
import com.example.dsi_android_ui.cart.CartItemsViewModel
import com.example.dsi_android_ui.cart.CartItemsViewModelFactory
import com.example.dsi_android_ui.databinding.FragmentSelectLocationBinding
import com.example.dsi_android_ui.models.CartItem
import com.example.dsi_android_ui.models.Product
import com.example.dsi_android_ui.service.ProductModel
import com.example.dsi_android_ui.ui.product_list.SelectableProduct

class SelectLocationProductDetails : BaseFragment() {
    private val cartViewModel by viewModels<CartItemsViewModel> {
        CartItemsViewModelFactory()
    }

    var locationList: MutableList<String> = ArrayList()
    var productCount: List<Int> = emptyList()
    lateinit var product: ProductModel
    var selectedProductDetails: MutableList<SelectableProduct> = ArrayList()
    var cartId: Int = 0

    private var _binding: FragmentSelectLocationBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_LOCATION_LIST = "LocationList"
        private const val ARG_PRODUCT_COUNT = "ProductCount"
        private const val ARG_PRODUCT = "ProductDetails"

        fun newInstance(
            product: ProductModel,
            locationList: MutableList<String>,
            productCount: List<Int>
        ) =
            SelectLocationProductDetails().apply {
                arguments = Bundle().apply {
                    putIntegerArrayList(ARG_PRODUCT_COUNT, ArrayList(productCount))
                    putParcelable(ARG_PRODUCT, product)
                    putStringArrayList(ARG_LOCATION_LIST, ArrayList(locationList))
                }
            }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            product = it.getParcelable(ARG_PRODUCT)!!
            productCount = it.getIntegerArrayList(ARG_PRODUCT_COUNT)!!
            locationList = it.getStringArrayList(ARG_LOCATION_LIST)!!.toMutableList()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectLocationBinding.inflate(inflater, container, false)

        val navigationIcon = resources.getDrawable(R.drawable.ic_arrow_back_24dp, null)
        binding.toolbar.navigationIcon = navigationIcon
        binding.toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
        binding.toolbar.title = product!!.product_name

        selectLocation()

        cartViewModel.postCartList.observe(viewLifecycleOwner, this::observeOnAddToCart)

        binding.selectProductsFooter.let {
            it.primaryButton.text = getString(R.string.add_to_cart)
            it.secondaryButton.text = getString(R.string.cancel)

            it.primaryButton.setOnClickListener { postCartData() }
            it.secondaryButton.setOnClickListener { activity?.onBackPressed() }
        }

        return binding.root
    }

    private fun selectLocation() {
        //adapter for location dropdown
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item_list, locationList)

        binding.selectedLocation.setAdapter(adapter)

        binding.selectedLocation.setText(locationList.first(), false)

        val values = ArrayList<String>()

        for (j in 0 until productCount[0]) {
            values.add(j, (j + 1).toString())
        }

        var adapterForCount = ArrayAdapter(requireContext(), R.layout.dropdown_item_list, values)

        binding.selectedStock.setAdapter(adapterForCount)

        binding.selectedStock.setText(values.last(), false)

        var selectedLocation = binding.selectedLocation.adapter.getItem(0).toString()
        val location = selectedLocation.split("-")
        val productDetails = product!!.copy(
            aisle = location[0],
            shelf = location[1],
            section = location[2],
            bin = location[3]
        )

        var selectedVal = values.last().toString()
        product!!.count = selectedVal.toIntOrNull()!!

        binding.selectedLocation.setOnItemClickListener { _, _, i: Int, _ ->
            binding.selectedLocation.setText(
                binding.selectedLocation.adapter.getItem(i).toString(),
                false
            )
            binding.selectedLocation.adapter.getItem(i).toString().also { selectedLocation = it }

            val location = selectedLocation.split("-")
            val productDetails = product!!.copy(
                aisle = location[0],
                shelf = location[1],
                section = location[2],
                bin = location[3]
            )

            adapter.notifyDataSetChanged()

            values.clear()

            for (j in 0 until productCount[i]) {
                values.add(j, (j + 1).toString())
            }

            adapterForCount = ArrayAdapter(requireContext(), R.layout.dropdown_item_list, values)

            binding.selectedStock.setAdapter(adapterForCount)

            binding.selectedStock.setText(values.last(), false)

            product = productDetails
        }

        binding.selectedStock.setOnItemClickListener { _, _, i: Int, _ ->
            binding.selectedStock.setText(
                binding.selectedStock.adapter.getItem(i).toString(),
                false
            )
            binding.selectedStock.adapter.getItem(i).toString().also { selectedVal = it }

            product!!.count = selectedVal.toIntOrNull()!!
            adapterForCount.notifyDataSetChanged()
        }
        product = productDetails

    }

    private fun postCartData() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val previousCartId = sharedPref!!.getInt("CartId", 0)

        val selectedProduct = SelectableProduct(product!!, product!!.count)
        selectedProductDetails.add(selectedProduct)
        cartViewModel.postCartItems(selectedProductDetails, previousCartId)
    }

    private fun observeOnAddToCart(cartItem: CartItem?) {
        if (cartItem == null) {
            showToast(R.string.toast_cart_error)
        } else {
            val cartId = cartItem.id!!
            onSuccess(cartId)
            showToast(R.string.toast_cart_success)
        }
    }

    private fun onSuccess(cart_id: Int) {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("CartId", cart_id)
            apply()
        }
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        val productDetailsFragment = ProductDetailsFragment()
        val bundle = Bundle()
        bundle.putParcelable("product", Product(product!!.product_name, product!!.gtin))
        productDetailsFragment.arguments = bundle
        transaction.replace(R.id.container, productDetailsFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}