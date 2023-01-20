package com.appbytes.pharma_manager.presentation.sales.payment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appbytes.pharma_manager.R
import com.appbytes.pharma_manager.business.domain.models.SalesOrder
import com.appbytes.pharma_manager.presentation.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.item_sales_list.*
import kotlinx.android.synthetic.main.item_sales_list.view.*

class SalesOrdersAdapter
    constructor(
        private val interaction: SalesOrderItemAdapter.Interaction? = null
    )
    :
    RecyclerView.Adapter<SalesOrdersAdapter.SalesOrdersViewHolder>() {


    var recyclerItemAdapter : SalesOrderItemAdapter? = null
    var itemNumber = 0

    class SalesOrdersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesOrdersViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_sales_list, parent, false)
        return SalesOrdersViewHolder(view)
    }

    override fun onBindViewHolder(holder: SalesOrdersViewHolder, position: Int) {
//        holder.itemView.setOnClickListener {
//        }
        Log.d("AppDebug", "onBind")
        holder.itemView.numberOfItem.setText("Item list " + itemNumber.toString() )
        holder.itemView.salesOrderItemRvId.apply {
            layoutManager = LinearLayoutManager(context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator)
            hasFixedSize()
            addItemDecoration(topSpacingDecorator)
            if (recyclerItemAdapter == null) {
                recyclerItemAdapter = SalesOrderItemAdapter(interaction)
            }
            adapter = recyclerItemAdapter
//            setDivider(R.drawable.blue_divider)
        }
    }

    override fun getItemCount(): Int {
        return 1
    }

    fun submitList(order: SalesOrder) {
        if (recyclerItemAdapter == null) {
            recyclerItemAdapter = SalesOrderItemAdapter(interaction)
        }
        recyclerItemAdapter?.submitList(order.sales_oder_medicines)
        itemNumber = order.sales_oder_medicines?.size!!
        notifyDataSetChanged()
    }
}