package com.devscore.digital_pharmacy.presentation.main.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.business.domain.util.StateMessageCallback
import com.devscore.digital_pharmacy.business.domain.util.SuccessHandling.Companion.SYNC_FINISH
import com.devscore.digital_pharmacy.presentation.cashregister.CashRegisterActivity
import com.devscore.digital_pharmacy.presentation.customer.CustomerActivity
import com.devscore.digital_pharmacy.presentation.inventory.InventoryActivity
import com.devscore.digital_pharmacy.presentation.main.BaseMainFragment
import com.devscore.digital_pharmacy.presentation.main.notification.NotificationViewModel
import com.devscore.digital_pharmacy.presentation.purchases.PurchasesActivity
import com.devscore.digital_pharmacy.presentation.sales.SalesActivity
import com.devscore.digital_pharmacy.presentation.shortlist.ShortListActivity
import com.devscore.digital_pharmacy.presentation.supplier.SupplierActivity
import com.devscore.digital_pharmacy.presentation.util.DateTime
import com.devscore.digital_pharmacy.presentation.util.processQueue
import kotlinx.android.synthetic.main.dynamic_notification.*
import kotlinx.android.synthetic.main.dynamic_sync.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import java.util.*


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
}