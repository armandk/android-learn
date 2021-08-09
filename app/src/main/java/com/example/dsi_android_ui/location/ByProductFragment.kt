package com.example.dsi_android_ui.location

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.dsi_android_ui.BaseFragment
import com.example.dsi_android_ui.R
import com.example.dsi_android_ui.utils.GenericResponse
import com.example.dsi_android_ui.viewmodel.location.LocationViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"


class ByProductFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var alise: String? = null
    private val TAG = "ByProductFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            alise = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_by_product, container, false)

        //location
        val locationViewModel = ViewModelProvider(this).get<LocationViewModel>(LocationViewModel::class.java)
        locationViewModel.shelfDetailsByAisleLiveData.observe(viewLifecycleOwner, Observer { genericResponse ->
            val state = genericResponse.state
            if (state == GenericResponse.State.LOADING) {
                Log.d(TAG, "LocationViewModel onChanged:Locading ")
                showProgressLoading(null)
                return@Observer
            }
            if (state == GenericResponse.State.FAILURE) {
                hideProgressDialog()
                Log.d(TAG, "LocationViewModel onChanged:failure " + genericResponse.error.errorMessage)
                return@Observer
            }
            if (state == GenericResponse.State.SUCCESS) {
                hideProgressDialog()
                val response = genericResponse.response
                //TODO - with response
                Log.d(TAG, "LocationViewModel onChanged:success $response")
            }
        })
        alise?.let { locationViewModel.getShelfDetailsByAisle(it) }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
                ByProductFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                    }
                }
    }
}