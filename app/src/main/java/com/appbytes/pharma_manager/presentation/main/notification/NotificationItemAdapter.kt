package com.appbytes.pharma_manager.presentation.main.notification

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.appbytes.pharma_manager.R
import com.appbytes.pharma_manager.business.domain.models.Notification
import kotlinx.android.synthetic.main.item_notification.view.*
import kotlinx.android.synthetic.main.item_sub_sales_list.view.*

class NotificationItemAdapter
constructor(
//    private val interaction: Interaction? = null
)
    : RecyclerView.Adapter<NotificationItemAdapter.SalesOrderItemDataViewHolder>() {

    val TAG = "SalesOrderItemAdapter"




    companion object {

        const val IMAGE_ITEM = 1
        const val LOADING_ITEM = 2
        const val NOT_FOUND = 3

        const val LOADING = 1
        const val RETRY =2
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesOrderItemDataViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_notification,parent,false)
        return SalesOrderItemDataViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SalesOrderItemDataViewHolder, position: Int) {
        holder.bind(differ.currentList.get(position))
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "SalesOrderItemAdapter List Size " + differ.currentList.size)
        return differ.currentList.size
    }

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Notification>() {

        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem == newItem
        }
    }

    private val differ =
        AsyncListDiffer(
            SalesOrderItemRecyclerCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class SalesOrderItemRecyclerCallback(
        private val adapter: NotificationItemAdapter
    ) : ListUpdateCallback {

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapter.notifyItemRangeChanged(position, count, payload)
        }

        override fun onInserted(position: Int, count: Int) {
            adapter.notifyItemRangeChanged(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapter.notifyDataSetChanged()
        }

        override fun onRemoved(position: Int, count: Int) {
            adapter.notifyDataSetChanged()
        }
    }

    fun submitList(list: List<Notification>?){
        differ.submitList(list)
        notifyDataSetChanged()
    }

    fun changeBottom(bottomState : Int) {
    }

    class SalesOrderItemDataViewHolder
    constructor(
        itemView: View,
//        private val interaction: Interaction? = null
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Notification) = with(itemView) {



            itemView.medicineName.setText(item.brand_name)
            itemView.stock.setText("Stock : " + item.stock)
            if (item.type.equals("Stock")) {
                itemView.stock.setText("Stock : " + item.stock)
            }
            else {
                itemView.stock.setText(item.exp_date)
            }
        }
    }


//    interface Interaction {
//
//        fun onItemDelete(item : SalesCart)
//    }
}