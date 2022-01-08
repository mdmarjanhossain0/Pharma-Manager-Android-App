package com.devscore.digital_pharmacy.presentation.inventory.add.addnonmedicine

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.util.StateMessage
import com.devscore.digital_pharmacy.presentation.inventory.add.InventoryAddProductFragmentArgs
import com.devscore.digital_pharmacy.presentation.inventory.add.InventoryAddProductPageAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_inventory_add_product.*

sealed class AddNonMedicineEvents {

    object NewAddNonMedicine : AddNonMedicineEvents()

    data class CacheState(val local_medicine : LocalMedicine): AddNonMedicineEvents()

    data class Error(val stateMessage: StateMessage): AddNonMedicineEvents()

    object OnRemoveHeadFromQueue: AddNonMedicineEvents()
}

/*
class InventoryAddProductFragment : Fragment(R.layout.fragment_inventory_add_product) {


    private val args : InventoryAddProductFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d("AppDebug", args.id.toString())
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_inventory_add_product, container, false)


        val viewPager: ViewPager2 = view.findViewById(R.id.addProductViewPagerId)
        val tabLayout: TabLayout = view.findViewById(R.id.addProductTabLayoutId)

        viewPager.adapter = childFragmentManager.let {
            InventoryAddProductPageAdapter(it, lifecycle, args.id)
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Medicine"
                1 -> tab.text = "Non Medicine"
            }
        }.attach()


        return view
    }


    override fun onDestroyView() {

        val viewPager2 = addProductViewPagerId
        viewPager2?.let {
            it.adapter = null
        }

        super.onDestroyView()
    }


    fun getLayoutRes(): Int = R.layout.fragment_inventory_add_product

}*/
