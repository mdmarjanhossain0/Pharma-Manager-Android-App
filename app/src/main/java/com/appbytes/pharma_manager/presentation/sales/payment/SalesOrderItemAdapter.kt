package com.appbytes.pharma_manager.presentation.sales.payment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.appbytes.pharma_manager.R
import com.appbytes.pharma_manager.business.domain.models.SalesOrderMedicine
import kotlinx.android.synthetic.main.item_sub_sales_list.view.*

class SalesOrderItemAdapter
    constructor(
        private val interaction: Interaction? = null
    )
    : RecyclerView.Adapter<SalesOrderItemAdapter.SalesOrderItemDataViewHolder>() {

    val TAG = "SalesOrderItemAdapter"




    companion object {

        const val IMAGE_ITEM = 1
        const val LOADING_ITEM = 2
        const val NOT_FOUND = 3

        const val LOADING = 1
        const val RETRY =2
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesOrderItemDataViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_sub_sales_list,parent,false)
        return SalesOrderItemDataViewHolder(itemView, interaction)
    }

    override fun onBindViewHolder(holder: SalesOrderItemDataViewHolder, position: Int) {
        holder.bind(differ.currentList.get(position))
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "SalesOrderItemAdapter List Size " + differ.currentList.size)
        return differ.currentList.size
    }

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SalesOrderMedicine>() {

        override fun areItemsTheSame(oldItem: SalesOrderMedicine, newItem: SalesOrderMedicine): Boolean {
            return oldItem.pk == newItem.pk
        }

        override fun areContentsTheSame(oldItem: SalesOrderMedicine, newItem: SalesOrderMedicine): Boolean {
            return oldItem == newItem
        }
    }

    private val differ =
        AsyncListDiffer(
            SalesOrderItemRecyclerCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class SalesOrderItemRecyclerCallback(
        private val adapter: SalesOrderItemAdapter
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

    fun submitList(list: List<SalesOrderMedicine>?){
        differ.submitList(list)
        notifyDataSetChanged()
    }

    fun changeBottom(bottomState : Int) {
    }

    class SalesOrderItemDataViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction? = null
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: SalesOrderMedicine) = with(itemView) {
            salesPaymentOrderItemBrandName.setText(item.brand_name)
            salesPaymentOrderItemUnitPrize.setText("৳" + (item.quantity!! * item.mrp!!))
            salesPaymentOrderItemUnitCount.setText(item.quantity.toString() + " " + item.unit_name.toString())
            salesPaymentOrderItemTotalAmount.setText("৳" + item.amount.toString())


            salesPaymentOrderItemDelete.setOnClickListener {
                interaction?.onItemDelete(item)
            }
        }
    }


    interface Interaction {

        fun onItemDelete(item : SalesOrderMedicine)
    }
}