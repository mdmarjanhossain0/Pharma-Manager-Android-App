package com.devscore.digital_pharmacy.presentation.inventory.global

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.business.domain.models.GlobalMedicine
import com.devscore.digital_pharmacy.presentation.util.GenericViewHolder
import kotlinx.android.synthetic.main.item_global.view.*

class GlobalAdapter
constructor(
    private val interaction: Interaction? = null
)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val TAG = "GlobalAdapter"

    var isLoading : Boolean = true

    val loadingItem = GlobalMedicine (
        id = -2,
        brand_name = "",
        sku = "",
        darNumber = "",
        mrNumber = "",
        generic = "",
        indication = "",
        symptom = "",
        strength = "",
        description = "",
        mrp = 0f,
        purchases_price = 0f,
        manufacture = "",
        kind = "",
        form = "",
        createdAt = "",
        updatedAt = ""
            )



    val notFound = GlobalMedicine (
        id = -3,
        brand_name = "",
        sku = "",
        darNumber = "",
        mrNumber = "",
        generic = "",
        indication = "",
        symptom = "",
        strength = "",
        description = "",
        mrp = 0f,
        purchases_price = 0f,
        manufacture = "",
        kind = "",
        form = "",
        createdAt = "",
        updatedAt = ""
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
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_global,parent,false)
                return GlobalDataViewHolder(itemView, interaction)
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
            if(differ.currentList.get(position).id == -2){
                return LOADING_ITEM
            }
            if(differ.currentList.get(position).id == -3){
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
            is GlobalDataViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }

    }

    override fun getItemCount(): Int {
        Log.d(TAG, "GlobalAdapter List Size " + differ.currentList.size)
        return differ.currentList.size
    }

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<GlobalMedicine>() {

        override fun areItemsTheSame(oldItem: GlobalMedicine, newItem: GlobalMedicine): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GlobalMedicine, newItem: GlobalMedicine): Boolean {
            return oldItem == newItem
        }
    }

    private val differ =
        AsyncListDiffer(
            GlobalRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class GlobalRecyclerChangeCallback(
        private val adapter: GlobalAdapter
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

    fun submitList(medicineList: List<GlobalMedicine>?, isLoading : Boolean = true, queryExhausted : Boolean = false){
        val newList = medicineList?.toMutableList()
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

    class GlobalDataViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: GlobalMedicine) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }






            itemView.cartImgId.setOnClickListener {
                interaction?.onAddMedicine(adapterPosition, item)
            }

            itemView.globalBrandNameTV.setText(item.brand_name)
            itemView.globalCompanyNameTV.setText(item.generic)
            if (item.mrp != null) {
                itemView.globalMRPTV.setText("MRP : ৳ "+ item.mrp.toString())
                itemView.globalPPTV.setText("PP : ৳ "+ item.purchases_price.toString())
            }
            else {
                itemView.globalMRPTV.setText("MRP ৳ ...")
            }







            if (item.manufacture != null) {
                itemView.globalManufactureName.setText(item.manufacture)
            }
        }
    }

    interface Interaction {

        fun onItemSelected(position: Int, item: GlobalMedicine)

        fun onAddMedicine(position: Int, item: GlobalMedicine)

        fun restoreListPosition()

        fun nextPage()
    }
}