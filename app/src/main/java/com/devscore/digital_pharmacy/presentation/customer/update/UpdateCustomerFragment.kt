package com.devscore.digital_pharmacy.presentation.customer.update

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.business.domain.models.Customer
import com.devscore.digital_pharmacy.business.domain.util.StateMessageCallback
import com.devscore.digital_pharmacy.presentation.customer.BaseCustomerFragment
import com.devscore.digital_pharmacy.presentation.customer.CustomerActivity
import com.devscore.digital_pharmacy.presentation.customer.createcustomer.CreateCustomerEvents
import com.devscore.digital_pharmacy.presentation.customer.createcustomer.CreateCustomerViewModel
import com.devscore.digital_pharmacy.presentation.util.processQueue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_update_customer.*


@AndroidEntryPoint
class UpdateCustomerFragment : BaseCustomerFragment(), OnCompleteCallback {

    private val viewModel: UpdateCustomerViewModel by viewModels()
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
        return inflater.inflate(R.layout.fragment_update_customer, container, false)
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
//            if (state.updated) {
//                Log.d(TAG, "BackPress")
//                (activity as CustomerActivity).onBackPressed()
//            }

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(UpdateCustomerEvents.OnRemoveHeadFromQueue)
                    }
                })
            Log.d(TAG, state.customer.toString())



            if (state.customer != null) {
                clearForm()
            }
        })
    }


    fun cacheState() {
        var blank = false



        if (customerName.text.isNullOrBlank()) {
            blank = true
            customerName.error = "Error"
        }
        val name = customerName.text.toString()
        val email = customerEmail.text.toString()
        if (customerContactNumber.text.isNullOrBlank()) {
            blank = true
            customerContactNumber.error = "Error"
        }
        val mobile = customerContactNumber.text.toString()
        val whatsapp = customerWhatsapp.text.toString()
        val facebook = customerFacebook.text.toString()
        val imo = customerImo.text.toString()
        val date_of_birth = customerDateOfBirth.text.toString()
        val address = customerAddress.text.toString()




        if (blank) {
            throw Exception("Error")
        }
        val customer = Customer(
            name = name,
            email = email,
            mobile = mobile,
            whatsapp = whatsapp,
            facebook = facebook,
            imo = imo,
            address = address,
            date_of_birth = date_of_birth,
        )
//        viewModel.onTriggerEvent(UpdateCustomerEvents.CacheState(customer))
        viewModel.onTriggerEvent(UpdateCustomerEvents.Update(customer))

    }



    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvent(UpdateCustomerEvents.GetCustomer(pk!!))
    }


    fun clearForm() {

        if (viewModel.state.value?.customer != null) {
            if (viewModel.state.value?.customer?.name != null){

                customerName.setText(viewModel.state.value?.customer?.name!!)
            }
            if (viewModel.state.value?.customer?.email != null){

                customerEmail.setText(viewModel.state.value?.customer?.email!!)
            }
            if (viewModel.state.value?.customer?.mobile != null) {

                customerContactNumber.setText(viewModel.state.value?.customer?.mobile!!)
            }
            if (viewModel.state.value?.customer?.whatsapp != null) {

                customerWhatsapp.setText(viewModel.state.value?.customer?.whatsapp!!)
            }
            if (viewModel.state.value?.customer?.facebook != null) {

                customerFacebook.setText(viewModel.state.value?.customer?.facebook!!)
            }
            if (viewModel.state.value?.customer?.imo != null) {

                customerImo.setText(viewModel.state.value?.customer?.imo!!)
            }
            if (viewModel.state.value?.customer?.address != null) {

                customerAddress.setText(viewModel.state.value?.customer?.address!!)
            }
            if (viewModel.state.value?.customer?.date_of_birth != null) {

                customerDateOfBirth.setText(viewModel.state.value?.customer?.date_of_birth!!)
            }
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
        (activity as CustomerActivity).onBackPressed()
    }

}