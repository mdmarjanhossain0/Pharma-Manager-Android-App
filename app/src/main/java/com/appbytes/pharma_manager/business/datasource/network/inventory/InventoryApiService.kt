package com.appbytes.pharma_manager.business.datasource.network.inventory

import android.util.Log
import com.appbytes.pharma_manager.business.datasource.network.GenericResponse
import com.appbytes.pharma_manager.business.datasource.network.inventory.network_responses.AddMedicineResponse
import com.appbytes.pharma_manager.business.datasource.network.inventory.network_responses.GlobalMedicineResponse
import com.appbytes.pharma_manager.business.datasource.network.inventory.network_responses.LocalMedicineResponse
import com.appbytes.pharma_manager.business.domain.models.AddMedicine
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface InventoryApiService {



    @GET("inventory/globalmedicine")
    suspend fun searchAllListGlobalMedicine(
        @Header("Authorization") authorization: String,
        @Query("search") query: String,
        @Query("ordering") ordering: String = "brand_name",
        @Query("page") page: Int
    ) : GlobalMedicineResponse

    @GET("inventory/globalmedicinebrandname")
    suspend fun searchByBrandNameGlobalMedicineList(
        @Header("Authorization") authorization: String,
        @Query("brand_name") query: String,
        @Query("ordering") ordering: String = "brand_name",
        @Query("page") page: Int
    ) : GlobalMedicineResponse

    @GET("inventory/globalmedicinegeneric")
    suspend fun searchByGenericNameGlobalMedicineList(
        @Header("Authorization") authorization: String,
        @Query("generic") query: String,
        @Query("ordering") ordering: String = "brand_name",
        @Query("page") page: Int
    ) : GlobalMedicineResponse


    @GET("inventory/globalmedicineindication")
    suspend fun searchByIndicationGlobalMedicineList(
        @Header("Authorization") authorization: String,
        @Query("search") query: String,
        @Query("ordering") ordering: String = "brand_name",
        @Query("page") page: Int
    ) : GlobalMedicineResponse


    @GET("inventory/globalmedicinemanufacturer")
    suspend fun searchByCompanyGlobalMedicineList(
        @Header("Authorization") authorization: String,
        @Query("manufacture") query: String,
        @Query("ordering") ordering: String = "brand_name",
        @Query("page") page: Int
    ) : GlobalMedicineResponse





    @GET("inventory/localmedicine")
    suspend fun searchLocalMedicineList(
        @Header("Authorization") authorization: String,
        @Query("search") query: String,
        @Query("ordering") ordering: String = "-created_at",
        @Query("page") page: Int
    ) : LocalMedicineResponse



    @GET("inventory/localmedicine")
    suspend fun searchLocalMedicine(
        @Header("Authorization") authorization: String,
        @Query("brand_name") brand_name: String,
        @Query("generic") generic: String,
        @Query("manufacture") manufacture: String,
        @Query("indication") indication: String,
        @Query("ordering") ordering: String = "-created_at",
        @Query("page") page: Int
    ) : LocalMedicineResponse


    @GET("inventory/localmedicinebrandname")
    suspend fun searchLocalMedicineListBrandName(
        @Header("Authorization") authorization: String,
        @Query("brand_name") query: String,
        @Query("ordering") ordering: String = "-created_at",
        @Query("page") page: Int
    ) : LocalMedicineResponse

    @GET("inventory/localmedicinegeneric")
    suspend fun searchLocalMedicineListGeneric(
        @Header("Authorization") authorization: String,
        @Query("generic") query: String,
        @Query("ordering") ordering: String = "-created_at",
        @Query("page") page: Int
    ) : LocalMedicineResponse

    @GET("inventory/localmedicineindication")
    suspend fun searchLocalMedicineListIndication(
        @Header("Authorization") authorization: String,
        @Query("indication") query: String,
        @Query("ordering") ordering: String = "-created_at",
        @Query("page") page: Int
    ) : LocalMedicineResponse

    @GET("inventory/localmedicinemenufacture")
    suspend fun searchLocalMedicineListManufacture(
        @Header("Authorization") authorization: String,
        @Query("manufacture") query: String,
        @Query("ordering") ordering: String = "-created_at",
        @Query("page") page: Int
    ) : LocalMedicineResponse


    @DELETE("inventory/{id}/medicine/delete")
    suspend fun deleteMedicine(
        @Header("Authorization") authorization: String,
        @Path("id")id : Int
    ) : GenericResponse


    @POST("inventory/addmedicine")
    suspend fun addMedicine(
        @Header("Authorization") authorization: String,
        @Body medicine : AddMedicine
    ) : AddMedicineResponse

    @Multipart
    @POST("inventory/addmedicines")
    suspend fun addMedicines (
        @Header("Authorization") authorization: String,
        @Part("brand_name") brand_name : RequestBody,
        @Part("sku") sku : RequestBody,
        @Part("dar_number") dar_number : RequestBody,
        @Part("mr_number") mr_number : RequestBody,
        @Part("generic") generic : RequestBody,
        @Part("indication") indication : RequestBody,
        @Part("symptom") symptom : RequestBody,
        @Part("strength") strength : RequestBody,
        @Part("description") description : RequestBody,
        @Part("mrp") mrp : RequestBody,
        @Part("purchase_price") purchase_price : RequestBody,
        @Part("discount") discount : RequestBody,
        @Part("is_percent_discount") is_percent_discount : RequestBody,
        @Part("manufacture") manufacture : RequestBody,
        @Part("kind") kind : RequestBody,
        @Part("form") form : RequestBody,
        @Part("remaining_quantity") remaining_quantity : RequestBody,
        @Part("damage_quantity") damage_quantity : RequestBody,
        @Part("exp_date") exp_date : RequestBody,
        @Part("rack_number") rack_number : RequestBody,
        @Part image: MultipartBody.Part?,
        @Part("units") units : RequestBody
    ) : AddMedicineResponse




    @Multipart
    @PUT("inventory/addmedicines/{id}")
    suspend fun updateMedicine (
        @Header("Authorization") authorization: String,
        @Path("id") id : Int,
        @Part("brand_name") brand_name : RequestBody,
        @Part("sku") sku : RequestBody,
        @Part("dar_number") dar_number : RequestBody,
        @Part("mr_number") mr_number : RequestBody,
        @Part("generic") generic : RequestBody,
        @Part("indication") indication : RequestBody,
        @Part("symptom") symptom : RequestBody,
        @Part("strength") strength : RequestBody,
        @Part("description") description : RequestBody,
        @Part("mrp") mrp : RequestBody,
        @Part("purchase_price") purchase_price : RequestBody,
        @Part("discount") discount : RequestBody,
        @Part("is_percent_discount") is_percent_discount : RequestBody,
        @Part("manufacture") manufacture : RequestBody,
        @Part("kind") kind : RequestBody,
        @Part("form") form : RequestBody,
        @Part("remaining_quantity") remaining_quantity : RequestBody,
        @Part("damage_quantity") damage_quantity : RequestBody,
        @Part("exp_date") exp_date : RequestBody,
        @Part("rack_number") rack_number : RequestBody,
        @Part image: MultipartBody.Part?,
        @Part("units") units : RequestBody
    ) : AddMedicineResponse
}





suspend fun InventoryApiService.searchGlobalMedicine(
    authorization: String,
    query: String,
    page: Int,
    action : String
) : GlobalMedicineResponse {
    when(action) {
        InventoryUtils.BRAND_NAME -> {
            Log.d("AppDebug", "Network Brand Name")
            return searchByBrandNameGlobalMedicineList(
                authorization = authorization,
                query = query,
                page = page
            )
        }

        InventoryUtils.GENERIC -> {
            Log.d("AppDebug", "Network Generic")
            return searchByGenericNameGlobalMedicineList(
                authorization = authorization,
                query = query,
                page = page
            )
        }

        InventoryUtils.INDICATION -> {
            Log.d("AppDebug", "Network Indication")
            return searchByIndicationGlobalMedicineList(
                authorization = authorization,
                query = query,
                page = page
            )
        }

        InventoryUtils.SYMPTOM -> {
            Log.d("AppDebug", "Network Symptom")
            return searchByCompanyGlobalMedicineList(
                authorization = authorization,
                query = query,
                page = page
            )
        }

        else -> {
            Log.d("AppDebug", "Network Global")
            return searchAllListGlobalMedicine(
                authorization = authorization,
                query = query,
                page = page
            )
        }
    }
}





suspend fun InventoryApiService.searchLocalMedicine(
    authorization: String,
    query: String,
    page: Int,
    action : String
) : LocalMedicineResponse {
    when(action) {
        InventoryUtils.BRAND_NAME -> {
            Log.d("AppDebug", "Network Brand Name")
            return searchLocalMedicineListBrandName(
                authorization = authorization,
                query = query,
                page = page
            )
        }

        InventoryUtils.GENERIC -> {
            Log.d("AppDebug", "Network Generic")
            return searchLocalMedicineListGeneric(
                authorization = authorization,
                query = query,
                page = page
            )
        }

        InventoryUtils.INDICATION -> {
            Log.d("AppDebug", "Network Indication")
            return searchLocalMedicineListIndication(
                authorization = authorization,
                query = query,
                page = page
            )
        }

        InventoryUtils.SYMPTOM -> {
            Log.d("AppDebug", "Network Symptom")
            return searchLocalMedicineListManufacture(
                authorization = authorization,
                query = query,
                page = page
            )
        }

        else -> {
            Log.d("AppDebug", "Network Global")
            return searchLocalMedicineList(
                authorization = authorization,
                query = query,
                page = page
            )
        }
    }
}