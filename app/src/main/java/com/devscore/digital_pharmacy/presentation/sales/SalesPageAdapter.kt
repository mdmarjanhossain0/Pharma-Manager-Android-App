package com.devscore.digital_pharmacy.presentation.sales

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.devscore.digital_pharmacy.presentation.inventory.global.GlobalFragment
import com.devscore.digital_pharmacy.presentation.inventory.local.LocalFragment
import com.devscore.digital_pharmacy.presentation.sales.odercompleted.SalesCompletedFragment
import com.devscore.digital_pharmacy.presentation.sales.orderlist.SalesOrdersFragment

class SalesPageAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {


    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {


        return when (position) {
            0 -> SalesOrdersFragment()
            1 -> SalesCompletedFragment()
            else -> SalesOrdersFragment()
        }
    }

}