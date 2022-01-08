package com.devscore.digital_pharmacy.inventory.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devscore.digital_pharmacy.R

class InventorySubSalesHistoryAdapter(val context: Context) :
    RecyclerView.Adapter<InventorySubSalesHistoryAdapter.InventorySubSalesHistoryViewHolder>() {

    class InventorySubSalesHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventorySubSalesHistoryViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_inventory_sales_history, parent, false)
        return InventorySubSalesHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: InventorySubSalesHistoryViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            //  context.startActivity(Intent(context, BuyCourseActivity::class.java))
        }
    }

    override fun getItemCount(): Int {
        return 10
    }
}