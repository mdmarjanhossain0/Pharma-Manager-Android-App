package com.appbytes.pharma_manager.purchase.adapter

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.appbytes.pharma_manager.presentation.purchases.ordercompleted.PurchaseCompletedFragment
import com.appbytes.pharma_manager.presentation.purchases.orderlist.PurchasesOrdersFragment
import com.appbytes.pharma_manager.purchase.PurchaseSavedFragment

class PurchasePageAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {


    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        var fragment = Fragment()

        when (position) {
            0 -> fragment = PurchasesOrdersFragment()
            1 -> fragment = PurchaseSavedFragment()
            2 -> fragment = PurchaseCompletedFragment()
        }

        return fragment
    }

}