package com.appbytes.pharma_manager.presentation.sales

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.appbytes.pharma_manager.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_inventory.*


class SalesFragment : Fragment(R.layout.fragment_sales) {

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//
//        val view: View = inflater.inflate(R.layout.fragment_inventory, container, false)
//
//
//
//
//        return view
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager: ViewPager2 = view.findViewById(R.id.salesViewPagerId)
        val tabLayout: TabLayout = view.findViewById(R.id.salesTabLayoutId)

        viewPager.adapter =
            SalesPageAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Orders"
                1 -> tab.text = "Completed"
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


    fun getLayoutRes(): Int = R.layout.fragment_sales


}