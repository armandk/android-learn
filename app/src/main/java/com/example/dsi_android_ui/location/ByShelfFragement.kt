package com.example.dsi_android_ui.location

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
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

/**
 * A simple [Fragment] subclass.
 * Use the [ByShelfFragement.newInstance] factory method to
 * create an instance of this fragment.
 */
class ByShelfFragement : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var aisles: String? = null
    private val _tag = "ByShelfFragement"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            aisles = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_by_shelf_fragement, container, false)

        //location
        val locationViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get<LocationViewModel>(LocationViewModel::class.java)
        locationViewModel.shelfByAisleLiveData.observe(viewLifecycleOwner, Observer { genericResponse ->
            val state = genericResponse.state
            if (state == GenericResponse.State.LOADING) {
                Log.d(_tag, "LocationViewModel onChanged:Locading ")
                showProgressLoading(null)
                return@Observer
            }
            if (state == GenericResponse.State.FAILURE) {
                hideProgressDialog()
                Log.d(_tag, "LocationViewModel onChanged:failure " + genericResponse.error.errorMessage)
                return@Observer
            }
            if (state == GenericResponse.State.SUCCESS) {
                hideProgressDialog()
                val response = genericResponse.response
                //TODO - with response
                Log.d(_tag, "LocationViewModel onChanged:success $response")
            }
        })
        aisles?.let { locationViewModel.getShelfByAisle(it) }


        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment ByShellFragement.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
                ByShelfFragement().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                    }
                }
    }
}