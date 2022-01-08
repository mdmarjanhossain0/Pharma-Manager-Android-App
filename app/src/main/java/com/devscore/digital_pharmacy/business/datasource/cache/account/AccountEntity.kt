package com.devscore.digital_pharmacy.business.datasource.cache.account

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.devscore.digital_pharmacy.business.domain.models.Account
import com.google.gson.annotations.SerializedName


@Entity(tableName = "account_properties")
data class AccountEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "pk")
    val pk: Int,

    @ColumnInfo(name = "email")
    val email: String,


    @ColumnInfo(name = "shop_name")
    val shop_name : String,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "mobile")
    val mobile : String,

    @ColumnInfo(name = "license_key")
    val license_key : String?,

    @ColumnInfo(name = "address")
    val address : String,

    @ColumnInfo(name = "profile_picture")
    val profile_picture : String?,

    @ColumnInfo(name = "is_employee")
    var is_employee : Int,

    @ColumnInfo(name = "role")
    var role : String
)

fun AccountEntity.toAccount(): Account {
    return Account(
        pk = pk,
        email = email,
        shop_name = shop_name,
        username = username,
        profile_picture = profile_picture,
        mobile = mobile,
        license_key = license_key,
        address = address,
        is_employee = is_employee,
        role = role
    )
}

fun Account.toEntity(): AccountEntity {
    return AccountEntity(
        pk = pk,
        email = email,
        shop_name = shop_name,
        username = username,
        profile_picture = profile_picture,
        mobile = mobile,
        license_key = license_key,
        address = address,
        is_employee = is_employee,
        role = role
    )
}