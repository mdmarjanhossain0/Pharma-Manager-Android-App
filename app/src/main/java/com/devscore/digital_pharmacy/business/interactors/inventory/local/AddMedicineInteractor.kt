package com.devscore.digital_pharmacy.business.interactors.inventory.local

import android.content.Context
import android.util.Log
import androidx.work.*
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.*
import com.devscore.digital_pharmacy.business.datasource.network.GenericResponse
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.datasource.network.inventory.InventoryApiService
import com.devscore.digital_pharmacy.business.datasource.network.inventory.network_responses.toLocalMedicine
import com.devscore.digital_pharmacy.business.domain.models.AddMedicine
import com.devscore.digital_pharmacy.business.domain.models.AuthToken
import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.models.toLocalMedicine
import com.devscore.digital_pharmacy.business.domain.util.*
import com.devscore.digital_pharmacy.business.interactors.account.TAG
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.IOException

class AddMedicineInteractor(
    private val service: InventoryApiService,
    private val cache: LocalMedicineDao,
    private val context: Context
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken? = null,
        medicine: AddMedicine,
        image : String?
    ): Flow<DataState<LocalMedicine>> = flow {

        emit(DataState.loading<LocalMedicine>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }
        Log.d(TAG, "Medicine " + medicine.toString())

        val json = Gson()
        val gson = json.toJson(medicine.units)
        val units = RequestBody.create(
            MediaType.parse("application/json"),
            gson
        )
        var multipartBody: MultipartBody.Part? = null
        try {
            val imageFile = java.io.File(medicine.image)
            if(imageFile.exists()) {
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
        }
        catch (e : Exception) {
            e.printStackTrace()
            Log.d(TAG, "Handle File Process Exception")
        }


        try{
            Log.d(TAG, "Call Api Section")
            val medicineResponse = service.addMedicines(
                authorization = "Token ${authToken.token}",
                brand_name = RequestBody.create(MediaType.parse("text/plain"), medicine.brand_name.toString()),
                sku = RequestBody.create(MediaType.parse("text/plain"), medicine.sku.toString()),
                dar_number = RequestBody.create(MediaType.parse("text/plain"), medicine.dar_number.toString()),
                mr_number = RequestBody.create(MediaType.parse("text/plain"), medicine.mr_number.toString()),
                generic = RequestBody.create(MediaType.parse("text/plain"), medicine.generic.toString()),
                indication = RequestBody.create(MediaType.parse("text/plain"), medicine.indication.toString()),
                symptom = RequestBody.create(MediaType.parse("text/plain"), medicine.symptom.toString()),
                strength = RequestBody.create(MediaType.parse("text/plain"), medicine.strength.toString()),
                description = RequestBody.create(MediaType.parse("text/plain"), medicine.description.toString()),
                mrp = RequestBody.create(MediaType.parse("text/plain"), medicine.mrp.toString()),
                purchase_price = RequestBody.create(MediaType.parse("text/plain"), medicine.purchases_price.toString()),
                discount = RequestBody.create(MediaType.parse("text/plain"), medicine.discount.toString()),
                is_percent_discount = RequestBody.create(MediaType.parse("text/plain"), medicine.is_percent_discount.toString()),
                manufacture = RequestBody.create(MediaType.parse("text/plain"), medicine.manufacture.toString()),
                kind = RequestBody.create(MediaType.parse("text/plain"), medicine.kind.toString()),
                form = RequestBody.create(MediaType.parse("text/plain"), medicine.form.toString()),
                remaining_quantity = RequestBody.create(MediaType.parse("text/plain"), medicine.remaining_quantity.toString()),
                damage_quantity = RequestBody.create(MediaType.parse("text/plain"), medicine.damage_quantity.toString()),
                exp_date = RequestBody.create(MediaType.parse("text/plain"), medicine.exp_date.toString()),
                rack_number = RequestBody.create(MediaType.parse("text/plain"), medicine.rack_number.toString()),
                image = multipartBody,
                units = units
            ).toLocalMedicine()


            try{
                cache.insertLocalMedicine(medicineResponse.toLocalMedicineEntity())
                for (unit in medicineResponse.toLocalMedicineUnitEntity()) {
                    cache.insertLocalMedicineUnit(unit)
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
            val stateMedicine = medicine.toLocalMedicine()

            emit(
                DataState.data(response = Response(
                    message = "Successfully Uploaded.",
                    uiComponentType = UIComponentType.Toast(),
                    messageType = MessageType.Success()
                ), data = stateMedicine))

        }catch (e: Exception){
            e.printStackTrace()
            Log.d(TAG, "Exception " + e.toString())

            when (e) {
                is HttpException -> {
                    val body = e.response()?.errorBody()?.string()!!
                    Log.d(com.devscore.digital_pharmacy.business.interactors.account.TAG, body)
                    val gson = GsonBuilder().create()
                    val errorEntity : GenericResponse = Gson().fromJson(body, GenericResponse::class.java)
                    Log.d(TAG, errorEntity.toString() + " ")
                    emit(
                        DataState.error<LocalMedicine>(
                            response = Response(
                                message = errorEntity.errorMessage,
                                uiComponentType = UIComponentType.Dialog(),
                                messageType = MessageType.Error()
                            )
                        )
                    )
                }


                is IOException -> {
                    try{
                        var failureMedicine = medicine.toLocalMedicine()
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
                    }

                    emit(
                        DataState.error<LocalMedicine>(
                            response = Response(
                                message = "Unable to upload medicine. Please careful and don't uninstall or log out",
                                uiComponentType = UIComponentType.Dialog(),
                                messageType = MessageType.Error()
                            )
                        )
                    )
                    Log.d(TAG, "IOException excepiton")
                }
                else -> {
                    Log.d(TAG, "Unknown excepiton")
                    emit(
                        DataState.error<LocalMedicine>(
                            response = Response(
                                message = e.message,
                                uiComponentType = UIComponentType.Dialog(),
                                messageType = MessageType.Error()
                            )
                        )
                    )
                }
            }
        }
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}


fun <T> handleUseCaseException(e: Throwable): DataState<T> {
    e.printStackTrace()
    when (e) {
        is HttpException -> { // Retrofit exception
            extractHttpExceptions(e)
            val errorResponse = convertErrorBody(e)
            return DataState.error<T>(
                response = Response(
                    message = errorResponse,
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                )
            )
        }


        is IOException -> {
            Log.d(TAG, "IOException excepiton")
            return DataState.error<T>(
                response = Response(
                    message = e.message,
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                )
            )
        }
        else -> {
            Log.d(TAG, "Unknown excepiton hffhsdlfhsdlfsdhlfjsdlfsdhflsdfjlfjlfjslfjsdlfj")
            return DataState.error<T>(
                response = Response(
                    message = e.message,
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                )
            )
        }
    }
}



private fun convertErrorBody(throwable: HttpException): String? {
    return try {
        throwable.response()?.errorBody()?.string()
    } catch (exception: java.lang.Exception) {
        ErrorHandling.UNKNOWN_ERROR
    }
}




private fun extractHttpExceptions(ex: HttpException){
    val body = ex.response()?.errorBody()?.string()!!
    Log.d(TAG, body)
    val gson = GsonBuilder().create()
    val errorEntity : GenericResponse = Gson().fromJson(body, GenericResponse::class.java)
    Log.d(TAG, errorEntity.toString() + " ")
    when (ex.code()) {
        400 ->
            Log.d(TAG, "400 Bad Request " + ex.response()?.errorBody().toString())
        500 ->
            Log.d(TAG, "500 Bad Request " + ex.response()?.errorBody().toString())
        401 ->
            Log.d(TAG, "401 Bad Request " + ex.response()?.errorBody().toString())

        404 ->
            Log.d(TAG, "404 Bad Request " + ex.response()?.errorBody().toString())

        else ->
            Log.d(TAG, "else Bad Request " + ex.response()?.errorBody().toString())

    }
}



//    val constraints = Constraints.Builder()
//        .setRequiresCharging(false)
//        .setRequiredNetworkType(NetworkType.CONNECTED)
//        .build()
//
//    val syncWorkRequest : WorkRequest =
//        OneTimeWorkRequestBuilder<UploadWorker>()
//            .setConstraints(constraints)
//            .build()
//
//    WorkManager
//    .getInstance(context)
//    .enqueue(syncWorkRequest)
//}


//when (e) {
//
//
//    is HttpException -> {
//        Log.d(TAG, "HttpException")
//        Log.d(TAG, e.code().toString())
//        try{
//            val failureMedicine = medicine.toLocalMedicine()
//            val room_id = cache.insertFailureMedicine(failureMedicine.toFailureMedicine())
//            failureMedicine.room_medicine_id = room_id
//            for (unit in failureMedicine.toFailureMedicineUnitEntity()) {
//                cache.insertFailureMedicineUnit(unit)
//            }
//        }catch (e: Exception){
//            Log.d(TAG, "Room Exception Enter Exception is Exception " + e.toString())
//            e.printStackTrace()
//        }
//        val constraints = Constraints.Builder().setRequiresCharging(false).setRequiredNetworkType(NetworkType.CONNECTED).build()
//
//        val syncWorkRequest : WorkRequest =
//            OneTimeWorkRequestBuilder<UploadWorker>()
//                .setConstraints(constraints)
//                .build()
//
//        WorkManager
//            .getInstance(context)
//            .enqueue(syncWorkRequest)
//    }
//    is ConnectException -> {
//        Log.d(TAG, "ConnectException")
//        try{
//            var failureMedicine = medicine.toLocalMedicine()
//            val room_id = cache.insertFailureMedicine(failureMedicine.toFailureMedicine())
//            failureMedicine = failureMedicine.copy(
//                room_medicine_id = room_id
//            )
//            for (unit in failureMedicine.toFailureMedicineUnitEntity()) {
//                cache.insertFailureMedicineUnit(unit)
//            }
//        }catch (e: Exception){
//            Log.d(TAG, "Room Exception Enter Exception is Exception " + e.toString())
//            e.printStackTrace()
//
//        }
//        val constraints = Constraints.Builder().setRequiresCharging(false).setRequiredNetworkType(NetworkType.CONNECTED).build()
//
//        val syncWorkRequest : WorkRequest =
//            OneTimeWorkRequestBuilder<UploadWorker>()
//                .setConstraints(constraints)
//                .build()
//
//        WorkManager
//            .getInstance(context)
//            .enqueue(syncWorkRequest)
//    }
//
//
//    is SocketTimeoutException -> {
//        Log.d(TAG, "TimeoutException")
//        try{
//            val failureMedicine = medicine.toLocalMedicine()
//            val room_id = cache.insertFailureMedicine(failureMedicine.toFailureMedicine())
//            failureMedicine.room_medicine_id = room_id
//            for (unit in failureMedicine.toFailureMedicineUnitEntity()) {
//                cache.insertFailureMedicineUnit(unit)
//            }
//        }catch (e: Exception){
//            Log.d(TAG, "Room Exception Enter Exception is Exception " + e.toString())
//            e.printStackTrace()
//
//        }
//        val constraints = Constraints.Builder().setRequiresCharging(false).setRequiredNetworkType(NetworkType.CONNECTED).build()
//
//        val syncWorkRequest : WorkRequest =
//            OneTimeWorkRequestBuilder<UploadWorker>()
//                .setConstraints(constraints)
//                .build()
//
//        WorkManager
//            .getInstance(context)
//            .enqueue(syncWorkRequest)
//    }
//}