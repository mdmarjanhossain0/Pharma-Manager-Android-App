package com.devscore.digital_pharmacy.inventory

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.devscore.digital_pharmacy.MainActivity
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.presentation.inventory.InventoryFragment
import com.devscore.digital_pharmacy.presentation.sales.SalesFragment
import com.google.android.material.navigation.NavigationView

class ContainerActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var navigationView: NavigationView
    var drawerLayout: DrawerLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)

        navigationView = NavigationView(this)
        onSetNavigationDrawerEvents()

        val value = intent.getStringExtra("INVENTORY")
        val value2 = intent.getStringExtra("SALES")


        if (value == "inventory") {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerId, InventoryFragment()).commit()
        }
        if (value2 == "sales") {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerId, SalesFragment()).commit()
        }

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
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerId, InventoryFragment()).commit()
            }

            R.id.navSalesTvId -> {
                drawerLayout!!.closeDrawer(navigationView, true)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerId, SalesFragment()).commit()
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


}