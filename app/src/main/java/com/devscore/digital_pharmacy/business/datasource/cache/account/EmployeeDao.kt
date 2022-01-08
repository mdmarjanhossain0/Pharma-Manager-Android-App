package com.devscore.digital_pharmacy.business.datasource.cache.account

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EmployeeDao {

//    @Query("SELECT * FROM account_properties WHERE email = :email")
//    suspend fun searchByEmail(email: String): AccountEntity?
//
//    @Query("SELECT * FROM account_properties WHERE pk = :pk")
//    suspend fun searchByPk(pk: Int): AccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: EmployeeEntity): Long

//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun insertOrIgnore(account: AccountEntity): Long

//    @Query("UPDATE account_properties SET email = :email, username = :username WHERE pk = :pk")
//    suspend fun updateAccount(pk: Int, email: String, username: String)


    @Query("SELECT * FROM AppClientEmployee")
    suspend fun getEmployee() : List<EmployeeEntity>
}