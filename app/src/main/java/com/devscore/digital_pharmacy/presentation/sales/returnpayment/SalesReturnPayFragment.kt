package com.devscore.digital_pharmacy.presentation.sales.returnpayment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.business.domain.models.SalesCart
import com.devscore.digital_pharmacy.business.domain.models.SalesOrderMedicine
import com.devscore.digital_pharmacy.business.domain.util.StateMessageCallback
import com.devscore.digital_pharmacy.presentation.sales.BaseSalesFragment
import com.devscore.digital_pharmacy.presentation.sales.payment.SalesOrderItemAdapter
import com.devscore.digital_pharmacy.presentation.sales.payment.SalesOrdersAdapter
import com.devscore.digital_pharmacy.presentation.sales.salesreturn.SalesReturnEvents
import com.devscore.digital_pharmacy.presentation.sales.salesreturn.SalesReturnState
import com.devscore.digital_pharmacy.presentation.sales.salesreturn.SalesReturnViewModel
import com.devscore.digital_pharmacy.presentation.util.TopSpacingItemDecoration
import com.devscore.digital_pharmacy.presentation.util.processQueue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_sales_pay_now.*
import kotlinx.coroutines.*

@AndroidEntryPoint
class SalesReturnPayFragment : BaseSalesFragment(), SalesOrderItemAdapter.Interaction, OnCompleteCallback{


    private var recyclerAdapter: SalesOrdersAdapter? = null // can leak memory so need to null
    private var recyclerItemAdapter: SalesOrderItemAdapter? = null
//    private val viewModel: SalesReturnViewModel by activityViewModels()
    private val viewModel : SalesReturnPayViewModel by activityViewModels()
    var pk : Int? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_sales_pay_now, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        initRecyclerView()
        initUIClick()
        subscribeObservers()
        Log.d(TAG, "SalesPayNowFragment ViewModel " + viewModel.toString())
    }

    private fun initUIClick() {

//        val callback = requireActivity().onBackPressedDispatcher.addCallback(this){
//            backPressWarning()
//            Log.d(TAG, "Fragment On Back Press Callback call")
//        }


        createSalesOrder.setOnClickListener {
            val due = viewModel.state.value?.totalAmountAfterFine!! - viewModel.state.value?.returnAmount!!
            if (due > 0 && viewModel.state.value?.customer == null) {
                dueWarning()
            }
            else {
                viewModel.onTriggerEvent(SalesReturnPayEvents.OrderCompleted)
            }
        }

        switchId.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                viewModel.onTriggerEvent(SalesReturnPayEvents.IsDiscountPercent(true))
            }
            else {
                viewModel.onTriggerEvent(SalesReturnPayEvents.IsDiscountPercent(false))
            }
        }


        salesPaymentReceiveAmount.doOnTextChanged { text, start, before, count ->
            if (text!!.isNotEmpty()) {
                viewModel.onTriggerEvent(SalesReturnPayEvents.ReceiveAmount(salesPaymentReceiveAmount.text.toString().toFloat()))
            }
            else {
                viewModel.onTriggerEvent(SalesReturnPayEvents.ReceiveAmount(0f))
            }
        }

        salesPaymentDiscount.doOnTextChanged { text, start, before, count ->
            if (text!!.isNotEmpty()) {
                viewModel.onTriggerEvent(SalesReturnPayEvents.Discount(salesPaymentDiscount.text.toString().toFloat()))
            }
            else {
                viewModel.onTriggerEvent(SalesReturnPayEvents.Discount(0f))
            }
        }

        /*img1.setOnClickListener {
            findNavController().navigate(R.id.action_salesPayNowFragment_to_addCustomerFragment2)
        }*/

        img1.visibility = View.INVISIBLE

        /*salesPaymentSearchView.setOnClickListener {
            Log.d(TAG, "OnClickListener")
            findNavController().navigate(R.id.action_salesPayNowFragment_to_salesCustomerListFragment)
        }*/


        salesPaymentSearchView.visibility = View.INVISIBLE

        addCustomerTvId.visibility = View.INVISIBLE


    }

    private fun subscribeObservers(){
        viewModel.state.observe(viewLifecycleOwner, { state ->

//            uiCommunicationListener.displayProgressBar(state.isLoading)

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(SalesReturnPayEvents.OnRemoveHeadFromQueue)
                    }
                })

            recyclerAdapter?.apply {
                submitList(order = state.order!!)
            }

            salesPaymentItemCount.setText("Items : " + state.order?.sales_oder_medicines?.size.toString())
            salesPaymentTotal.setText("Total : ৳" + state.totalAmount.toString())



            salesPaymentTotalAmount.setText("৳ " + state.totalAmount)
            val totalAmountAfterDiscount = state.totalAmountAfterFine!!
            totalAfterDiscountValue.setText("৳ " + totalAmountAfterDiscount.toString())
            val due = totalAmountAfterDiscount - state.fineAmount!!
            salesPaymentDueAmount.setText("৳ " + due.toString())

            if (viewModel.state.value?.customer != null) {
                salesPaymentSearchView.setText("        " + viewModel.state.value?.customer?.name!!)
            }
            else {
                salesPaymentSearchView.setText("      " + "Walk-In Customer")
            }

//            if (state.uploaded) {
//                viewModel.state.value = SalesReturnState()
//                findNavController().navigate(R.id.action_salesReturnPayFragment_to_salesFragment)
//            }

            if (state.pk > 0) {
                orderNo.setText("#Order Number: " + state.pk)
            }
        })
    }

    private  fun resetUI(){
        uiCommunicationListener.hideSoftKeyboard()
    }

    private fun initRecyclerView(){
        salesPaymentRvId.apply {
            layoutManager = LinearLayoutManager(this@SalesReturnPayFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator)
            addItemDecoration(topSpacingDecorator)
            recyclerAdapter = SalesOrdersAdapter(this@SalesReturnPayFragment)
            adapter = recyclerAdapter
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        recyclerAdapter = null
    }


    fun notItemAvailableInCart() {
        val dialog = MaterialDialog(requireContext())
            .show {
                title(R.string.Warning)
                message(text = "No item available in cart")
                negativeButton {
                    dismiss()
                }
                onDismiss {
                }
                cancelable(false)
            }


        CoroutineScope(Dispatchers.IO).launch {
            delay(2000)
            withContext(Dispatchers.Main){
                if (dialog == null) {
                    return@withContext
                }
                dialog.dismiss()
            }
        }
    }

    override fun onItemDelete(item: SalesOrderMedicine) {
//        viewModel.onTriggerEvent(SalesReturnEvents.DeleteMedicine(item.medicine!!))
    }



    fun dueWarning() {
        MaterialDialog(requireContext())
            .show{
                title(R.string.Warning)
                message(text = "Customer must be select for due payment")
                negativeButton {
                    dismiss()
                }
                onDismiss {
                }
                cancelable(false)
            }
    }

    override fun done() {
        findNavController().popBackStack()
    }
}