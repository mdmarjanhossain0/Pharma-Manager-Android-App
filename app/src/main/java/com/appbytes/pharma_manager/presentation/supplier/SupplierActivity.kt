package com.appbytes.pharma_manager.presentation.supplier

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import com.appbytes.pharma_manager.CashRecordActivity
import com.appbytes.pharma_manager.MainActivity
import com.appbytes.pharma_manager.R
import com.appbytes.pharma_manager.business.domain.util.StateMessageCallback
import com.appbytes.pharma_manager.presentation.BaseActivity
import com.appbytes.pharma_manager.presentation.auth.AuthActivity
import com.appbytes.pharma_manager.presentation.customer.CustomerActivity
import com.appbytes.pharma_manager.presentation.inventory.InventoryActivity
import com.appbytes.pharma_manager.presentation.purchases.PurchasesActivity
import com.appbytes.pharma_manager.presentation.sales.SalesActivity
import com.appbytes.pharma_manager.presentation.session.SessionEvents
import com.appbytes.pharma_manager.presentation.util.processQueue
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_supplier.*
import kotlinx.android.synthetic.main.layout_drawer_menu.*

@AndroidEntryPoint
class SupplierActivity : BaseActivity(), View.OnClickListener {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration


    private lateinit var navigationView: NavigationView
    var drawerLayout: DrawerLayout? = null


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_supplier)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.supplier_fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        setupActionBar()
        intClick()
        navigationView = NavigationView(this)
        onSetNavigationDrawerEvents()
        subscribeObservers()
    }

    private fun intClick() {
        backImage.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupActionBar() {
    }


    fun subscribeObservers() {
        sessionManager.state.observe(this) { state ->
            displayProgressBar(state.isLoading)
            processQueue(
                context = this,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        sessionManager.onTriggerEvent(SessionEvents.OnRemoveHeadFromQueue)
                    }
                })
            if (state.authToken == null || state.authToken.accountPk == -1) {
                navAuthActivity()
            }
        }
    }

    override fun expandAppBar() {
    }

    private fun navAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }











    private fun onSetNavigationDrawerEvents() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        val menuImg: ImageView = findViewById(R.id.menuImgId)
        val closeImg: ImageView = findViewById(R.id.productDetailsCloseIcon)
        val navDashboardTv: TextView = findViewById(R.id.navDashboardTvId)
        val navInventoryTv: TextView = findViewById(R.id.navInventoryTvId)
        val navSalesTv: TextView = findViewById(R.id.navSalesTvId)

        menuImg.setOnClickListener(this)
        closeImg.setOnClickListener(this)
        navDashboardTv.setOnClickListener(this)
        navInventoryTv.setOnClickListener(this)
        navSalesTv.setOnClickListener(this)
        navPurchaseTvId.setOnClickListener(this)
        navCustomersTvId.setOnClickListener(this)
        navShortListTvId.setOnClickListener(this)
        navCashReceiveTvId.setOnClickListener(this)
        navVendorsTvId.setOnClickListener(this)


    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.menuImgId -> {
                drawerLayout!!.openDrawer(navigationView, true)
            }
            R.id.productDetailsCloseIcon -> {
                drawerLayout!!.closeDrawer(navigationView, true)
            }
            R.id.navDashboardTvId -> {
                drawerLayout!!.closeDrawer(navigationView, true)
                this.startActivity(Intent(this, MainActivity::class.java))
            }

            R.id.navInventoryTvId -> {
                drawerLayout!!.closeDrawer(navigationView, true)
                Toast.makeText(this, "You are in Inventory", Toast.LENGTH_SHORT).show()
                this.startActivity(Intent(this, InventoryActivity::class.java))
            }

            R.id.navSalesTvId -> {
                drawerLayout!!.closeDrawer(navigationView, true)
                this.startActivity(Intent(this, SalesActivity::class.java))
            }

            R.id.navShortListTvId -> {
                drawerLayout!!.closeDrawer(navigationView, true)
                this.startActivity(Intent(this, SalesActivity::class.java))
            }

            R.id.navPurchaseTvId -> {
                drawerLayout!!.closeDrawer(navigationView, true)
                this.startActivity(Intent(this, PurchasesActivity::class.java))
            }

            R.id.navCashReceiveTvId -> {
                drawerLayout!!.closeDrawer(navigationView, true)
                this.startActivity(Intent(this, CashRecordActivity::class.java))
            }

            R.id.navVendorsTvId -> {
                drawerLayout!!.closeDrawer(navigationView, true)
                Toast.makeText(this, "You are in Suppliers", Toast.LENGTH_SHORT).show()
//                this.startActivity(Intent(this, SupplierActivity::class.java))
            }

            R.id.navCustomersTvId -> {
                drawerLayout!!.closeDrawer(navigationView, true)
                this.startActivity(Intent(this, CustomerActivity::class.java))
            }

            else -> {
                drawerLayout!!.closeDrawer(GravityCompat.START, true)
            }
        }
    }

    override fun displayProgressBar(isLoading: Boolean) {
        if (isLoading) {
            supplier_progress_bar.visibility = View.VISIBLE
        } else {
            supplier_progress_bar.visibility = View.GONE
        }
    }

}