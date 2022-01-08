package com.devscore.digital_pharmacy.inventory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.inventory.adapter.InventorySubReturnsHistoryAdapter

class InventorySubReturnsHistoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       val view:View= inflater.inflate(R.layout.fragment_inventory_sub_returns_history, container, false)


        val inventoryReturnHistoryRv: RecyclerView? = view.findViewById(R.id.inventoryReturnHistoryRvId)


        if (inventoryReturnHistoryRv != null) {
            inventoryReturnHistoryRv.layoutManager = LinearLayoutManager(context)
            inventoryReturnHistoryRv.adapter = context?.let { InventorySubReturnsHistoryAdapter(it) }
        }


   return view
    }


}