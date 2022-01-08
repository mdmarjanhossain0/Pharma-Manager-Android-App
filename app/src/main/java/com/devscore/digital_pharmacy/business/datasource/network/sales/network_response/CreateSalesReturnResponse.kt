package com.devscore.digital_pharmacy.business.datasource.network.sales.network_response

import com.devscore.digital_pharmacy.business.domain.models.SalesOrder
import com.devscore.digital_pharmacy.business.domain.models.SalesReturn
import com.google.gson.annotations.SerializedName

data class CreateSalesReturnResponse (

    @SerializedName("pk") var pk : Int?,
    @SerializedName("customer") var customer : Int,
    @SerializedName("sales_order") var sales_order : Int?,
    @SerializedName("customer_name") var customer_name : String?,
    @SerializedName("total_amount") var total_amount : Float?,
    @SerializedName("total_after_fine") var total_after_fine : Float?,
    @SerializedName("return_amount") var return_amount : Float?,
    @SerializedName("fine") var fine : Float?,
    @SerializedName("is_fine_percent") var is_fine_percent : Boolean,
    @SerializedName("sales_return_medicines") var sales_return_medicines : List<SalesOrderItemDto>,
    @SerializedName("created_at")var created_at : String,
    @SerializedName("updated_at")var updated_at : String?,
    @SerializedName("brand_name") var brand_name : String?
)





fun CreateSalesReturnResponse.toSalesReturn() : SalesReturn {
    return SalesReturn(
        pk = pk,
        customer = customer,
        sales_order = sales_order,
        total_amount = total_amount,
        total_after_fine = total_after_fine,
        return_amount = return_amount,
        fine = fine,
        is_fine_percent = is_fine_percent,
        created_at = created_at,
        updated_at = updated_at,
        sales_return_medicines = sales_return_medicines.map {
            it.toSalesOrderMedicine()
        }
    )
}