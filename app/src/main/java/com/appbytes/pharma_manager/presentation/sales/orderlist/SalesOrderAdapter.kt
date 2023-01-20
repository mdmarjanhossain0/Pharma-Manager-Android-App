package com.appbytes.pharma_manager.presentation.sales.orderlist

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.appbytes.pharma_manager.R
import com.appbytes.pharma_manager.business.domain.models.SalesOrder
import com.appbytes.pharma_manager.presentation.util.GenericViewHolder
import kotlinx.android.synthetic.main.item_sales_orders.view.*
import java.text.SimpleDateFormat
import java.util.*

class SalesOrderAdapter
constructor(
    private val interaction: Interaction? = null
)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val TAG = "SalesOrderAdapter"

    var isLoading : Boolean = true

    val loadingItem = SalesOrder (
        pk = -2,
        customer = -1,
        customer_name = null,
        mobile = null,
        total_amount = 0f,
        total_after_discount = .05f,
        paid_amount = 0f,
        discount = 0f,
        is_discount_percent = false,
        is_return = false,
        status = 0,
        created_at = "",
        updated_at = "",
        sales_oder_medicines = null
    )



    val notFound = SalesOrder (
        pk = -3,
        customer = -1,
        customer_name = null,
        mobile = null,
        total_amount = 0f,
        total_after_discount = 0f,
        paid_amount = 0f,
        discount = 0f,
        is_discount_percent = false,
        is_return = false,
        status = 0,
        created_at = "",
        updated_at = "",
        sales_oder_medicines = null
    )


    companion object {

        const val IMAGE_ITEM = 1
        const val LOADING_ITEM = 2
        const val NOT_FOUND = 3

        const val LOADING = 1
        const val RETRY =2
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType) {
            IMAGE_ITEM -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_sales_orders,parent,false)
                return SalesOrderDataViewHolder(itemView, interaction)
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
            else -> {
                return GenericViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_result_not_found,
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (differ.currentList.size != 0) {
            if(differ.currentList.get(position).pk == -2){
                return LOADING_ITEM
            }
            if(differ.currentList.get(position).pk == -3){
                return NOT_FOUND
            }
            return IMAGE_ITEM
        }
        else {
            return LOADING_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is SalesOrderDataViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }

    }

    override fun getItemCount(): Int {
        Log.d(TAG, "SalesOrderAdapter List Size " + differ.currentList.size)
        return differ.currentList.size
    }

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SalesOrder>() {

        override fun areItemsTheSame(oldItem: SalesOrder, newItem: SalesOrder): Boolean {
            return oldItem.pk == newItem.pk
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: SalesOrder, newItem: SalesOrder): Boolean {
            return oldItem == newItem
        }
    }

    private val differ =
        AsyncListDiffer(
            GlobalRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class GlobalRecyclerChangeCallback(
        private val adapter: SalesOrderAdapter
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

    fun submitList(list: List<SalesOrder>?, isLoading : Boolean = true, queryExhausted : Boolean = false){
        val newList = list?.toMutableList()
        if (isLoading) {
            newList?.add(loadingItem)
        }
        else {
            if (queryExhausted) {
                newList?.add(notFound)
            }
        }
        differ.submitList(newList)
    }

    fun changeBottom(bottomState : Int) {
    }

    class SalesOrderDataViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: SalesOrder) = with(itemView) {

            if (item.created_at != null) {
                val timeZone = TimeZone.getTimeZone("Asia/Dhaka")
                val dateFormatter = SimpleDateFormat("dd/MM/yyyy hh:mm")
                val sourceFormat = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
                val destFormat = SimpleDateFormat("dd/MM/yyyy HH:mm aa")
                sourceFormat.timeZone = timeZone
                val convertedDate = sourceFormat.parse(item.created_at)
                val newDate = destFormat.format(convertedDate)

                itemView.orderDateTime.setText(newDate)
                Log.d("AppDebug", " " + item.created_at + " " + newDate)
            }

            itemView.orderProcess.setOnClickListener {
                interaction?.onItemProcess(adapterPosition, item)
            }

            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }




            if (item.pk != null && item.pk!! > 0) {
                itemView.orderId.setText("Order ID : # " + item.pk.toString())
                var orderMedicines : String? = ""
                var count = 0
                for (medicine in item.sales_oder_medicines!!) {
                    count = count + 1
                    orderMedicines = orderMedicines + medicine.brand_name
                    if (count < 2) {
                        orderMedicines = orderMedicines + "\n"
                    }
                    if (count > 2) {
                        orderMedicines = orderMedicines + "\n"
                        orderMedicines = orderMedicines + (item?.sales_oder_medicines!!.size!! - 2).toString() + " more medicines"
                        break
                    }
                }
                itemView.orderMedicines.setText(orderMedicines)


                itemView.salesGenerateDelete.visibility = View.VISIBLE
            }
            else {
                val id = SpannableString("Draft : # " + item.room_id.toString())
                id.setSpan(ForegroundColorSpan(Color.RED), 0, id.length, 1)
//                itemView.orderId.setTextColor(Color.RED)
                itemView.orderId.setText(id)
            }
            orderMRP.setText("Total: ৳ " + item.total_after_discount!!)
            itemView.salesGenerateDelete.setOnClickListener {
                interaction?.onItemDelete(adapterPosition, item)
            }
        }
    }

    interface Interaction {

        fun onItemSelected(position: Int, item: SalesOrder)

        fun onItemProcess(position: Int, item: SalesOrder)


        fun onItemRetrun(position: Int, item: SalesOrder)




        fun onItemDelete(position: Int, item: SalesOrder)

        fun restoreListPosition()

        fun nextPage()
    }
}