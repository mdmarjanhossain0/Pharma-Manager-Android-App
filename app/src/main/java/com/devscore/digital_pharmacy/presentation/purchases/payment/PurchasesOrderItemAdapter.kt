package com.devscore.digital_pharmacy.presentation.purchases.payment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.business.domain.models.PurchasesCart
import kotlinx.android.synthetic.main.item_sub_sales_list.view.*

class PurchasesOrderItemAdapter
constructor(
    private val interaction: Interaction? = null
)
    : RecyclerView.Adapter<PurchasesOrderItemAdapter.PurchasesOrderItemDataViewHolder>() {

    val TAG = "SalesOrderItemAdapter"




    companion object {

        const val IMAGE_ITEM = 1
        const val LOADING_ITEM = 2
        const val NOT_FOUND = 3

        const val LOADING = 1
        const val RETRY =2
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchasesOrderItemDataViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_sub_sales_list,parent,false)
        return PurchasesOrderItemDataViewHolder(itemView, interaction)
    }

    override fun onBindViewHolder(holder: PurchasesOrderItemDataViewHolder, position: Int) {
        holder.bind(differ.currentList.get(position))
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "SalesOrderItemAdapter List Size " + differ.currentList.size)
        return differ.currentList.size
    }

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PurchasesCart>() {

        override fun areItemsTheSame(oldItem: PurchasesCart, newItem: PurchasesCart): Boolean {
            return oldItem.medicine?.id == newItem.medicine?.id
        }

        override fun areContentsTheSame(oldItem: PurchasesCart, newItem: PurchasesCart): Boolean {
            return oldItem == newItem
        }
    }

    private val differ =
        AsyncListDiffer(
            SalesOrderItemRecyclerCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class SalesOrderItemRecyclerCallback(
        private val adapter: PurchasesOrderItemAdapter
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

    fun submitList(list: List<PurchasesCart>?){
        differ.submitList(list)
        notifyDataSetChanged()
    }

    fun changeBottom(bottomState : Int) {
    }

    class PurchasesOrderItemDataViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction? = null
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: PurchasesCart) = with(itemView) {
            salesPaymentOrderItemBrandName.setText(item.medicine?.brand_name)
            salesPaymentOrderItemUnitPrize.setText("৳" + (item.purchasesUnit?.quantity!! * item.medicine?.mrp!!))
            salesPaymentOrderItemUnitCount.setText(item.quantity.toString() + " " + item.purchasesUnit?.name.toString())
            salesPaymentOrderItemTotalAmount.setText("৳" + item.amount.toString())


            salesPaymentOrderItemDelete.setOnClickListener {
                interaction?.onItemDelete(item)
            }
        }
    }


    interface Interaction {

        fun onItemDelete(item : PurchasesCart)
    }
}