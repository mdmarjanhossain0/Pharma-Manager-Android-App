package com.devscore.digital_pharmacy.presentation.sales.card

import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.StateMessage
import com.devscore.digital_pharmacy.presentation.sales.details.SalesDetailsEvents

sealed class SalesCardEvents {

    object GenerateNewOrder : SalesCardEvents()

    object OrderCompleted : SalesCardEvents()

    object NewLocalMedicineSearch : SalesCardEvents()

    data class OrderDetails(val pk : Int) : SalesCardEvents()

    data class AddToCard(val medicine : LocalMedicine, val quantity : Int = 1, val unitId : Int = -1): SalesCardEvents()

    data class ChangeMRP(val cart : SalesCart, val mrp : Float): SalesCardEvents()

    data class UpdateQuantity(val cart : SalesCart, val quantity : Int?) : SalesCardEvents()

    data class ChangeUnit(val cart : SalesCart, val unit : MedicineUnits?, val quantity : Int?) : SalesCardEvents()
//    data class ChangeUnit(val cart : SalesCart, val unit : Int?, val quantity : Int?) : SalesCardEvents()
//    data class ChangeUnit(val medicine: LocalMedicine, val unit : Int?, val quantity : Int?) : SalesCardEvents()

    data class IsDiscountPercent(val isDiscountPercent : Boolean = false) : SalesCardEvents()

    data class ReceiveAmount(val amount : Float? = 0f) : SalesCardEvents()

    data class Discount(val discount : Float? = 0f) : SalesCardEvents()

    data class DeleteMedicine(val medicine : LocalMedicine) : SalesCardEvents()


    data class SelectCustomer(val customer : Customer) : SalesCardEvents()

    data class SearchWithQuery(val query: String) : SalesCardEvents()



    data class DeleteOrder(val order : SalesOrder) : SalesCardEvents()

    object NextPage: SalesCardEvents()

    data class UpdateQuery(val query: String): SalesCardEvents()


    object GetOrderAndFilter: SalesCardEvents()

    data class Error(val stateMessage: StateMessage): SalesCardEvents()

    object OnRemoveHeadFromQueue: SalesCardEvents()
}