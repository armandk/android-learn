package com.example.dsi_android_ui

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.example.dsi_android_ui.location.LocationListAdapter
import com.example.dsi_android_ui.search_product.Location
import com.example.dsi_android_ui.search_product.ProductDetails
import com.example.dsi_android_ui.search_product.SearchServiceViewModel
import com.example.dsi_android_ui.search_product.SearchViewModelFactory
import com.example.dsi_android_ui.service.LocationModel
import com.example.dsi_android_ui.service.model.MoveToLocation
import com.example.dsi_android_ui.utils.GenericResponse
import com.example.dsi_android_ui.utils.LocationHierarchyLevel
import com.example.dsi_android_ui.viewmodel.location.LocationViewModel
import com.google.android.material.textfield.TextInputEditText

private const val ARG_PARAM1 = "param1"
private const val ARG_GTIN = "param2"

class AddLocationFragment : BaseFragment(), View.OnClickListener {
    private var onBack: ((Location) -> Unit)? = null
    private var location: Location? = null
    private lateinit var newLocation: Location
    private var gtin: String? = null
    private val _tag = "LocationFragment"
    private lateinit var aislesDataListAdapter: LocationListAdapter
    private lateinit var shelvesDataListAdapter: LocationListAdapter
    private lateinit var sectionsDataListAdapter: LocationListAdapter
    private lateinit var binDataListAdapter: LocationListAdapter
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var searchServiceViewModel: SearchServiceViewModel
    // views
    private var aislesView: AutoCompleteTextView? = null
    private var shelvesView: AutoCompleteTextView? = null
    private var sectionsView: AutoCompleteTextView? = null
    private var binsView: AutoCompleteTextView? = null
    private var totalProducts: TextInputEditText? = null
    private var etName: TextInputEditText? = null
    private lateinit var etMin: TextInputEditText
    private lateinit var etMax: TextInputEditText
    private lateinit var productToolbar: Toolbar
    private var locationList:List<Location>?= null
    var oldAisle = location?.aisle
    var oldShelf = location?.shelf
    var oldSection = location?.section
    var oldBin = location?.bin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (it.getSerializable(ARG_PARAM1) != null) {
                location = it.getSerializable(ARG_PARAM1) as Location
                 oldAisle = location?.aisle
                 oldShelf = location?.shelf
                 oldSection = location?.section
                 oldBin = location?.bin
            }
            gtin = it.getString(ARG_GTIN)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_location, container, false)
        productToolbar = view.findViewById(R.id.toolbar)
        productToolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }

        locationViewModel = ViewModelProvider(this).get(LocationViewModel::class.java)
        searchServiceViewModel = ViewModelProvider(this, SearchViewModelFactory()).get(SearchServiceViewModel::class.java)
        aislesView = view.findViewById(R.id.aisle_autocomplete)
        shelvesView = view.findViewById(R.id.shelf_autocomplete)
        sectionsView = view.findViewById(R.id.section_autocomplete)
        binsView = view.findViewById(R.id.bin_autocomplete)
        totalProducts = view.findViewById(R.id.totalProductsEdtText)
        etMin = view.findViewById(R.id.et_min)
        etMax = view.findViewById(R.id.et_max)
        etName = view.findViewById(R.id.et_name)

        // action buttons
        val submitButton: Button = view.findViewById(R.id.buttonSave)
        val cancelButton: Button = view.findViewById(R.id.buttonCancel)

        aislesDataListAdapter =
                LocationListAdapter(requireContext(), ArrayList())
        shelvesDataListAdapter =
                LocationListAdapter(requireContext(), ArrayList())
        sectionsDataListAdapter =
                LocationListAdapter(requireContext(), ArrayList())
        binDataListAdapter =
                LocationListAdapter(requireContext(), ArrayList())

        // set adapters
        aislesView?.setAdapter(aislesDataListAdapter)
        shelvesView?.setAdapter(shelvesDataListAdapter)
        sectionsView?.setAdapter(sectionsDataListAdapter)
        binsView?.setAdapter(binDataListAdapter)


        // clicks
        aislesView?.setOnClickListener(this)
        shelvesView?.setOnClickListener(this)
        sectionsView?.setOnClickListener(this)
        binsView?.setOnClickListener(this)
        aislesView?.setOnItemClickListener { adapter, _, position, _ ->
            val aisle = adapter.getItemAtPosition(position) as LocationModel
            locationViewModel.shelfByAisleLiveData.value = GenericResponse(GenericResponse.State.SUCCESS, listOf(),null)
            locationViewModel.sectionByAisleAndShelfLiveData.value = GenericResponse(GenericResponse.State.SUCCESS, listOf(),null)
            locationViewModel.binByAisleAndShelfAndSectionLiveData.value = GenericResponse(GenericResponse.State.SUCCESS, listOf(),null)
            Log.d(_tag, "You clicked on  " + aisle.name)
            locationViewModel.getShelfByAisle(aisle.name)
        }
        shelvesView?.setOnItemClickListener { adapter, _, position, _ ->
            val aisle = aislesView?.text.toString()
            val shelf = adapter.getItemAtPosition(position) as LocationModel
            locationViewModel.sectionByAisleAndShelfLiveData.value = GenericResponse(GenericResponse.State.SUCCESS, listOf(),null)
            locationViewModel.binByAisleAndShelfAndSectionLiveData.value = GenericResponse(GenericResponse.State.SUCCESS, listOf(),null)

            Log.d(_tag, "You clicked on  " + shelf.name)
            locationViewModel.getSectionByAisleAndShelf(aisle, shelf.name)
        }
        sectionsView?.setOnItemClickListener { adapter, _, position, _ ->
            val aisle = aislesView?.text.toString()
            val shelf = shelvesView?.text.toString()
            val section = adapter.getItemAtPosition(position) as LocationModel
            locationViewModel.binByAisleAndShelfAndSectionLiveData.value = GenericResponse(GenericResponse.State.SUCCESS, listOf(),null)

            Log.d(_tag, "You clicked on  " + section.name)
            locationViewModel.getBinByAisleAndShelfAndSection(aisle, shelf, section.name)
        }

        binsView?.setOnItemClickListener { adapter, _, position, _ ->
            val aisle = aislesView?.text.toString()
            val shelf = shelvesView?.text.toString()
            val section = sectionsView?.text.toString()
            val bin = adapter.getItemAtPosition(position) as LocationModel

            locationViewModel.getProductDetailsByLocation(aisle, shelf, section, bin.name)

        }
        submitButton.setOnClickListener {
            val aisle: String? = aislesView?.text.toString()
            var shelf: String? = shelvesView?.text.toString()
            var section: String? = sectionsView?.text.toString()
            var bin: String? = binsView?.text.toString()
            val count = totalProducts?.text.toString().toIntOrNull()
            val min = etMin.text.toString().toIntOrNull()
            val max = etMax.text.toString().toIntOrNull()
            val name = etName?.text.toString()

            var oldAisle = location?.aisle?:aisle
            var oldShelf = location?.shelf?:shelf
            var oldSection = location?.section?:section
            var oldBin = location?.bin?:bin

            hideKeyboard(it)
            if (count != null && count<0) {
                totalProducts?.error = "Count cannot be less than zero"
                return@setOnClickListener

            }
            if (min != null && min <0) {
                etMin?.error = "Minimum count cannot be less than zero"
                return@setOnClickListener

            }
            if (max != null && max < 0) {
                etMax?.error = "Maximum count cannot be less than zero"
                return@setOnClickListener

            }
            if(name==null){
                etName?.error="name cannot be null"
                return@setOnClickListener
            }

            if (count == null) {
                totalProducts?.error = "min should not be empty"
                return@setOnClickListener
            }
            if(min == null){
                    etMin.error = "min should not be empty"
                return@setOnClickListener
            }
            if(max == null){
                    etMax.error = "max should not be empty"
                return@setOnClickListener
            }
            if(min >= max){
                etMin.error = "min should not greater than max"
                return@setOnClickListener
            }

            if(count > max){
                totalProducts?.error = "Total count should not be greater than max"
                return@setOnClickListener
            }

            if(location == null){
                locationList?.let {
                    for (loc in it) {
                        if (aisle.equals(loc.aisle) && shelf.equals(loc.shelf) && section.equals(loc.section) && bin.equals(loc.bin)) {
                            AlertDialog.Builder(context).setMessage(" $aisle-$shelf-$section-$bin -> Location Already Exists")
                                    .setPositiveButton("ok") { dialog, _ -> dialog.dismiss() }
                                    .create().show()
                            return@setOnClickListener
                        }
                    }
                }

            }

         if (TextUtils.isEmpty(aisle) ||
                    TextUtils.isEmpty(section) ||
                    TextUtils.isEmpty(shelf) ||
                    TextUtils.isEmpty(bin)) {
                AlertDialog.Builder(context)
                        .setMessage("please select all dropdowns")
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .create().show()
                return@setOnClickListener
            }

            newLocation = Location()
            newLocation.aisle = aisle
            newLocation.shelf = shelf
            newLocation.bin = bin
            newLocation.section =section
            newLocation.count = count
            newLocation.max = max
            newLocation.min = min
            newLocation.info = name


            if(location != null && !(aisle.equals(oldAisle) && shelf.equals(oldShelf) && section.equals(oldSection) && bin.equals(oldBin))) {

                    val moveToLocation = MoveToLocation(
                            gtin,
                            gtin,
                            oldAisle,
                            oldShelf,
                            oldSection,
                            oldBin,
                            aisle,
                            shelf,
                            section,
                            bin,
                            count,
                            "full",
                            name,
                            min,
                            max
                    )

                    locationViewModel.saveLocation(moveToLocation)

            }else {
                val moveToLocation = MoveToLocation(
                        gtin,
                        gtin,
                        oldAisle,
                        oldShelf,
                        oldSection,
                        oldBin,
                        null,
                        null,
                        null,
                        null,
                        count,
                        "full",
                        name,
                        min,
                        max
                )

                locationViewModel.updateLocation(moveToLocation)
            }

        }
        cancelButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        registerListeners()
        //get All Locations
        searchServiceViewModel.productSearchBySku(gtin)
        searchServiceViewModel.productDetailsLiveData.observe(viewLifecycleOwner,
                { productDetail: ProductDetails ->
                    locationList = productDetail.productList.getLocations()
                })
        locationViewModel.getAisles()

        locationViewModel.saveLocationLiveData.observe(viewLifecycleOwner, {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            onBack?.invoke(newLocation)
            requireActivity().onBackPressed()
        })

        location?.let {
            totalProducts?.text = Editable.Factory.getInstance().newEditable(it.count.toString())

            val title = listOf(it.aisle, it.shelf, it.section, it.bin).joinToString(separator = "-")
            productToolbar.title = title

            etName?.text = Editable.Factory.getInstance().newEditable(it.info.toString())
            etMin.text = Editable.Factory.getInstance().newEditable(it.min.toString())
            etMax.text = Editable.Factory.getInstance().newEditable(it.max.toString())
        }

        return view
    }

    private fun registerListeners() {
        // register observer for aisles

        locationViewModel.allAislesLiveData.observe(viewLifecycleOwner, { it ->

            val aisles = it.response
            Log.d(_tag, "LocationViewModel onChanged:success $it")
            if (null != aisles) {
                aislesDataListAdapter.setData(aisles)
                aislesView?.setText("",false)
                Log.d(_tag, "LocationViewModel onChanged:success $aisles")

                location?.let {
                    oldAisle?.let {val selectedAisle = LocationModel(location?.aisle!!, 0, LocationHierarchyLevel.AISLE)
                        val pos = aisles.indexOf(selectedAisle)
                        oldAisle = null
                        if (pos >= 0) {
                            aislesView?.setText(aislesDataListAdapter.getItem(pos)?.name, false)
                            locationViewModel.getShelfByAisle(selectedAisle.name)
                        }  }


                }
            }
        })

        locationViewModel.shelfByAisleLiveData.observe(viewLifecycleOwner, {
            val shelves = it.response
            if (null != shelves) {
                shelvesDataListAdapter.setData(shelves)
                shelvesView?.setText("",false)

                location?.let { location ->
                    oldShelf?.let { val selectedAisle = LocationModel(location.aisle, 0, LocationHierarchyLevel.AISLE)
                        val selectedShelf = LocationModel(location.shelf, 0, LocationHierarchyLevel.SHELF)
                        val pos = shelves.indexOf(selectedShelf)
                        oldShelf = null
                        if (pos >= 0) {
                            shelvesView?.setText(shelvesDataListAdapter.getItem(pos)?.name, false)
                            locationViewModel.getSectionByAisleAndShelf(selectedAisle.name, selectedShelf.name)
                        }
                    }
                }

            }
        })
        locationViewModel.sectionByAisleAndShelfLiveData.observe(viewLifecycleOwner, {

            val sections = it.response
            // sections?.forEach { value -> System.out.println(value) }
            if (null != sections) {
                sectionsDataListAdapter.setData(sections)
                sectionsView?.setText("",false)

                location?.let { location ->

                    oldSection?.let {
                        val selectedAisle = LocationModel(location.aisle, 0, LocationHierarchyLevel.AISLE)
                        val selectedShelf = LocationModel(location.shelf, 0, LocationHierarchyLevel.SHELF)
                        val selectedSection = LocationModel(location.section, 0, LocationHierarchyLevel.SECTION)
                        val pos = sections.indexOf(selectedSection)
                        oldSection = null
                        if (pos >= 0) {
                            sectionsView?.setText(sectionsDataListAdapter.getItem(pos)?.name, false)
                            locationViewModel.getBinByAisleAndShelfAndSection(selectedAisle.name, selectedShelf.name, selectedSection.name)
                        }
                    }


                }
            }
        })

        locationViewModel.binByAisleAndShelfAndSectionLiveData.observe(viewLifecycleOwner, {

            val bins = it.response
            if (null != bins) {
                binDataListAdapter.setData(bins)
                binsView?.setText("",false)
                location?.let { location ->
                    oldBin?.let {
                        val selectedBinList = LocationModel(location.bin, 0, LocationHierarchyLevel.BIN)
                        val pos = bins.indexOf(selectedBinList)
                        oldBin = null
                        if (pos >= 0) {
                            binsView?.setText(binDataListAdapter.getItem(pos)?.name, false)
                        }
                    }


                }
            }
        })

        locationViewModel.productDetailsByLocationLiveData.observe(viewLifecycleOwner, {
            val response = it.response
            response?.let {
                if(it.isNotEmpty()){

                    val product = it[0]
                    val title = listOf(product.aisle, product.shelf, product.section, product.bin).joinToString(separator = "-")

                    etName?.text = Editable.Factory.getInstance().newEditable(product.info.toString())
                }
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Location?, param2: String?) =
                AddLocationFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_PARAM1, param1)
                        putString(ARG_GTIN, param2)
                    }
                }
    }

    override fun onClick(v: View?) {
        hideKeyboard(v)
    }

    fun setListener(onBack: (Location)->Unit) {
        this.onBack = onBack
    }
}