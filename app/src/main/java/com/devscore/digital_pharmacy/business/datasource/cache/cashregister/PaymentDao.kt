package com.devscore.digital_pharmacy.business.datasource.cache.cashregister

import androidx.lifecycle.LiveData
import androidx.room.*
import com.devscore.digital_pharmacy.business.datasource.cache.supplier.FailureSupplierEntity
import com.devscore.digital_pharmacy.business.domain.util.Constants

@Dao
interface PaymentDao {

    // Insert

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment : PaymentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFailurePayment(payment : FailurePaymentEntity): Long




    // Delete


    @Delete
    suspend fun deletePayment(payment : PaymentEntity)

    @Delete
    suspend fun deleteFailurePayment(payment : FailurePaymentEntity)






    @Query("DELETE FROM Payment WHERE pk = :pk")
    suspend fun deletePayment(pk : Int)


    @Query("DELETE FROM FailurePayment WHERE room_id = :room_id")
    suspend fun deleteFailurePayment(room_id : Long)









    // Complex Query

    @Query("""
        SELECT * FROM Payment 
        WHERE customer LIKE '%' || :query || '%' 
        OR pk LIKE '%' || :query || '%'
        LIMIT (:page * :pageSize)
        """)
    suspend fun searchPayment (
        query: String,
        page: Int,
        pageSize: Int = Constants.PAGINATION_PAGE_SIZE
    ): List<PaymentEntity>



    @Query("""
        SELECT * FROM FailurePayment 
        WHERE customer LIKE '%' || :query || '%'
        LIMIT (:page * :pageSize)
        """)
    suspend fun searchFailurePayment (
        query: String,
        page: Int,
        pageSize: Int = Constants.PAGINATION_PAGE_SIZE
    ): List<FailurePaymentEntity>






    @Query("""
        SELECT * FROM FailurePayment
        """)
    suspend fun getFailurePayments (): List<FailurePaymentEntity>


    @Query("""
        SELECT * FROM Payment WHERE pk = :pk
        """)
    suspend fun getPayment (
        pk : Int
    ): PaymentEntity


    @Query("SELECT * FROM  FailurePayment")
    fun getSyncDataLiveData(): LiveData<List<FailurePaymentEntity>>
}