package com.devscore.digital_pharmacy.inventory.adapter

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.devscore.digital_pharmacy.inventory.*

class InventorySubSalesHistoryPageAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {


    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        var fragment = Fragment()

        when (position) {
            0 -> fragment = InventorySubSalesHistoryFragment()
            1 -> fragment = InventorySubReturnsHistoryFragment()
            2 -> fragment = InventorySubPurchaseHistoryFragment()
            3 -> fragment = InventorySubOverviewHistoryFragment()
        }

        return fragment
    }

}