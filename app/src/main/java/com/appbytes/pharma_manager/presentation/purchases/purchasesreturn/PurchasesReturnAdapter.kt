package com.appbytes.pharma_manager.presentation.purchases.purchasesreturn

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.appbytes.pharma_manager.R
import com.appbytes.pharma_manager.business.domain.models.MedicineUnits
import com.appbytes.pharma_manager.business.domain.models.PurchasesCart
import com.appbytes.pharma_manager.business.domain.models.SelectMedicineUnit
import com.appbytes.pharma_manager.presentation.inventory.add.addmedicine.SelectUnitAdapter
import com.appbytes.pharma_manager.presentation.util.GenericViewHolder
import com.appbytes.pharma_manager.presentation.util.TopSpacingItemDecoration
import com.appbytes.pharma_manager.presentation.util.setDivider
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialog_unit_select.*
import kotlinx.android.synthetic.main.item_sales_cart.view.*
import java.lang.Exception

class PurchasesReturnAdapter
constructor(
    private val interaction: Interaction? = null
)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val TAG = "PurchasesCartAdapter"

    var isLoading : Boolean = true

    val loadingItem = PurchasesCart(
        medicine = null,
        purchasesUnit = null,
        quantity = -2,
        amount = 0f
    )



    val notFound = PurchasesCart(
        medicine = null,
        purchasesUnit = null,
        quantity = -3,
        amount = 0f
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
                return PurchasesDataViewHolder(itemView, interaction)
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
            is PurchasesDataViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }

    }

    override fun getItemCount(): Int {
        Log.d(TAG, "SalesOrderAdapter List Size " + differ.currentList.size)
        return differ.currentList.size
    }

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PurchasesCart>() {

        override fun areItemsTheSame(oldItem: PurchasesCart, newItem: PurchasesCart): Boolean {
            return oldItem.medicine?.id == newItem.medicine?.id
        }

        override fun areContentsTheSame(oldItem: PurchasesCart, newItem: PurchasesCart): Boolean {
            return oldItem == newItem
        }
    }

    private val differ =
        AsyncListDiffer(
            GlobalRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class GlobalRecyclerChangeCallback(
        private val adapter: PurchasesReturnAdapter
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

    fun submitList(list: List<PurchasesCart>?, isLoading : Boolean = true, queryExhausted : Boolean = false){
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

    class PurchasesDataViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView), SelectUnitAdapter.Interaction {

        private var unitRecyclerAdapter : SelectUnitAdapter? = null
        private var bottomSheetDialog : BottomSheetDialog? = null
        var mItem : PurchasesCart? = null

        fun bind(item: PurchasesCart) = with(itemView) {
            mItem = item

            itemView.salesCardItemBrandName.setText(item.medicine!!.brand_name!!)
            itemView.salesCurrentUnitPriceTv.setText("PP : ৳ ")
            itemView.salesCurrentUnitPrice.setText(item.medicine?.purchase_price.toString())
            itemView.stockTvId.setText("Stock : " + item.medicine?.remaining_quantity)

            itemView.salesCurrentUnitPrice.addTextChangedListener(object : TextWatcher {
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
                        val purchase_price = itemView.salesCurrentUnitPrice.text.toString().toFloat()
                        if (purchase_price == item.medicine?.purchase_price) {
                            return
                        }
                        interaction?.onItemChangePP(
                            position = adapterPosition,
                            item = item,
                            purchase_price = purchase_price
                        )
                        item.medicine?.purchase_price = purchase_price
                        itemView.salesCurrentUnitPrice.setText(item.medicine?.purchase_price.toString())
                        val amount = item.medicine?.purchase_price!! * item.purchasesUnit?.quantity!! * itemView.salesCartItemQuantityCount.text.toString().toInt()
                        itemView.salesCartSubTotal.setText("Sub Total ৳ " + amount)
                    }
                    catch (e : Exception) {

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
                    interaction?.onChangeUnit(
                        position = adapterPosition,
                        item = item,
                        unit = item.purchasesUnit!!,
                        quantity = itemView.salesCartItemQuantityCount.text.toString().toInt())
                    val amount = item.medicine?.purchase_price!! * item.purchasesUnit?.quantity!! * itemView.salesCartItemQuantityCount.text.toString().toInt()
                    itemView.salesCartSubTotal.setText("Sub Total ৳ " + amount)
                }

            })

            itemView.salesCartSubTotal.setText("Sub Total ৳ " + item.amount)
            if (item.purchasesUnit != null) {
                itemView.salesCardItemUnit.setText(item.purchasesUnit?.name)
            }


            itemView.salesCartItemQuantityCount.setText(item.quantity.toString())


            itemView.historyImgId.setOnClickListener {
                interaction?.onItemDelete(adapterPosition, item)
            }
        }


        private fun initDialogRecyclerAdapter(recyclerView : RecyclerView, list : List<SelectMedicineUnit>) {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                val topSpacingDecorator = TopSpacingItemDecoration(5)
                removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
                addItemDecoration(topSpacingDecorator)
                unitRecyclerAdapter = SelectUnitAdapter(this@PurchasesDataViewHolder)
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
            val amount = mItem?.medicine?.purchase_price!! * item.unit.quantity!! * itemView.salesCartItemQuantityCount.text.toString().toInt()
            itemView.salesCartSubTotal.setText("Sub Total ৳ " + amount)
            itemView.salesCardItemUnit.setText(item.unit!!.name)
            bottomSheetDialog?.dismiss()
        }

    }
    interface Interaction {

        fun onItemSelected(position: Int, item: PurchasesCart)

        fun onChangeUnit(position: Int, item: PurchasesCart, unit : MedicineUnits, quantity : Int)

        fun onItemChangePP(position: Int, item: PurchasesCart, purchase_price : Float)




        fun onItemDelete(position: Int, item: PurchasesCart)

        fun alertDialog(item : PurchasesCart, message : String)

        fun restoreListPosition()

        fun nextPage()
    }
}