package com.appbytes.pharma_manager.presentation.purchases.purchasesreturn

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appbytes.pharma_manager.business.domain.models.*
import com.appbytes.pharma_manager.business.domain.util.*
import com.appbytes.pharma_manager.business.interactors.inventory.local.SearchLocalMedicine
import com.appbytes.pharma_manager.business.interactors.purchases.PurchasesOrderDetailsInteractor
import com.appbytes.pharma_manager.business.interactors.purchases.PurchasesReturnInteractor
import com.appbytes.pharma_manager.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class PurchasesReturnViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val createPurchasesReturnInteractor : PurchasesReturnInteractor,
    private val orderDetailsInteractor: PurchasesOrderDetailsInteractor,
    private val localMedicine: SearchLocalMedicine
) : ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<PurchasesReturnState> = MutableLiveData(PurchasesReturnState())

    init {
    }

    fun onTriggerEvent(event: PurchasesReturnEvents) {
        when (event) {
            is PurchasesReturnEvents.GenerateNewOrder -> {
                createNewOrder()
            }

            is PurchasesReturnEvents.OrderDetails -> {
                getOrderDetails(event.pk)
            }

            is PurchasesReturnEvents.NewLocalMedicineSearch -> {
                search()
            }


            is PurchasesReturnEvents.AddToCard -> {
                addToCard(event.medicine, event.quantity, event.unitId)
            }


            is PurchasesReturnEvents.ChangePP -> {
                changePP(event.cart, event.purchase_price)
            }


            is PurchasesReturnEvents.UpdateQuantity -> {
                updateQuantity(event.cart, event.quantity!!)
            }
            is PurchasesReturnEvents.ChangeUnit -> {
                Log.d(TAG, "SalesViewModel ChangeUnit Call")
                changeUnit(event.cart, event.unit!!, event.quantity!!)
            }

            is PurchasesReturnEvents.ReceiveAmount -> {
                receiveAmount(event.amount!!)
            }

            is PurchasesReturnEvents.IsDiscountPercent -> {
                isFinePercent(event.isDiscountPercent)
            }

            is PurchasesReturnEvents.Discount -> {
                fine(event.discount)
            }

            is PurchasesReturnEvents.DeleteMedicine -> {
                deleteFromCart(event.medicine)
            }


            is PurchasesReturnEvents.SelectSupplier -> {
                selectSupplier(event.vendor)
            }
            is PurchasesReturnEvents.NextPage -> {
                incrementPageNumber()
                search()
            }

            is PurchasesReturnEvents.UpdateQuery -> {
                onUpdateQuery(event.query)
            }

            is PurchasesReturnEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is PurchasesReturnEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
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
                        order = orderWithMedicine.order)


                    if (orderWithMedicine.medicineList != null) {
                        for (i in 0..(orderWithMedicine.medicineList?.size!! - 1)) {
                            onTriggerEvent(
                                PurchasesReturnEvents.AddToCard(
                                    medicine = orderWithMedicine.medicineList?.get(i)!!,
                                    quantity = orderWithMedicine.order.purchases_order_medicines?.get(i)?.quantity?.toInt()!!,
                                    unitId = orderWithMedicine.order.purchases_order_medicines?.get(i)?.unit!!
                                ))
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

    private fun selectSupplier(vendor: Supplier) {
        state.value?.let { state ->
            this.state.value = state.copy(
                vendor = vendor
            )
        }
    }

    private fun deleteFromCart(medicine : LocalMedicine) {
        state.value?.let { state ->
            var checkExist = 0
            for (item in state.purchasesCartList) {
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

            var previousSalesCartItem : PurchasesCart? = null

            for (item in state.purchasesCartList) {
                if (item.medicine?.id == medicine.id) {
                    previousSalesCartItem = item
                }
            }

            if (previousSalesCartItem == null) {
                throw Exception("Item Not Found")
            }


            val totalAmount = previousAmount!! - previousSalesCartItem.amount!!

            val newCartList = state.purchasesCartList.toMutableList()
            for (item in state.purchasesCartList) {
                if (item.medicine?.id == medicine.id) {
                    newCartList.remove(item)
                    break
                }
            }

            this.state.value = state.copy(
                purchasesCartList = newCartList,
                totalAmount = totalAmount,
                totalAmountAfterFine = totalAmount,
            )
        }
    }

    private fun fine(discount: Float?) {
        state.value?.let {state ->
            var discountAmount = 0f
            if (state.is_fine_percent) {
                discountAmount = ((state.totalAmount!! * discount!!) / 100)
            }
            else {
                discountAmount = discount!!
            }
            val totalAmountAfterDiscount = state.totalAmount!! + discountAmount
            this.state.value = state.copy(
                fine = discount,
                fineAmount = discountAmount,
                totalAmountAfterFine = totalAmountAfterDiscount
            )
        }
    }

    private fun isFinePercent(discountPercent: Boolean) {
        state.value?.let {state ->
            var discountAmount : Float = 0f
            if (discountPercent) {
                discountAmount = ((state.totalAmount!! * state.fine!!) / 100 )
            }
            else {
                discountAmount = state.fine!!
            }
            val totalAmountAfterDiscount = state.totalAmount!! + discountAmount
            this.state.value = state.copy(
                is_fine_percent = discountPercent,
                fineAmount = discountAmount,
                totalAmountAfterFine = totalAmountAfterDiscount
            )
        }
    }

    private fun receiveAmount(amount : Float) {
        state.value?.let {state ->
            this.state.value = state.copy(
                returnAmount = amount
            )
        }
    }



    private fun updateQuantity(cart : PurchasesCart, quantity: Int) {
        state.value?.let { state ->
            val previousAmount = state.totalAmount
            Log.d(TAG, "Previous Amount " + previousAmount.toString())
            val unitEquivalentQuantity = cart.purchasesUnit?.quantity

            val newAmount = cart.medicine?.mrp!! * quantity * unitEquivalentQuantity!!
            Log.d(TAG, "New Amount " + newAmount.toString())

            val totalAmount = previousAmount!! + newAmount - cart.amount!!


            val previousCartList = state.purchasesCartList.toMutableList()
            val newCartList = mutableListOf<PurchasesCart>()


            for (item in previousCartList) {
                if (item.medicine?.id == cart.medicine?.id) {
                    Log.d(TAG, "Id Match And Add Successfully")
                    newCartList.add(
                        PurchasesCart(
                            medicine = cart.medicine,
                            purchasesUnit = cart.purchasesUnit,
                            quantity = quantity,
                            amount = newAmount
                        )
                    )
                }
                else {
                    newCartList.add(item)
                }
            }

            Log.d(TAG, "Previous Cart List " + previousCartList.size + " " + previousCartList.toString())
            Log.d(TAG, "New Cart List " + newCartList.size + " " + newCartList.toString())

            this.state.value = state.copy(
                purchasesCartList = newCartList,
                totalAmount = totalAmount,
                totalAmountAfterFine = totalAmount,
            )
        }
    }

    private fun changeUnit(cart : PurchasesCart, unit: MedicineUnits, quantity : Int) {
        state.value?.let { state ->
            val previousAmount = state.totalAmount
            Log.d(TAG, "Previous Amount " + previousAmount.toString())
            val unitEquivalentQuantity = unit.quantity

            val newAmount = cart.medicine?.purchase_price!! * quantity!! * unitEquivalentQuantity
            Log.d(TAG, "New Amount " + newAmount.toString())

            val totalAmount = previousAmount!! + newAmount - cart.amount!!

            val previousCartList = state.purchasesCartList.toMutableList()
//            val newCartList = mutableListOf<SalesCart>()
            previousCartList.find {
                it.medicine?.id == cart.medicine?.id!!
            }?.purchasesUnit = unit

            previousCartList.find {
                it.medicine?.id == cart.medicine?.id!!
            }?.amount = newAmount


            previousCartList.find {
                it.medicine?.id == cart.medicine?.id!!
            }?.quantity = quantity


            Log.d(TAG, "Previous Cart List " + previousCartList.size + " " + previousCartList.toString())

            this.state.value = state.copy(
                purchasesCartList = previousCartList,
                totalAmount = totalAmount,
                totalAmountAfterFine = totalAmount
            )
        }
    }



    private fun changePP(cart : PurchasesCart, purchase_price : Float) {
        state.value?.let { state ->


            val previousAmount = state.totalAmount
            Log.d(TAG, "Previous Amount " + previousAmount.toString())
            val unitEquivalentQuantity = cart.purchasesUnit?.quantity
            cart.medicine?.purchase_price = purchase_price

            val newAmount = cart.medicine?.purchase_price!! * cart.quantity!! * unitEquivalentQuantity!!
            Log.d(TAG, "New Amount " + newAmount.toString())

            val totalAmount = previousAmount!! + newAmount - cart.amount!!

            val previousCartList = state.purchasesCartList.toMutableList()
            previousCartList.find {
                it.medicine?.id == cart.medicine?.id!!
            }?.purchasesUnit = cart.purchasesUnit

            previousCartList.find {
                it.medicine?.id == cart.medicine?.id!!
            }?.amount = newAmount


            previousCartList.find {
                it.medicine?.id == cart.medicine?.id!!
            }?.quantity = cart.quantity



            Log.d(TAG, "Previous Cart List " + previousCartList.size + " " + previousCartList.toString())

            this.state.value = state.copy(
                purchasesCartList = previousCartList,
                totalAmount = totalAmount,
                totalAmountAfterFine = totalAmount,
            )
        }
    }


    private fun addToCard(medicine : LocalMedicine, quantity : Int = 1, unitId : Int = -1) {
        state.value?.let { state ->

            try {
                for (item in state.purchasesCartList) {
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
                if (unitId != -1) {
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
                        if (unit.type == "PURCHASES_UNIT") {
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
                val newAmount = medicine.purchase_price!! * quantity!! * unitEquivalentQuantity!!
                Log.d(TAG, "New Amount " + newAmount.toString())

                val totalAmount = newAmount!! + previousAmount!!

                val newCartList = state.purchasesCartList.toMutableList()
                newCartList.add(
                    PurchasesCart(
                        medicine = medicine,
                        purchasesUnit = salesUnit!!,
                        quantity = quantity,
                        amount = newAmount
                    )
                )


                this.state.value = state.copy(
                    purchasesCartList = newCartList,
                    totalAmount = totalAmount,
                    totalAmountAfterFine = totalAmount
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
            val pageNumber : Int = (state.medicineList.size / 5) as Int + 1
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
            createPurchasesReturnInteractor.execute(
                authToken = sessionManager.state.value?.authToken,
                state.returnOrder
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { order ->
                    if (order.pk != null) {
                        this.state.value = state.copy(
                            returnOrder = order.toCreatePurchasesReturn(),
                            uploaded = true
                        )
                    }

                    this.state.value = state.copy(
                        returnOrder = order.toCreatePurchasesReturn()
                    )
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

    private fun processOder() {
        val list = processOrderMedicine()
        state.value?.let { state ->
            this.state.value = state.copy(
                returnOrder = CreatePurchasesReturn (
                    vendor = state.vendor?.pk,
                    purchases_order = state.order.pk,
                    total_amount = state.totalAmount?.toFloat(),
                    total_after_fine = state.totalAmountAfterFine?.toFloat(),
                    return_amount = state.returnAmount!!,
                    fine = state.fineAmount,
                    is_fine_percent = (state.fine == state.totalAmountAfterFine),
                    purchases_return_medicines = list.map { it.toCreatePurchasesOrderMedicine() }
                )
            )
        }
    }

    private fun processOrderMedicine() : List<PurchasesOrderMedicine> {
        val list = mutableListOf<PurchasesOrderMedicine>()
        state.value?.let {state ->
            for (item in state.purchasesCartList) {
                list.add(
                    PurchasesOrderMedicine(
                        unit = item.purchasesUnit?.id!!,
                        quantity = item.quantity?.toFloat()!!,
                        mrp = item.medicine?.mrp!!,
                        purchase_price = item.medicine?.purchase_price!!,
                        local_medicine = item.medicine?.id!!,
                        brand_name = item.medicine?.brand_name!!,
                        unit_name = item.purchasesUnit?.name!!,
                        amount = item.amount
                    )
                )
            }
        }
        return list
    }


    private fun search() {
//        resetPage()
//        clearList()


        Log.d(TAG, "SalesViewModel page number " + state.value?.page)
/*        state.value?.let { state ->
            searchLocalMedicine.execute(
                authToken = sessionManager.state.value?.authToken,
                query = state.query,
                page = state.page,
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { list ->
                    Log.d(TAG, "ViewModel List Size " + list.size)
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
        }*/
    }

}