package com.appbytes.pharma_manager.business.datasource.cache.inventory.local

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*
import com.appbytes.pharma_manager.business.datasource.network.inventory.InventoryUtils
import com.appbytes.pharma_manager.business.domain.util.Constants
import com.appbytes.pharma_manager.business.domain.util.Constants.Companion.PAGINATION_PAGE_SIZE

@Dao
interface LocalMedicineDao {

    // Insert

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocalMedicine(localMedicine : LocalMedicineEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocalMedicineUnit(localMedicineUnit : LocalMedicineUnitsEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFailureMedicine(localMedicine : FailureMedicineEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFailureMedicineUnit(localMedicineUnit : FailureMedicineUnitEntity): Long





    // Delete


    @Delete
    suspend fun deleteLocalMedicine(localMedicine : LocalMedicineEntity)

    @Delete
    suspend fun deleteFailureMedicine(localMedicine : FailureMedicineEntity)






    @Query("DELETE FROM LocalMedicine WHERE id = :id")
    suspend fun deleteLocalMedicine(id: Int)

    @Query("DELETE FROM FailureMedicine WHERE room_medicine_id = :room_medicine_id")
    suspend fun deleteFailureLocalMedicine(room_medicine_id : Long)



    @Transaction
    @Query(" SELECT * FROM LocalMedicine LIMIT (:page * :pageSize)")
    suspend fun getAllLocalMedicineWithUnits(
        page: Int,
        pageSize: Int = Constants.PAGINATION_PAGE_SIZE
    ): List<LocalMedicineWithUnits>





    @Query(" SELECT * FROM LocalMedicine LIMIT (:page * :pageSize)")
    fun getAllLocalMedicine(
        page: Int,
        pageSize: Int = Constants.PAGINATION_PAGE_SIZE
    ): List<LocalMedicineEntity>





    @Transaction
    @Query(" SELECT * FROM LocalMedicine WHERE id = :id")
   fun getRequestedData(
        id : Int = -1
    ): LiveData<List<LocalMedicineEntity>>

    @Transaction
    @Query(" SELECT * FROM FailureMedicine")
    fun getSyncData(): List<FailureMedicineWithUnit>


    @Transaction
    @Query(" SELECT * FROM FailureMedicine")
    fun getSyncDataLiveData(): LiveData<List<FailureMedicineWithUnit>>




    @Transaction
    @Query(" SELECT * FROM LocalMedicine WHERE id = :id")
    suspend fun getMedicineById(
        id : Int = -1
    ): LocalMedicineWithUnits?


    @Transaction
    @Query(" SELECT * FROM FailureMedicine WHERE room_medicine_id = :room_id")
    suspend fun getFailureMedicineById(room_id : Long = -1): FailureMedicineWithUnit








    // Complex Query

    @Transaction
    @Query("""
        SELECT * FROM LocalMedicine WHERE id = :id
        """)
    suspend fun getLocalMedicine(
        id: Int
    ): LocalMedicineWithUnits?


    @Transaction
    @Query("""
        SELECT * FROM LocalMedicine 
        WHERE brand_name LIKE '%' || :query || '%' 
        OR generic LIKE '%' || :query || '%' 
        OR manufacture LIKE '%' || :query || '%' 
        ORDER BY id DESC LIMIT (:page * :pageSize)
        """)
    suspend fun searchLocalMedicineWithUnitWithQuery(
        query: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<LocalMedicineWithUnits>


    @Transaction
    @Query("""
        SELECT * FROM LocalMedicine 
        WHERE brand_name LIKE '%' || :brand_name || '%' 
        OR generic LIKE '%' || :generic || '%' 
        OR manufacture LIKE '%' || :manufacture || '%' 
        OR indication LIKE '%' || :indication || '%' 
        ORDER BY id DESC LIMIT (:page * :pageSize)
        """)
    suspend fun searchLocalMedicine(
        brand_name: String,
        generic: String,
        manufacture: String,
        indication: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<LocalMedicineWithUnits>


    @Transaction
    @Query("""
        SELECT * FROM LocalMedicine 
        WHERE brand_name LIKE :query || '%'
        ORDER BY id DESC LIMIT (:page * :pageSize)
        """)
    suspend fun searchLocalMedicineWithUnitBrandName(
        query: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<LocalMedicineWithUnits>

    @Transaction
    @Query("""
        SELECT * FROM LocalMedicine 
        WHERE generic LIKE :query || '%'
        ORDER BY id DESC LIMIT (:page * :pageSize)
        """)
    suspend fun searchLocalMedicineWithUnitGeneric(
        query: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<LocalMedicineWithUnits>

    @Transaction
    @Query("""
        SELECT * FROM LocalMedicine 
        WHERE indication LIKE :query || '%'
        ORDER BY id DESC LIMIT (:page * :pageSize)
        """)
    suspend fun searchLocalMedicineWithUnitIndication(
        query: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<LocalMedicineWithUnits>

    @Transaction
    @Query("""
        SELECT * FROM LocalMedicine 
        WHERE manufacture LIKE :query || '%' 
        ORDER BY id DESC LIMIT (:page * :pageSize)
        """)
    suspend fun searchLocalMedicineWithUnitManufacture(
        query: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<LocalMedicineWithUnits>



    @Transaction
    @Query("""
        SELECT * FROM FailureMedicine 
        WHERE brand_name LIKE '%' || :query || '%' 
        OR generic LIKE '%' || :query || '%' 
        OR manufacture LIKE '%' || :query || '%'
        """)
    suspend fun searchFailureMedicineWithUnits(
        query: String
    ): List<FailureMedicineWithUnit>







    @Query("DELETE FROM LocalMedicine")
    suspend fun deleteLocalMedicine()

    @Query("DELETE FROM FailureMedicine")
    suspend fun deleteLocalFailureMedicine()
}





suspend fun LocalMedicineDao.searchLocalMedicine(
    query: String,
    page: Int,
    ordering: String = "id",
    action : String
) : List<LocalMedicineWithUnits> {
    when(action) {
        InventoryUtils.BRAND_NAME -> {
            Log.d("AppDebug", "Cache Brand Name")
            return searchLocalMedicineWithUnitBrandName(
                query = query,
                page = page,
//                ordering = ordering
            )
        }

        InventoryUtils.GENERIC -> {
            Log.d("AppDebug", "Cache Generic")
            return searchLocalMedicineWithUnitGeneric(
                query = query,
                page = page,
//                ordering = ordering
            )
        }

        InventoryUtils.INDICATION -> {
            Log.d("AppDebug", "Cache Indication")
            return searchLocalMedicineWithUnitIndication(
                query = query,
                page = page,
//                ordering = ordering
            )
        }

        InventoryUtils.SYMPTOM -> {
            Log.d("AppDebug", "Cache Symptom")
            return searchLocalMedicineWithUnitManufacture(
                query = query,
                page = page,
//                ordering = ordering
            )
        }

        else -> {
            Log.d("AppDebug", "Cache Global")
            return searchLocalMedicineWithUnitWithQuery(
                query = query,
                page = page,
//                ordering = ordering
            )
        }
    }
}