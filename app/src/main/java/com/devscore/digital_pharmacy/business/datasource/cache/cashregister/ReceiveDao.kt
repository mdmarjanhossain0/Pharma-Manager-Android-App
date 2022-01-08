package com.devscore.digital_pharmacy.business.datasource.cache.cashregister

import androidx.lifecycle.LiveData
import androidx.room.*
import com.devscore.digital_pharmacy.business.datasource.cache.sales.*
import com.devscore.digital_pharmacy.business.datasource.cache.supplier.FailureSupplierEntity
import com.devscore.digital_pharmacy.business.domain.util.Constants

@Dao
interface ReceiveDao {

    // Insert

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceive(receive : ReceiveEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFailureReceive(receive : FailureReceiveEntity): Long




    // Delete


    @Delete
    suspend fun deleteReceive(receive : ReceiveEntity)

    @Delete
    suspend fun deleteFailureReceive(receive : FailureReceiveEntity)






    @Query("DELETE FROM Receive WHERE pk = :pk")
    suspend fun deleteReceive(pk : Int)


    @Query("DELETE FROM FailureReceive WHERE room_id = :room_id")
    suspend fun deleteFailureReceive(room_id : Long)









    // Complex Query




    @Query("""
        SELECT * FROM FailureReceive
        """)
    suspend fun getFailureReceives (): List<FailureReceiveEntity>

    @Query("""
        SELECT * FROM Receive 
        WHERE customer LIKE '%' || :query || '%' 
        OR pk LIKE '%' || :query || '%'
        LIMIT (:page * :pageSize)
        """)
    suspend fun searchReceive (
        query: String,
        page: Int,
        pageSize: Int = Constants.PAGINATION_PAGE_SIZE
    ): List<ReceiveEntity>



    @Query("""
        SELECT * FROM FailureReceive 
        WHERE customer LIKE '%' || :query || '%'
        LIMIT (:page * :pageSize)
        """)
    suspend fun searchFailureReceive (
        query: String,
        page: Int,
        pageSize: Int = Constants.PAGINATION_PAGE_SIZE
    ): List<FailureReceiveEntity>


    @Query("""
        SELECT * FROM Receive WHERE pk = :pk
        """)
    suspend fun getReceive (
        pk : Int
    ): ReceiveEntity



    @Query("SELECT * FROM  FailureReceive")
    fun getSyncDataLiveData(): LiveData<List<FailureReceiveEntity>>
}