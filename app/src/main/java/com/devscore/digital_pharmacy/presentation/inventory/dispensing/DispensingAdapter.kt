package com.devscore.digital_pharmacy.presentation.inventory.dispensing

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.*
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.business.domain.models.DispensingMedicine
import kotlinx.android.synthetic.main.item_dispensing.view.*
import kotlinx.android.synthetic.main.item_local.view.*

class DispensingAdapter
constructor(
    private val interaction: Interaction? = null
)
    : RecyclerView.Adapter<DispensingAdapter.DispensingViewHolder>() {

    val TAG = "DispensingAdapter"

    companion object {

        const val IMAGE_ITEM = 1

        const val LOADING = 1
        const val RETRY =2
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DispensingViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_dispensing ,parent,false)
        return DispensingViewHolder(itemView, interaction)
    }


    override fun onBindViewHolder(holder: DispensingViewHolder , position: Int) {
        holder.bind(differ.currentList.get(position))
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "DispensingAdapter List Size " + differ.currentList.size)
        return differ.currentList.size
    }

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DispensingMedicine>() {

        override fun areItemsTheSame(oldItem: DispensingMedicine, newItem: DispensingMedicine): Boolean {
            return oldItem.localMedicine.id == newItem.localMedicine.id
        }

        override fun areContentsTheSame(oldItem: DispensingMedicine, newItem: DispensingMedicine): Boolean {
            return oldItem == newItem
        }
    }

    private val differ =
        AsyncListDiffer(
            DispensingRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class DispensingRecyclerChangeCallback(
        private val adapter: DispensingAdapter
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

    fun submitList(medicineList: List<DispensingMedicine>?, ){
        val newList = medicineList?.toMutableList()
        differ.submitList(newList)
    }

    fun changeBottom(bottomState : Int) {
    }

    class DispensingViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: DispensingMedicine) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }
            Log.d("LocalAdapter", item.toString())
            itemView.dispensingBrandName.localBrandNameTV.setText(item.localMedicine.brand_name)
            itemView.dispensingCompanyName.setText(item.localMedicine.generic)
            itemView.dispensingMRP.setText(item.localMedicine.mrp.toString())
            itemView.disposingCount.setText(item.dispensingQuantity.toString())

            val ad = ArrayAdapter(
                context,
                android.R.layout.simple_spinner_item,
                item.localMedicine.units
            )

            ad.setDropDownViewResource(
                android.R.layout
                    .simple_spinner_dropdown_item
            )

            itemView.dispensingUnitSpinner.setAdapter(ad);


            itemView.dispensingCountIncrement.setOnClickListener {
                val itemCount = itemView.disposingCount.text as Int
                val updateValue = itemCount + 1
                itemView.disposingCount.setText(updateValue.toString())
                interaction?.onDispensingCountIncrement(adapterPosition, item, updateValue)
            }


            itemView.dispensingCountDecrement.setOnClickListener {
                val itemCount = itemView.disposingCount.text as Int
                val updateValue = itemCount - 1
                itemView.disposingCount.setText(updateValue.toString())
                interaction?.onDispensingCountIncrement(adapterPosition, item, updateValue)
            }


            itemView.dispensingImage.setOnClickListener {
                interaction?.onItemDeleteSelected(adapterPosition, item)
            }

        }
    }

    interface Interaction {

        fun onItemSelected(position: Int, item: DispensingMedicine)

        fun onDispensingCountIncrement(position: Int, item: DispensingMedicine, value : Int)

        fun onDispensingCountDecrement(position: Int, item: DispensingMedicine, value : Int)

        fun onItemDeleteSelected(position: Int, item: DispensingMedicine)

        fun restoreListPosition()

        fun nextPage()
    }
}