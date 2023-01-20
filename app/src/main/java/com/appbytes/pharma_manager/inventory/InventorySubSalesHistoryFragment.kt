package com.appbytes.pharma_manager.inventory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appbytes.pharma_manager.R
import com.appbytes.pharma_manager.inventory.adapter.InventorySubSalesHistoryAdapter


class InventorySubSalesHistoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view:View= inflater.inflate(R.layout.fragment_inventory_sub_sales_history, container, false)


        val inventorySalesHistoryRv: RecyclerView? = view.findViewById(R.id.inventorySalesHistoryRvId)


        if (inventorySalesHistoryRv != null) {
            inventorySalesHistoryRv.layoutManager = LinearLayoutManager(context)
            inventorySalesHistoryRv.adapter = context?.let { InventorySubSalesHistoryAdapter(it) }
        }

        return view
    }


}