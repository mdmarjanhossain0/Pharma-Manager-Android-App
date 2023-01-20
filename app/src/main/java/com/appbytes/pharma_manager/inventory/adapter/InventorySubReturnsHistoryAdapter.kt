package com.appbytes.pharma_manager.inventory.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appbytes.pharma_manager.R

class InventorySubReturnsHistoryAdapter(val context: Context) :
    RecyclerView.Adapter<InventorySubReturnsHistoryAdapter.InventorySubReturnsHistoryViewHolder>() {

    class InventorySubReturnsHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventorySubReturnsHistoryViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_inventory_returns_history, parent, false)
        return InventorySubReturnsHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: InventorySubReturnsHistoryViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            //  context.startActivity(Intent(context, BuyCourseActivity::class.java))
        }
    }

    override fun getItemCount(): Int {
        return 10
    }
}