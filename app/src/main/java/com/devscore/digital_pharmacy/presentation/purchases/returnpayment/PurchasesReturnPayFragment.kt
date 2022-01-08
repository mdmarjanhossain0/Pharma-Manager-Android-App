package com.devscore.digital_pharmacy.presentation.purchases.returnpayment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
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
import com.devscore.digital_pharmacy.business.domain.models.PurchasesCart
import com.devscore.digital_pharmacy.business.domain.util.StateMessageCallback
import com.devscore.digital_pharmacy.presentation.purchases.BasePurchasesFragment
import com.devscore.digital_pharmacy.presentation.purchases.cart.PurchasesCartEvents
import com.devscore.digital_pharmacy.presentation.purchases.cart.PurchasesCartState
import com.devscore.digital_pharmacy.presentation.purchases.cart.PurchasesCartViewModel
import com.devscore.digital_pharmacy.presentation.purchases.payment.PurchasesOrderItemAdapter
import com.devscore.digital_pharmacy.presentation.purchases.payment.PurchasesOrdersAdapter
import com.devscore.digital_pharmacy.presentation.purchases.purchasesreturn.PurchasesReturnEvents
import com.devscore.digital_pharmacy.presentation.purchases.purchasesreturn.PurchasesReturnState
import com.devscore.digital_pharmacy.presentation.purchases.purchasesreturn.PurchasesReturnViewModel
import com.devscore.digital_pharmacy.presentation.util.TopSpacingItemDecoration
import com.devscore.digital_pharmacy.presentation.util.processQueue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_purchases_payment.*
import kotlinx.android.synthetic.main.fragment_purchases_payment.createSalesOrder
import kotlinx.android.synthetic.main.fragment_purchases_payment.img2
import kotlinx.android.synthetic.main.fragment_purchases_payment.salesPaymentItemCount
import kotlinx.android.synthetic.main.fragment_purchases_payment.salesPaymentTotal
import kotlinx.android.synthetic.main.fragment_purchases_payment.switchId
import kotlinx.android.synthetic.main.fragment_purchases_payment.totalAfterDiscountValue
import kotlinx.coroutines.*


@AndroidEntryPoint
class PurchasesReturnPayFragment : BasePurchasesFragment(), PurchasesOrderItemAdapter.Interaction{


    private var recyclerAdapter: PurchasesOrdersAdapter? = null // can leak memory so need to null
    private val viewModel: PurchasesReturnViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_purchases_payment, container, false)
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
        createSalesOrder.setOnClickListener {
            val due = viewModel.state.value?.totalAmountAfterFine!! - viewModel.state.value?.returnAmount!!
            if (due > 0 && viewModel.state.value?.vendor == null) {
                dueWarning()
            }
            else {
                viewModel.onTriggerEvent(PurchasesReturnEvents.GenerateNewOrder)
            }
        }

        switchId.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                viewModel.onTriggerEvent(PurchasesReturnEvents.IsDiscountPercent(true))
            }
            else {
                viewModel.onTriggerEvent(PurchasesReturnEvents.IsDiscountPercent(false))
            }
        }


        purchasesPaymentReceiveAmount.doOnTextChanged { text, start, before, count ->
            if (text!!.isNotEmpty()) {
                viewModel.onTriggerEvent(PurchasesReturnEvents.ReceiveAmount(purchasesPaymentReceiveAmount.text.toString().toFloat()))
            }
            else {
                viewModel.onTriggerEvent(PurchasesReturnEvents.ReceiveAmount(0f))
            }
        }

        purchasesPaymentDiscount.doOnTextChanged { text, start, before, count ->
            if (text!!.isNotEmpty()) {
                viewModel.onTriggerEvent(PurchasesReturnEvents.Discount(purchasesPaymentDiscount.text.toString().toFloat()))
            }
            else {
                viewModel.onTriggerEvent(PurchasesReturnEvents.Discount(0f))
            }
        }

//        purchasesCreateVendor.setOnClickListener {
//            findNavController().navigate(R.id.action_purchasesPaymentFragment_to_createSupplierFragment2)
//        }
//
//        purchasesPaymentSearchView.setOnClickListener {
//            Log.d(TAG, "OnClickListener")
//            findNavController().navigate(R.id.action_purchasesPaymentFragment_to_purchasesSuplierListFragment)
//        }

        purchasesCreateVendor.visibility = View.INVISIBLE


        purchasesPaymentSearchView.visibility = View.INVISIBLE

        purchasesAddVendorTvId.visibility = View.INVISIBLE



        img2.visibility = View.INVISIBLE
        externalTV.visibility = View.INVISIBLE

    }

    private fun subscribeObservers(){
        viewModel.state.observe(viewLifecycleOwner, { state ->

//            uiCommunicationListener.displayProgressBar(state.isLoading)

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(PurchasesReturnEvents.OnRemoveHeadFromQueue)
                    }
                })

            recyclerAdapter?.apply {
                submitList(order = state.order, cartList = state.purchasesCartList)
            }

            salesPaymentItemCount.setText("Items : " + state.purchasesCartList.size.toString())
            salesPaymentTotal.setText("Total : ৳" + state.totalAmount.toString())



            purchasesPaymentTotalAmount.setText("৳ " + state.totalAmount)
            val totalAmountAfterDiscount = state.totalAmountAfterFine!!
            totalAfterDiscountValue.setText("৳ " + totalAmountAfterDiscount.toString())
            val due = totalAmountAfterDiscount - state.returnAmount!!
            purchasesPaymentDueAmount.setText("৳ " + due.toString())


            if (state.vendor != null) {
                purchasesPaymentSearchView.setText("     " + state.vendor.agent_name)
            }
            else {
                purchasesPaymentSearchView.setText("      " + "Walk-In Supplier")
            }



            if (state.uploaded) {
                viewModel.state.value = PurchasesReturnState()
                findNavController().navigate(R.id.action_purchasesReturnPayFragment_to_purchaseFragment)
            }


            if (state.order.pk!! > 0) {
                orderNo.setText("#Order Number: " + state.order.pk)
            }
        })
    }

    private  fun resetUI(){
        uiCommunicationListener.hideSoftKeyboard()
    }

    private fun initRecyclerView(){
        purchasesPaymentRvTd.apply {
            layoutManager = LinearLayoutManager(this@PurchasesReturnPayFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator)
            addItemDecoration(topSpacingDecorator)
            recyclerAdapter = PurchasesOrdersAdapter(this@PurchasesReturnPayFragment)
            adapter = recyclerAdapter
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        recyclerAdapter = null
    }




    fun backPressWarning() {
        MaterialDialog(requireContext())
            .show{
                title(R.string.are_you_sure)
                message(text = "Cart item will be dismiss")
                positiveButton(R.string.text_ok){
                    viewModel.state.value = PurchasesReturnState()
                    findNavController().popBackStack()
                    dismiss()
                }
                negativeButton {
                    dismiss()
                }
                onDismiss {
                }
                cancelable(false)
            }
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

    override fun onItemDelete(item: PurchasesCart) {
        viewModel.onTriggerEvent(PurchasesReturnEvents.DeleteMedicine(item.medicine!!))
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
}