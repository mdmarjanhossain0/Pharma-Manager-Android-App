package com.appbytes.pharma_manager.presentation.main.dashboard

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.*
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.appbytes.pharma_manager.R
import com.appbytes.pharma_manager.presentation.cashregister.CashRegisterActivity
import com.appbytes.pharma_manager.presentation.customer.CustomerActivity
import com.appbytes.pharma_manager.presentation.inventory.InventoryActivity
import com.appbytes.pharma_manager.presentation.main.BaseMainFragment
import com.appbytes.pharma_manager.presentation.main.notification.NotificationViewModel
import com.appbytes.pharma_manager.presentation.purchases.PurchasesActivity
import com.appbytes.pharma_manager.presentation.sales.SalesActivity
import com.appbytes.pharma_manager.presentation.shortlist.ShortListActivity
import com.appbytes.pharma_manager.presentation.supplier.SupplierActivity
import kotlinx.android.synthetic.main.dynamic_notification.*
import kotlinx.android.synthetic.main.dynamic_sync.*
import kotlinx.android.synthetic.main.fragment_dashboard.*


import android.util.DisplayMetrics
import android.util.Log
import android.view.Window
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import com.appbytes.pharma_manager.business.domain.util.StateMessageCallback
import com.appbytes.pharma_manager.business.domain.util.SuccessHandling.Companion.SYNC_FINISH
import com.appbytes.pharma_manager.presentation.util.DateTime
import com.appbytes.pharma_manager.presentation.util.processQueue
import com.google.android.material.card.MaterialCardView
import java.util.*
import java.util.concurrent.atomic.AtomicInteger


class DashboardFragment : BaseMainFragment() {


    private val viewModel : DashBoardViewModel by viewModels()
    private val shareViewModel : NotificationViewModel by activityViewModels()


    var local : Int = 0
    var sales : Int = 0
    var purchases : Int = 0
    var customer : Int = 0
    var supplier : Int = 0
    var shortList : Int = 0
    var receive : Int = 0
    var payment : Int = 0
    var toast : Boolean = false

    lateinit var topLayout: RelativeLayout
    lateinit var reportCard: MaterialCardView
    lateinit var linearLayout1: LinearLayout
    lateinit var linearLayout2:LinearLayout
    lateinit var linearLayout3:LinearLayout
    lateinit var customerView: ImageView
    lateinit var supplierView:ImageView
    lateinit var cashRegister:ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view: View =  inflater.inflate(R.layout.fragment_dashboard, container, false)
        val inventoryImg: ImageView = view.findViewById(R.id.inventoryImgId)
        val salesImg: ImageView = view.findViewById(R.id.salesImgId)





        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUIClick()
        subscribeObserver()
        setLayoutSize()
    }

    private fun subscribeObserver() {
        val calender = Calendar.getInstance()
        val month = calender.get(Calendar.MONTH)
        titleTvId.setText("Sales In " + DateTime.getMonth(month + 1))
        viewModel.state.observe(viewLifecycleOwner, Observer { state ->
            uiCommunicationListener.displayProgressBar(state.isLoading)

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(DashBoardEvents.OnRemoveHeadFromQueue)
                    }
                })



            amountValueTvId.setText("৳ " + state.details.total)
            dueValueTvId.setText("৳ " + state.details.due)
            itemCount.setText("Quantity : " + state.details.sales)





//            val sync = state.local + state.sales + state.purchases + state.customer + state.supplier
//
//            if (sync > 0) {
//                dynamic_sync_card.visibility = View.VISIBLE
//                dynamicSync.setText(sync.toString())
//            }
//            else {
//                dynamic_sync_card.visibility = View.INVISIBLE
//            }
        })





        viewModel.localMedicineLiveData.observe(viewLifecycleOwner, Observer { state ->
            viewModel.onTriggerEvent(DashBoardEvents.Local(state.size))
            Log.d(TAG, "Local " + state.size)
            local = state.size
            val sync = local + sales + purchases + customer + supplier + shortList + receive + payment
            if (sync > 0) {
                dynamic_sync_card.visibility = View.VISIBLE
                dynamicSync.setText(sync.toString())
            }
            else {
                dynamic_sync_card.visibility = View.INVISIBLE
                if (toast) {
                    Toast.makeText(context, SYNC_FINISH, Toast.LENGTH_SHORT).show()
                    toast = false
                }
            }
        })

        viewModel.salesLiveDataLiveData.observe(viewLifecycleOwner, Observer { state ->
            viewModel.onTriggerEvent(DashBoardEvents.Sales(state.size))
            Log.d(TAG, "Sales " + state.size)
            sales = state.size
            val sync = local + sales + purchases + customer + supplier + shortList + receive + payment
            if (sync > 0) {
                dynamic_sync_card.visibility = View.VISIBLE
                dynamicSync.setText(sync.toString())
            }
            else {
                dynamic_sync_card.visibility = View.INVISIBLE
                if (toast) {
                    Toast.makeText(context, SYNC_FINISH, Toast.LENGTH_SHORT).show()
                    toast = false
                }
            }
        })


        viewModel.purchasesLiveData.observe(viewLifecycleOwner, Observer { state ->
            viewModel.onTriggerEvent(DashBoardEvents.Purchases(state.size))
            Log.d(TAG, "Purchases " + state.size)
            purchases = state.size
            val sync = local + sales + purchases + customer + supplier + shortList + receive + payment
            if (sync > 0) {
                dynamic_sync_card.visibility = View.VISIBLE
                dynamicSync.setText(sync.toString())
            }
            else {
                dynamic_sync_card.visibility = View.INVISIBLE
                if (toast) {
                    Toast.makeText(context, SYNC_FINISH, Toast.LENGTH_SHORT).show()
                    toast = false
                }
            }
        })


        viewModel.customerLiveData.observe(viewLifecycleOwner, Observer { state ->
            viewModel.onTriggerEvent(DashBoardEvents.Customer(state.size))
            Log.d(TAG, "Customer " + state.size)
            customer = state.size
            val sync = local + sales + purchases + customer + supplier + shortList + receive + payment
            if (sync > 0) {
                dynamic_sync_card.visibility = View.VISIBLE
                dynamicSync.setText(sync.toString())
            }
            else {
                dynamic_sync_card.visibility = View.INVISIBLE
                if (toast) {
                    Toast.makeText(context, SYNC_FINISH, Toast.LENGTH_SHORT).show()
                    toast = false
                }
            }
        })

        viewModel.supplierLiveData.observe(viewLifecycleOwner, Observer { state ->
            viewModel.onTriggerEvent(DashBoardEvents.Supplier(state.size))
            Log.d(TAG, "Supplier " + state.size)
            supplier = state.size
            val sync = local + sales + purchases + customer + supplier + shortList + receive + payment
//            viewModel.state.value = viewModel.state.value?.copy(
//                supplier = state.size
//            )
//            val sync = state.size + viewModel.state.value?.sales!! + viewModel.state.value?.purchases!! + viewModel.state.value?.customer!! + viewModel.state.value?.supplier!!
//            val sync = state.size + viewModel.salesLiveData.value?.size!! + viewModel.purchasesLiveData.value?.size!! + viewModel.customerLiveData.value?.size!! + viewModel.localMedicine.value?.size!!
            if (sync > 0) {
                dynamic_sync_card.visibility = View.VISIBLE
                dynamicSync.setText(sync.toString())
            }
            else {
                dynamic_sync_card.visibility = View.INVISIBLE
                if (toast) {
                    Toast.makeText(context, SYNC_FINISH, Toast.LENGTH_SHORT).show()
                    toast = false
                }
            }
        })




        viewModel.shortListLiveData.observe(viewLifecycleOwner, Observer { state ->
//            viewModel.onTriggerEvent(DashBoardEvents.Local(state.size))
            Log.d(TAG, "shortList " + state.size)
            shortList = state.size
            val sync = local + sales + purchases + customer + supplier + shortList + receive + payment
            if (sync > 0) {
                dynamic_sync_card.visibility = View.VISIBLE
                dynamicSync.setText(sync.toString())
            }
            else {
                dynamic_sync_card.visibility = View.INVISIBLE
                if (toast) {
                    Toast.makeText(context, SYNC_FINISH, Toast.LENGTH_SHORT).show()
                    toast = false
                }
            }
        })



        viewModel.receiveLiveData.observe(viewLifecycleOwner, Observer { state ->
//            viewModel.onTriggerEvent(DashBoardEvents.Local(state.size))
            Log.d(TAG, "Receive " + state.size)
            receive = state.size
            val sync = local + sales + purchases + customer + supplier + shortList + receive + payment
            if (sync > 0) {
                dynamic_sync_card.visibility = View.VISIBLE
                dynamicSync.setText(sync.toString())
            }
            else {
                dynamic_sync_card.visibility = View.INVISIBLE
                if (toast) {
                    Toast.makeText(context, SYNC_FINISH, Toast.LENGTH_SHORT).show()
                    toast = false
                }
            }
        })



        viewModel.paymentLiveData.observe(viewLifecycleOwner, Observer { state ->
//            viewModel.onTriggerEvent(DashBoardEvents.Local(state.size))
            Log.d(TAG, "Payment " + state.size)
            payment = state.size
            val sync = local + sales + purchases + customer + supplier + shortList + receive + payment
            if (sync > 0) {
                dynamic_sync_card.visibility = View.VISIBLE
                dynamicSync.setText(sync.toString())
            }
            else {
                dynamic_sync_card.visibility = View.INVISIBLE
                if (toast) {
                    Toast.makeText(context, SYNC_FINISH, Toast.LENGTH_SHORT).show()
                    toast = false
                }
            }
        })

        shareViewModel.state.observe(viewLifecycleOwner, Observer { state ->
            if (state.notificationList.size > 0) {
                dynamic_notification_card.visibility = View.VISIBLE
                dynamicNotification.setText(state.notificationList.size.toString())
            }
        })
    }

    private fun initUIClick() {

        inventoryImgId.setOnClickListener(){
            val intent = Intent(context, InventoryActivity::class.java)

            intent.putExtra("INVENTORY","inventory")

            startActivity(intent)
        }

        supplier_id.setOnClickListener {
            val intent = Intent(context, SupplierActivity::class.java)
            startActivity(intent)
        }

        customer_id.setOnClickListener {
            val intent = Intent(context, CustomerActivity::class.java)
            startActivity(intent)
        }


        salesImgId.setOnClickListener {
            val intent = Intent(context, SalesActivity::class.java)
            startActivity(intent)
        }


        purchaseImgId.setOnClickListener {
            val intent = Intent(context, PurchasesActivity::class.java)
            startActivity(intent)
        }

        dashBoardCashRegister.setOnClickListener {
            val intent = Intent(context, CashRegisterActivity::class.java)
            startActivity(intent)
        }

        shortlistImgId.setOnClickListener {
            val intent = Intent(context, ShortListActivity::class.java)
            startActivity(intent)
        }


        sync.setOnClickListener {
            toast = true
            viewModel.onTriggerEvent(DashBoardEvents.Sync)
        }



        notification.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_notificationsFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvent(DashBoardEvents.GetMonthSalesTotalReport)
    }


    fun setLayoutSize() {
        topLayout = view?.findViewById(R.id.topLayoutId) as RelativeLayout
        reportCard = view?.findViewById(R.id.cardId) as MaterialCardView
        linearLayout1 = view?.findViewById(R.id.linear1) as LinearLayout
        linearLayout2 = view?.findViewById(R.id.linear2) as LinearLayout
        linearLayout3 = view?.findViewById(R.id.linear3) as LinearLayout
        customerView = view?.findViewById(R.id.customer_img) as ImageView
        supplierView = view?.findViewById(R.id.supplier_img) as ImageView
        cashRegister = view?.findViewById(R.id.cash_register_img) as ImageView
// Get the layout id
        val layoutHeight = AtomicInteger()
        topLayout!!.post(Runnable {
            val rect = Rect()
            val win: Window = activity?.getWindow()!! // Get the Window
            win.getDecorView().getWindowVisibleDisplayFrame(rect)

            // Get the height of Status Bar
            val statusBarHeight: Int = rect.top

            // Get the height occupied by the decoration contents
            val contentViewTop: Int = win.findViewById<View>(Window.ID_ANDROID_CONTENT).getTop()

            // Calculate titleBarHeight by deducting statusBarHeight from contentViewTop
            val titleBarHeight = contentViewTop - statusBarHeight

            // By now we got the height of titleBar & statusBar
            // Now lets get the screen size
            val metrics = DisplayMetrics()
            activity?.getWindowManager()?.getDefaultDisplay()?.getMetrics(metrics)
            val screenHeight = metrics.heightPixels
            val screenWidth = metrics.widthPixels

            // Now calculate the height that our layout can be set
            // If you know that your application doesn't have statusBar added, then don't add here also. Same applies to application bar also
            layoutHeight.set(screenHeight - (titleBarHeight + statusBarHeight))

            // Lastly, set the height of the layout
            val rootParams = topLayout.getLayoutParams() as LinearLayout.LayoutParams
            rootParams.height = Math.round(layoutHeight.get() * 0.15).toInt()
            topLayout.setLayoutParams(rootParams)

            //set the top margin of the report Card
            val cardViewParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            reportCard.setLayoutParams(cardViewParams)
            val cardViewMarginParams = reportCard.getLayoutParams() as ViewGroup.MarginLayoutParams
            cardViewMarginParams.setMargins(
                Math.round(screenWidth * 0.05).toInt(),
                (-Math.round(layoutHeight.get() * 0.055)).toInt(),
                Math.round(screenWidth * 0.05).toInt(), 0
            )
            reportCard.requestLayout()

            //set the margins of linearLayout1
            val linear1params = linearLayout1.getLayoutParams() as LinearLayout.LayoutParams
            linear1params.setMargins(
                Math.round(screenWidth * 0.05).toInt(),
                Math.round(layoutHeight.get() * 0.05).toInt(),
                Math.round(screenWidth * 0.05).toInt(), 0
            )
            linearLayout1.setLayoutParams(linear1params)

            //set the margins of linearLayout2
            val linear2params = linearLayout2.getLayoutParams() as LinearLayout.LayoutParams
            linear2params.setMargins(
                Math.round(screenWidth * 0.05).toInt(),
                Math.round(layoutHeight.get() * 0.05).toInt(),
                Math.round(screenWidth * 0.05).toInt(), 0
            )
            linearLayout2.setLayoutParams(linear2params)

            //set the height of linearLayout3
            val linear3params = linearLayout3.getLayoutParams() as LinearLayout.LayoutParams
            linear3params.height = Math.round(layoutHeight.get() * 0.18).toInt()
            linear3params.setMargins(
                0,
                Math.round(layoutHeight.get() * 0.08).toInt(), 0,
                Math.round(layoutHeight.get() * 0.05).toInt()
            )
            linearLayout3.setLayoutParams(linear3params)

            //set the width and height of 3 images of linearLayout3
            val customerparams = customerView.getLayoutParams() as LinearLayout.LayoutParams
            customerparams.height = Math.round(layoutHeight.get() * 0.05).toInt()
            customerparams.width = Math.round(screenWidth * 0.15).toInt()
            customerView.setLayoutParams(customerparams)
            customerView.requestLayout()
            val supplierparams = supplierView.getLayoutParams() as LinearLayout.LayoutParams
            supplierparams.height = Math.round(layoutHeight.get() * 0.05).toInt()
            supplierparams.width = Math.round(screenWidth * 0.15).toInt()
            supplierView.setLayoutParams(supplierparams)
            supplierView.requestLayout()
            val cashRegisterparams = cashRegister.getLayoutParams() as LinearLayout.LayoutParams
            cashRegisterparams.height = Math.round(layoutHeight.get() * 0.05).toInt()
            cashRegisterparams.width = Math.round(screenWidth * 0.15).toInt()
            cashRegister.setLayoutParams(cashRegisterparams)
            cashRegister.requestLayout()
        })
    }

    fun getScreenHeight(): Int {
        val layoutHeight = AtomicInteger()
        val rect = Rect()
        val win: Window = activity?.getWindow()!! // Get the Window
        win.getDecorView().getWindowVisibleDisplayFrame(rect)

        // Get the height of Status Bar
        val statusBarHeight: Int = rect.top

        // Get the height occupied by the decoration contents
        val contentViewTop: Int = win.findViewById<View>(Window.ID_ANDROID_CONTENT).getTop()

        // Calculate titleBarHeight by deducting statusBarHeight from contentViewTop
        val titleBarHeight = contentViewTop - statusBarHeight

        // By now we got the height of titleBar & statusBar
        // Now lets get the screen size
        val metrics = DisplayMetrics()
        activity?.getWindowManager()?.getDefaultDisplay()?.getMetrics(metrics)
        val screenHeight = metrics.heightPixels

        // Now calculate the height that our layout can be set
        // If you know that your application doesn't have statusBar added, then don't add here also. Same applies to application bar also
        layoutHeight.set(screenHeight - (titleBarHeight + statusBarHeight))
        return layoutHeight.get()
    }

    fun getScreenWidth(): Int {
        val rect = Rect()
        val win: Window = activity?.getWindow()!! // Get the Window
        win.getDecorView().getWindowVisibleDisplayFrame(rect)


        // Now lets get the screen size
        val metrics = DisplayMetrics()
        activity?.getWindowManager()?.getDefaultDisplay()?.getMetrics(metrics)
        return metrics.widthPixels
    }
}