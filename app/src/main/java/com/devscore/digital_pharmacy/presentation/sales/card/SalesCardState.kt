package com.devscore.digital_pharmacy.presentation.sales.card

import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.Queue
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

data class SalesCardState (
    val isLoading : Boolean = false,
    val order : SalesOrder = SalesOrder(
        pk = -2,
        customer = -1,
        customer_name = null,
        mobile = null,
        total_amount = 0f,
        total_after_discount = 0f,
        paid_amount = 0f,
        discount = 0f,
        is_discount_percent = false,
        is_return = false,
        status = 0,
        created_at = "",
        updated_at = "",
        sales_oder_medicines = ArrayList<SalesOrderMedicine>()
    ),
    val pk : Int = -2,
    val account : Account? = null,
    val totalAmount : Float = 0f,
    val is_discount_percent : Boolean = false,
    val receivedAmount : Float = 0f,
    val discount : Float = 0f,
    val discountAmount : Float = 0f,
    val totalAmountAfterDiscount : Float = 0f,
    val customer : Customer? = null,
    val salesCartList : MutableList<SalesCart> = mutableListOf(),
    val medicineList : List<LocalMedicine> = listOf(),
    val query: String = "",
    val page: Int = 1,
    val isQueryExhausted: Boolean = false, // no more results available, prevent next page
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
    val uploaded : Boolean = false,
    val deleted : Boolean = false
)

//val unitList = mutableListOf<MedicineUnits>()
//for (unit in item.units) {
//    unitList.add(
//        MedicineUnits(
//            id = unit.id,
//            quantity = unit.quantity,
//            name = unit.name,
//            type = unit.type
//        )
//    )
//}
//val medicine = LocalMedicine(
//    id = item.id,
//    brand_name = item.brand_name,
//    sku = item.sku,
//    dar_number = item.dar_number,
//    mr_number = item.mr_number,
//    generic = item.generic,
//    indication = item.indication,
//    symptom = item.symptom,
//    strength = item.strength,
//    description = item.description,
//    image = item.image,
//    mrp = item.mrp,
//    purchase_price = item.purchase_price,
//    discount = item.discount,
//    is_percent_discount = item.is_percent_discount,
//    manufacture = item.manufacture,
//    kind = item.kind,
//    form = item.form,
//    remaining_quantity = item.remaining_quantity,
//    damage_quantity = item.damage_quantity,
//    exp_date = item.exp_date,
//    rack_number = item.rack_number,
//    units = unitList
//)