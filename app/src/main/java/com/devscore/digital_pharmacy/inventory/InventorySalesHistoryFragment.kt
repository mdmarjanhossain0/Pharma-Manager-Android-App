package com.devscore.digital_pharmacy.inventory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.inventory.adapter.InventorySubSalesHistoryPageAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class InventorySalesHistoryFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view :View = inflater.inflate(R.layout.fragment_inventory_sales_history, container, false)

        val viewPager: ViewPager2 = view.findViewById(R.id.inventoryViewPagerId)
        val tabLayout: TabLayout = view.findViewById(R.id.salesTabLayoutId)

        viewPager.adapter = fragmentManager?.let { InventorySubSalesHistoryPageAdapter(it, lifecycle) }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Sales History"
                1 -> tab.text = "Returns History"
                2 -> tab.text = "Purchase History"
                3 -> tab.text = "Overview History"
            }
        }.attach()


        return view
    }


}