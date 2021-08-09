package com.example.dsi_android_ui.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.dsi_android_ui.BaseFragment
import com.example.dsi_android_ui.R
import com.google.android.material.tabs.TabLayout

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [LocationDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LocationDetailFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            name = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
         val view =  inflater.inflate(R.layout.fragment_location_detail, container, false)
        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)

        toolbar.setTitle("Location")
        val navigationIcon = resources.getDrawable(R.drawable.ic_arrow_back_24dp, null)
        toolbar.navigationIcon = navigationIcon
        toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
        val viewPager :ViewPager =view.findViewById(R.id.view_pager)

        if(name == null)
            name = ""
       viewPager.adapter = activity?.supportFragmentManager?.let { LocationViewPagerAdapter(it, name!!) }
        val tabs :TabLayout = view.findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment LocationDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
                LocationDetailFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                    }
                }
    }
}