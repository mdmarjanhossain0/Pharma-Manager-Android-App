package com.devscore.digital_pharmacy.presentation.purchases.orderlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.business.domain.models.PurchasesOrder
import com.devscore.digital_pharmacy.business.domain.util.StateMessageCallback
import com.devscore.digital_pharmacy.presentation.purchases.BasePurchasesFragment
import com.devscore.digital_pharmacy.presentation.purchases.PurchasesActivity
import com.devscore.digital_pharmacy.presentation.purchases.cart.PurchasesCartEvents
import com.devscore.digital_pharmacy.presentation.purchases.cart.PurchasesCartViewModel
import com.devscore.digital_pharmacy.presentation.util.TopSpacingItemDecoration
import com.devscore.digital_pharmacy.presentation.util.processQueue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_purchase_generated.*

@AndroidEntryPoint
class PurchasesOrdersFragment : BasePurchasesFragment(),
    PurchasesOrderAdapter.Interaction {


    private var recyclerAdapter: PurchasesOrderAdapter? = null
    private val viewModel: PurchasesOrderListViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_purchase_generated, container, false)
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
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.onTriggerEvent(PurchasesOrderListEvents.SearchNewOrder)
            swipeRefreshLayout.isRefreshing = false
        }
        generateNewPurchasesOrder.setOnClickListener {
            (activity as PurchasesActivity).navigatePurchasesGenerateToPurchasesInventoryFragment()
        }
    }

    private fun subscribeObservers(){
        viewModel.state.observe(viewLifecycleOwner, { state ->

//            uiCommunicationListener.displayProgressBar(state.isLoading)

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(PurchasesOrderListEvents.OnRemoveHeadFromQueue)
                    }
                })

            recyclerAdapter?.apply {
                submitList(list = state.orderList, state.isLoading, state.isQueryExhausted)
            }
        })
    }

    private  fun resetUI(){
//        uiCommunicationListener.hideSoftKeyboard()
//        focusableView.requestFocus()
    }

    private fun initRecyclerView(){
        purchaseGeneratedRvId.apply {
            layoutManager = LinearLayoutManager(this@PurchasesOrdersFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerAdapter = PurchasesOrderAdapter(this@PurchasesOrdersFragment)
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    Log.d(TAG, "onScrollStateChanged: exhausted? ${viewModel.state.value?.isQueryExhausted}")
                    if (
                        lastPosition == recyclerAdapter?.itemCount?.minus(1)
                        && viewModel.state.value?.isLoading == false
                        && viewModel.state.value?.isQueryExhausted == false
                    ) {
                        Log.d(TAG, "GlobalFragment: attempting to load next page...")
                        viewModel.onTriggerEvent(PurchasesOrderListEvents.NextPage)
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
        viewModel.onTriggerEvent(PurchasesOrderListEvents.SearchNewOrder)
    }

    override fun onItemSelected(position: Int, item: PurchasesOrder) {
//        viewModel.onTriggerEvent(PurchasesOrderListEvents.PurchasesCompleted(item))
        if (item.pk == null || item.pk < 1) {
            Toast.makeText(context, "You should sync first", Toast.LENGTH_SHORT).show()
        }
        else {
            (activity as PurchasesActivity).navigatePurchasesDetailsFragment(item.pk!!)
        }
    }

    override fun onItemProcess(position: Int, item: PurchasesOrder) {
        (activity as PurchasesActivity).navigatePurchasesFragmentToPurchasesPayFragment(item.pk!!)
    }

    override fun onItemDelete(position: Int, item: PurchasesOrder) {
        viewModel.onTriggerEvent(PurchasesOrderListEvents.DeleteOrder(item))
    }


    fun oderDetails(item: PurchasesOrder) {
    }

}