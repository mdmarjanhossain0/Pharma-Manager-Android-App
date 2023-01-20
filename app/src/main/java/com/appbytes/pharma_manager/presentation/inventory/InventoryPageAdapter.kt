package com.appbytes.pharma_manager.presentation.inventory

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.appbytes.pharma_manager.presentation.inventory.global.GlobalFragment
import com.appbytes.pharma_manager.presentation.inventory.local.LocalFragment

class InventoryPageAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {


    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {


        return when (position) {
            0 -> LocalFragment()
            1 -> GlobalFragment()
            else -> LocalFragment()
        }
    }

}