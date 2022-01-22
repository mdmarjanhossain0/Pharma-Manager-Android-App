package com.devscore.digital_pharmacy.presentation.sales

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import com.devscore.digital_pharmacy.CashRecordActivity
import com.devscore.digital_pharmacy.MainActivity
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.business.domain.util.StateMessageCallback
import com.devscore.digital_pharmacy.presentation.BaseActivity
import com.devscore.digital_pharmacy.presentation.auth.AuthActivity
import com.devscore.digital_pharmacy.presentation.customer.CustomerActivity
import com.devscore.digital_pharmacy.presentation.inventory.InventoryActivity
import com.devscore.digital_pharmacy.presentation.inventory.InventoryFragment
import com.devscore.digital_pharmacy.presentation.purchases.PurchasesActivity
import com.devscore.digital_pharmacy.presentation.session.SessionEvents
import com.devscore.digital_pharmacy.presentation.supplier.SupplierActivity
import com.devscore.digital_pharmacy.presentation.util.processQueue
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_sales.*
import kotlinx.android.synthetic.main.layout_drawer_menu.*

@AndroidEntryPoint
class SalesActivity : BaseActivity(), View.OnClickListener {

    private lateinit var navigationView: NavigationView
    var drawerLayout: DrawerLayout? = null


    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    override fun displayProgressBar(isLoading: Boolean) {
        if(isLoading){
            sales_progress_bar.visibility = View.VISIBLE
        }
        else{
            sales_progress_bar.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales)

        setUpUI()
        initUIClick()

        navigationView = NavigationView(this)
        onSetNavigationDrawerEvents()
        subscribeObservers()

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

    private fun navAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun initUIClick() {
        backImage.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setUpUI() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.sales_fragment_container) as NavHostFragment
        navController = navHostFragment.navController
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
                this.startActivity(Intent(this, InventoryActivity::class.java))
            }

            R.id.navSalesTvId -> {
                drawerLayout!!.closeDrawer(navigationView, true)
                Toast.makeText(this, "You are in Sales", Toast.LENGTH_SHORT).show()
//                this.startActivity(Intent(this, SalesActivity::class.java))
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
                this.startActivity(Intent(this, SupplierActivity::class.java))
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

    override fun onBackPressed() {
        if (drawerLayout!!.isDrawerOpen(navigationView)) {
            drawerLayout!!.closeDrawer(navigationView, true)
        } else {
            super.onBackPressed()
        }
    }

    override fun expandAppBar() {
    }


    fun navigateSalesOrderToInventoryFragment() {
        navController.navigate(R.id.action_salesFragment_to_salesInventoryFragment)
    }

    fun navigateSalesCardFragmentToSalesPaymentFragment() {
        navController.navigate(R.id.action_salesCartFragment_to_salesPayNowFragment)
    }

    fun navigateSalesInventoryToCardFragment() {
        navController.navigate(R.id.action_salesInventoryFragment_to_salesCartFragment)
    }

    fun navigateSalesToSalesReturn(pk : Int) {
        val bundle = bundleOf("pk" to pk)
//        navController.navigate(R.id.action_salesFragment_to_salesReturnFragment)
        navController.navigate(R.id.action_salesDetailsFragment_to_salesReturnFragment, bundle)
    }

    fun navigateDetailsFragment(pk : Int) {
        val bundle = bundleOf("pk" to pk)
        navController.navigate(R.id.action_salesFragment_to_salesDetailsFragment, bundle)
    }
}