package com.devscore.digital_pharmacy.presentation.inventory.add.addmedicine

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.business.domain.models.MedicineUnits
import com.devscore.digital_pharmacy.business.domain.models.SelectMedicineUnit
import kotlinx.android.synthetic.main.item_select_unit.view.*
import kotlinx.android.synthetic.main.item_unit.view.*
import kotlinx.coroutines.*

class SelectUnitAdapter(
    private val interaction : Interaction
) : RecyclerView.Adapter<SelectUnitAdapter.ItemViewHolder>() {

    private var list : List<SelectMedicineUnit> = mutableListOf()
    class ItemViewHolder(itemView: View, interaction : Interaction) : RecyclerView.ViewHolder(itemView) {
        fun bind(itemView: View, item : SelectMedicineUnit, interaction: Interaction) {
            itemView.unitName.setText(item.unit.name)
            if (item.isSelect) {
                itemView.unitSelect.visibility = View.VISIBLE
            }
            itemView.setOnClickListener {
                itemView.unitSelect.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.Main).launch {
                    delay(500)
                    itemView.unitSelect.visibility = View.GONE
                    interaction.onItemSelectedUnit(adapterPosition, item)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_select_unit, parent, false)
        return ItemViewHolder(view, interaction)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(holder.itemView, list.get(position), interaction)
    }

    override fun getItemCount(): Int {
        return list.size
    }





    fun submit(list : List<SelectMedicineUnit>) {
        this.list = list
        notifyDataSetChanged()
    }



    interface Interaction {

        fun onItemSelectedUnit(position: Int, item: SelectMedicineUnit)
    }
}