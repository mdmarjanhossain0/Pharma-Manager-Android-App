package com.devscore.digital_pharmacy.presentation.sales.customerlist

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.business.domain.models.Customer
import com.devscore.digital_pharmacy.presentation.util.GenericViewHolder
import kotlinx.android.synthetic.main.item_customer_list.view.*

class SalesCustomerListAdapter
constructor(
    private val interaction: Interaction? = null
)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val TAG = "CustomerListAdapter"

    companion object {

        const val IMAGE_ITEM = 1
        const val LOADING_ITEM = 2

        const val LOADING = 1
        const val RETRY =2
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType) {
            IMAGE_ITEM -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_customer_list,parent,false)
                return CustomerDataViewHolder(itemView, interaction)
            }

            LOADING_ITEM -> {
                Log.d(TAG, "onCreateViewHolder: No more results...")
                return GenericViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_loading,
                        parent,
                        false
                    )
                )
            }
        }
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_local ,parent,false)
        return CustomerDataViewHolder(itemView, interaction)
    }

    override fun getItemViewType(position: Int): Int {
        if(differ.currentList.size < (position + 1)){
            return LOADING_ITEM
        }
        Log.d(TAG, "Data Item")
        return IMAGE_ITEM
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is CustomerDataViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }

    }

    override fun getItemCount(): Int {
        Log.d(TAG, "GlobalAdapter List Size " + differ.currentList.size)
        return differ.currentList.size + 1
    }

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Customer>() {

        override fun areItemsTheSame(oldItem: Customer, newItem: Customer): Boolean {
            if (oldItem.pk == null || newItem.pk == null) {
                return oldItem.room_id == oldItem.room_id
            }
            return oldItem.pk == newItem.pk
        }

        override fun areContentsTheSame(oldItem: Customer, newItem: Customer): Boolean {
            return oldItem == newItem
        }
    }

    private val differ =
        AsyncListDiffer(
            LocalRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class LocalRecyclerChangeCallback(
        private val adapter: SalesCustomerListAdapter
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

    fun submitList(list: List<Customer>?, ){
        val newList = list?.toMutableList()
        differ.submitList(newList)
    }

    fun changeBottom(bottomState : Int) {
    }

    class CustomerDataViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Customer) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onSelectCustomer(adapterPosition, item)
            }

            itemView.customerName.setText(item.name)
            itemView.customerContactNumber.setText(item.mobile)
            itemView.customerLoyaltyPoint.setText("Loyalty Point : " + item.loyalty_point)
            itemView.customerBalance.setText("Balance : " + item.total_balance)
            if (item.address != null) {
                itemView.addressLayout.visibility = View.VISIBLE
                itemView.addressId.setText(item.address.toString())
            }
            if (item.mobile != null) {
                itemView.phoneLayout.visibility = View.VISIBLE
                itemView.phoneId.setText(item.mobile.toString())
            }
            if (item.email != null) {
                itemView.emailLayout.visibility = View.VISIBLE
                itemView.emailId.setText(item.email.toString())
            }
            if (item.whatsapp != null) {
                itemView.whatsappLayout.visibility = View.VISIBLE
                itemView.whatsappId.setText(item.whatsapp.toString())
            }
            if (item.imo != null) {
                itemView.imoLayout.visibility = View.VISIBLE
                itemView.imoId.setText(item.imo.toString())
            }

        }
    }

    interface Interaction {
        fun onSelectCustomer(position: Int, item: Customer)
    }
}