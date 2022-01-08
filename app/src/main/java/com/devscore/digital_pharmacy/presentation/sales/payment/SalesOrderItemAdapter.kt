package com.devscore.digital_pharmacy.presentation.sales.payment

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.business.domain.models.SalesCart
import com.devscore.digital_pharmacy.business.domain.models.SalesOrderMedicine
import com.devscore.digital_pharmacy.presentation.sales.card.SalesCardAdapter
import com.devscore.digital_pharmacy.presentation.util.GenericViewHolder
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

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SalesCart>() {

        override fun areItemsTheSame(oldItem: SalesCart, newItem: SalesCart): Boolean {
            return oldItem.medicine?.id == newItem.medicine?.id
        }

        override fun areContentsTheSame(oldItem: SalesCart, newItem: SalesCart): Boolean {
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

    fun submitList(list: List<SalesCart>?){
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

        fun bind(item: SalesCart) = with(itemView) {
            salesPaymentOrderItemBrandName.setText(item.medicine?.brand_name)
            salesPaymentOrderItemUnitPrize.setText("৳" + (item.salesUnit?.quantity!! * item.medicine?.mrp!!))
            salesPaymentOrderItemUnitCount.setText(item.quantity.toString() + " " + item.salesUnit?.name.toString())
            salesPaymentOrderItemTotalAmount.setText("৳" + item.amount.toString())


            salesPaymentOrderItemDelete.setOnClickListener {
                interaction?.onItemDelete(item)
            }
        }
    }


    interface Interaction {

        fun onItemDelete(item : SalesCart)
    }
}