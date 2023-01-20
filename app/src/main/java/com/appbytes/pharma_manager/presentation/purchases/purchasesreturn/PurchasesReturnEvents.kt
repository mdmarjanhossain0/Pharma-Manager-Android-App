package com.appbytes.pharma_manager.presentation.purchases.purchasesreturn

import com.appbytes.pharma_manager.business.domain.models.*
import com.appbytes.pharma_manager.business.domain.util.StateMessage

sealed class PurchasesReturnEvents {

    object GenerateNewOrder : PurchasesReturnEvents()

    data class OrderDetails(val pk : Int) : PurchasesReturnEvents()

    object NewLocalMedicineSearch : PurchasesReturnEvents()

    data class AddToCard(val medicine : LocalMedicine, val quantity : Int = 1, val unitId : Int = -1): PurchasesReturnEvents()

    data class UpdateQuantity(val cart : PurchasesCart, val quantity : Int?) : PurchasesReturnEvents()




    data class ChangePP(val cart : PurchasesCart, val purchase_price : Float): PurchasesReturnEvents()

    data class ChangeUnit(val cart : PurchasesCart, val unit : MedicineUnits?, val quantity : Int?) : PurchasesReturnEvents()
//    data class ChangeUnit(val cart : SalesCart, val unit : Int?, val quantity : Int?) : SalesCardEvents()
//    data class ChangeUnit(val medicine: LocalMedicine, val unit : Int?, val quantity : Int?) : SalesCardEvents()

    data class IsDiscountPercent(val isDiscountPercent : Boolean = false) : PurchasesReturnEvents()

    data class ReceiveAmount(val amount : Float? = 0f) : PurchasesReturnEvents()

    data class Discount(val discount : Float? = 0f) : PurchasesReturnEvents()

    data class DeleteMedicine(val medicine : LocalMedicine) : PurchasesReturnEvents()


    data class SelectSupplier(val vendor : Supplier) : PurchasesReturnEvents()

    data class SearchWithQuery(val query: String) : PurchasesReturnEvents()

    object NextPage: PurchasesReturnEvents()

    data class UpdateQuery(val query: String): PurchasesReturnEvents()


    object GetOrderAndFilter: PurchasesReturnEvents()

    data class Error(val stateMessage: StateMessage): PurchasesReturnEvents()

    object OnRemoveHeadFromQueue: PurchasesReturnEvents()
}