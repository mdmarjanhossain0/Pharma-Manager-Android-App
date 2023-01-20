package com.appbytes.pharma_manager.presentation.purchases.details

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.appbytes.pharma_manager.R
import com.appbytes.pharma_manager.business.domain.models.PurchasesOrderMedicine
import kotlinx.android.synthetic.main.item_sub_sales_list.view.*

class PurchasesDetailsAdapter
constructor(
    private val interaction: Interaction? = null
)
    : RecyclerView.Adapter<PurchasesDetailsAdapter.SalesOrderItemDataViewHolder>() {

    val TAG = "SalesOrderItemAdapter"




    companion object {

        const val IMAGE_ITEM = 1
        const val LOADING_ITEM = 2
        const val NOT_FOUND = 3

        const val LOADING = 1
        const val RETRY =2
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesOrderItemDataViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_details_sales_list,parent,false)
        return SalesOrderItemDataViewHolder(itemView, interaction)
    }

    override fun onBindViewHolder(holder: SalesOrderItemDataViewHolder, position: Int) {
        holder.bind(differ.currentList.get(position))
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "SalesOrderItemAdapter List Size " + differ.currentList.size)
        return differ.currentList.size
    }

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PurchasesOrderMedicine>() {

        override fun areItemsTheSame(oldItem: PurchasesOrderMedicine, newItem: PurchasesOrderMedicine): Boolean {
            return oldItem.pk == newItem.pk
        }

        override fun areContentsTheSame(oldItem: PurchasesOrderMedicine, newItem: PurchasesOrderMedicine): Boolean {
            return oldItem == newItem
        }
    }

    private val differ =
        AsyncListDiffer(
            SalesOrderItemRecyclerCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class SalesOrderItemRecyclerCallback(
        private val adapter: PurchasesDetailsAdapter
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

    fun submitList(list: List<PurchasesOrderMedicine>?){
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

        fun bind(item: PurchasesOrderMedicine) = with(itemView) {
            salesPaymentOrderItemBrandName.setText(item.brand_name)
            salesPaymentOrderItemUnitPrize.setText("৳" + (item.quantity!! * item.purchase_price!!))
            salesPaymentOrderItemUnitCount.setText(item.quantity.toString() + " " + item.unit_name.toString())
            salesPaymentOrderItemTotalAmount.setText("৳" + item.amount.toString())


            salesPaymentOrderItemDelete.visibility = View.INVISIBLE
        }
    }


    interface Interaction {

        fun onItemDelete(item : PurchasesOrderMedicine)
    }
}