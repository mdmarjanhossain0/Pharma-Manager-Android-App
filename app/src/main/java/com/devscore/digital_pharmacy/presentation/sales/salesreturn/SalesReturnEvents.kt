package com.devscore.digital_pharmacy.presentation.sales.salesreturn

import com.devscore.digital_pharmacy.business.domain.models.Customer
import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.models.MedicineUnits
import com.devscore.digital_pharmacy.business.domain.models.SalesCart
import com.devscore.digital_pharmacy.business.domain.util.StateMessage
import com.devscore.digital_pharmacy.presentation.sales.card.SalesCardEvents

sealed class SalesReturnEvents {

    object GenerateNewOrder : SalesReturnEvents()

    data class OrderDetails(val pk : Int) : SalesReturnEvents()

    object NewLocalMedicineSearch : SalesReturnEvents()

    data class AddToCard(val medicine : LocalMedicine, val quantity : Int = 1, val unitId : Int = -1): SalesReturnEvents()

    data class UpdateQuantity(val cart : SalesCart, val quantity : Int?) : SalesReturnEvents()

    data class ChangeUnit(val cart : SalesCart, val unit : MedicineUnits?, val quantity : Int?) : SalesReturnEvents()
//    data class ChangeUnit(val cart : SalesCart, val unit : Int?, val quantity : Int?) : SalesCardEvents()
//    data class ChangeUnit(val medicine: LocalMedicine, val unit : Int?, val quantity : Int?) : SalesCardEvents()

    data class IsDiscountPercent(val isDiscountPercent : Boolean = false) : SalesReturnEvents()

    data class ReceiveAmount(val amount : Float? = 0f) : SalesReturnEvents()

    data class Discount(val discount : Float? = 0f) : SalesReturnEvents()

    data class DeleteMedicine(val medicine : LocalMedicine) : SalesReturnEvents()


    data class SelectCustomer(val customer : Customer) : SalesReturnEvents()

    data class SearchWithQuery(val query: String) : SalesReturnEvents()

    object NextPage: SalesReturnEvents()

    data class UpdateQuery(val query: String): SalesReturnEvents()


    object GetOrderAndFilter: SalesReturnEvents()

    data class Error(val stateMessage: StateMessage): SalesReturnEvents()

    object OnRemoveHeadFromQueue: SalesReturnEvents()
}