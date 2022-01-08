package com.devscore.digital_pharmacy.presentation.purchases.cart

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.ArrayAdapter
import com.devscore.digital_pharmacy.business.domain.models.MedicineUnits
import android.widget.TextView

import android.view.ViewGroup


class SpinnerAdapter(
    context: Context,
    textViewResourceId: Int,
    private val values: Array<MedicineUnits>) : ArrayAdapter<MedicineUnits>(context, textViewResourceId, values) {
    override fun getCount(): Int {
        return values.size
    }

    override fun getItem(position: Int): MedicineUnits {
        return values[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label = super.getView(position, convertView, parent!!) as TextView
        label.setTextColor(Color.BLACK)

        label.setText(values[position].name)

        return label
    }

    override fun getDropDownView(
        position: Int, convertView: View?,
        parent: ViewGroup?
    ): View {
        val label = super.getDropDownView(position, convertView, parent) as TextView
        label.setTextColor(Color.BLACK)
        label.setText(values[position].name)
        return label
    }

}