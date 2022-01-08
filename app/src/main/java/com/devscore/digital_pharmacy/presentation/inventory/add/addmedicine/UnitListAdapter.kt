package com.devscore.digital_pharmacy.presentation.inventory.add.addmedicine

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.business.domain.models.MedicineUnits
import kotlinx.android.synthetic.main.item_unit.view.*

class UnitListAdapter(
    private val interaction : Interaction
) : RecyclerView.Adapter<UnitListAdapter.ItemViewHolder>() {

    private var list : List<MedicineUnits> = mutableListOf()
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(itemView: View, item : MedicineUnits, interaction: Interaction) {
            itemView.unitDetails.setText(item.quantity.toString() + " Pcs = 1 " + item.name.toString())
            itemView.unitDelete.setOnClickListener {
                interaction.onItemSelected(adapterPosition, item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_unit, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(holder.itemView, list.get(position), interaction)
    }

    override fun getItemCount(): Int {
        return list.size
    }





    fun submit(list : List<MedicineUnits>) {
        this.list = list
        notifyDataSetChanged()
    }



    interface Interaction {

        fun onItemSelected(position: Int, item: MedicineUnits)
    }
}