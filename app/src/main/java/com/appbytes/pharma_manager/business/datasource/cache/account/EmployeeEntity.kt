package com.appbytes.pharma_manager.business.datasource.cache.account

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.appbytes.pharma_manager.business.domain.models.Employee

@Entity(tableName = "AppClientEmployee")
data class EmployeeEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "pk")
    val pk: Int,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "username")
    val username: String,

//    @ColumnInfo(name = "business_name")
//    val business_name : String,

    @ColumnInfo(name = "mobile")
    val mobile : String,

    @ColumnInfo(name = "license_key")
    val license_key : String,

    @ColumnInfo(name = "address")
    val address : String?,

    @ColumnInfo(name = "profile_picture")
    val profile_picture : String?,

    @ColumnInfo(name = "is_employee")
    var is_employee : Int,


    @ColumnInfo(name = "role")
    var role : String?,

    @ColumnInfo(name="is_active")
    var is_active : Boolean
)

fun EmployeeEntity.toEmployee(): Employee {
    return Employee(
        pk = pk,
        email = email,
        username = username,
        profile_picture = profile_picture,
        mobile = mobile,
        license_key = license_key,
        address = address,
        is_employee = is_employee,
        role = role,
        is_active = is_active
    )
}

fun Employee.toEmployeeEntity(): EmployeeEntity {
    return EmployeeEntity(
        pk = pk,
        email = email,
        username = username,
        profile_picture = profile_picture,
        mobile = mobile,
        license_key = license_key,
        address = address,
        is_employee = is_employee,
        role = role,
        is_active = is_active
    )
}