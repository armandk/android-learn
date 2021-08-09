package com.example.dsi_android_ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.dsi_android_ui.cart.CartItemsViewModel
import com.example.dsi_android_ui.cart.CartItemsViewModelFactory
import com.example.dsi_android_ui.databinding.FragmentSelectProductsBinding
import com.example.dsi_android_ui.models.CartItem
import com.example.dsi_android_ui.ui.product_list.SelectableProduct
import com.example.dsi_android_ui.ui.product_list.SelectableProductListAdapter
import com.example.dsi_android_ui.ui.product_list.SelectableProductsViewModel

class SelectProductsFragment : BaseFragment() {

    private val viewModel: SelectableProductsViewModel by activityViewModels()
    private val cartViewModel by activityViewModels<CartItemsViewModel> {
        CartItemsViewModelFactory()
    }

    private var _binding: FragmentSelectProductsBinding? = null
    private val binding get() = _binding!!
    private var hasRequestedAddToCart = false
    private var title: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = arguments

        if (bundle != null) {
            title = bundle.getString("title")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectProductsBinding.inflate(inflater, container, false)

        val adapter = SelectableProductListAdapter(viewModel.selectableProducts)
        adapter.productSelected.observe(viewLifecycleOwner, this::onProductSelected)
        adapter.productDeselected.observe(viewLifecycleOwner) { viewModel.onProductDeselected(it) }
        adapter.productStockSelected.observe(viewLifecycleOwner) { (selectedStock, selectableProduct) ->
            viewModel.onProductStockSelected(selectedStock, selectableProduct)
        }
        adapter.productClicked.observe(viewLifecycleOwner, this::onProductClicked)

        viewModel.selectedProducts.observe(viewLifecycleOwner) { adapter.setSelected(it) }
        cartViewModel.postCartList.observe(viewLifecycleOwner, this::observeOnAddToCart)

        binding.selectableProductList.adapter = adapter

        binding.selectProductsToolbar.let {
            it.title = title
            it.setNavigationOnClickListener { requireActivity().onBackPressed() }
        }

        binding.selectProductsFooter.let {
            it.primaryButton.text = getString(R.string.add_to_cart)
            it.secondaryButton.text = getString(R.string.cancel)

            it.primaryButton.setOnClickListener {
                addToCart()
            }
            it.secondaryButton.setOnClickListener {
                activity?.onBackPressed()
            }
        }

        return binding.root
    }

    private fun observeOnAddToCart(cartItem: CartItem?) {
        if (!hasRequestedAddToCart) {
            return
        }

        if (cartItem == null) {
            showToast(R.string.toast_cart_error)
        } else {
            val cartId = cartItem.id!!
            onSuccess(cartId)
            showToast(R.string.toast_cart_success)
        }
    }

    private fun addToCart() {
        val selectedProducts = viewModel.getSelectedProductsWithConflictResolutions()

        hasRequestedAddToCart = true

        if (selectedProducts.isNullOrEmpty()) {
            showToast(R.string.toast_cart_warning)
        } else {
            val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
            val previousCartId = sharedPref.getInt("CartId", 0)

            cartViewModel.postCartItems(selectedProducts, previousCartId)
        }
    }

    private fun onSuccess(cart_id: Int) {
        val fragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)

        with(sharedPref.edit()) {
            putInt("CartId", cart_id)
            apply()
        }

        transaction.replace(
            R.id.container,
            CartFragment.newInstance(cart_id, true, null, null, null, null)
        )
        fragmentManager.popBackStack()
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun navigateToConflictResolution(selectableProduct: SelectableProduct) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        val conflictResolutionFragment = ConflictResolutionLocationFragment()

        viewModel.setProductForConflictResolution(selectableProduct)

        transaction.replace(R.id.container, conflictResolutionFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun onProductSelected(selectableProduct: SelectableProduct) {
        if (selectableProduct.product.Locations.size > 1) {
            navigateToConflictResolution(selectableProduct)
        } else {
            viewModel.onProductSelected(selectableProduct)
        }
    }

    private fun onProductClicked(selectableProduct: SelectableProduct) {
        if (selectableProduct.product.Locations.size > 1) {
            navigateToConflictResolution(selectableProduct)
        }
    }

}
