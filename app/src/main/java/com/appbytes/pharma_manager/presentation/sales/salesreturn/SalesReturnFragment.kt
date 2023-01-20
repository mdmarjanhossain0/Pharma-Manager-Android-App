package com.appbytes.pharma_manager.presentation.sales.salesreturn

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.appbytes.pharma_manager.R
import com.appbytes.pharma_manager.business.domain.models.MedicineUnits
import com.appbytes.pharma_manager.business.domain.models.SalesCart
import com.appbytes.pharma_manager.business.domain.util.StateMessageCallback
import com.appbytes.pharma_manager.presentation.sales.BaseSalesFragment
import com.appbytes.pharma_manager.presentation.util.TopSpacingItemDecoration
import com.appbytes.pharma_manager.presentation.util.processQueue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_sales_return.*
import kotlinx.coroutines.*

@AndroidEntryPoint
class SalesReturnFragment : BaseSalesFragment(),
    SalesReturnAdapter.Interaction {


    private var recyclerAdapter: SalesReturnAdapter? = null // can leak memory so need to null
    private val viewModel: SalesReturnViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_sales_return, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        initRecyclerView()
        initUIClick()
        subscribeObservers()
        Log.d(TAG, "SalesCartFragment ViewModel " + viewModel.toString())
    }

    private fun initUIClick() {

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this){
            backPressWarning()
            Log.d(TAG, "Fragment On Back Press Callback call")
        }


        salesCardGenerate.setOnClickListener {
            if (viewModel.state.value?.salesCartList?.size!! < 1) {
                notItemAvailableInCart()
                return@setOnClickListener
            }
            findNavController().navigate(R.id.action_salesReturnFragment_to_salesReturnPayFragment)
        }


        searchViewId.visibility = View.GONE
    }

    private fun subscribeObservers(){
        viewModel.state.observe(viewLifecycleOwner, { state ->

//            uiCommunicationListener.displayProgressBar(state.isLoading)

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(SalesReturnEvents.OnRemoveHeadFromQueue)
                    }
                })

            recyclerAdapter?.apply {
                submitList(list = state.salesCartList, state.isLoading, state.isQueryExhausted)
            }

            salesCartItemCount.setText("Items : " + state.salesCartList.size.toString())
            salesCartTotalAmount.setText("Total : ৳" + state.totalAmount.toString())

            for (item in state.salesCartList) {
                Log.d(TAG, "Quantity " + item.quantity)
            }
        })
    }

    private  fun resetUI(){
//        uiCommunicationListener.hideSoftKeyboard()
//        focusableView.requestFocus()
    }

    private fun initRecyclerView(){
        salesCardRvId.apply {
            layoutManager = LinearLayoutManager(this@SalesReturnFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator)
            addItemDecoration(topSpacingDecorator)
            recyclerAdapter = SalesReturnAdapter(this@SalesReturnFragment)
            adapter = recyclerAdapter
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        recyclerAdapter = null
    }

    override fun onItemSelected(position: Int, item: SalesCart) {
        oderDetails(item)
    }

    override fun onChangeUnit(position: Int, item: SalesCart, unit: MedicineUnits, quantity : Int) {
        Log.d(TAG, "SalesFragment onChangeUnit Call " + Thread.currentThread().name)
        viewModel.onTriggerEvent(SalesReturnEvents.ChangeUnit(item, unit, quantity))
        Log.d(TAG, "SalesFragment onChangeUnit Call Finish " + Thread.currentThread().name)
//        viewModel.onTriggerEvent(SalesCardEvents.ChangeUnit(item.medicine!!, unitId, quantity!!))
    }

    override fun onUpdateQuantity(position: Int, item: SalesCart, quantity: Int) {
        Log.d(TAG, "SalesFragment onQuantity Call " + Thread.currentThread().name)
        viewModel.onTriggerEvent(SalesReturnEvents.UpdateQuantity(item, quantity))
        Log.d(TAG, "SalesFragment onQuantity Call Finish " + Thread.currentThread().name)
    }


    fun oderDetails(item: SalesCart) {
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


    override fun alertDialog(item : SalesCart, message : String) {
        val dialog = MaterialDialog(requireContext())
            .show {
                title(R.string.Warning)
                message(text = item.medicine?.brand_name + "..." + message)
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


    fun backPressWarning() {
        MaterialDialog(requireContext())
            .show{
                title(R.string.are_you_sure)
                message(text = "Cart item will be dismiss")
                positiveButton(R.string.text_ok){
                    viewModel.state.value = SalesReturnState()
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