package com.devscore.digital_pharmacy.presentation.main.notification

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.business.domain.models.Notification
import com.devscore.digital_pharmacy.presentation.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.item_notification_list.view.*

class NotificationAdapter
    :
    RecyclerView.Adapter<NotificationAdapter.SalesOrdersViewHolder>() {


    var recyclerItemAdapterStock : NotificationItemAdapter? = null
    var recyclerItemAdapterExpired : NotificationItemAdapter? = null
    var itemNumber = 0




    private var list : List<Notification>? = null

    class SalesOrdersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesOrdersViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_notification_list, parent, false)
        return SalesOrdersViewHolder(view)
    }

    override fun onBindViewHolder(holder: SalesOrdersViewHolder, position: Int) {
//        holder.itemView.setOnClickListener {
//        }
        Log.d("AppDebug", "onBind")
        holder.itemView.numberOfItem.setText("Notification " + itemNumber.toString() )
        holder.itemView.salesOrderItemRvId.apply {
            layoutManager = LinearLayoutManager(context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator)
            hasFixedSize()
            addItemDecoration(topSpacingDecorator)
            if (position == 0) {
                if (recyclerItemAdapterStock == null) {
                    recyclerItemAdapterStock = NotificationItemAdapter()
                }
                adapter = recyclerItemAdapterStock
            }
            else {
                if (recyclerItemAdapterExpired == null) {
                    recyclerItemAdapterExpired = NotificationItemAdapter()
                }
                adapter = recyclerItemAdapterExpired
            }





            if (list != null) {
                if (position == 0) {
                    holder.itemView.numberOfItem.setText("Out of stock " + list?.filter { it.type.equals("Stock") }!!.size )
                }
                else {
                    holder.itemView.numberOfItem.setText("Expired " + list?.filter { it.type.equals("Expired") }!!.size )
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return 2
    }

    fun submitList(list : List<Notification>) {
        if (recyclerItemAdapterStock == null) {
            recyclerItemAdapterStock = NotificationItemAdapter()
        }




        if (recyclerItemAdapterExpired == null) {
            recyclerItemAdapterExpired = NotificationItemAdapter()
        }
        recyclerItemAdapterStock?.submitList(list.filter {
            it.type.equals("Stock")
        })




        recyclerItemAdapterExpired?.submitList(list.filter {
            it.type.equals("Expired")
        })
        itemNumber = list.size

//        if (list != null) {
//            if (position == 0) {
//                holder.itemView.numberOfItem.setText("Out of stock " + list?.filter { it.type.equals("Stock") }!!.size )
//                submitList(list?.filter { it.type.equals("Stock") }!!)
//            }
//            else {
//                holder.itemView.numberOfItem.setText("Out of stock " + list?.filter { it.type.equals("Expired") }!!.size )
//                submitList(list?.filter { it.type.equals("Expired") }!!)
//            }
//        }


        this.list = list
        notifyDataSetChanged()
    }
}