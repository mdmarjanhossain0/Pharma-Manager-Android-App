package com.devscore.digital_pharmacy.presentation.purchases.ordercompleted

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.business.domain.models.PurchasesOrder
import com.devscore.digital_pharmacy.business.domain.util.StateMessageCallback
import com.devscore.digital_pharmacy.presentation.purchases.BasePurchasesFragment
import com.devscore.digital_pharmacy.presentation.purchases.PurchasesActivity
import com.devscore.digital_pharmacy.presentation.purchases.orderlist.PurchasesOrderAdapter
import com.devscore.digital_pharmacy.presentation.purchases.orderlist.PurchasesOrderListEvents
import com.devscore.digital_pharmacy.presentation.purchases.orderlist.PurchasesOrderListViewModel
import com.devscore.digital_pharmacy.presentation.purchases.purchasesreturn.PurchasesReturnEvents
import com.devscore.digital_pharmacy.presentation.purchases.purchasesreturn.PurchasesReturnViewModel
import com.devscore.digital_pharmacy.presentation.util.TopSpacingItemDecoration
import com.devscore.digital_pharmacy.presentation.util.processQueue
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_purchase_generated.*
import kotlinx.android.synthetic.main.fragment_purchase_generated.searchBarLayout
import kotlinx.android.synthetic.main.fragment_purchase_generated.searchViewId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class PurchaseCompletedFragment : BasePurchasesFragment(),
    PurchasesOrderCompletedAdapter.Interaction {


    private var recyclerAdapter: PurchasesOrderCompletedAdapter? = null
    private val viewModel: PurchasesOrderCompletedViewModel by viewModels()

    private val shareViewModel : PurchasesReturnViewModel by activityViewModels()
    private val disposables = CompositeDisposable()


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
        bouncingSearch()
        subscribeObservers()
    }

    private fun initUIClick() {

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.onTriggerEvent(PurchasesOrderCompletedEvents.SearchNewOrder)
            swipeRefreshLayout.isRefreshing = false
        }

        generateNewPurchasesOrder.setOnClickListener {
            (activity as PurchasesActivity).navigatePurchasesGenerateToPurchasesInventoryFragment()
        }



        searchBarLayout.visibility = View.VISIBLE
    }

    private fun subscribeObservers(){
        viewModel.state.observe(viewLifecycleOwner, { state ->

//            uiCommunicationListener.displayProgressBar(state.isLoading)

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(PurchasesOrderCompletedEvents.OnRemoveHeadFromQueue)
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
            layoutManager = LinearLayoutManager(this@PurchaseCompletedFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerAdapter = PurchasesOrderCompletedAdapter(this@PurchaseCompletedFragment)
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
                        viewModel.onTriggerEvent(PurchasesOrderCompletedEvents.NextPage)
                    }
                }
            })
            adapter = recyclerAdapter
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        recyclerAdapter = null
        disposables.clear()
    }

    override fun onItemSelected(position: Int, item: PurchasesOrder) {
//        oderDetails(item)
        (activity as PurchasesActivity).navigatePurchasesDetailsFragment(item.pk!!)
    }

    override fun onItemReturn(position: Int, item: PurchasesOrder) {
//        shareViewModel.onTriggerEvent(PurchasesReturnEvents.OrderDetails(item.pk!!))
//        (activity as PurchasesActivity).navigatePurchasesToPurchasesReturnFragment()
    }


    fun oderDetails(item: PurchasesOrder) {
    }




    fun bouncingSearch() {
        val searchQueryObservable = Observable.create(object : ObservableOnSubscribe<String> {
            override fun subscribe(emitter: ObservableEmitter<String>) {
                searchViewId.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextChange(newText: String): Boolean {
                        if (!emitter.isDisposed) {
                            emitter.onNext(newText)
                        }
                        return true
                    }

                    override fun onQueryTextSubmit(query: String): Boolean {
                        executeNewQuery(query)
                        return true
                    }
                })
            }
        })
            .debounce(1000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())


        searchQueryObservable.subscribe(
            object : Observer<String> {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onNext(t: String) {
                    Log.d(TAG, t.toString())
                    CoroutineScope(Dispatchers.Main).launch {
                        executeNewQuery(t)
                    }
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {
                }

            }
        )
    }

    private fun executeNewQuery(query: String) {
        viewModel.onTriggerEvent(PurchasesOrderCompletedEvents.UpdateQuery(query))
        viewModel.onTriggerEvent(PurchasesOrderCompletedEvents.SearchNewOrder)
    }
}