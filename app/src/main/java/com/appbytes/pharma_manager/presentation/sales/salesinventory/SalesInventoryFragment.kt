package com.appbytes.pharma_manager.presentation.sales.salesinventory

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.appbytes.pharma_manager.R
import com.appbytes.pharma_manager.business.domain.models.LocalMedicine
import com.appbytes.pharma_manager.business.domain.util.StateMessageCallback
import com.appbytes.pharma_manager.presentation.inventory.BaseInventoryFragment
import com.appbytes.pharma_manager.presentation.sales.SalesActivity
import com.appbytes.pharma_manager.presentation.sales.card.SalesCardEvents
import com.appbytes.pharma_manager.presentation.sales.card.SalesCardState
import com.appbytes.pharma_manager.presentation.sales.card.SalesCardViewModel
import com.appbytes.pharma_manager.presentation.util.TopSpacingItemDecoration
import com.appbytes.pharma_manager.presentation.util.processQueue
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.dynamic_cart.*
import kotlinx.android.synthetic.main.fragment_local.*
import kotlinx.android.synthetic.main.fragment_sales_inventory.*
import kotlinx.android.synthetic.main.inventory_details_dialog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class SalesInventoryFragment : BaseInventoryFragment(),
    SalesInventoryAdapter.Interaction {


    private lateinit var searchView: SearchView
    private var recyclerAdapter: SalesInventoryAdapter? = null // can leak memory so need to null
    private val viewModel: SalesCardViewModel by activityViewModels()
    private val disposables = CompositeDisposable()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_sales_inventory, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        initRecyclerView()
        initUIClick()
        bouncingSearch()
        subscribeObservers()
        Log.d(TAG, "SalesInventoryFragment ViewModel " + viewModel.toString())
    }

    private fun initUIClick() {


        val callback = requireActivity().onBackPressedDispatcher.addCallback(this){
            backPressWarning()
            Log.d(TAG, "Fragment On Back Press Callback call")
        }

        salesInventoryCard.setOnClickListener {
            (activity as SalesActivity).navigateSalesInventoryToCardFragment()
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
                        viewModel.onTriggerEvent(SalesCardEvents.OnRemoveHeadFromQueue)
                    }
                })

            Log.d(TAG, "State " + state.toString())
            recyclerAdapter?.apply {
                submitList(medicineList = state.medicineList, state.isLoading, state.isQueryExhausted)
            }
            dynamicCart.setText(state.salesCartList.size.toString())
        })
    }

    private fun executeNewQuery(query: String){
//        resetUI()
        viewModel.onTriggerEvent(SalesCardEvents.UpdateQuery(query))
        viewModel.onTriggerEvent(SalesCardEvents.NewLocalMedicineSearch)
    }

    private  fun resetUI(){
//        uiCommunicationListener.hideSoftKeyboard()
//        focusableView.requestFocus()
    }

    private fun initRecyclerView(){
        salesInventoryRvId.apply {
            layoutManager = LinearLayoutManager(this@SalesInventoryFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerAdapter = SalesInventoryAdapter(this@SalesInventoryFragment)
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
                        viewModel.onTriggerEvent(SalesCardEvents.NextPage)
                    }
                }
            })
            adapter = recyclerAdapter
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        recyclerAdapter = null
        disposables.dispose()
    }

    override fun onItemSelected(position: Int, item: LocalMedicine) {
//        localMedicineDetails(item)
    }

    override fun onItemCard(position: Int, item: LocalMedicine) {
        viewModel.onTriggerEvent(SalesCardEvents.AddToCard(item))
    }

    override fun onItemDeleteSelected(position: Int, item: LocalMedicine) {
    }

    override fun restoreListPosition() {
    }

    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvent(SalesCardEvents.NewLocalMedicineSearch)
    }

    override fun nextPage() {
//        viewModel.onTriggerEvent(LocalMedicineEvents.NewLocalMedicineSearch)
    }



    fun localMedicineDetails(item: LocalMedicine) {
        val dialog = MaterialDialog(requireContext())
        dialog.cancelable(false)
        dialog.setContentView(R.layout.inventory_details_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.productDetailsBrandName.setText(item.brand_name)
        dialog.productDetailsMenufactureName.setText(item.manufacture)
        dialog.productDetailsCompanyName.setText(item.generic)
        dialog.productDetailsMRPValue.setText(item.mrp.toString())
        dialog.productDetailsCloseButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.productDetailsCloseIcon.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


    fun bouncingSearch() {
        val searchQueryObservable = Observable.create(object : ObservableOnSubscribe<String> {
            override fun subscribe(emitter: ObservableEmitter<String>) {
                salesInventorySearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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


    fun backPressWarning() {
        MaterialDialog(requireContext())
            .show{
                title(R.string.are_you_sure)
                message(text = "Cart item will be dismiss")
                positiveButton(R.string.text_ok){
                    viewModel.state.value = SalesCardState()
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

}