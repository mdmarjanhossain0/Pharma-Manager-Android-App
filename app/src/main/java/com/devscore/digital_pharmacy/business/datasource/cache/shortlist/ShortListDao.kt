package com.devscore.digital_pharmacy.business.datasource.cache.shortlist

import androidx.lifecycle.LiveData
import androidx.room.*
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.*
import com.devscore.digital_pharmacy.business.datasource.cache.supplier.FailureSupplierEntity
import com.devscore.digital_pharmacy.business.domain.util.Constants

@Dao
interface ShortListDao {

    // Insert

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShortList(medicine : ShortListEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFailureShortList(medicine : FailureShortListEntity): Long





    // Delete


    @Delete
    suspend fun deleteShortList(medicine : ShortListEntity)

    @Delete
    suspend fun deleteFailureShortList(medicine : FailureShortListEntity)






    @Query("DELETE FROM ShortList WHERE pk = :pk")
    suspend fun deleteShortList(pk: Int)

    @Query("DELETE FROM FailureShortList WHERE room_id = :room_id")
    suspend fun deleteFailureShortList(room_id : Long)



//    @Transaction
//    @Query(" SELECT * FROM LocalMedicine LIMIT (:page * :pageSize)")
//    suspend fun getAllLocalMedicineWithUnits(
//        page: Int,
//        pageSize: Int = Constants.PAGINATION_PAGE_SIZE
//    ): List<LocalMedicineWithUnits>
//
//
//
//
//
//    @Query(" SELECT * FROM LocalMedicine LIMIT (:page * :pageSize)")
//    fun getAllLocalMedicine(
//        page: Int,
//        pageSize: Int = Constants.PAGINATION_PAGE_SIZE
//    ): List<LocalMedicineEntity>
//
//
//
//
//
//    @Transaction
//    @Query(" SELECT * FROM LocalMedicine WHERE id = :id")
//    fun getRequestedData(
//        id : Long = -1
//    ): LiveData<List<LocalMedicineEntity>>
//
//    @Transaction
//    @Query(" SELECT * FROM FailureMedicine")
//    fun getSyncData(): List<FailureMedicineWithUnit>








    // Complex Query


    @Transaction
    @Query("""
        SELECT * FROM ShortList 
        WHERE manufacture LIKE '%' || :filter || '%'
        AND (brand_name LIKE '%' || :query || '%' 
        OR generic LIKE '%' || :query || '%')
        ORDER BY pk DESC LIMIT (:page * :pageSize)
        """)
    suspend fun searchShortList(
        query: String,
        filter : String,
        page: Int,
        pageSize: Int = Constants.PAGINATION_PAGE_SIZE
    ): List<ShortListEntity>



    @Transaction
    @Query("""
        SELECT * FROM FailureShortList 
        WHERE manufacture LIKE '%' || :filter || '%'
        AND (brand_name LIKE '%' || :query || '%' 
        OR generic LIKE '%' || :query || '%')
        ORDER BY room_id DESC
        """)
    suspend fun searchFailureShortList(
        query: String,
        filter : String
    ): List<FailureShortListEntity>


    @Transaction
    @Query("""
        SELECT * FROM FailureShortList
        """)
    suspend fun getFailureShortList(): List<FailureShortListEntity>





    @Query("SELECT * FROM  FailureShortList")
    fun getSyncDataLiveData(): LiveData<List<FailureShortListEntity>>
}