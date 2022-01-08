package com.devscore.digital_pharmacy.presentation.cashregister.receive

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.business.domain.models.Receive
import com.devscore.digital_pharmacy.business.domain.util.StateMessageCallback
import com.devscore.digital_pharmacy.presentation.cashregister.BaseCashRegisterFragment
import com.devscore.digital_pharmacy.presentation.cashregister.CashRegisterActivity
import com.devscore.digital_pharmacy.presentation.cashregister.payment.PaymentEvents
import com.devscore.digital_pharmacy.presentation.util.ReceivePaymentType.Companion.CASH_IN
import com.devscore.digital_pharmacy.presentation.util.ReceivePaymentType.Companion.CUSTOMER_PAY
import com.devscore.digital_pharmacy.presentation.util.ReceivePaymentType.Companion.OTHER_INCOME
import com.devscore.digital_pharmacy.presentation.util.processQueue
import kotlinx.android.synthetic.main.fragment_vendor_receive.*
import java.util.*


class VendorReceiveFragment : BaseCashRegisterFragment() {

    private val viewModel: ReceiveViewModel by activityViewModels()

    private lateinit var datePicker : DatePickerDialog
    val calender = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_vendor_receive, container, false)
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
        receiveAdd.setOnClickListener {
            try {
                cacheState()
                if (viewModel.state.value?.type.equals(CUSTOMER_PAY)) {
                    if (viewModel.state.value?.customer == null) {
                        throw Exception("Select a customer")
                    }
                }
                viewModel.onTriggerEvent(ReceiveEvents.NewReceiveCreate)
            }
            catch (e : Exception) {
                MaterialDialog(requireContext())
                    .show{
                        title(R.string.text_info)
                        message(text = e.message)
                        onDismiss {
                        }
                        cancelable(true)
                    }
            }
        }

        receiveClear.setOnClickListener {
            clearWarning()
        }

//        receiveTypeTV.setOnClickListener {
//            receiveTypeTV.visibility = View.INVISIBLE
//            receiveSpinner.visibility = View.VISIBLE
//            receiveSpinner.performClick()
//        }

        val typeList = mutableListOf<String>()
        typeList.add(CASH_IN)
        typeList.add(CUSTOMER_PAY)
        typeList.add(OTHER_INCOME)


        val kindAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            typeList.toTypedArray()
        )

        kindAdapter.setDropDownViewResource(
            android.R.layout
                .simple_spinner_dropdown_item
        )

        receiveTypeTV.setAdapter(kindAdapter)
        receiveTypeTV.setAdapter(kindAdapter)
        receiveTypeTV.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (typeList.get(position) == viewModel.state.value?.type) {
                    Log.d("AppDebug", "spinner return")
                    return
                }


                viewModel.onTriggerEvent(ReceiveEvents.AddType(typeList.get(position)))
//                receiveTypeTV.setText(typeList.get(position))
//                receiveSpinner.visibility = View.INVISIBLE
//                receiveTypeTV.visibility = View.VISIBLE
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
//                receiveSpinner.visibility = View.INVISIBLE
//                receiveTypeTV.visibility = View.VISIBLE
            }

        })


        receiveSearch.setOnClickListener {
            (activity as CashRegisterActivity).navigateReceiveToCustomerFragment()
        }



        val dataPickerListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                val date = year.toString() + "-" + (month + 1) + "-" + dayOfMonth
                receiveDate.setText(date)
                Log.d(TAG, "Year " + year + " Month " + month + " Day " + dayOfMonth)
            }

        }




        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH)
        val date = calender.get(Calendar.DATE)
        val today = year.toString() + "-" + (month + 1) + "-" + date
        receiveDate.setText(today)

        receiveDate.setOnClickListener {
            datePicker = DatePickerDialog(requireContext(),dataPickerListener, year, month, date)
            datePicker.show()
        }
    }

    private fun subscribeObservers(){
        viewModel.state.observe(viewLifecycleOwner, { state ->

            uiCommunicationListener.displayProgressBar(state.isLoading)

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(ReceiveEvents.OnRemoveHeadFromQueue)
                    }
                })

            Log.d(TAG, state.receive.toString())



            if (state.customer != null) {
                receiveSearch.setText(state.customer.name)
            }


            if (state.type == CUSTOMER_PAY) {
                receiveSearch.visibility = View.VISIBLE
            }
            else {
                receiveSearch.visibility = View.GONE
//                if (state.customer != null) {
//                    viewModel.onTriggerEvent(ReceiveEvents.AddCustomer(null))
//                }
            }
        })
    }


    fun cacheState() {
        val date = receiveDate.text.toString()
//        val type = receiveTypeTV.text.toString()
        val type = viewModel.state.value?.type
        val amount = receiveAmount.text.toString().toFloat()
//        val balance = receiveBalance.text.toString().toFloat()
        val remark = receiveRemark1.text.toString() + " " + receiveRemark2.text.toString()



        viewModel.onTriggerEvent(ReceiveEvents.AddDate(date))
        viewModel.onTriggerEvent(ReceiveEvents.AddType(type!!))
        viewModel.onTriggerEvent(ReceiveEvents.AddAmount(amount))
//        viewModel.onTriggerEvent(ReceiveEvents.AddBalance(balance))
        viewModel.onTriggerEvent(ReceiveEvents.AddRemark(remark))
    }



    override fun onDestroyView() {
        super.onDestroyView()
    }


    fun clearForm() {
        receiveDate.setText("")
//        receiveTypeTV.setText("")
        receiveAmount.text.clear()
        receiveBalance.text.clear()
        receiveRemark1.text.clear()
        receiveRemark2.text.clear()

        val receive = Receive(
            pk = -1,
            room_id = -1,
            date = "",
            customer = -1,
            vendor = -1,
            type = "",
            total_amount = 0f,
            balance = 0f,
            remarks = "",
            created_at = "",
            updated_at = "",
            customer_name = "",
            vendor_name = ""
        )
        viewModel.onTriggerEvent(ReceiveEvents.CacheState(receive))
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

}