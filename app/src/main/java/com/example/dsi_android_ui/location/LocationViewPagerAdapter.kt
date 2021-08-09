package com.example.dsi_android_ui.location

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter



class LocationViewPagerAdapter(fm: FragmentManager, val aisle: String) : FragmentPagerAdapter(fm) {
    private val TAB_TITLE= arrayOf("By Shelf","By Product")
    override fun getItem(position: Int): Fragment {

        return when(position)
        {
            0 -> {

                //TODO need to change
                return ByShelfFragement.newInstance(aisle)
            }
           else ->{
               return ByProductFragment.newInstance(aisle)
            }
        }
    }

    override fun getCount(): Int {
      return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        return TAB_TITLE[position]
    }

}
