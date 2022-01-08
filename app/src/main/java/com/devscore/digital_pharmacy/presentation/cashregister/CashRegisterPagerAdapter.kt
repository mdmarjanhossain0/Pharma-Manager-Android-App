package com.devscore.digital_pharmacy.presentation.cashregister

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.devscore.digital_pharmacy.presentation.cashregister.payment.VendorPaymentFragment
import com.devscore.digital_pharmacy.presentation.cashregister.receive.VendorReceiveFragment
import com.devscore.digital_pharmacy.presentation.inventory.global.GlobalFragment
import com.devscore.digital_pharmacy.presentation.inventory.local.LocalFragment

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