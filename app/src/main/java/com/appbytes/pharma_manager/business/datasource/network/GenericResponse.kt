package com.appbytes.pharma_manager.business.datasource.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GenericResponse(

    @SerializedName("response")
    @Expose
    val response: String?,

    @SerializedName("error_message")
    val errorMessage: String?,
)