package com.appbytes.pharma_manager.presentation.sales.card

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.appbytes.pharma_manager.R
import com.appbytes.pharma_manager.business.domain.models.MedicineUnits
import com.appbytes.pharma_manager.business.domain.models.SalesCart
import com.appbytes.pharma_manager.business.domain.util.StateMessageCallback
import com.appbytes.pharma_manager.presentation.inventory.add.addmedicine.SelectUnitAdapter
import com.appbytes.pharma_manager.presentation.sales.BaseSalesFragment
import com.appbytes.pharma_manager.presentation.sales.SalesActivity
import com.appbytes.pharma_manager.presentation.util.TopSpacingItemDecoration
import com.appbytes.pharma_manager.presentation.util.processQueue
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.dialog_unit_select.*
import kotlinx.android.synthetic.main.fragment_sales_cart.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class SalesCartFragment : BaseSalesFragment(),
    SalesCardAdapter.Interaction, OnCompleteCallback{


    private lateinit var searchView: SearchView
    private var recyclerAdapter: SalesCardAdapter? = null // can leak memory so need to null
    private val viewModel: SalesCardViewModel by activityViewModels()
    private lateinit var menu: Menu
    private var unitRecyclerAdapter : SelectUnitAdapter? = null
    private var bottomSheetDialog : BottomSheetDialog? = null
    var pk : Int? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_sales_cart, container, false)
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


        salesCardGenerate.setOnClickListener {
            viewModel.onTriggerEvent(SalesCardEvents.GenerateNewOrder)
        }

        salesCardPay.setOnClickListener {
            if (viewModel.state.value?.salesCartList?.size!! < 1) {
                notItemAvailableInCart()
                return@setOnClickListener
            }
            (activity as SalesActivity).navigateSalesCardFragmentToSalesPaymentFragment()
        }


//        searchViewId.setOnClickListener {
//            (activity as SalesActivity).onBackPressed()
//        }
    }

    private fun subscribeObservers(){
        viewModel.submit(this)
        viewModel.state.observe(viewLifecycleOwner, { state ->

            uiCommunicationListener.displayProgressBar(state.isLoading)

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(SalesCardEvents.OnRemoveHeadFromQueue)
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

//            if (state.uploaded) {
//                viewModel.state.value = SalesCardState()
//                findNavController().navigate(R.id.action_salesCartFragment_to_salesFragment)
//            }


            if (state.pk == -2) {
                salesCardGenerate.visibility = View.VISIBLE
                salesCardPay.visibility = View.GONE
            }
            else {
                salesCardGenerate.visibility = View.GONE
                salesCardPay.visibility = View.VISIBLE
            }


            if (state.customer != null) {
                viewModel.onTriggerEvent(SalesCardEvents.GenerateNewOrder)
            }
        })
    }

    private  fun resetUI(){
//        uiCommunicationListener.hideSoftKeyboard()
//        focusableView.requestFocus()
    }

    private fun initRecyclerView(){
        salesCardRvId.apply {
            layoutManager = LinearLayoutManager(this@SalesCartFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator)
            addItemDecoration(topSpacingDecorator)
            recyclerAdapter = SalesCardAdapter(this@SalesCartFragment)
            adapter = recyclerAdapter
        }
    }


    override fun onResume() {
        super.onResume()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        recyclerAdapter = null
    }

    override fun onItemSelected(position: Int, item: SalesCart) {
        oderDetails(item)
    }

    override fun onChangeUnit(position: Int, item: SalesCart, unit: MedicineUnits, quantity : Int) {
        viewModel.onTriggerEvent(SalesCardEvents.ChangeUnit(item, unit, quantity))
    }

    override fun onItemDelete(position: Int, item: SalesCart) {
        viewModel.onTriggerEvent(SalesCardEvents.DeleteMedicine(item.medicine!!))
    }

    override fun onItemChangeMRP(position: Int, item: SalesCart, mrp: Float) {
        viewModel.onTriggerEvent(SalesCardEvents.ChangeMRP(item, mrp))
    }

    override fun onUpdateQuantity(position: Int, item: SalesCart, quantity: Int) {
        viewModel.onTriggerEvent(SalesCardEvents.UpdateQuantity(item, quantity))
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


        CoroutineScope(IO).launch {
            delay(2000)
            withContext(Main){
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


        CoroutineScope(IO).launch {
            delay(2000)
            withContext(Main){
                if (dialog == null) {
                    return@withContext
                }
                dialog.dismiss()
            }
        }
    }

    override fun done() {
        viewModel.state.value = SalesCardState()
        findNavController().navigate(R.id.action_salesCartFragment_to_salesFragment)
    }
}