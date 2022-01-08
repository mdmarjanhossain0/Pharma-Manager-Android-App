package com.devscore.digital_pharmacy.business.datasource.cache.sales

import androidx.lifecycle.LiveData
import androidx.room.*
import com.devscore.digital_pharmacy.business.domain.util.Constants

@Dao
interface SalesDao {

    // Insert

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSalesOder(salesOder : SalesOrderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaleOderMedicine(salesOderMedicine : SalesOrderMedicineEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFailureSalesOder(failureSalesOder : FailureSalesOrderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFailureSalesOderMedicine(failureSalesOderMedicine : FailureSalesOrderMedicineEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSalesDetailMonth(details : SalesDetailsMonthEntity): Long





    // Delete


    @Delete
    suspend fun deleteSalesOder(salesOder : SalesOrderEntity)

    @Delete
    suspend fun deleteFailureSalesOder(failureSalesOder : FailureSalesOrderEntity)






    @Query("DELETE FROM SalesOrder WHERE pk = :pk")
    suspend fun deleteSalesOder(pk : Int)


    @Query("DELETE FROM FailureSalesOrder WHERE room_id = :room_id")
    suspend fun deleteFailureSalesOder(room_id : Long)









    // Complex Query





    @Transaction
    @Query("""
        SELECT * FROM SalesOrder 
        WHERE status = :status
        AND (customer = :pk )
        ORDER BY pk DESC LIMIT (:page * :pageSize)
        """)
    suspend fun searchCustomerOrders(
        pk : Int,
        status : Int = 3,
        page: Int,
        pageSize: Int = Constants.PAGINATION_PAGE_SIZE
    ): List<SalesOderWithMedicine>

    @Transaction
    @Query("""
        SELECT * FROM SalesOrder 
        WHERE status = 0
        ORDER BY pk DESC LIMIT (:page * :pageSize)
        """)
    suspend fun searchGenerateOrderWithMedicine(
        page: Int,
        pageSize: Int = Constants.PAGINATION_PAGE_SIZE
    ): List<SalesOderWithMedicine>

    @Transaction
    @Query("""
        SELECT * FROM SalesOrder 
        WHERE status = :status
        AND (mobile LIKE '%' || :query || '%' 
        OR pk LIKE '%' || :query || '%' )
        ORDER BY pk DESC LIMIT (:page * :pageSize)
        """)
    suspend fun searchCompleteOrderWithMedicine(
        query: String,
        status : Int = 3,
        page: Int,
        pageSize: Int = Constants.PAGINATION_PAGE_SIZE
    ): List<SalesOderWithMedicine>

    @Transaction
    @Query("""
        SELECT * FROM SalesOrder 
        
        """)
    suspend fun getAll(
    ): List<SalesOderWithMedicine>



    @Transaction
    @Query("""
        SELECT * FROM FailureSalesOrder 
        ORDER BY room_id DESC
        """)
    suspend fun searchFailureSalesOderWithMedicine(
    ): List<FailureSalesOrderWithMedicine>


    @Transaction
    @Query("SELECT * FROM SalesOrder WHERE pk = :pk")
    suspend fun getSalesOrder(
        pk : Int
    ): SalesOderWithMedicine




    @Query("""
        SELECT * FROM SalesDetailsMonth WHERE fake_id = :fake_id
        """)
    suspend fun getSalesDetails(fake_id : Int = 1): SalesDetailsMonthEntity





    @Query("SELECT * FROM FailureSalesOrder")
    fun getSyncData(): List<FailureSalesOrderWithMedicine>


    @Query("SELECT * FROM FailureSalesOrder")
    fun getSyncDataLiveData(): LiveData<List<FailureSalesOrderWithMedicine>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card : CardEntity): Long


    @Query("DELETE FROM SalesCard WHERE room_id = :room_id")
    suspend fun deleteCard(room_id : Long)

    @Transaction
    @Query("""
        SELECT * FROM SalesCard
        """)
    suspend fun getAllCard(
    ): List<CardEntity>

    @Transaction
    @Query("""
        SELECT * FROM SalesCard 
        
        """)
    fun getAllCardLiveData(
    ): LiveData<List<CardEntity>>
}