package com.example.dsi_android_ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.transition.TransitionManager
import android.transition.Visibility
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dsi_android_ui.adapter.CartListAdapter
import com.example.dsi_android_ui.cart.CartItemsViewModel
import com.example.dsi_android_ui.cart.CartItemsViewModelFactory
import com.example.dsi_android_ui.cart.model.CartToLocationModel
import com.example.dsi_android_ui.databinding.FragmentCartBinding
import com.example.dsi_android_ui.models.CartItem
import com.example.dsi_android_ui.search_product.Location
import com.example.dsi_android_ui.ui.browse_by_location.BrowseByNestedLocation
import com.example.dsi_android_ui.utils.LocationHierarchyLevel
import com.example.dsi_android_ui.utils.createCustomSnackbar
import com.google.android.material.snackbar.Snackbar

class CartFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private val viewModel by activityViewModels<CartItemsViewModel> {
        CartItemsViewModelFactory()
    }
    var initialLoad = false
    val destAilseParam = "destAilse"
    val destShelfParam = "destShelf"
    val destSectionParam = "destSection"
    val destBinParam = "destBin"
    private var cartLists: List<CartItem>? = listOf()
    private lateinit var cartViewModelFactory: CartItemsViewModelFactory
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private var myCartTitle: String = ""
    var cartId = 0
    var status = false

    private var destAlise : String? = null
    private var destShelf : String? = null
    private var destSection : String? = null
    private var destBin : String? = null
    private var isEditMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = this.arguments
        if (bundle != null) {
            cartId = bundle.getInt(ARG_CART_ID)
            status = bundle.getBoolean(ARG_STATUS)
            destAlise = bundle.getString(destAilseParam)
            destShelf = bundle.getString(destShelfParam)
            destSection = bundle.getString(destSectionParam)
            destBin = bundle.getString(destBinParam)
            if(!TextUtils.isEmpty(destAlise)){
                isEditMode = true
            }
        }
        initialLoad = true
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        val view = binding
        val cartToolbar: Toolbar = view.cartToolBar
        recyclerView = binding.cartList
        myCartTitle = getString(R.string.myCartTitle)

        (activity as MainActivity).changeBottomNavigation(R.id.work_cart)


        //This is not working will check later.
        /*when (status) {
            true -> {
                createCustomSnackbar(
                    requireView(),
                    getString(R.string.toast_cart_success),
                    Snackbar.LENGTH_LONG,
                    R.drawable.outline_shopping_cart_24,
                    null
                ).show()
            }
        } */

        //Mock scanner code
        binding.scannerImage.setOnClickListener {
            // Inflate a custom view using layout inflater
            val viewForScanner = inflater.inflate(R.layout.scanning_popup, null)
            // Initialize a new instance of popup window
            val popupWindow = PopupWindow(
                viewForScanner,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            Handler().postDelayed({
                popupWindow.dismiss()
                moveToProductList()
            }, 1000)

            popupWindow.setOnDismissListener {
                Toast.makeText(
                    viewForScanner.context,
                    "Scanning Completed",
                    Toast.LENGTH_SHORT
                ).show()
            }

            TransitionManager.beginDelayedTransition(binding.root)
            popupWindow.showAtLocation(binding.root, Gravity.CENTER, 0, 0)

        }
        viewModel.cartLiveData.observe(viewLifecycleOwner, { cartList ->
            if(cartList != null) {
                cartList.forEach { it.selectedCount = 1 }
                cartLists = viewModel.cartLiveData.value
                recyclerView.setHasFixedSize(true)
                recyclerView.addItemDecoration(
                    DividerItemDecoration(
                        recyclerView.context,
                        DividerItemDecoration.VERTICAL
                    )
                )
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.adapter = CartListAdapter(cartList, this::onItemSelected, isEditMode)
            }
            if(cartList != null && cartList.isNotEmpty()){
                val sizeOfCart: String = " (".plus(cartList.size.toString()).plus(")")
                cartToolbar.title = myCartTitle + sizeOfCart
                binding.cartImage.visibility = GONE
                binding.myCartMessage.visibility = GONE
                binding.cartEmptyMessage.visibility = GONE
                binding.cartAddProductsMessage.visibility = GONE
                if(!isEditMode){
                    binding.buttonLayout.visibility = GONE
                }else{
                    binding.buttonLayout.visibility = VISIBLE
                }
            }
            else{
                binding.cartImage.visibility = VISIBLE
                binding.myCartMessage.visibility = VISIBLE
                binding.cartEmptyMessage.visibility = VISIBLE
                binding.cartAddProductsMessage.visibility = VISIBLE
                cartToolbar.title= myCartTitle
                binding.cartImage.setImageResource(R.drawable.outline_shopping_cart_24)
                binding.myCartMessage.text = getString(R.string.myCartTitle)
                binding.myCartMessage.setTextColor(Color.BLACK)
                binding.cartEmptyMessage.text = getString(R.string.cart_empty_message)
                binding.cartAddProductsMessage.text = getString(R.string.cart_add_products_message)
                binding.buttonLayout.visibility = GONE

            }
        })

        if (cartId == 0) {
            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
            cartId = sharedPref!!.getInt("CartId", 0)
        }
        binding.btnAddToLocation.setOnClickListener {
            cartLists?.let {
                val data =  it.filter { it.isSelected && it.selectedCount > 0 }
                if(data.isEmpty()){
                    AlertDialog.Builder(context).setMessage(R.string.select_stock_msg)
                            .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
                            .create().show()
                    return@setOnClickListener
                }

                if(destAlise!=null && destShelf!=null && destSection!=null && destBin !=null){

                    val selectedCartItems =  data.map { d ->
                        CartItem(
                                id = d.id,
                                user_id = d.user_id,
                                sku = d.sku,
                                aisle = d.aisle,
                                shelf = d.shelf,
                                section = d.section,
                                bin = d.bin,
                                count = d.selectedCount,
                                department = d.department,
                                category = d.category,
                                product_name = d.product_name,
                                sale_price = d.sale_price,
                                reg_price = d.reg_price,
                                status_icon = d.status_icon,
                                status = d.status,
                                gtin = d.gtin,
                                isSelected = d.isSelected,
                                selectedCount = d.selectedCount,

                        )
                    }
                    val selectedLocation = Location()
                    selectedLocation.aisle = destAlise
                    selectedLocation.section =destSection
                    selectedLocation.shelf = destShelf
                    selectedLocation.bin = destBin
                    initialLoad = false
                    viewModel.moveToLocation(cartId,CartToLocationModel(cartId,selectedCartItems, selectedLocation))
                }

            }
        }

        binding.btnCancel.setOnClickListener { activity?.onBackPressed() }

        viewModel.locationMoveStatus.observe(viewLifecycleOwner, {
            if(!initialLoad) {
                activity?.onBackPressed()
            }
            //viewModel.getCartItems(cartId)
        })

        //for demo, once the Picking cart is ready will send the cart_id
        viewModel.getCartItems(cartId)


        return view.root
    }

    private fun moveToProductList() {
        val rnds = (0..10).random()
        if (rnds % 2 == 0) {
            val locationHierarchyLevel = LocationHierarchyLevel.BIN
            val location = listOf("A3", "S1", "S2", "B3")
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(
                R.id.container,
                BrowseByNestedLocation.newInstance(locationHierarchyLevel, location)
            )
            transaction.addToBackStack(null)
            transaction.commit()
        } else {
            Toast.makeText(context, "Location Not found!!", Toast.LENGTH_SHORT).show()
        }


    }


    private fun onItemSelected(isChecked:Boolean, cartItem: CartItem) {
        cartLists?.indexOf(cartItem)
        binding.btnAddToLocation.isEnabled = false
        cartLists?.forEach {
            if(it.isSelected){
                binding.btnAddToLocation.isEnabled = true
            }
        }
        val sortedList = cartLists?.sortedBy { !it.isSelected }
        recyclerView.adapter = sortedList?.let { CartListAdapter(it, this::onItemSelected, isEditMode) }

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val ARG_CART_ID = "CartId"
        private const val ARG_STATUS = "Status"

        @JvmStatic
        fun newInstance(cart_id: Int, status: Boolean,destAilse: String?,destShelf: String?,destSection: String?,destBin: String?) =
                CartFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_CART_ID, cart_id)
                        putBoolean(ARG_STATUS, status)
                        putString(destAilseParam, destAilse)
                        putString(destShelfParam, destShelf)
                        putString(destSectionParam, destSection)
                        putString(destBinParam, destBin)

                    }
                }
    }


}