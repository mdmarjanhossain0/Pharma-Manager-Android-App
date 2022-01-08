package com.devscore.digital_pharmacy.presentation.inventory.local

import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.presentation.util.CROSS_FADE_DURATION
import com.devscore.digital_pharmacy.presentation.util.GenericViewHolder
import com.devscore.digital_pharmacy.presentation.util.loadPhotoUrlWithThumbnail
import kotlinx.android.synthetic.main.item_local.view.*

class LocalAdapter
constructor(
    private val interaction: Interaction? = null
)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val TAG = "LocalAdapter"


    val loadingItem = LocalMedicine(
        id = -2,
        brand_name = "",
        sku = null,
        dar_number = null,
        mr_number = null,
        generic = "",
        indication = null,
        symptom = null,
        strength = null,
        description = null,
        image = null,
        mrp = 0f,
        purchase_price = 0f,
        discount = 0f,
        is_percent_discount = false,
        manufacture = null,
        kind = null,
        form = null,
        remaining_quantity = 0f,
        damage_quantity = null,
        exp_date = null,
        rack_number = null,
        units = listOf()
    )

    val notFound = LocalMedicine(
        id = -3,
        brand_name = "",
        sku = null,
        dar_number = null,
        mr_number = null,
        generic = "",
        indication = null,
        symptom = null,
        strength = null,
        description = null,
        image = null,
        mrp = 0f,
        purchase_price = 0f,
        discount = 0f,
        is_percent_discount = false,
        manufacture = null,
        kind = null,
        form = null,
        remaining_quantity = 0f,
        damage_quantity = null,
        exp_date = null,
        rack_number = null,
        units = listOf()
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
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_local,parent,false)
                return LocalDataViewHolder(itemView, interaction)
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
                return LocalAdapter.LOADING_ITEM
            }
            if(differ.currentList.get(position).id == -3){
                return LocalAdapter.NOT_FOUND
            }
            return LocalAdapter.IMAGE_ITEM
        }
        else {
            return LocalAdapter.LOADING_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is LocalDataViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }

    }

    override fun getItemCount(): Int {
        Log.d(TAG, "GlobalAdapter List Size " + differ.currentList.size)
        return differ.currentList.size
    }

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LocalMedicine>() {

        override fun areItemsTheSame(oldItem: LocalMedicine, newItem: LocalMedicine): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: LocalMedicine, newItem: LocalMedicine): Boolean {
            return oldItem == newItem
        }
    }

    private val differ =
        AsyncListDiffer(
            LocalRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class LocalRecyclerChangeCallback(
        private val adapter: LocalAdapter
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

    fun submitList(medicineList: List<LocalMedicine>?, isLoading : Boolean = true, queryExhausted : Boolean = false){
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

    class LocalDataViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: LocalMedicine) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }
            Log.d("LocalAdapter", item.toString())

            if (item.id != null && item.id!! > 0) {
                itemView.localBrandNameTV.setText(item.brand_name)
            }
            else {
                val brand_name = SpannableString(item.brand_name)
                brand_name.setSpan(ForegroundColorSpan(Color.RED), 0, brand_name.length, 1)

                itemView.localBrandNameTV.setText(brand_name)
            }
            itemView.localCompanyNameTV.setText(item.generic)
            if (item.mrp != null) {
                itemView.localMRPTV.setText("MRP ৳ "+ item.mrp.toString())
            }
            else {
                itemView.localMRPTV.setText("MRP ৳ ...")
            }

//            itemView.localMedicineReturn.setOnClickListener {
//                interaction?.onItemReturnSelected(adapterPosition, item)
//            }

            itemView.historyImgId.setOnClickListener {
                interaction?.onItemAddShortList(adapterPosition, item)
            }

//            itemView.localMedicineDelete.setOnClickListener {
//                interaction?.onItemDeleteSelected(adapterPosition, item)
//            }



            Log.d("AppDebug", "Position " + adapterPosition +" " + item.image.toString())
//            if (item.image != null) {
                Glide.with(context)
                    .setDefaultRequestOptions(RequestOptions()
                        .placeholder(R.drawable.paracetamol)
                        .error(R.drawable.paracetamol))
                    .load(item.image)
                    .into(itemView.itemImgId)
//                Picasso.get()
//                    .load(item.image)
//                    .centerCrop()
//                    .into(itemView.itemImgId)
//                itemView.itemImgId.loadPhotoUrlWithThumbnail(item.image!!, "#FFFFFF")
//            }

            if (item.remaining_quantity != null) {
                itemView.stockTvId.setText("Stock : " + item.remaining_quantity!!.toInt().toString())
            }

            if (item.manufacture != null) {
                itemView.localMedicineManufactureNameTV.setText(item.manufacture)
            }






            itemView.deleteLocalMedicine.setOnClickListener {
                interaction?.onItemDeleteSelected(adapterPosition, item)
            }
        }
    }

    interface Interaction {

        fun onItemSelected(position: Int, item: LocalMedicine)

        fun onItemReturnSelected(position: Int, item: LocalMedicine)

        fun onItemDeleteSelected(position: Int, item: LocalMedicine)


        fun onItemAddShortList(position: Int, item: LocalMedicine)

        fun restoreListPosition()

        fun nextPage()
    }
}