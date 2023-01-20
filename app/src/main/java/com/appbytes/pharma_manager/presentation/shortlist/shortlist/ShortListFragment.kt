package com.appbytes.pharma_manager.presentation.shortlist.shortlist

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.appbytes.pharma_manager.R
import com.appbytes.pharma_manager.business.domain.models.LocalMedicine
import com.appbytes.pharma_manager.business.domain.models.toLocalMedicine
import com.appbytes.pharma_manager.business.domain.util.StateMessageCallback
import com.appbytes.pharma_manager.presentation.purchases.cart.PurchasesCartEvents
import com.appbytes.pharma_manager.presentation.purchases.cart.PurchasesCartViewModel
import com.appbytes.pharma_manager.presentation.shortlist.BaseShortListFragment
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
import kotlinx.android.synthetic.main.fragment_global.*
import kotlinx.android.synthetic.main.fragment_purchase_short_list.*
import kotlinx.android.synthetic.main.fragment_sales_inventory.*
import kotlinx.android.synthetic.main.inventory_details_dialog.*
import kotlinx.android.synthetic.main.inventory_list_filter_dialog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit



@AndroidEntryPoint
class ShortListFragment : BaseShortListFragment(),
    ShortListAdapter.Interaction {


    private var recyclerAdapter: ShortListAdapter? = null // can leak memory so need to null
    private val viewModel : ShortListViewModel by viewModels()
    private val shareViewModel: PurchasesCartViewModel by activityViewModels()
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
        Log.d(TAG, "SalesInventoryFragment ViewModel " + shareViewModel.toString())
    }

    private fun initUIClick() {

//        val callback = requireActivity().onBackPressedDispatcher.addCallback(this){
//            backPressWarning()
//            Log.d(TAG, "Fragment On Back Press Callback call")
//        }
        salesInventoryCard.setOnClickListener {
            findNavController().navigate(R.id.action_shortListFragment_to_shortListSalesCartFragment)
        }






        shortListFilter.visibility = View.VISIBLE
        shortListFilter.setOnClickListener {
            showFilterDialog()
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
                        shareViewModel.onTriggerEvent(PurchasesCartEvents.OnRemoveHeadFromQueue)
                    }
                })

            recyclerAdapter?.apply {
                submitList(medicineList = state.localMedicineList.map { it.toLocalMedicine() }, state.isLoading, state.isQueryExhausted)
            }
        })


        shareViewModel.state.observe(viewLifecycleOwner, { state ->

//            uiCommunicationListener.displayProgressBar(state.isLoading)

            dynamicCart.setText(state.purchasesCartList.size.toString())
        })
    }

    private fun executeNewQuery(query: String){
        viewModel.onTriggerEvent(ShortListEvents.UpdateQuery(query))
        viewModel.onTriggerEvent(ShortListEvents.NewShortListSearch)
    }

    private  fun resetUI(){
//        uiCommunicationListener.hideSoftKeyboard()
//        focusableView.requestFocus()
    }

    private fun initRecyclerView(){
        salesInventoryRvId.apply {
            layoutManager = LinearLayoutManager(this@ShortListFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerAdapter = ShortListAdapter(this@ShortListFragment)
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    Log.d(TAG, "onScrollStateChanged: exhausted? ${shareViewModel.state.value?.isQueryExhausted}")
                    if (
                        lastPosition == recyclerAdapter?.itemCount?.minus(1)
                        && viewModel.state.value?.isLoading == false
                        && viewModel.state.value?.isQueryExhausted == false
                    ) {
                        Log.d(TAG, "GlobalFragment: attempting to load next page...")
                        viewModel.onTriggerEvent(ShortListEvents.NextPage)
                    }
                }
            })
            adapter = recyclerAdapter

//            val mySwipeHelper: MySwipeHelper = object : MySwipeHelper(context, salesInventoryRvId, 200) {
//
//                override fun instantiateMyButton(
//                    viewHolder: RecyclerView.ViewHolder?,
//                    buffer: MutableList<MyButton>?
//                ) {
//                    buffer?.add(
//                        MyButton(context,
//                            "Delete",
//                            0,
//                            R.drawable.ic_delete,
//                            Color.parseColor("#DEDDDD"),
//                            object : MyButtonClickListener {
//                                override fun onClick(pos: Int) {
//                                    viewModel.onTriggerEvent(ShortListEvents.DeleteShortList(viewModel.state.value?.localMedicineList?.get(pos)!!))
//                                }
//                            })
//                    )
//                }
//            }
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
        Log.d(TAG, "Short List add card")
        shareViewModel.onTriggerEvent(PurchasesCartEvents.AddToCard(item))
    }

    override fun onItemDeleteSelected(position: Int, item: LocalMedicine) {
        viewModel.onTriggerEvent(ShortListEvents.DeleteShortList(viewModel.state.value?.localMedicineList?.get(position)!!))
    }

    override fun restoreListPosition() {
    }

    override fun onResume() {
        super.onResume()
//        shareViewModel.onTriggerEvent(PurchasesCartEvents.NewLocalMedicineSearch)
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


    fun showFilterDialog() {
        val dialog = MaterialDialog(requireContext())
        dialog.cancelable(false)
        dialog.setContentView(R.layout.inventory_list_filter_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.manufactureFilter.setText(viewModel.state.value?.filter)
        dialog.globalMedicineFilterClear.setOnClickListener {
            dialog.dismiss()
        }
        dialog.globalMedicineApplyFilter.setOnClickListener {
//            val generic = dialog.globalFilterGeneric.text.toString()
            val manufacturer = dialog.manufactureFilter.text.toString()
            applyFilter(manufacturer)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun applyFilter(manufacturer: String) {
        viewModel.onTriggerEvent(ShortListEvents.UpdateFilter(manufacturer))
    }
}