package com.devscore.digital_pharmacy.presentation.purchases.purchasesreturn

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
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.business.domain.models.MedicineUnits
import com.devscore.digital_pharmacy.business.domain.models.PurchasesCart
import com.devscore.digital_pharmacy.business.domain.util.StateMessageCallback
import com.devscore.digital_pharmacy.presentation.purchases.BasePurchasesFragment
import com.devscore.digital_pharmacy.presentation.purchases.cart.PurchasesCartAdapter
import com.devscore.digital_pharmacy.presentation.purchases.cart.PurchasesCartEvents
import com.devscore.digital_pharmacy.presentation.util.TopSpacingItemDecoration
import com.devscore.digital_pharmacy.presentation.util.processQueue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_sales_return.*
import kotlinx.coroutines.*


@AndroidEntryPoint
class PurchasesReturnFragment : BasePurchasesFragment(),
    PurchasesReturnAdapter.Interaction {


    private var recyclerAdapter: PurchasesReturnAdapter? = null // can leak memory so need to null
    private val viewModel: PurchasesReturnViewModel by activityViewModels()

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
            if (viewModel.state.value?.purchasesCartList?.size!! < 1) {
                notItemAvailableInCart()
                return@setOnClickListener
            }
            findNavController().navigate(R.id.action_purchasesReturnFragment_to_purchasesReturnPayFragment)
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
                        viewModel.onTriggerEvent(PurchasesReturnEvents.OnRemoveHeadFromQueue)
                    }
                })

            recyclerAdapter?.apply {
                submitList(list = state.purchasesCartList, state.isLoading, state.isQueryExhausted)
            }

            salesCartItemCount.setText("Items : " + state.purchasesCartList.size.toString())
            salesCartTotalAmount.setText("Total : ৳" + state.totalAmount.toString())

            for (item in state.purchasesCartList) {
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
            layoutManager = LinearLayoutManager(this@PurchasesReturnFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator)
            addItemDecoration(topSpacingDecorator)
            recyclerAdapter = PurchasesReturnAdapter(this@PurchasesReturnFragment)
            adapter = recyclerAdapter
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        recyclerAdapter = null
    }

    override fun onItemSelected(position: Int, item: PurchasesCart) {
        oderDetails(item)
    }

    override fun onChangeUnit(position: Int, item: PurchasesCart, unit: MedicineUnits, quantity : Int) {
        Log.d(TAG, "SalesFragment onChangeUnit Call " + Thread.currentThread().name)
        viewModel.onTriggerEvent(PurchasesReturnEvents.ChangeUnit(item, unit, quantity))
        Log.d(TAG, "SalesFragment onChangeUnit Call Finish " + Thread.currentThread().name)
//        viewModel.onTriggerEvent(SalesCardEvents.ChangeUnit(item.medicine!!, unitId, quantity!!))
    }

    override fun onItemChangePP(position: Int, item: PurchasesCart, purchase_price: Float) {
        viewModel.onTriggerEvent(PurchasesReturnEvents.ChangePP(item, purchase_price))
    }

    override fun onItemDelete(position: Int, item: PurchasesCart) {
        viewModel.onTriggerEvent(PurchasesReturnEvents.DeleteMedicine(item.medicine!!))
    }



    fun oderDetails(item: PurchasesCart) {
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


    override fun alertDialog(item : PurchasesCart, message : String) {
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

    override fun restoreListPosition() {
    }

    override fun nextPage() {
    }


    fun backPressWarning() {
        MaterialDialog(requireContext())
            .show{
                title(R.string.are_you_sure)
                message(text = "Cart item will be dismiss")
                positiveButton(R.string.text_ok){
                    viewModel.state.value = PurchasesReturnState()
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