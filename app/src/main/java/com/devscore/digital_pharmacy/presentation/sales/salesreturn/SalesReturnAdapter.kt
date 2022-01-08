package com.devscore.digital_pharmacy.presentation.sales.salesreturn

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.*
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.business.domain.models.MedicineUnits
import com.devscore.digital_pharmacy.business.domain.models.SalesCart
import com.devscore.digital_pharmacy.presentation.purchases.cart.SpinnerAdapter
import com.devscore.digital_pharmacy.presentation.util.GenericViewHolder
import kotlinx.android.synthetic.main.item_sales_cart.view.*
import java.lang.Exception

class SalesReturnAdapter
constructor(
    private val interaction: Interaction? = null
)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val TAG = "SalesCardAdapter"

    var isLoading : Boolean = true

    val loadingItem = SalesCart(
        medicine = null,
        salesUnit = null,
        quantity = -2,
        amount = null
    )



    val notFound = SalesCart(
        medicine = null,
        salesUnit = null,
        quantity = -3,
        amount = null
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
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_sales_cart,parent,false)
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
            if(differ.currentList.get(position).quantity == -2){
                return LOADING_ITEM
            }
            if(differ.currentList.get(position).quantity == -3){
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
            GlobalRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class GlobalRecyclerChangeCallback(
        private val adapter: SalesReturnAdapter
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

    fun submitList(list: List<SalesCart>?, isLoading : Boolean = true, queryExhausted : Boolean = false){
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

        fun bind(item: SalesCart) = with(itemView) {

            itemView.salesCardItemBrandName.setText(item.medicine!!.brand_name!!)

            itemView.salesCurrentUnitPrice.setText("৳ " + item.medicine?.mrp!! * item.salesUnit?.quantity!!)


            itemView.unitSwith.setOnClickListener {
                try {
                    for (i in 0..(item.medicine?.units?.size!! - 1)) {
                        if (item.medicine?.units?.get(i)?.id == item.salesUnit?.id) {
                            interaction?.onChangeUnit(
                                position = adapterPosition,
                                item = item,
                                unit = item.medicine?.units?.get(i+1)!!,
                                quantity = itemView.salesCartItemQuantityCount.text.toString().toInt())
                            val amount = item.medicine?.mrp!! * item.medicine?.units?.get(i+1)?.quantity!! * itemView.salesCartItemQuantityCount.text.toString().toInt()
                            itemView.salesCartSubTotal.setText("Sub Total ৳ " + amount)
                            itemView.salesCardItemUnit.setText(item.medicine?.units?.get(i+1)!!.name)
                            Log.d("AppDebug", "SalesCartAdapter " + item.toString())

                            break
                        }
                    }
                }
                catch (e : Exception) {
                    e.printStackTrace()
                    interaction?.onChangeUnit(
                        position = adapterPosition,
                        item = item,
                        unit = item.medicine?.units?.get(0)!!,
                        quantity = itemView.salesCartItemQuantityCount.text.toString().toInt())
                    val amount = item.medicine?.mrp!! * item.medicine?.units?.get(0)?.quantity!! * itemView.salesCartItemQuantityCount.text.toString().toInt()
                    itemView.salesCartSubTotal.setText("Sub Total ৳ " + amount)
                    itemView.salesCardItemUnit.setText(item.medicine?.units?.get(0)!!.name)
                }
            }



            val newList = mutableListOf<MedicineUnits>()
            for (a in item.medicine?.units!!) {
                newList.add(a)
            }
            /*val menu =
                CustomPowerMenu.Builder(itemView.context, UnitMenuAdapter())
                    .addItemList(newList)
                    .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT)
                    .setMenuRadius(10f)
                    .setMenuShadow(10f)

            val customPowerMenu = menu.build()
            customPowerMenu.setOnMenuItemClickListener { position, selectItem ->
                itemView.salesCardItemUnit.setText(selectItem?.name)
                Log.d("AppDebug", "Sales Adapter Menu Item Select " + selectItem.toString())
                interaction?.onChangeUnit(
                    position = adapterPosition,
                    item = item,
                    unit = selectItem!!,
                    quantity = itemView.salesCartItemQuantityCount.text.toString().toInt())
                val amount = item.medicine?.mrp!! * selectItem.quantity * itemView.salesCartItemQuantityCount.text.toString().toInt()
                itemView.salesCartSubTotal.setText("Sub Total ৳ " + amount)
                Log.d("AppDebug", "SalesCartAdapter " + item.toString())
                customPowerMenu.dismiss()
            }
*/





            itemView.setOnClickListener {
//                interaction?.onItemSelected(adapterPosition, item)
            }
//            itemView.salesCardItemUnit.setOnClickListener {
//                customPowerMenu.showAsAnchorCenter(itemView.salesCardItemUnit)
//
//            }

            itemView.salesCardItemIncrease.setOnClickListener {
                val value = itemView.salesCartItemQuantityCount.text.toString().toInt() + 1
                itemView.salesCartItemQuantityCount.setText(value.toString())
            }
            itemView.salesCartItemDecrease.setOnClickListener {
                val value = itemView.salesCartItemQuantityCount.text.toString().toInt() - 1
                itemView.salesCartItemQuantityCount.setText(value.toString())
            }

            itemView.salesCartItemQuantityCount.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    try {
                        val quantity = itemView.salesCartItemQuantityCount.text.toString().toInt()
                        interaction?.onChangeUnit(
                            position = adapterPosition,
                            item = item,
                            unit = item.salesUnit!!,
                            quantity = quantity)
                        val amount = item.medicine?.mrp!! * item.salesUnit?.quantity!! * itemView.salesCartItemQuantityCount.text.toString().toInt()
                        itemView.salesCartSubTotal.setText("Sub Total ৳ " + amount)
                    }
                    catch (e : Exception) {
                        interaction?.onChangeUnit(
                            position = adapterPosition,
                            item = item,
                            unit = item.salesUnit!!,
                            quantity = 0)
                        val amount = item.medicine?.mrp!! * item.salesUnit?.quantity!! * 0
                        itemView.salesCartSubTotal.setText("Sub Total ৳ " + amount)
                    }
                }

            })

//            itemView.salesCartItemQuantityCount.doOnTextChanged { text, start, before, count ->
//                try {
//                    val value = itemView.salesCartItemQuantityCount.text.toString().toInt()
//                    if (value < 1) {
//                        itemView.salesCartItemQuantityCount.setText("1")
//                        interaction?.alertDialog(item, "It cann't decrease")
//                        return@doOnTextChanged
//                    }
//                    Log.d("AppDebug", "salesCartItemQuantityCount Call")
//                    interaction?.onChangeUnit(
//                        position = adapterPosition,
//                        item = item,
//                        unit = item.salesUnit!!,
//                        quantity = value)
//                }
//                catch (e : Exception) {
//                    itemView.salesCartItemQuantityCount.setText("1")
//                    interaction?.alertDialog(item, "It cann't decrease")
//                    return@doOnTextChanged
//                }
//            }


            itemView.salesCardItemUnit.setOnClickListener {
                itemView.salesCardItemUnitSpinner.visibility = View.VISIBLE
                itemView.salesCardItemUnit.visibility = View.INVISIBLE

                val kindAdapter = SpinnerAdapter(
                    context,
                    android.R.layout.simple_spinner_item,
                    newList.toTypedArray()
                )

                kindAdapter.setDropDownViewResource(
                    android.R.layout
                        .simple_spinner_dropdown_item
                )

                itemView.salesCardItemUnitSpinner.setAdapter(kindAdapter)
                itemView.salesCardItemUnitSpinner.setAdapter(kindAdapter)
                itemView.salesCardItemUnitSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        if (newList.get(position).id == item.salesUnit?.id) {
                            Log.d("AppDebug", "spinner return")
                            return
                        }

                        interaction?.onChangeUnit(
                            position = adapterPosition,
                            item = item,
                            unit = newList.get(position),
                            quantity = itemView.salesCartItemQuantityCount.text.toString().toInt())
                        itemView.salesCurrentUnitPrice.setText("৳ " + item.medicine?.mrp!! * newList.get(position).quantity!!)

                        val amount = item.medicine?.purchase_price!! * newList.get(position)?.quantity!! * itemView.salesCartItemQuantityCount.text.toString().toInt()
                        itemView.salesCartSubTotal.setText("Sub Total ৳ " + amount)
                        itemView.salesCardItemUnit.setText(newList.get(position)!!.name)
                        Log.d("AppDebug", "SalesCartAdapter " + item.toString())
                        itemView.salesCardItemUnitSpinner.visibility = View.INVISIBLE
                        itemView.salesCardItemUnit.visibility = View.VISIBLE
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        itemView.salesCardItemUnitSpinner.visibility = View.INVISIBLE
                        itemView.salesCardItemUnit.visibility = View.VISIBLE
                    }

                })

                itemView.salesCardItemUnitSpinner.performClick()

            }

            itemView.salesCartSubTotal.setText("Sub Total ৳ " + item.amount)
            if (item.salesUnit != null) {
                itemView.salesCardItemUnit.setText(item.salesUnit?.name)
            }


            if (itemView.salesCartItemQuantityCount.text.toString().toInt() == 1) {
                itemView.salesCartItemQuantityCount.setText(item.quantity.toString())
            }
        }
    }

    interface Interaction {

        fun onItemSelected(position: Int, item: SalesCart)

        fun onChangeUnit(position: Int, item: SalesCart, unit : MedicineUnits, quantity : Int)


        fun onUpdateQuantity(position: Int, item: SalesCart, quantity : Int)

        fun alertDialog(item : SalesCart, message : String)
    }
}