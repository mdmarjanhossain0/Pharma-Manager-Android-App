package com.devscore.digital_pharmacy.presentation.supplier.details

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.business.domain.models.PurchasesOrder
import com.devscore.digital_pharmacy.business.domain.models.SalesOrder
import com.devscore.digital_pharmacy.business.domain.util.StateMessageCallback
import com.devscore.digital_pharmacy.presentation.customer.BaseCustomerFragment
import com.devscore.digital_pharmacy.presentation.customer.details.CustomerDetailsEvents
import com.devscore.digital_pharmacy.presentation.customer.details.CustomerDetailsViewModel
import com.devscore.digital_pharmacy.presentation.purchases.ordercompleted.PurchasesOrderCompletedAdapter
import com.devscore.digital_pharmacy.presentation.sales.odercompleted.SalesCompletedAdapter
import com.devscore.digital_pharmacy.presentation.util.TopSpacingItemDecoration
import com.devscore.digital_pharmacy.presentation.util.processQueue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_customer_previous_orders.*


@AndroidEntryPoint
class SupplierPreviousOrdersFragment : BaseCustomerFragment(),
    PurchasesOrderCompletedAdapter.Interaction {


    private var recyclerAdapter: PurchasesOrderCompletedAdapter? = null // can leak memory so need to null
    private val viewModel: SupplierDetailsViewModel by viewModels()
    private var pk : Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pk = arguments?.getInt("pk")
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_customer_previous_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        initRecyclerView()
        initUIClick()
        subscribeObservers()
    }

    private fun initUIClick() {
    }

    private fun subscribeObservers(){
        viewModel.state.observe(viewLifecycleOwner, { state ->

            uiCommunicationListener.displayProgressBar(state.isLoading)

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(SupplierDetailsEvents.OnRemoveHeadFromQueue)
                    }
                })

            recyclerAdapter?.apply {
                submitList(list = state.orderList, state.isLoadingList, state.isQueryExhausted)
            }




            if (state.supplier != null) {
                totalBalance.setText("Balance : ৳ " + state.supplier?.total_balance)
                dueBalance.setText("Due : ৳" +state.supplier?.due_balance)
            }
//            if (state.supplier?.total_balance != null) {
//                totalBalance.setText("Balance : ৳ " + state.supplier?.total_balance)
//            }
//            else {
//                totalBalance.setText("Balance : ৳ " + 00.0)
//            }


//            if (state.supplier?.due_balance != null) {
//                dueBalance.setText("Due : ৳" +state.supplier?.due_balance)
//            }
//            else {
//                dueBalance.setText("Due : ৳" + 00.0)
//            }
        })
    }

    private fun executeNewQuery(query: String){
        resetUI()
        viewModel.onTriggerEvent(SupplierDetailsEvents.UpdateQuery(query))
//        viewModel.onTriggerEvent(CustomerDetailsEvents.SearchOrders)
    }

    private  fun resetUI(){
//        uiCommunicationListener.hideSoftKeyboard()
//        focusableView.requestFocus()
    }

    private fun initRecyclerView(){
        orderDetailRvId.apply {
            layoutManager = LinearLayoutManager(this@SupplierPreviousOrdersFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerAdapter = PurchasesOrderCompletedAdapter(this@SupplierPreviousOrdersFragment)
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    Log.d(TAG, "onScrollStateChanged: exhausted? ${viewModel.state.value?.isQueryExhausted}")
                    if (
                        lastPosition == recyclerAdapter?.itemCount?.minus(1)
                        && viewModel.state.value?.isLoadingList == false
                        && viewModel.state.value?.isQueryExhausted == false
                    ) {
                        Log.d(TAG, "GlobalFragment: attempting to load next page...")
                        viewModel.onTriggerEvent(SupplierDetailsEvents.NextPage(pk!!))
                    }
                }
            })
            adapter = recyclerAdapter
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        recyclerAdapter = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvent(SupplierDetailsEvents.GetDetails(pk!!))
        viewModel.onTriggerEvent(SupplierDetailsEvents.SearchOrders(pk!!))
    }

    override fun onItemSelected(position: Int, item: PurchasesOrder) {
    }

    override fun onItemReturn(position: Int, item: PurchasesOrder) {
    }





    fun oderDetails(item: SalesOrder) {
//        val dialog = MaterialDialog(requireContext())
//        dialog.cancelable(false)
//        dialog.setContentView(R.layout.inventory_details_dialog)
//        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialog.productDetailsBrandName.setText(item.brand_name)
//        dialog.productDetailsMenufactureName.setText(item.manufacture)
//        dialog.productDetailsCompanyName.setText(item.generic)
//        dialog.productDetailsMRPValue.setText(item.mrp.toString())
//        dialog.productDetailsCloseButton.setOnClickListener {
//            dialog.dismiss()
//        }
//        dialog.productDetailsCloseIcon.setOnClickListener {
//            dialog.dismiss()
//        }
//        dialog.show()
    }

}