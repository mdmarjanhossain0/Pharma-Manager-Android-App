package com.appbytes.pharma_manager.presentation.supplier.update

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.appbytes.pharma_manager.R
import com.appbytes.pharma_manager.business.domain.models.Supplier
import com.appbytes.pharma_manager.business.domain.util.StateMessageCallback
import com.appbytes.pharma_manager.presentation.customer.BaseCustomerFragment
import com.appbytes.pharma_manager.presentation.supplier.SupplierActivity
import com.appbytes.pharma_manager.presentation.util.processQueue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_create_supplier.*
import kotlinx.android.synthetic.main.fragment_update_supplier.*
import kotlinx.android.synthetic.main.fragment_update_supplier.supplierAddressFirst
import kotlinx.android.synthetic.main.fragment_update_supplier.supplierAddressSecond
import kotlinx.android.synthetic.main.fragment_update_supplier.supplierCompanyName
import kotlinx.android.synthetic.main.fragment_update_supplier.supplierContactNumber
import kotlinx.android.synthetic.main.fragment_update_supplier.supplierEmail
import kotlinx.android.synthetic.main.fragment_update_supplier.supplierFacebook
import kotlinx.android.synthetic.main.fragment_update_supplier.supplierImo
import kotlinx.android.synthetic.main.fragment_update_supplier.supplierWhatsapp
import kotlinx.android.synthetic.main.fragment_update_supplier.suppliertName

@AndroidEntryPoint
class UpdateSupplierFragment : BaseCustomerFragment(), OnCompleteCallback {

    private val viewModel: UpdateSupplierViewModel by viewModels()
    var pk : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pk = arguments?.getInt("pk")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_update_supplier, container, false)
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
        updateButton.setOnClickListener {
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

        updateReset.setOnClickListener {
//            clearWarning()
            clearForm()
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
                        viewModel.onTriggerEvent(UpdateSupplierEvents.OnRemoveHeadFromQueue)
                    }
                })
            Log.d(TAG, state.supplier.toString())



            if (state.supplier != null) {
                clearForm()
            }

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
//        viewModel.onTriggerEvent(UpdateSupplierEvents.CacheState(supplier))
        viewModel.onTriggerEvent(UpdateSupplierEvents.Update(supplier))

    }



    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvent(UpdateSupplierEvents.GetSupplier(pk!!))
    }


    fun clearForm() {

        if (viewModel.state.value?.supplier != null) {
            if (viewModel.state.value?.supplier?.company_name != null)
                supplierCompanyName.setText(viewModel.state.value?.supplier?.company_name!!)
            if (viewModel.state.value?.supplier?.agent_name != null)
                suppliertName.setText(viewModel.state.value?.supplier?.agent_name!!)
            if (viewModel.state.value?.supplier?.email != null)
                supplierEmail.setText(viewModel.state.value?.supplier?.email!!)
            if (viewModel.state.value?.supplier?.mobile != null)
                supplierContactNumber.setText(viewModel.state.value?.supplier?.mobile!!)
            if (viewModel.state.value?.supplier?.whatsapp != null)
                supplierWhatsapp.setText(viewModel.state.value?.supplier?.whatsapp!!)
            if (viewModel.state.value?.supplier?.facebook != null)
                supplierFacebook.setText(viewModel.state.value?.supplier?.facebook!!)
            if (viewModel.state.value?.supplier?.imo != null)
                supplierImo.setText(viewModel.state.value?.supplier?.imo!!)
            if (viewModel.state.value?.supplier?.address != null)
                supplierAddressFirst.setText(viewModel.state.value?.supplier?.address!!)
        }
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
        (activity as SupplierActivity).onBackPressed()
    }

}