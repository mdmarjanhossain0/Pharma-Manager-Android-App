package com.devscore.digital_pharmacy.business.interactors.inventory.local

import android.content.Context
import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.*
import com.devscore.digital_pharmacy.business.datasource.network.inventory.InventoryApiService
import com.devscore.digital_pharmacy.business.datasource.network.inventory.network_responses.toLocalMedicine
import com.devscore.digital_pharmacy.business.domain.models.AuthToken
import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.util.*
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class AddFailureMedicineInteractor(
    private val service: InventoryApiService,
    private val cache: LocalMedicineDao,
    private val context: Context
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken? = null,
        medicines: List<LocalMedicine>,
        image : String?
    ): Flow<DataState<LocalMedicine>> = flow {

        emit(DataState.loading<LocalMedicine>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }
        Log.d(TAG, "Medicine " + medicines.toString())


        for (medicine in medicines) {
            val json = Gson()
            val gson = json.toJson(medicine.units)
            val units = RequestBody.create(
                MediaType.parse("application/json"),
                gson
            )
            var multipartBody: MultipartBody.Part? = null
            try {
                val imageFile = File(medicine.image)
                if (imageFile.exists()) {
                    val requestBody =
                        RequestBody.create(
                            MediaType.parse("image/jpeg"),
                            imageFile
                        )
                    multipartBody = MultipartBody.Part.createFormData(
                        "image",
                        imageFile.name,
                        requestBody
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d(TAG, "Handle File Process Exception")
            }


            try {
                Log.d(TAG, "Call Api Section")
                val medicineResponse = service.addMedicines(
                    authorization = "Token ${authToken.token}",
                    brand_name = RequestBody.create(
                        MediaType.parse("text/plain"),
                        medicine.brand_name.toString()
                    ),
                    sku = RequestBody.create(
                        MediaType.parse("text/plain"),
                        medicine.sku.toString()
                    ),
                    dar_number = RequestBody.create(
                        MediaType.parse("text/plain"),
                        medicine.dar_number.toString()
                    ),
                    mr_number = RequestBody.create(
                        MediaType.parse("text/plain"),
                        medicine.mr_number.toString()
                    ),
                    generic = RequestBody.create(
                        MediaType.parse("text/plain"),
                        medicine.generic.toString()
                    ),
                    indication = RequestBody.create(
                        MediaType.parse("text/plain"),
                        medicine.indication.toString()
                    ),
                    symptom = RequestBody.create(
                        MediaType.parse("text/plain"),
                        medicine.symptom.toString()
                    ),
                    strength = RequestBody.create(
                        MediaType.parse("text/plain"),
                        medicine.strength.toString()
                    ),
                    description = RequestBody.create(
                        MediaType.parse("text/plain"),
                        medicine.description.toString()
                    ),
                    mrp = RequestBody.create(
                        MediaType.parse("text/plain"),
                        medicine.mrp.toString()
                    ),
                    purchase_price = RequestBody.create(
                        MediaType.parse("text/plain"),
                        medicine.purchase_price.toString()
                    ),
                    discount = RequestBody.create(
                        MediaType.parse("text/plain"),
                        medicine.discount.toString()
                    ),
                    is_percent_discount = RequestBody.create(
                        MediaType.parse("text/plain"),
                        medicine.is_percent_discount.toString()
                    ),
                    manufacture = RequestBody.create(
                        MediaType.parse("text/plain"),
                        medicine.manufacture.toString()
                    ),
                    kind = RequestBody.create(
                        MediaType.parse("text/plain"),
                        medicine.kind.toString()
                    ),
                    form = RequestBody.create(
                        MediaType.parse("text/plain"),
                        medicine.form.toString()
                    ),
                    remaining_quantity = RequestBody.create(
                        MediaType.parse("text/plain"),
                        medicine.remaining_quantity.toString()
                    ),
                    damage_quantity = RequestBody.create(
                        MediaType.parse("text/plain"),
                        medicine.damage_quantity.toString()
                    ),
                    exp_date = RequestBody.create(MediaType.parse("text/plain"), medicine.exp_date.toString()),
                    rack_number = RequestBody.create(
                        MediaType.parse("text/plain"),
                        medicine.rack_number.toString()
                    ),
                    image = multipartBody,
                    units = units
                ).toLocalMedicine()


                try {
                    cache.insertLocalMedicine(medicineResponse.toLocalMedicineEntity())
                    for (unit in medicineResponse.toLocalMedicineUnitEntity()) {
                        cache.insertLocalMedicineUnit(unit)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }





                try {
                    cache.deleteFailureLocalMedicine(medicine.room_medicine_id!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } catch (e: Exception) {
                Log.d(TAG, "Exception " + e.toString())
               /* try{
                    var failureMedicine = medicine
                    val room_id = cache.insertFailureMedicine(failureMedicine.toFailureMedicine())
                    Log.d(TAG, "Room id " + room_id.toString())
                    failureMedicine = failureMedicine.copy(
                        room_medicine_id = room_id
                    )
                    for (unit in failureMedicine.toFailureMedicineUnitEntity()) {
                        Log.d(TAG, "Medicine Unit " + unit.toString())
                        val newUnit = unit.copy(
                            room_id = null
                        )
                        val unitId = cache.insertFailureMedicineUnit(newUnit)
                        Log.d(TAG, "Unit id " + unitId)
                    }
                }catch (e: Exception){
                    Log.d(TAG, "Room Exception Enter Exception is Exception " + e.toString())
                    e.printStackTrace()
                }*/

            }
        }


        val localMedicines = cache.getSyncData()
        Log.d(TAG, "Size " + localMedicines.size + " " + localMedicines.toString())
//        emit(
//            DataState.data(response = Response(
//                message = "Successfully Uploaded.",
//                uiComponentType = UIComponentType.None(),
//                messageType = MessageType.Success()
//            ), data = medicines[0]))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}