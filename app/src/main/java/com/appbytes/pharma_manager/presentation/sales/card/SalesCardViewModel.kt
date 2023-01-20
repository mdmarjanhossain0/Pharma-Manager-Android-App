package com.appbytes.pharma_manager.presentation.sales.card

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appbytes.pharma_manager.business.datasource.cache.sales.SalesDao
import com.appbytes.pharma_manager.business.domain.models.*
import com.appbytes.pharma_manager.business.domain.util.*
import com.appbytes.pharma_manager.business.domain.util.Constants.Companion.PAGINATION_PAGE_SIZE
import com.appbytes.pharma_manager.business.interactors.account.GetAccount
import com.appbytes.pharma_manager.business.interactors.inventory.local.SearchForCartMedicineInteractor
import com.appbytes.pharma_manager.business.interactors.sales.CreateSalesOderInteractor
import com.appbytes.pharma_manager.business.interactors.sales.DeleteSalesOrderInteractor
import com.appbytes.pharma_manager.business.interactors.sales.SalesCompleted
import com.appbytes.pharma_manager.business.interactors.sales.SalesOrderDetailsInteractor
import com.appbytes.pharma_manager.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SalesCardViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val createSalesOrder : CreateSalesOderInteractor,
    private val orderDetailsInteractor: SalesOrderDetailsInteractor,
    private val searchLocalMedicine: SearchForCartMedicineInteractor,
    private val salesDao: SalesDao,
    private val salesCompleted: SalesCompleted,
    private val deleteSalesOrderInteractor: DeleteSalesOrderInteractor,
    private val getAccountInteractor : GetAccount
) : ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<SalesCardState> = MutableLiveData(SalesCardState())
    private lateinit var callback : OnCompleteCallback
    fun submit(callback: OnCompleteCallback) {
        this.callback = callback
    }

    init {
//        getAccount()
//        test()
    }

    fun onTriggerEvent(event: SalesCardEvents) {
        when (event) {
            is SalesCardEvents.GenerateNewOrder -> {
                createNewOrder()
            }

            is SalesCardEvents.OrderCompleted -> {
//                orderCompleted()
            }

            is SalesCardEvents.NewLocalMedicineSearch -> {
                search()
            }



            is SalesCardEvents.OrderDetails -> {
                getOrderDetails(event.pk)
            }


            is SalesCardEvents.DeleteOrder -> {
                Log.d(TAG, "Delete Order")
//                deleteOrder(event.order)
            }

            is SalesCardEvents.AddToCard -> {
                addToCard(event.medicine, event.quantity, event.unitId)
            }



            is SalesCardEvents.ChangeMRP -> {
                changeMRP(event.cart, event.mrp)
            }


            is SalesCardEvents.UpdateQuantity -> {
                updateQuantity(event.cart, event.quantity!!)
            }
            is SalesCardEvents.ChangeUnit -> {
                Log.d(TAG, "SalesViewModel ChangeUnit Call")
                changeUnit(event.cart, event.unit!!, event.quantity)
            }

            is SalesCardEvents.ReceiveAmount -> {
                receiveAmount(event.amount!!)
            }

            is SalesCardEvents.IsDiscountPercent -> {
                isDiscountPercent(event.isDiscountPercent)
            }

            is SalesCardEvents.Discount -> {
                discount(event.discount)
            }

            is SalesCardEvents.DeleteMedicine -> {
                deleteFromCart(event.medicine)
            }


            is SalesCardEvents.SelectCustomer -> {
                selectCustomer(event.customer)
            }
            is SalesCardEvents.NextPage -> {
                incrementPageNumber()
                search()
            }

            is SalesCardEvents.UpdateQuery -> {
                onUpdateQuery(event.query)
            }

            is SalesCardEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is SalesCardEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
        }
    }


    fun test() {
        state.value?.let { state ->
            this.state.value = state.copy(
                pk = 5
            )
        }
    }

    private fun selectCustomer(customer: Customer) {
        state.value?.let { state ->
            this.state.value = state.copy(
                customer = customer
            )
        }
    }

    private fun deleteFromCart(medicine : LocalMedicine) {
        state.value?.let { state ->
            var checkExist = 0
            for (item in state.salesCartList) {
                if (item.medicine?.id == medicine.id) {
                    checkExist = 1
                    break
                }
            }
            if (checkExist == 0 ) {
                return@deleteFromCart
            }
            Log.d(TAG, "Medicine exist in cart")
            Log.d(TAG, "Show Medicine property " + medicine.toString())

            val previousAmount = state.totalAmount
            Log.d(TAG, "Previous Amount " + previousAmount.toString())

            var previousSalesCartItem : SalesCart? = null

            for (item in state.salesCartList) {
                if (item.medicine?.id == medicine.id) {
                    previousSalesCartItem = item
                }
            }

            if (previousSalesCartItem == null) {
                throw Exception("Item Not Found")
            }


            val totalAmount = previousAmount!! - previousSalesCartItem.amount!!

            val newCartList = state.salesCartList.toMutableList()
            for (item in state.salesCartList) {
                if (item.medicine?.id == medicine.id) {
                    newCartList.remove(item)
                    break
                }
            }

            this.state.value = state.copy(
                salesCartList = newCartList,
                totalAmount = totalAmount,
                totalAmountAfterDiscount = totalAmount,
            )
        }
    }

    private fun discount(discount: Float?) {
        state.value?.let {state ->
            var discountAmount = 0f
            if (state.is_discount_percent) {
                discountAmount = ((state.totalAmount!! * discount!!) / 100)
            }
            else {
                discountAmount = discount!!
            }
            val totalAmountAfterDiscount = state.totalAmount!! - discountAmount
            this.state.value = state.copy(
                discount = discount,
                discountAmount = discountAmount,
                totalAmountAfterDiscount = totalAmountAfterDiscount
            )
        }
    }

    private fun isDiscountPercent(discountPercent: Boolean) {
        state.value?.let {state ->
            var discountAmount : Float = 0f
            if (discountPercent) {
                discountAmount = ((state.totalAmount!! * state.discount!!) / 100 )
            }
            else {
                discountAmount = state.discount!!
            }
            val totalAmountAfterDiscount = state.totalAmount!! - discountAmount
            this.state.value = state.copy(
                is_discount_percent = discountPercent,
                discountAmount = discountAmount,
                totalAmountAfterDiscount = totalAmountAfterDiscount
            )
        }
    }

    private fun receiveAmount(amount : Float) {
        state.value?.let {state ->
            this.state.value = state.copy(
                receivedAmount = amount
            )
        }
    }



    private fun updateQuantity(cart : SalesCart, quantity: Int) {
        state.value?.let { state ->
            val previousAmount = state.totalAmount
            Log.d(TAG, "Previous Amount " + previousAmount.toString())
            val unitEquivalentQuantity = cart.salesUnit?.quantity

            val newAmount = cart.medicine?.mrp!! * quantity * unitEquivalentQuantity!!
            Log.d(TAG, "New Amount " + newAmount.toString())

            val totalAmount = previousAmount!! + newAmount - cart.amount!!


            val previousCartList = state.salesCartList.toMutableList()
            val newCartList = mutableListOf<SalesCart>()


            for (item in previousCartList) {
                if (item.medicine?.id == cart.medicine?.id) {
                    Log.d(TAG, "Id Match And Add Successfully")
                    newCartList.add(SalesCart(
                        medicine = cart.medicine,
                        salesUnit = cart.salesUnit,
                        quantity = quantity,
                        amount = newAmount
                    ))
                }
                else {
                    newCartList.add(item)
                }
            }

            Log.d(TAG, "Previous Cart List " + previousCartList.size + " " + previousCartList.toString())
            Log.d(TAG, "New Cart List " + newCartList.size + " " + newCartList.toString())

            this.state.value = state.copy(
                salesCartList = newCartList,
                totalAmount = totalAmount,
                totalAmountAfterDiscount = totalAmount,
            )
        }
    }


    private fun changeMRP(cart : SalesCart, mrp : Float) {
        state.value?.let { state ->


            val previousAmount = state.totalAmount
            Log.d(TAG, "Previous Amount " + previousAmount.toString())
            val unitEquivalentQuantity = cart.salesUnit?.quantity
            cart.medicine?.mrp = mrp

            val newAmount = cart.medicine?.mrp!! * cart.quantity!! * unitEquivalentQuantity!!
            Log.d(TAG, "New Amount " + newAmount.toString())

            val totalAmount = previousAmount!! + newAmount - cart.amount!!

            val previousCartList = state.salesCartList.toMutableList()
            previousCartList.find {
                it.medicine?.id == cart.medicine?.id!!
            }?.salesUnit = cart.salesUnit

            previousCartList.find {
                it.medicine?.id == cart.medicine?.id!!
            }?.amount = newAmount


            previousCartList.find {
                it.medicine?.id == cart.medicine?.id!!
            }?.quantity = cart.quantity



            Log.d(TAG, "Previous Cart List " + previousCartList.size + " " + previousCartList.toString())

            this.state.value = state.copy(
                salesCartList = previousCartList,
                totalAmount = totalAmount,
                totalAmountAfterDiscount = totalAmount,
            )
        }
    }

    private fun changeUnit(cart : SalesCart, unit: MedicineUnits, quantity : Int?) {
        if (cart.salesUnit == unit && cart.quantity == quantity) {
            Log.d(TAG, "Return from changeUnit")
            return
        }
        state.value?.let { state ->


            val previousAmount = state.totalAmount
            Log.d(TAG, "Previous Amount " + previousAmount.toString())
            val unitEquivalentQuantity = unit.quantity

            val newAmount = cart.medicine?.mrp!! * quantity!! * unitEquivalentQuantity
            Log.d(TAG, "New Amount " + newAmount.toString())

            val totalAmount = previousAmount!! + newAmount - cart.amount!!

            val previousCartList = state.salesCartList.toMutableList()
//            val newCartList = mutableListOf<SalesCart>()
            previousCartList.find {
                it.medicine?.id == cart.medicine?.id!!
            }?.salesUnit = unit

            previousCartList.find {
                it.medicine?.id == cart.medicine?.id!!
            }?.amount = newAmount


            previousCartList.find {
                it.medicine?.id == cart.medicine?.id!!
            }?.quantity = quantity


//            for (item in previousCartList) {
//                if (item.medicine?.id == cart.medicine?.id) {
//                    Log.d(TAG, "Id Match And Add Successfully")
//                    newCartList.add(SalesCart(
//                        medicine = cart.medicine,
//                        salesUnit = unit,
//                        quantity = quantity,
//                        amount = newAmount
//                    ))
//                }
//                else {
//                    newCartList.add(item)
//                }
//            }

            Log.d(TAG, "Previous Cart List " + previousCartList.size + " " + previousCartList.toString())
//            Log.d(TAG, "New Cart List " + newCartList.size + " " + newCartList.toString())

            this.state.value = state.copy(
                salesCartList = previousCartList,
                totalAmount = totalAmount,
                totalAmountAfterDiscount = totalAmount,
            )
        }
    }

    private fun addToCard(item : LocalMedicine, quantity : Int = 1, unitId : Int = -1) {
        var unitList = mutableListOf<MedicineUnits>()
        for (unit in item.units) {
            unitList.add(
                MedicineUnits(
                    id = unit.id,
                    quantity = unit.quantity,
                    name = unit.name,
                    type = unit.type
                )
            )
        }
        var medicine = LocalMedicine(
            id = item.id,
            brand_name = item.brand_name,
            sku = item.sku,
            dar_number = item.dar_number,
            mr_number = item.mr_number,
            generic = item.generic,
            indication = item.indication,
            symptom = item.symptom,
            strength = item.strength,
            description = item.description,
            image = item.image,
            mrp = item.mrp,
            purchase_price = item.purchase_price,
            discount = item.discount,
            is_percent_discount = item.is_percent_discount,
            manufacture = item.manufacture,
            kind = item.kind,
            form = item.form,
            remaining_quantity = item.remaining_quantity,
            damage_quantity = item.damage_quantity,
            exp_date = item.exp_date,
            rack_number = item.rack_number,
            units = unitList
        )
        Log.d(TAG, "item has code " + item.hashCode())
        Log.d(TAG, "medicine has code " + medicine.hashCode())
        state.value?.let { state ->

            try {
                for (item in state.salesCartList) {
                    if (item.medicine?.id == medicine.id) {
                        return@addToCard
                    }
                }
                Log.d(TAG, "Successfully Add To Cart")
                Log.d(TAG, "Show Medicine property " + medicine.toString())

                val previousAmount = state.totalAmount?.toInt()
                Log.d(TAG, "Previous Amount " + previousAmount.toString())
                var unitEquivalentQuantity : Int = 0
                var salesUnit : MedicineUnits? = null
                if (unitId == -1) {
                    for (unit in medicine.units) {
                        if (unit.id == unitId) {
                            unitEquivalentQuantity = unit.quantity
                            salesUnit = unit
                            break
                        }
                    }
                }
                else {
                    for (unit in medicine.units) {
                        if (unit.type == "SALES") {
                            unitEquivalentQuantity = unit.quantity
                            salesUnit = unit
                            break
                        }
                    }
                }

                if (unitEquivalentQuantity == 0) {
                    unitEquivalentQuantity = medicine.units.first().quantity
                    salesUnit = medicine.units.first()
                }
                if (unitEquivalentQuantity == 0) {
                    throw Exception("Unit Not Found")
                }

                if (salesUnit == null) {
                    throw Exception("Unit Not Found")
                }
                val newAmount = medicine.mrp!! * quantity!! * unitEquivalentQuantity!!
                Log.d(TAG, "New Amount " + newAmount.toString())

                val totalAmount = newAmount!! + previousAmount!!

                val newCartList = state.salesCartList.toMutableList()
                newCartList.add(SalesCart(
                    medicine = medicine,
                    salesUnit = salesUnit!!,
                    quantity = quantity,
                    amount = newAmount
                ))


                this.state.value = state.copy(
                    salesCartList = newCartList,
                    totalAmount = totalAmount,
                    totalAmountAfterDiscount = totalAmount
                )

            }
            catch (e : Exception) {
                e.printStackTrace()
                appendToMessageQueue(
                    StateMessage(
                        response = Response(
                            message = medicine.brand_name + " " + " has no unit",
                            uiComponentType = UIComponentType.Dialog(),
                            messageType = MessageType.Error()
                        )
                    )
                )
            }
        }
    }


    private fun removeHeadFromQueue() {
        state.value?.let { state ->
            try {
                val queue = state.queue
                queue.remove() // can throw exception if empty
                this.state.value = state.copy(queue = queue)
            } catch (e: Exception) {
                Log.d(TAG, "removeHeadFromQueue: Nothing to remove from DialogQueue")
            }
        }
    }

    private fun appendToMessageQueue(stateMessage: StateMessage){
        state.value?.let { state ->
            val queue = state.queue
            if(!stateMessage.doesMessageAlreadyExistInQueue(queue = queue)){
                if(!(stateMessage.response.uiComponentType is UIComponentType.None)){
                    queue.add(stateMessage)
                    this.state.value = state.copy(queue = queue)
                }
            }
        }
    }

    private fun onUpdateQueryExhausted(isExhausted: Boolean) {
        state.value?.let { state ->
            this.state.value = state.copy(isQueryExhausted = isExhausted)
        }
    }

    private fun clearList() {
    }

    private fun resetPage() {
        state.value = state.value?.copy(page = 1)
        onUpdateQueryExhausted(false)
    }

    private fun incrementPageNumber() {
        state.value?.let { state ->
            val pageNumber : Int = (state.medicineList.size / PAGINATION_PAGE_SIZE) as Int + 1
            Log.d(TAG, "Pre increment page number " + pageNumber)
            this.state.value = state.copy(page = pageNumber)
        }
        Log.d(TAG, "After increment page number " + this.state.value?.page!!.toString())
    }

    private fun onUpdateQuery(query: String) {
        state.value = state.value?.copy(query = query)
    }


    private fun createNewOrder() {
        processOder()
        Log.d(TAG, "ViewModel page number " + state.value?.page)
        state.value?.let { state ->
            createSalesOrder.execute(
                authToken = sessionManager.state.value?.authToken,
                state.order.toCreateSalesOrder()
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { order ->
                    if (order.pk != null) {
                        this.state.value = state.copy(
                            order = order
                        )
                    }

                    this.state.value = state.copy(
                        order = order
                    )
                    callback.done()
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }

            }.launchIn(viewModelScope)
        }
    }

    private fun processOder() {
        val list = processOrderMedicine()
        state.value?.let { state ->
            this.state.value = state.copy(
                order = SalesOrder(
                    pk = state.pk,
                    customer = state.customer?.pk,
                    mobile = null,
                    total_amount = state.totalAmount?.toFloat(),
                    total_after_discount = state.totalAmountAfterDiscount?.toFloat(),
                    paid_amount = state.receivedAmount,
                    discount = state.discountAmount,
                    is_discount_percent = (state.discount == state.totalAmountAfterDiscount),
                    is_return = false,
                    status = 0,
                    created_at = "",
                    updated_at = "",
                    sales_oder_medicines = list
                )
            )
        }
    }

    private fun processOrderMedicine() : List<SalesOrderMedicine> {
        val list = mutableListOf<SalesOrderMedicine>()
        state.value?.let {state ->
            for (item in state.salesCartList) {
                list.add(
                    SalesOrderMedicine(
                        unit = item.salesUnit?.id!!,
                        quantity = item.quantity?.toFloat()!!,
                        mrp = item.medicine?.mrp!!,
                        local_medicine = item.medicine?.id!!,
                        brand_name = item.medicine?.brand_name!!,
                        unit_name = item.salesUnit?.name!!,
                        amount = item.amount
                    )
                )
            }
        }
        return list
    }


    private fun search() {

        Log.d(TAG, "SalesViewModel page number " + state.value.toString())
        state.value?.let { state ->
            searchLocalMedicine.execute(
                authToken = sessionManager.state.value?.authToken,
                query = state.query,
                action = "",
                page = state.page,
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { list ->
                    this.state.value = state.copy(medicineList = list)
                }

                dataState.stateMessage?.let { stateMessage ->
                    if(stateMessage.response.message?.contains(ErrorHandling.INVALID_PAGE) == true){
                        onUpdateQueryExhausted(true)
                    }else{
                        appendToMessageQueue(stateMessage)
                    }
                }

            }.launchIn(viewModelScope)
        }
    }


    private fun getOrderDetails(pk : Int) {
        state.value?.let { state ->
            orderDetailsInteractor.execute(
                authToken = sessionManager.state.value?.authToken,
                pk = pk
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { orderWithMedicine ->
                    this.state.value = state.copy(
                        order = orderWithMedicine.order,
                        pk = orderWithMedicine.order.pk!!
                    )
                    if (orderWithMedicine.medicineList != null) {
                        for (i in 0..(orderWithMedicine.medicineList?.size!! - 1)) {
                            onTriggerEvent(
                                SalesCardEvents.AddToCard(
                                    medicine = orderWithMedicine.medicineList?.get(i)!!,
                                    quantity = orderWithMedicine.order.sales_oder_medicines?.get(i)?.quantity?.toInt()!!,
                                    unitId = orderWithMedicine.order.sales_oder_medicines?.get(i)?.unit!!
                                ))
                            if (orderWithMedicine.order.sales_oder_medicines?.get(i)?.mrp!! != 0f) {
                                changeMRP(this.state.value?.salesCartList?.get(i)!!, orderWithMedicine.order.sales_oder_medicines?.get(i)?.mrp!!)
                            }
                        }
                    }

                }

                dataState.stateMessage?.let { stateMessage ->
                    if(stateMessage.response.message?.contains(ErrorHandling.INVALID_PAGE) == true){
                        onUpdateQueryExhausted(true)
                    }else{
                        appendToMessageQueue(stateMessage)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }


}

interface OnCompleteCallback {
    fun done()
}