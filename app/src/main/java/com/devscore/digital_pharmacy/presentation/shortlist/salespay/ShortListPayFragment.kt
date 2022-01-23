package com.devscore.digital_pharmacy.presentation.shortlist.salespay

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
import com.devscore.digital_pharmacy.business.domain.models.PurchasesOrderMedicine
import com.devscore.digital_pharmacy.business.domain.models.SalesCart
import com.devscore.digital_pharmacy.business.domain.util.StateMessageCallback
import com.devscore.digital_pharmacy.presentation.purchases.BasePurchasesFragment
import com.devscore.digital_pharmacy.presentation.purchases.cart.PurchasesCartEvents
import com.devscore.digital_pharmacy.presentation.purchases.cart.PurchasesCartState
import com.devscore.digital_pharmacy.presentation.purchases.cart.PurchasesCartViewModel
import com.devscore.digital_pharmacy.presentation.purchases.payment.PurchasesOrderItemAdapter
import com.devscore.digital_pharmacy.presentation.purchases.payment.PurchasesOrdersAdapter
import com.devscore.digital_pharmacy.presentation.sales.BaseSalesFragment
import com.devscore.digital_pharmacy.presentation.sales.card.SalesCardEvents
import com.devscore.digital_pharmacy.presentation.sales.card.SalesCardState
import com.devscore.digital_pharmacy.presentation.sales.card.SalesCardViewModel
import com.devscore.digital_pharmacy.presentation.sales.payment.SalesOrderItemAdapter
import com.devscore.digital_pharmacy.presentation.sales.payment.SalesOrdersAdapter
import com.devscore.digital_pharmacy.presentation.util.TopSpacingItemDecoration
import com.devscore.digital_pharmacy.presentation.util.processQueue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_purchases_payment.*
import kotlinx.coroutines.*

@AndroidEntryPoint
class ShortListPayFragment : BasePurchasesFragment(), PurchasesOrderItemAdapter.Interaction{


    private var recyclerAdapter: PurchasesOrdersAdapter? = null // can leak memory so need to null
    private val viewModel: PurchasesCartViewModel by activityViewModels()


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
            val due = viewModel.state.value?.totalAmountAfterDiscount!! - viewModel.state.value?.receivedAmount!!
            if (due > 0 && viewModel.state.value?.vendor == null) {
                dueWarning()
            }
            else {
                viewModel.onTriggerEvent(PurchasesCartEvents.GenerateNewOrder)
            }
        }

        switchId.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                viewModel.onTriggerEvent(PurchasesCartEvents.IsDiscountPercent(true))
            }
            else {
                viewModel.onTriggerEvent(PurchasesCartEvents.IsDiscountPercent(false))
            }
        }


        purchasesPaymentReceiveAmount.doOnTextChanged { text, start, before, count ->
            if (text!!.isNotEmpty()) {
                viewModel.onTriggerEvent(PurchasesCartEvents.ReceiveAmount(purchasesPaymentReceiveAmount.text.toString().toFloat()))
            }
            else {
                viewModel.onTriggerEvent(PurchasesCartEvents.ReceiveAmount(0f))
            }
        }

        purchasesPaymentDiscount.doOnTextChanged { text, start, before, count ->
            if (text!!.isNotEmpty()) {
                viewModel.onTriggerEvent(PurchasesCartEvents.Discount(purchasesPaymentDiscount.text.toString().toFloat()))
            }
            else {
                viewModel.onTriggerEvent(PurchasesCartEvents.Discount(0f))
            }
        }

        purchasesCreateVendor.setOnClickListener {
            findNavController().navigate(R.id.action_shortListPayFragment_to_shortListAddFragment)
        }

        purchasesPaymentSearchView.setOnClickListener {
            Log.d(TAG, "OnClickListener")
            findNavController().navigate(R.id.action_shortListPayFragment_to_shortListCustomerListFragment)
        }



        img2.visibility = View.INVISIBLE
        externalTV.visibility = View.INVISIBLE

    }

    private fun subscribeObservers(){
        viewModel.state.observe(viewLifecycleOwner, { state ->

            uiCommunicationListener.displayProgressBar(state.isLoading)

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(PurchasesCartEvents.OnRemoveHeadFromQueue)
                    }
                })

            recyclerAdapter?.apply {
                submitList(order = state.order)
            }

            salesPaymentItemCount.setText("Items : " + state.purchasesCartList.size.toString())
            salesPaymentTotal.setText("Total : ৳" + state.totalAmount.toString())



            purchasesPaymentTotalAmount.setText("৳ " + state.totalAmount)
            val totalAmountAfterDiscount = state.totalAmountAfterDiscount!!
            totalAfterDiscountValue.setText("৳ " + totalAmountAfterDiscount.toString())
            val due = totalAmountAfterDiscount - state.receivedAmount!!
            purchasesPaymentDueAmount.setText("৳ " + due.toString())


            if (state.vendor != null) {
                purchasesPaymentSearchView.setText("     " + state.vendor.agent_name)
            }
            else {
                purchasesPaymentSearchView.setText("      " + "Walk-In Supplier")
            }



            if (state.uploaded) {
                viewModel.state.value = PurchasesCartState()
                findNavController().navigate(R.id.action_shortListPayFragment_to_shortListFragment)
            }
        })
    }

    private  fun resetUI(){
        uiCommunicationListener.hideSoftKeyboard()
    }

    private fun initRecyclerView(){
        purchasesPaymentRvTd.apply {
            layoutManager = LinearLayoutManager(this@ShortListPayFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator)
            addItemDecoration(topSpacingDecorator)
            recyclerAdapter = PurchasesOrdersAdapter(this@ShortListPayFragment)
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
                    viewModel.state.value = PurchasesCartState()
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

    override fun onItemDelete(item: PurchasesOrderMedicine) {
//        viewModel.onTriggerEvent(PurchasesCartEvents.DeleteMedicine(item.medicine!!))
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