package com.devscore.digital_pharmacy.presentation.inventory.inventoryreturn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devscore.digital_pharmacy.R

class InventoryReturnFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view: View = inflater.inflate(R.layout.fragment_return1, container, false)

        val return1Rv: RecyclerView? = view.findViewById(R.id.returnRvId)

        if (return1Rv != null) {
            return1Rv.layoutManager = LinearLayoutManager(context)
            return1Rv.adapter = context?.let { InventoryReturnAdapter(it) }
        }

        return view
    }


}