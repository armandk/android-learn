package com.example.dsi_android_ui.location

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dsi_android_ui.BaseFragment
import com.example.dsi_android_ui.R
import com.example.dsi_android_ui.utils.GenericResponse
import com.example.dsi_android_ui.viewmodel.location.LocationViewModel
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LocationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LocationFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val _tag = "LocationFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_location, container, false)

        val recyclerView = view.findViewById(R.id.location_list) as RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = LinearLayoutManager(context)
        val locationViewModel = ViewModelProvider(this, NewInstanceFactory()).get(LocationViewModel::class.java)
        val adapter =object  : MyLocationAdpater(ArrayList()){
            override fun recyclerviewClick(v: Any?) {
              Toast.makeText(requireContext(),"On Item click",Toast.LENGTH_LONG).show()

                val position : Int = v as Int
                val aisle = locationViewModel.allAislesLiveData.value?.response?.get(position)
                aisle?.let {
                val transaction = activity!!.supportFragmentManager.beginTransaction()
                transaction.replace(R.id.container,  LocationDetailFragment.newInstance(it.name) )
                transaction.addToBackStack(null)
                transaction.commit()
                }
            }
        }
        recyclerView.adapter = adapter


        //location

        locationViewModel.allAislesLiveData.observe(viewLifecycleOwner, Observer { genericResponse ->
            val state = genericResponse.state
            if (state == GenericResponse.State.LOADING) {
                Log.d(_tag, "LocationViewModel onChanged:Loading ")
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
                adapter.setData(response)
                Log.d(_tag, "LocationViewModel onChanged:success $response")
            }
        })



        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)

        //productToolbar.setTitle("Title");
        //productToolbar.setTitle("Title");
        val navigationIcon = resources.getDrawable(R.drawable.ic_arrow_back_24dp, null)
        toolbar.navigationIcon = navigationIcon
        toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
        //TODO set Action


        locationViewModel.getAisles()
        //end location
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LocationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                LocationFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}