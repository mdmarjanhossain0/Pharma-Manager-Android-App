package com.devscore.digital_pharmacy.presentation.purchases

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.devscore.digital_pharmacy.presentation.purchases.ordercompleted.PurchaseCompletedFragment
import com.devscore.digital_pharmacy.presentation.purchases.orderlist.PurchasesOrdersFragment
import com.devscore.digital_pharmacy.presentation.sales.odercompleted.SalesCompletedFragment
import com.devscore.digital_pharmacy.presentation.sales.orderlist.SalesOrdersFragment

class PurchasesPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {


    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {


        return when (position) {
            0 -> PurchasesOrdersFragment()
            1 -> PurchaseCompletedFragment()
            else -> PurchasesOrdersFragment()
        }
    }

}