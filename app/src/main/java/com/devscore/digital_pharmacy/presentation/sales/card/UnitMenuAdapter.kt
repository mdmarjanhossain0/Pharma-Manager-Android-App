package com.devscore.digital_pharmacy.presentation.sales.card

import android.content.Context
import android.widget.TextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.business.domain.models.MedicineUnits

import com.skydoves.powermenu.MenuBaseAdapter


class UnitMenuAdapter : MenuBaseAdapter<MedicineUnits>() {
    override fun getView(index: Int, view: View?, viewGroup: ViewGroup): View {
        var view: View? = view
        val context: Context = viewGroup.context
        if (view == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.item_unit_menu, viewGroup, false)
        }
        val item: MedicineUnits = getItem(index) as MedicineUnits
        val title: TextView = view?.findViewById(R.id.unitMenuTextView)!!
        title.setText(item.name)
        return super.getView(index, view, viewGroup)
    }
}