package com.devscore.digital_pharmacy.presentation.inventory.global

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.afollestad.materialdialogs.MaterialDialog
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.business.datasource.network.inventory.InventoryUtils
import com.devscore.digital_pharmacy.business.domain.models.GlobalMedicine
import com.devscore.digital_pharmacy.business.domain.util.StateMessageCallback
import com.devscore.digital_pharmacy.presentation.inventory.BaseInventoryFragment
import com.devscore.digital_pharmacy.presentation.inventory.InventoryActivity
import com.devscore.digital_pharmacy.presentation.inventory.local.LocalMedicineEvents
import com.devscore.digital_pharmacy.presentation.util.TopSpacingItemDecoration
import com.devscore.digital_pharmacy.presentation.util.processQueue
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_global.*
import kotlinx.android.synthetic.main.inventory_details_dialog.*
import kotlinx.android.synthetic.main.inventory_list_filter_dialog.*
import kotlinx.android.synthetic.main.item_global.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import com.devscore.digital_pharmacy.MainActivity
import com.devscore.digital_pharmacy.presentation.inventory.global.MySwipeHelper.MyButton
import com.devscore.digital_pharmacy.presentation.util.setDivider
import kotlinx.android.synthetic.main.fragment_global.globalFragmentBrandNameAction
import kotlinx.android.synthetic.main.fragment_global.globalFragmentCompanyNameAction
import kotlinx.android.synthetic.main.fragment_global.globalFragmentGenericAction
import kotlinx.android.synthetic.main.fragment_global.globalFragmentIndicationAction


class GlobalFragment : BaseInventoryFragment(),
    GlobalAdapter.Interaction {

    private var recyclerAdapter: GlobalAdapter? = null
    private val viewModel: GlobalViewModel by activityViewModels()
    private val disposables = CompositeDisposable()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_global, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        initRecyclerView()
        bouncingSearch()
        initUIClick()
        subscribeObservers()
    }

    private fun initUIClick() {

        globalFragmentBrandNameAction.setOnClickListener {
            if (viewModel.state.value?.action.equals(InventoryUtils.BRAND_NAME)!!) {
                viewModel.onTriggerEvent(GlobalEvents.SetSearchSelection(""))
            }
            else {
                viewModel.onTriggerEvent(GlobalEvents.SetSearchSelection(InventoryUtils.BRAND_NAME))
            }
        }

        globalFragmentGenericAction.setOnClickListener {
            if (viewModel.state.value?.action.equals(InventoryUtils.GENERIC)!!) {
                viewModel.onTriggerEvent(GlobalEvents.SetSearchSelection(""))
            }
            else {
                viewModel.onTriggerEvent(GlobalEvents.SetSearchSelection(InventoryUtils.GENERIC))
            }
        }

        globalFragmentIndicationAction.setOnClickListener {
            if (viewModel.state.value?.action.equals(InventoryUtils.INDICATION)!!) {
                viewModel.onTriggerEvent(GlobalEvents.SetSearchSelection(""))
            }
            else {
                viewModel.onTriggerEvent(GlobalEvents.SetSearchSelection(InventoryUtils.INDICATION))
            }
        }


        globalFragmentCompanyNameAction.setOnClickListener {
            if (viewModel.state.value?.action.equals(InventoryUtils.SYMPTOM)!!) {
                viewModel.onTriggerEvent(GlobalEvents.SetSearchSelection(""))
            }
            else {
                viewModel.onTriggerEvent(GlobalEvents.SetSearchSelection(InventoryUtils.SYMPTOM))
            }
        }
    }

    private fun subscribeObservers(){
        viewModel.state.observe(viewLifecycleOwner, { state ->

//            uiCommunicationListener.displayProgressBar(state.isLoading)
            Log.d(TAG, "GlobalFragment Loading State" + state.isLoading)

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(GlobalEvents.OnRemoveHeadFromQueue)
                    }
                })

            recyclerAdapter?.apply {
                submitList(medicineList = state.globalMedicineList, state.isLoading, state.isQueryExhausted)
            }


            Log.d(TAG, "Action " + state.action)



            when(state.action) {
                InventoryUtils.BRAND_NAME -> {

                    globalFragmentBrandNameAction.background = context?.resources?.getDrawable(R.drawable.blue_shape_outline_background_left)
                    globalFragmentBrandNameAction.setTextColor(resources.getColor(R.color.white))

                    globalFragmentGenericAction.background = context?.resources?.getDrawable(R.drawable.box_outline)
                    globalFragmentGenericAction.setTextColor(resources.getColor(R.color.colorPrimary))
                    globalFragmentIndicationAction.background = context?.resources?.getDrawable(R.drawable.box_outline)
                    globalFragmentIndicationAction.setTextColor(resources.getColor(R.color.colorPrimary))
                    globalFragmentCompanyNameAction.background = context?.resources?.getDrawable(R.drawable.right_outline)
                    globalFragmentCompanyNameAction.setTextColor(resources.getColor(R.color.colorPrimary))
                }
                InventoryUtils.GENERIC -> {


                    globalFragmentBrandNameAction.background = context?.resources?.getDrawable(R.drawable.left_outline)
                    globalFragmentBrandNameAction.setTextColor(resources.getColor(R.color.colorPrimary))

                    globalFragmentGenericAction.background = context?.resources?.getDrawable(R.color.colorPrimaryVariant)
                    globalFragmentGenericAction.setTextColor(resources.getColor(R.color.white))

                    globalFragmentIndicationAction.background = context?.resources?.getDrawable(R.drawable.box_outline)
                    globalFragmentIndicationAction.setTextColor(resources.getColor(R.color.colorPrimary))
                    globalFragmentCompanyNameAction.background = context?.resources?.getDrawable(R.drawable.right_outline)
                    globalFragmentCompanyNameAction.setTextColor(resources.getColor(R.color.colorPrimary))
                }
                InventoryUtils.INDICATION -> {



                    globalFragmentBrandNameAction.background = context?.resources?.getDrawable(R.drawable.left_outline)
                    globalFragmentBrandNameAction.setTextColor(resources.getColor(R.color.colorPrimary))
                    globalFragmentGenericAction.background = context?.resources?.getDrawable(R.drawable.box_outline)
                    globalFragmentGenericAction.setTextColor(resources.getColor(R.color.colorPrimary))

                    globalFragmentIndicationAction.background = context?.resources?.getDrawable(R.color.colorPrimaryVariant)
                    globalFragmentIndicationAction.setTextColor(resources.getColor(R.color.white))
                    globalFragmentCompanyNameAction.background = context?.resources?.getDrawable(R.drawable.right_outline)
                    globalFragmentCompanyNameAction.setTextColor(resources.getColor(R.color.colorPrimary))
                }
                InventoryUtils.SYMPTOM -> {


                    globalFragmentBrandNameAction.background = context?.resources?.getDrawable(R.drawable.left_outline)
                    globalFragmentBrandNameAction.setTextColor(resources.getColor(R.color.colorPrimary))

                    globalFragmentGenericAction.background = context?.resources?.getDrawable(R.drawable.box_outline)
                    globalFragmentGenericAction.setTextColor(resources.getColor(R.color.colorPrimary))
                    globalFragmentIndicationAction.background = context?.resources?.getDrawable(R.drawable.box_outline)
                    globalFragmentIndicationAction.setTextColor(resources.getColor(R.color.colorPrimary))
                    globalFragmentCompanyNameAction.background = context?.resources?.getDrawable(R.drawable.blue_shape_outline_background_right)
                    globalFragmentCompanyNameAction.setTextColor(resources.getColor(R.color.white))
                }
                else -> {
                    globalFragmentBrandNameAction.background = context?.resources?.getDrawable(R.drawable.left_outline)
                    globalFragmentBrandNameAction.setTextColor(resources.getColor(R.color.colorPrimary))

                    globalFragmentGenericAction.background = context?.resources?.getDrawable(R.drawable.box_outline)
                    globalFragmentGenericAction.setTextColor(resources.getColor(R.color.colorPrimary))
                    globalFragmentIndicationAction.background = context?.resources?.getDrawable(R.drawable.box_outline)
                    globalFragmentIndicationAction.setTextColor(resources.getColor(R.color.colorPrimary))
                    globalFragmentCompanyNameAction.background = context?.resources?.getDrawable(R.drawable.right_outline)
                    globalFragmentCompanyNameAction.setTextColor(resources.getColor(R.color.colorPrimary))
                }

            }
        })
    }

    private fun executeNewQuery(query: String){
        resetUI()
        viewModel.onTriggerEvent(GlobalEvents.UpdateQuery(query))
        viewModel.onTriggerEvent(GlobalEvents.NewMedicineSearch)
    }

    private  fun resetUI(){
//        uiCommunicationListener.hideSoftKeyboard()
//        focusableView.requestFocus()
    }

    private fun initRecyclerView(){
        globalRvId.apply {
            layoutManager = LinearLayoutManager(this@GlobalFragment.context)
//            val topSpacingDecorator = TopSpacingItemDecoration(15)
//            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
//            addItemDecoration(topSpacingDecorator)

            recyclerAdapter = GlobalAdapter(this@GlobalFragment)
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    Log.d(TAG, "onScrollStateChanged: exhausted? ${viewModel.state.value?.isQueryExhausted}")
                    Log.d(TAG, "onScrollStateChanged: exhausted? ${viewModel.state.value?.isLoading}")
                    Log.d(TAG, "onScrollStateChanged: exhausted? ${layoutManager.findLastVisibleItemPosition()}")
                    if (
                        lastPosition == recyclerAdapter?.itemCount?.minus(1)
                        && viewModel.state.value?.isLoading == false
                        && viewModel.state.value?.isQueryExhausted == false
                    ) {
                        Log.d(TAG, "GlobalFragment: attempting to load next page...")
                        viewModel.onTriggerEvent(GlobalEvents.NextPage)
                    }
                }
            })
            adapter = recyclerAdapter
//            setDivider(R.drawable.recycler_view_divider)
//            val itemDecoration: ItemDecoration =
//                DividerItemDecoration(context, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL)
//            addItemDecoration(itemDecoration)

            /*val mySwipeHelper: MySwipeHelper = object : MySwipeHelper(context, globalRvId, 200) {

                override fun instantiateMyButton(
                    viewHolder: RecyclerView.ViewHolder?,
                    buffer: MutableList<MyButton>?
                ) {
                    buffer?.add(
                        MyButton(context,
                            "Update",
                            0,
                            R.drawable.ic_cart,
                            Color.parseColor("#DEDDDD"),
                            object : MyButtonClickListener {
                                override fun onClick(pos: Int) {
                                    (activity as InventoryActivity).navigateGlobalFragmentToAddMedicineContainerFragment(viewModel.state.value?.globalMedicineList?.get(pos)?.id)
                                }
                            })
                    )
                }
            }*/
        }
    }

    override fun onItemSelected(position: Int, item: GlobalMedicine) {
//        (activity as InventoryActivity).navigateGlobalFragmentToAddMedicineContainerFragment(item.id)
    }

    override fun onAddMedicine(position: Int, item: GlobalMedicine) {
        viewModel.onTriggerEvent(GlobalEvents.AddMedicine(item))
    }

    override fun restoreListPosition() {
    }

    override fun nextPage() {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerAdapter = null
        disposables.clear()
    }


    fun showFilterDialog() {
        val dialog = MaterialDialog(requireContext())
        dialog.cancelable(false)
        dialog.setContentView(R.layout.inventory_list_filter_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.globalMedicineFilterClear.setOnClickListener {
            dialog.dismiss()
        }
        dialog.globalMedicineApplyFilter.setOnClickListener {
            val generic = dialog.globalFilterGeneric.text.toString()
//            val manufacturer = dialog.globalFilterManufacturer.text.toString()
//            applyFilter(generic, manufacturer)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun applyFilter(generic: String, manufacturer: String) {
        if (generic == "" && manufacturer == ""){
            return
        }

        if (manufacturer == "") {
            viewModel.onTriggerEvent(GlobalEvents.GenericFilter(generic))
            return
        }

        if (generic == "") {
            viewModel.onTriggerEvent(GlobalEvents.ManufacturerFilter(generic))
            return
        }

        viewModel.onTriggerEvent(GlobalEvents.GenericWithManufacturerFilter(generic, manufacturer))
    }


    fun bouncingSearch() {
        val searchQueryObservable = Observable.create(object : ObservableOnSubscribe<String> {
            override fun subscribe(emitter: ObservableEmitter<String>) {
                globalFragmentSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
                    if (viewModel.state.value?.query != t.toString()) {
                        CoroutineScope(Dispatchers.Main).launch {
                            executeNewQuery(t)
                        }
                    }
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {
                }

            }
        )
    }


    private fun toast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}