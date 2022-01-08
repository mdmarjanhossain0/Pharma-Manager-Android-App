package com.devscore.digital_pharmacy.presentation.sales.card

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.devscore.digital_pharmacy.business.domain.models.SalesCart
import com.devscore.digital_pharmacy.business.domain.models.SalesOrderMedicine
import com.devscore.digital_pharmacy.presentation.util.GenericViewHolder
import kotlinx.android.synthetic.main.fragment_add_product_sub_medicine.*
import kotlinx.android.synthetic.main.item_sales_cart.view.*
import com.skydoves.powermenu.MenuAnimation

import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.business.domain.models.MedicineUnits
import com.devscore.digital_pharmacy.business.domain.models.SelectMedicineUnit
import com.devscore.digital_pharmacy.presentation.inventory.add.addmedicine.SelectUnitAdapter
import com.devscore.digital_pharmacy.presentation.purchases.cart.SpinnerAdapter
import com.devscore.digital_pharmacy.presentation.util.TopSpacingItemDecoration
import com.devscore.digital_pharmacy.presentation.util.loadPhotoUrlWithThumbnail
import com.devscore.digital_pharmacy.presentation.util.setDivider
import com.google.android.material.bottomsheet.BottomSheetDialog

import com.skydoves.powermenu.CustomPowerMenu
import com.skydoves.powermenu.OnMenuItemClickListener
import kotlinx.android.synthetic.main.dialog_unit_select.*
import kotlinx.android.synthetic.main.fragment_add_product_sub_medicine.view.*
import kotlinx.android.synthetic.main.item_local.view.*
import kotlinx.android.synthetic.main.item_sales_cart.view.historyImgId
import kotlinx.android.synthetic.main.item_sales_cart.view.itemImgId
import kotlinx.android.synthetic.main.item_sales_cart.view.stockTvId
import kotlinx.android.synthetic.main.item_sales_inventory.view.*
import kotlinx.coroutines.*
import java.lang.Exception


class SalesCardAdapter
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
        private val adapter: SalesCardAdapter
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
    ) : RecyclerView.ViewHolder(itemView), SelectUnitAdapter.Interaction {
        private var unitRecyclerAdapter : SelectUnitAdapter? = null
        private var bottomSheetDialog : BottomSheetDialog? = null
        var mItem : SalesCart? = null

        fun bind(item: SalesCart) = with(itemView) {
            mItem = item

            itemView.salesCardItemBrandName.setText(item.medicine!!.brand_name!!)
            itemView.salesCurrentUnitPrice.setText(item.medicine?.mrp.toString())
            itemView.stockTvId.setText("Stock : " + item.medicine?.remaining_quantity)

            itemView.salesCurrentUnitPrice.addTextChangedListener(object : TextWatcher {
                var lastInput = ""
                var debounceJob: Job? = null
                val uiScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    Log.d("AppDebug", "onTextChanged")
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s != null) {
                        val newtInput = s.toString()
                        debounceJob?.cancel()
                        if (lastInput != newtInput) {
                            lastInput = newtInput
                            debounceJob = uiScope.launch {
                                delay(2000)
                                if (lastInput == newtInput) {
                                    try {
                                        val mrp = itemView.salesCurrentUnitPrice.text.toString().toFloat()
                                        if (mrp == item.medicine?.mrp) {
                                            return@launch
                                        }
                                        interaction?.onItemChangeMRP(
                                            position = adapterPosition,
                                            item = item,
                                            mrp = mrp
                                        )
                                        item.medicine?.mrp = mrp
                                        itemView.salesCurrentUnitPrice.setText(item.medicine?.mrp.toString())
                                        val amount = item.medicine?.mrp!! * item.salesUnit?.quantity!! * itemView.salesCartItemQuantityCount.text.toString().toInt()
                                        itemView.salesCartSubTotal.setText("Sub Total ৳ " + amount)
                                    }
                                    catch (e : Exception) {
//                        interaction?.onItemChangeMRP(
//                            position = adapterPosition,
//                            item = item,
//                            mrp = 0f
//                        )
//                        itemView.salesCurrentUnitPrice.setText(item.medicine?.mrp.toString())
                                    }
                                }
                            }
                        }
                    }
                }

            })


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

            itemView.salesCardItemUnit.setOnClickListener {
                bottomSheetDialog = BottomSheetDialog(itemView.context)
                bottomSheetDialog?.setContentView(R.layout.dialog_unit_select)
                val list = item.medicine?.units?.map {
                    SelectMedicineUnit(
                        unit = it
                    )
                }
                initDialogRecyclerAdapter(bottomSheetDialog?.selectUnitRvId!!,list!!)
                bottomSheetDialog?.show()

            }

            itemView.salesCartSubTotal.setText("Sub Total ৳ " + item.amount)
            if (item.salesUnit != null) {
                itemView.salesCardItemUnit.setText(item.salesUnit?.name)
            }


            if (itemView.salesCartItemQuantityCount.text.toString().toInt() == 1) {
                itemView.salesCartItemQuantityCount.setText(item.quantity.toString())
            }




            itemView.historyImgId.setOnClickListener {
                interaction?.onItemDelete(adapterPosition, item)
            }





            Glide.with(context)
                .setDefaultRequestOptions(
                    RequestOptions()
                        .placeholder(R.drawable.paracetamol)
                        .error(R.drawable.paracetamol))
                .load(item.medicine?.image)
                .into(itemView.itemImgId)
//            if (item.medicine?.image != null) {
//                itemView.itemImgId.loadPhotoUrlWithThumbnail(item.medicine?.image!!, "#FFFFFF")
//            }

            if (item.medicine?.remaining_quantity != null) {
                itemView.stockTvId.setText("Stock : " + item.medicine?.remaining_quantity!!.toInt().toString())
            }
        }

        private fun initDialogRecyclerAdapter(recyclerView : RecyclerView, list : List<SelectMedicineUnit>) {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                val topSpacingDecorator = TopSpacingItemDecoration(5)
                removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
                addItemDecoration(topSpacingDecorator)
                unitRecyclerAdapter = SelectUnitAdapter(this@SalesOrderDataViewHolder)
                adapter = unitRecyclerAdapter
                setDivider(R.drawable.recycler_view_divider)
            }
            unitRecyclerAdapter?.submit(list)
        }

        override fun onItemSelectedUnit(position: Int, item: SelectMedicineUnit) {
            interaction?.onChangeUnit(
                position = adapterPosition,
                item = mItem!!,
                unit = item.unit,
                quantity = itemView.salesCartItemQuantityCount.text.toString().toInt())

            val amount = mItem?.medicine?.mrp!! * item.unit.quantity!! * itemView.salesCartItemQuantityCount.text.toString().toInt()
            itemView.salesCartSubTotal.setText("Sub Total ৳ " + amount)
            itemView.salesCardItemUnit.setText(item.unit!!.name)
            bottomSheetDialog?.dismiss()
        }
    }

    interface Interaction {

        fun onItemSelected(position: Int, item: SalesCart)

        fun onChangeUnit(position: Int, item: SalesCart, unit : MedicineUnits, quantity : Int)


        fun onItemDelete(position: Int, item: SalesCart)


        fun onItemChangeMRP(position: Int, item: SalesCart, mrp : Float)


        fun onUpdateQuantity(position: Int, item: SalesCart, quantity : Int)

        fun alertDialog(item : SalesCart, message : String)
    }
}