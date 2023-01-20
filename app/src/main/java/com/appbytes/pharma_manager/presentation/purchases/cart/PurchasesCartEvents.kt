package com.appbytes.pharma_manager.presentation.purchases.cart

import com.appbytes.pharma_manager.business.domain.models.*
import com.appbytes.pharma_manager.business.domain.util.StateMessage

sealed class PurchasesCartEvents {

    object GenerateNewOrder : PurchasesCartEvents()



    object PurchasesOrderCompleted : PurchasesCartEvents()

    object NewLocalMedicineSearch : PurchasesCartEvents()

    data class OrderDetails(val pk : Int) : PurchasesCartEvents()

    data class AddToCard(val medicine : LocalMedicine, val quantity : Int = 1, val unitId : Int = -1): PurchasesCartEvents()





    data class ChangeMRP(val cart : PurchasesCart, val mrp : Float): PurchasesCartEvents()


    data class ChangePP(val cart : PurchasesCart, val purchases_price : Float): PurchasesCartEvents()

    data class ChangeUnit(val cart : PurchasesCart, val unit : MedicineUnits?, val quantity : Int?) : PurchasesCartEvents()
//    data class ChangeUnit(val medicine: LocalMedicine, val unit : Int?, val quantity : Int?) : PurchasesCartEvents()

    data class IsDiscountPercent(val isDiscountPercent : Boolean = false) : PurchasesCartEvents()

    data class ReceiveAmount(val amount : Float? = 0f) : PurchasesCartEvents()

    data class Discount(val discount : Float? = 0f) : PurchasesCartEvents()

    data class DeleteMedicine(val medicine : LocalMedicine) : PurchasesCartEvents()

    data class SelectSupplier(val supplier : Supplier) : PurchasesCartEvents()

    data class SearchWithQuery(val query: String) : PurchasesCartEvents()


    data class DeleteOrder(val order : PurchasesOrder) : PurchasesCartEvents()

    object NextPage: PurchasesCartEvents()

    data class UpdateQuery(val query: String): PurchasesCartEvents()


    object GetOrderAndFilter: PurchasesCartEvents()

    data class Error(val stateMessage: StateMessage): PurchasesCartEvents()

    object OnRemoveHeadFromQueue: PurchasesCartEvents()
}