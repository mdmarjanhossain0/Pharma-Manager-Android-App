package com.appbytes.pharma_manager.presentation.supplier.createsupplier

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.appbytes.pharma_manager.R
import com.appbytes.pharma_manager.business.domain.models.Supplier
import com.appbytes.pharma_manager.business.domain.util.StateMessageCallback
import com.appbytes.pharma_manager.presentation.purchases.payment.PurchasesPayEvents
import com.appbytes.pharma_manager.presentation.purchases.payment.PurchasesPayViewModel
import com.appbytes.pharma_manager.presentation.supplier.BaseSupplierFragment
import com.appbytes.pharma_manager.presentation.util.processQueue
import kotlinx.android.synthetic.main.add_product_dialog.*
import kotlinx.android.synthetic.main.fragment_add_customer.*
import kotlinx.android.synthetic.main.fragment_add_product_sub_medicine.*
import kotlinx.android.synthetic.main.fragment_create_supplier.*


class CreateSupplierFragment : BaseSupplierFragment(), OnCompleteCallback {

    private val viewModel: SupplierCreateViewModel by viewModels()
    private val shareViewModel : PurchasesPayViewModel by activityViewModels()
    var returnable : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            returnable = arguments?.getBoolean("returnable", false)!!
        }
        catch (e : Exception) {
            returnable = false
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_create_supplier, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        initUIClick()
        initUI()
        subscribeObservers()
    }

    private fun initUI() {
    }

    private fun initUIClick() {
        createSupplier.setOnClickListener {
            try {
            cacheState()
            }
            catch (e : Exception) {
//                MaterialDialog(requireContext())
//                    .show{
//                        title(R.string.text_info)
//                        message(text = "Something is wrong")
//                        onDismiss {
//                        }
//                        cancelable(true)
//                    }
            }
        }

        createSupplierClear.setOnClickListener {
            clearWarning()
        }
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
                        viewModel.onTriggerEvent(SupplierCreateEvents.OnRemoveHeadFromQueue)
                    }
                })
            Log.d(TAG, state.supplier.toString())
        })
    }


    fun cacheState() {
        var blank = false
        if (supplierCompanyName.text.isNullOrBlank()) {
            blank = true
            supplierCompanyName.error = "Error"
        }
        val company_name = supplierCompanyName.text.toString()

        if (suppliertName.text.isNullOrBlank()) {
            blank = true
            suppliertName.error = "Error"
        }
        val agent_name = suppliertName.text.toString()

        val email = supplierEmail.text.toString()
        if (!supplierEmail.text.isNullOrBlank()) {
            if (!Patterns.EMAIL_ADDRESS.matcher(supplierEmail.text).matches()) {
                blank = true
                supplierEmail.error = "Invalid email address"
            }
        }
        if (supplierContactNumber.text.isNullOrBlank()) {
            blank = true
            supplierContactNumber.error = "Error"
        }
        val mobile = supplierContactNumber.text.toString()
        val whatsapp = supplierWhatsapp.text.toString()
        val facebook = supplierFacebook.text.toString()
        val imo = supplierImo.text.toString()
        val address = supplierAddressFirst.text.toString() + " " + supplierAddressSecond.text.toString()




        if (blank) {
            throw Exception("Error")
        }
        val supplier = Supplier(
            company_name = company_name,
            agent_name = agent_name,
            email = email,
            mobile = mobile,
            whatsapp = whatsapp,
            facebook = facebook,
            imo = imo,
            address = address
        )
        viewModel.onTriggerEvent(SupplierCreateEvents.CacheState(supplier))
        if (returnable!!) {
            viewModel.onTriggerEvent(SupplierCreateEvents.NewSupplierCreateAndReturn)
        }
        else {
            viewModel.onTriggerEvent(SupplierCreateEvents.NewSupplierCreate)
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
    }


    fun clearForm() {
        supplierCompanyName.text.clear()
        suppliertName.text.clear()
        supplierEmail.text.clear()
        supplierContactNumber.text.clear()
        supplierWhatsapp.text.clear()
        supplierFacebook.text.clear()
        supplierImo.text.clear()
        supplierAddressFirst.text.clear()
        supplierAddressSecond.text.clear()

        val supplier = Supplier(
            company_name = "",
            agent_name = "",
            email = "",
            mobile = "",
            whatsapp = "",
            facebook = "",
            imo = "",
            address = ""
        )
        viewModel.onTriggerEvent(SupplierCreateEvents.CacheState(supplier))
    }



    fun clearWarning() {
        MaterialDialog(requireContext())
            .show{
                title(R.string.are_you_sure)
                message(text = "Are you sure to clear the form")
                positiveButton(R.string.text_ok){
                    clearForm()
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

    override fun done() {
        if (returnable!!) {
            shareViewModel.onTriggerEvent(PurchasesPayEvents.SelectSupplier(viewModel.state.value?.supplier!!))
        }
        findNavController().popBackStack()
    }

}