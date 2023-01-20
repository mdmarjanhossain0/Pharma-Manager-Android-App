package com.appbytes.pharma_manager.presentation.cashregister

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.appbytes.pharma_manager.presentation.cashregister.payment.VendorPaymentFragment
import com.appbytes.pharma_manager.presentation.cashregister.receive.VendorReceiveFragment

class CashRegisterPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {


    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {


        return when (position) {
            0 -> VendorReceiveFragment()
            1 -> VendorPaymentFragment()
            else -> VendorReceiveFragment()
        }
    }

}