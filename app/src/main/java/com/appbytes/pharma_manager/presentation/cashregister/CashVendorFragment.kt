package com.appbytes.pharma_manager.presentation.cashregister

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.appbytes.pharma_manager.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_inventory.*


class CashVendorFragment : Fragment(R.layout.fragment_cash_vendor) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager: ViewPager2 = view.findViewById(R.id.vendorViewPagerId)
        val tabLayout: TabLayout = view.findViewById(R.id.vendorTabLayoutId)

        viewPager.adapter =
            CashRegisterPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Receive"
                1 -> tab.text = "Payment"
            }
        }.attach()
    }


    override fun onDestroyView() {

        val viewPager2 = viewPager
        viewPager2?.let {
            it.adapter = null
        }

        super.onDestroyView()
    }


    fun getLayoutRes(): Int = R.layout.fragment_cash_vendor


}