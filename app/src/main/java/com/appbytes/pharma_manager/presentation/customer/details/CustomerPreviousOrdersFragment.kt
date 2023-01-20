package com.appbytes.pharma_manager.presentation.customer.details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appbytes.pharma_manager.R
import com.appbytes.pharma_manager.business.domain.models.SalesOrder
import com.appbytes.pharma_manager.business.domain.util.StateMessageCallback
import com.appbytes.pharma_manager.presentation.customer.BaseCustomerFragment
import com.appbytes.pharma_manager.presentation.sales.odercompleted.SalesCompletedAdapter
import com.appbytes.pharma_manager.presentation.util.TopSpacingItemDecoration
import com.appbytes.pharma_manager.presentation.util.processQueue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_customer_previous_orders.*
import kotlinx.android.synthetic.main.fragment_sales_orders.*


@AndroidEntryPoint
class CustomerPreviousOrdersFragment : BaseCustomerFragment(),
    SalesCompletedAdapter.Interaction {


    private var recyclerAdapter: SalesCompletedAdapter? = null // can leak memory so need to null
    private val viewModel: CustomerDetailsViewModel by viewModels()
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
                        viewModel.onTriggerEvent(CustomerDetailsEvents.OnRemoveHeadFromQueue)
                    }
                })

            recyclerAdapter?.apply {
                submitList(list = state.orderList, state.isLoadingList, state.isQueryExhausted)
            }



            if (state.customer != null) {
                totalBalance.setText("Balance : ৳ " + state.customer.total_balance)
                dueBalance.setText("Due : ৳" +state.customer.due_balance)
            }
        })
    }

    private fun executeNewQuery(query: String){
        resetUI()
        viewModel.onTriggerEvent(CustomerDetailsEvents.UpdateQuery(query))
//        viewModel.onTriggerEvent(CustomerDetailsEvents.SearchOrders)
    }

    private  fun resetUI(){
//        uiCommunicationListener.hideSoftKeyboard()
//        focusableView.requestFocus()
    }

    private fun initRecyclerView(){
        orderDetailRvId.apply {
            layoutManager = LinearLayoutManager(this@CustomerPreviousOrdersFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerAdapter = SalesCompletedAdapter(this@CustomerPreviousOrdersFragment)
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
                        viewModel.onTriggerEvent(CustomerDetailsEvents.NextPage(pk!!))
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
        viewModel.onTriggerEvent(CustomerDetailsEvents.GetDetails(pk!!))
        viewModel.onTriggerEvent(CustomerDetailsEvents.SearchOrders(pk!!))
    }

    override fun onItemSelected(position: Int, item: SalesOrder) {
    }

    override fun onItemRetrun(position: Int, item: SalesOrder) {
//        shareViewModel.onTriggerEvent(SalesReturnEvents.OrderDetails(item.pk!!))
//        (activity as SalesActivity).navigateSalesToSalesReturn()
    }


    override fun restoreListPosition() {
    }

    override fun nextPage() {
//        viewModel.onTriggerEvent(LocalMedicineEvents.NewLocalMedicineSearch)
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