package com.devscore.digital_pharmacy.business.datasource.cache.inventory.global

import android.util.Log
import androidx.room.*
import com.devscore.digital_pharmacy.business.datasource.network.inventory.InventoryApiService
import com.devscore.digital_pharmacy.business.datasource.network.inventory.InventoryUtils
import com.devscore.digital_pharmacy.business.datasource.network.inventory.network_responses.GlobalMedicineResponse
import com.devscore.digital_pharmacy.business.domain.util.Constants.Companion.PAGINATION_PAGE_SIZE

@Dao
interface GlobalMedicineDao {



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(blogPost: GlobalMedicineEntity): Long

    @Delete
    suspend fun deleteBlogPost(blogPost: GlobalMedicineEntity)

    @Query("DELETE FROM GlobalMedicine WHERE id = :id")
    suspend fun deleteBlogPost(id: Int)

    @Query(" SELECT * FROM GlobalMedicine LIMIT (:page * :pageSize)")
    suspend fun getAllGlobalMedicine(
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<GlobalMedicineEntity>


    @Query("SELECT * FROM GlobalMedicine WHERE id = :id ")
    suspend fun getGlobalMedicine(id: Int): GlobalMedicineEntity?


    @Query("""
        SELECT * FROM GlobalMedicine 
        WHERE brand_name LIKE '%' || :query || '%' 
        OR generic LIKE '%' || :query || '%' 
        OR manufacture LIKE '%' || :query || '%' 
        ORDER BY brand_name ASC LIMIT (:page * :pageSize)
        """)
    suspend fun searchGlobalMedicineWithQuery(
        query: String,
        page: Int,
//        ordering: String = "id",
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<GlobalMedicineEntity>

    @Query("""
        SELECT * FROM GlobalMedicine 
        WHERE brand_name LIKE :query || '%'
        ORDER BY brand_name ASC LIMIT (:page * :pageSize)
        """)
    suspend fun searchGlobalMedicineWithBrandName(
        query: String,
        page: Int,
//        ordering: String,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<GlobalMedicineEntity>

    @Query("""
        SELECT * FROM GlobalMedicine 
        WHERE generic LIKE :query || '%'
        ORDER BY brand_name ASC LIMIT (:page * :pageSize)
        """)
    suspend fun searchGlobalMedicineWithGeneric(
        query: String,
        page: Int,
//        ordering: String,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<GlobalMedicineEntity>

    @Query("""
        SELECT * FROM GlobalMedicine 
        WHERE indication LIKE :query || '%'
        ORDER BY brand_name ASC LIMIT (:page * :pageSize)
        """)
    suspend fun searchGlobalMedicineWithIndication(
        query: String,
        page: Int,
//        ordering: String,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<GlobalMedicineEntity>

    @Query("""
        SELECT * FROM GlobalMedicine 
        WHERE manufacture LIKE :query || '%'
        ORDER BY brand_name ASC LIMIT (:page * :pageSize)
        """)
    suspend fun searchGlobalMedicineWithSymptom(
        query: String,
        page: Int,
//        ordering: String,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<GlobalMedicineEntity>
}



suspend fun GlobalMedicineDao.searchCacheGlobalMedicine(
    query: String,
    page: Int,
    ordering: String = "id",
    action : String
) : List<GlobalMedicineEntity> {
    when(action) {
        InventoryUtils.BRAND_NAME -> {
            Log.d("AppDebug", "Cache Brand Name")
            return searchGlobalMedicineWithBrandName(
                query = query,
                page = page,
//                ordering = ordering
            )
        }

        InventoryUtils.GENERIC -> {
            Log.d("AppDebug", "Cache Generic")
            return searchGlobalMedicineWithGeneric(
                query = query,
                page = page,
//                ordering = ordering
            )
        }

        InventoryUtils.INDICATION -> {
            Log.d("AppDebug", "Cache Indication")
            return searchGlobalMedicineWithIndication(
                query = query,
                page = page,
//                ordering = ordering
            )
        }

        InventoryUtils.SYMPTOM -> {
            Log.d("AppDebug", "Cache Symptom")
            return searchGlobalMedicineWithSymptom(
                query = query,
                page = page,
//                ordering = ordering
            )
        }

        else -> {
            Log.d("AppDebug", "Cache Global")
            return searchGlobalMedicineWithQuery(
                query = query,
                page = page,
//                ordering = ordering
            )
        }
    }
}