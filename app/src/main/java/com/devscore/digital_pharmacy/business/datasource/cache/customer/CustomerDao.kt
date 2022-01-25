package com.devscore.digital_pharmacy.business.datasource.cache.customer

import androidx.lifecycle.LiveData
import androidx.room.*
import com.devscore.digital_pharmacy.business.datasource.cache.supplier.FailureSupplierEntity
import com.devscore.digital_pharmacy.business.datasource.cache.supplier.SupplierEntity
import com.devscore.digital_pharmacy.business.domain.util.Constants

@Dao
interface CustomerDao {



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customerEntity: CustomerEntity): Long


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFailureCustomer(failureCustomerEntity: FailureCustomerEntity): Long


    @Delete
    suspend fun deleteCustomer(customerEntity: CustomerEntity)

    @Query("DELETE FROM AppClientVendor WHERE pk = :pk")
    suspend fun deleteCustomer(pk : Int)


    @Delete
    suspend fun deleteFailureCustomer(failureCustomerEntity: FailureCustomerEntity)

    @Query("DELETE FROM FailureAppClientCustomer WHERE room_id = :room_id")
    suspend fun deleteFailureCustomer(room_id : Long)


    @Query("""
        SELECT * FROM AppClientCustomer 
        WHERE pk = :pk
        """)
    suspend fun getCustomer (
        pk : Int
    ): CustomerEntity


    @Query("""
        SELECT * FROM AppClientCustomer 
        WHERE name LIKE '%' || :query || '%' 
        OR email LIKE '%' || :query || '%' 
        OR mobile LIKE '%' || :query || '%' 
        OR whatsapp LIKE '%' || :query || '%' 
        OR facebook LIKE '%' || :query || '%' 
        OR imo LIKE '%' || :query || '%' 
        OR address LIKE '%' || :query || '%' 
        ORDER BY pk DESC LIMIT (:page * :pageSize)
        """)
    suspend fun searchAllCustomer (
        query: String,
        page: Int,
        pageSize: Int = Constants.PAGINATION_PAGE_SIZE
    ): List<CustomerEntity>

    @Query("""
        SELECT * FROM FailureAppClientCustomer 
        WHERE name LIKE '%' || :query || '%' 
        OR email LIKE '%' || :query || '%' 
        OR mobile LIKE '%' || :query || '%' 
        OR whatsapp LIKE '%' || :query || '%' 
        OR facebook LIKE '%' || :query || '%' 
        OR imo LIKE '%' || :query || '%' 
        OR address LIKE '%' || :query || '%'
        ORDER BY room_id DESC
        """)
    suspend fun searchAllFailureSupplier (
        query: String,
    ): List<FailureCustomerEntity>

    @Query("SELECT * FROM FailureAppClientCustomer")
    fun getSyncData(): List<FailureCustomerEntity>



    @Query("SELECT * FROM FailureAppClientCustomer")
    fun getSyncDataLiveData(): LiveData<List<FailureCustomerEntity>>











    @Query("DELETE FROM AppClientCustomer")
    suspend fun delete()

    @Query("DELETE FROM FailureAppClientCustomer")
    suspend fun deleteFailure()
}