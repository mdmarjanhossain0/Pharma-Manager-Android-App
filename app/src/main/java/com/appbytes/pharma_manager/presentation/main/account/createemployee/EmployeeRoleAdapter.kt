package com.appbytes.pharma_manager.presentation.main.account.createemployee

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appbytes.pharma_manager.R
import kotlinx.android.synthetic.main.item_employee_role.view.*
import kotlinx.android.synthetic.main.item_select_unit.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EmployeeRoleAdapter(
    private val interaction: Interaction
) : RecyclerView.Adapter<EmployeeRoleAdapter.ItemViewHolder>() {

    private var list : List<String> = mutableListOf()
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(itemView: View, item : String, interaction: Interaction) {
            itemView.roleName.setText(item)
            itemView.setOnClickListener {
                itemView.roleSelect.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.Main).launch {
                    delay(500)
                    itemView.roleSelect.visibility = View.GONE
                    interaction.onItemSelected(adapterPosition, item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_employee_role, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(holder.itemView, list.get(position), interaction)
    }

    override fun getItemCount(): Int {
        return list.size
    }





    fun submit(list : List<String>) {
        this.list = list
        notifyDataSetChanged()
    }



    interface Interaction {

        fun onItemSelected(position: Int, item: String)
    }
}