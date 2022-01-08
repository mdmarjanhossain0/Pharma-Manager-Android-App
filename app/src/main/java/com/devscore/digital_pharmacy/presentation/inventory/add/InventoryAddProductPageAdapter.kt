package com.devscore.digital_pharmacy.presentation.inventory.add

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.devscore.digital_pharmacy.presentation.inventory.add.addmedicine.AddProductSubMedicineFragment
import com.devscore.digital_pharmacy.presentation.inventory.add.addnonmedicine.AddProductSubNonMedicineFragment

class InventoryAddProductPageAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    val medicineArgs : Int
) : FragmentStateAdapter(fragmentManager, lifecycle) {


    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        var fragment = Fragment()

        when (position) {
            0 -> {
                fragment = AddProductSubMedicineFragment()
                val data = bundleOf("id" to medicineArgs)
                fragment.arguments = data
            }
            1 -> fragment = AddProductSubNonMedicineFragment()
        }

        return fragment
    }

}