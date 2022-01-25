package com.devscore.digital_pharmacy.business.datasource.cache.auth

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AuthTokenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(authToken: AuthTokenEntity): Long

    @Query("DELETE FROM auth_token")
    suspend fun clearTokens()

    @Query("SELECT * FROM auth_token WHERE account_pk = :pk")
    suspend fun searchByPk(pk: Int): AuthTokenEntity?


    @Query("SELECT * FROM auth_token")
    suspend fun getAll(): List<AuthTokenEntity>


    @Query("DELETE FROM LocalMedicine")
    suspend fun deleteLocalMedicine()

    @Query("DELETE FROM FailureMedicine")
    suspend fun deleteLocalFailureMedicine()

    @Query("DELETE FROM AppClientCustomer")
    suspend fun deleteCustomer()

    @Query("DELETE FROM FAILUREAPPCLIENTCUSTOMER")
    suspend fun deleteCustomerF()

    @Query("DELETE FROM AppClientVendor")
    suspend fun deleteSupplier()

    @Query("DELETE FROM FailureAppClientVendor")
    suspend fun deleteSupplierF()

    @Query("DELETE FROM SalesOrder")
    suspend fun deleteSales()

    @Query("DELETE FROM FailureSalesOrder")
    suspend fun deleteSalesF()

    @Query("DELETE FROM PurchasesOrder")
    suspend fun deletePurchases()

    @Query("DELETE FROM FailurePurchasesOrder")
    suspend fun deletePurchasesF()

    @Query("DELETE FROM ShortList")
    suspend fun deleteShortList()

    @Query("DELETE FROM FailureShortList")
    suspend fun deleteShortListF()

    @Query("DELETE FROM AppClientEmployee")
    suspend fun deleteEmployee()

    @Query("DELETE FROM Receive")
    suspend fun deleteReceive()

    @Query("DELETE FROM FailureReceive")
    suspend fun deleteReceiveF()

    @Query("DELETE FROM Payment")
    suspend fun deletePayment()

    @Query("DELETE FROM FailurePayment")
    suspend fun deletePaymentF()

}


















