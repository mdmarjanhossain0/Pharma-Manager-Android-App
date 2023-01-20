package com.appbytes.pharma_manager.presentation.sales.payment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.appbytes.pharma_manager.R
import com.appbytes.pharma_manager.business.domain.util.StateMessageCallback
import com.appbytes.pharma_manager.presentation.sales.BaseSalesFragment
import com.appbytes.pharma_manager.presentation.util.TopSpacingItemDecoration
import com.appbytes.pharma_manager.presentation.util.processQueue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_sales_cart.*
import kotlinx.android.synthetic.main.fragment_sales_pay_now.*
import kotlinx.android.synthetic.main.item_sales_list.*
import kotlinx.coroutines.*

import androidx.core.widget.doOnTextChanged
import com.appbytes.pharma_manager.business.domain.models.SalesOrderMedicine
import com.appbytes.pharma_manager.presentation.sales.SalesActivity


@AndroidEntryPoint
class SalesPayNowFragment : BaseSalesFragment(), SalesOrderItemAdapter.Interaction, OnCompleteCallback{


    private var recyclerAdapter: SalesOrdersAdapter? = null // can leak memory so need to null
    private val viewModel: SalesPayViewModel by activityViewModels()
    var pk : Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pk = arguments?.getInt("pk", -2)
    }


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


        requireActivity().onBackPressedDispatcher.addCallback(this) {
            viewModel.state.value = SalesPayState()
            findNavController().popBackStack()
        }


        createSalesOrder.setOnClickListener {
            val due = viewModel.state.value?.totalAmountAfterDiscount!! - viewModel.state.value?.receivedAmount!!
            if (due > 0 && viewModel.state.value?.customer == null) {
                dueWarning()
            }
            else {
                viewModel.onTriggerEvent(SalesPayEvents.OrderCompleted)
            }
        }

        switchId.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                viewModel.onTriggerEvent(SalesPayEvents.IsDiscountPercent(true))
            }
            else {
                viewModel.onTriggerEvent(SalesPayEvents.IsDiscountPercent(false))
            }
        }


        salesPaymentReceiveAmount.doOnTextChanged { text, start, before, count ->
            if (text!!.isNotEmpty()) {
                viewModel.onTriggerEvent(SalesPayEvents.ReceiveAmount(salesPaymentReceiveAmount.text.toString().toFloat()))
            }
            else {
                viewModel.onTriggerEvent(SalesPayEvents.ReceiveAmount(0f))
            }
        }

        salesPaymentDiscount.doOnTextChanged { text, start, before, count ->
            if (text!!.isNotEmpty()) {
                viewModel.onTriggerEvent(SalesPayEvents.Discount(salesPaymentDiscount.text.toString().toFloat()))
            }
            else {
                viewModel.onTriggerEvent(SalesPayEvents.Discount(0f))
            }
        }

        img1.setOnClickListener {
            val bundle = bundleOf("returnable" to true)
            findNavController().navigate(R.id.action_salesPayNowFragment_to_addCustomerFragment2, bundle)
        }

        salesPaymentSearchView.setOnClickListener {
            Log.d(TAG, "OnClickListener")
            findNavController().navigate(R.id.action_salesPayNowFragment_to_salesCustomerListFragment)
        }

    }

    private fun subscribeObservers(){
        viewModel.submit(this)
        viewModel.state.observe(viewLifecycleOwner, { state ->

            uiCommunicationListener.displayProgressBar(state.isLoading)

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(SalesPayEvents.OnRemoveHeadFromQueue)
                    }
                })

            if (state.order != null) {
                recyclerAdapter?.apply {
                    submitList(order = state.order)
                }
            }

            salesPaymentItemCount.setText("Items : " + state.order?.sales_oder_medicines?.size.toString())
            salesPaymentTotal.setText("Total : ৳" + state.totalAmount.toString())



            salesPaymentTotalAmount.setText("৳ " + state.totalAmount)
            val totalAmountAfterDiscount = state.totalAmountAfterDiscount!!
            totalAfterDiscountValue.setText("৳ " + totalAmountAfterDiscount.toString())
            val due = totalAmountAfterDiscount - state.receivedAmount!!
            salesPaymentDueAmount.setText("৳ " + due.toString())

            if (viewModel.state.value?.customer != null) {
                salesPaymentSearchView.setText("        " + viewModel.state.value?.customer?.name!!)
            }
            else {
                salesPaymentSearchView.setText("      " + "Walk-In Customer")
            }

//            if (state.uploaded) {
//                viewModel.state.value = SalesCardState()
//                findNavController().navigate(R.id.action_salesPayNowFragment_to_salesFragment)
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
            layoutManager = LinearLayoutManager(this@SalesPayNowFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator)
            addItemDecoration(topSpacingDecorator)
            recyclerAdapter = SalesOrdersAdapter(this@SalesPayNowFragment)
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
//        viewModel.onTriggerEvent(SalesCardEvents.DeleteMedicine(item.medicine!!))
    }


    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvent(SalesPayEvents.OrderDetails(pk!!))
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
        (activity as SalesActivity).onBackPressed()
    }
}