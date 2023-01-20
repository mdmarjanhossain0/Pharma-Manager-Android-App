package com.appbytes.pharma_manager.business.datasource.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.appbytes.pharma_manager.business.datasource.cache.account.AccountDao
import com.appbytes.pharma_manager.business.datasource.cache.account.AccountEntity
import com.appbytes.pharma_manager.business.datasource.cache.account.EmployeeDao
import com.appbytes.pharma_manager.business.datasource.cache.account.EmployeeEntity
import com.appbytes.pharma_manager.business.datasource.cache.auth.AuthTokenDao
import com.appbytes.pharma_manager.business.datasource.cache.auth.AuthTokenEntity
import com.appbytes.pharma_manager.business.datasource.cache.cashregister.*
import com.appbytes.pharma_manager.business.datasource.cache.customer.CustomerDao
import com.appbytes.pharma_manager.business.datasource.cache.customer.CustomerEntity
import com.appbytes.pharma_manager.business.datasource.cache.customer.FailureCustomerEntity
import com.appbytes.pharma_manager.business.datasource.cache.inventory.global.GlobalMedicineDao
import com.appbytes.pharma_manager.business.datasource.cache.inventory.global.GlobalMedicineEntity
import com.appbytes.pharma_manager.business.datasource.cache.inventory.local.*
import com.appbytes.pharma_manager.business.datasource.cache.purchases.*
import com.appbytes.pharma_manager.business.datasource.cache.sales.*
import com.appbytes.pharma_manager.business.datasource.cache.shortlist.FailureShortListEntity
import com.appbytes.pharma_manager.business.datasource.cache.shortlist.ShortListDao
import com.appbytes.pharma_manager.business.datasource.cache.shortlist.ShortListEntity
import com.appbytes.pharma_manager.business.datasource.cache.supplier.FailureSupplierEntity
import com.appbytes.pharma_manager.business.datasource.cache.supplier.SupplierDao
import com.appbytes.pharma_manager.business.datasource.cache.supplier.SupplierEntity

@Database(entities = [
    AuthTokenEntity::class,
    AccountEntity::class,
    GlobalMedicineEntity::class,
    LocalMedicineEntity::class,
    LocalMedicineUnitsEntity::class,
    FailureMedicineEntity::class,
    FailureMedicineUnitEntity::class,
    SupplierEntity::class,
    FailureSupplierEntity::class,
    CustomerEntity::class,
    FailureCustomerEntity::class,
    SalesOrderEntity::class,
    SalesOrderMedicineEntity::class,
    FailureSalesOrderEntity::class,
    FailureSalesOrderMedicineEntity::class,
    PurchasesOrderEntity::class,
    PurchasesOrderMedicineEntity::class,
    FailurePurchasesOrderEntity::class,
    FailurePurchasesOrderMedicineEntity::class,
    ReceiveEntity::class,
    FailureReceiveEntity::class,
    PaymentEntity::class,
    FailurePaymentEntity::class,
    ShortListEntity::class,
    FailureShortListEntity::class,
    SalesDetailsMonthEntity::class,
    EmployeeEntity::class,
    CardEntity::class
], version = 47)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getAuthTokenDao(): AuthTokenDao

    abstract fun getAccountPropertiesDao(): AccountDao

    abstract fun getGlobalMedicineDao() : GlobalMedicineDao

    abstract fun getLocalMedicineDao() : LocalMedicineDao

    abstract fun getSupplierDao() : SupplierDao

    abstract fun getCustomerDao() : CustomerDao

    abstract fun getSalesDao() : SalesDao

    abstract fun getPurchasesDao() : PurchasesDao

    abstract fun getReceiveDao() : ReceiveDao

    abstract fun getPaymentDao() : PaymentDao

    abstract fun getShortListDao() : ShortListDao

    abstract fun getEmployeeDao() : EmployeeDao


    companion object{
        val DATABASE_NAME: String = "digital_pharmacy"
    }
}