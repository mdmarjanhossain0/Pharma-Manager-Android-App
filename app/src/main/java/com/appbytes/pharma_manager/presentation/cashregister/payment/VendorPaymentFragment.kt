package com.appbytes.pharma_manager.presentation.cashregister.payment

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
import com.appbytes.pharma_manager.R
import com.appbytes.pharma_manager.business.domain.models.Payment
import com.appbytes.pharma_manager.business.domain.util.StateMessageCallback
import com.appbytes.pharma_manager.presentation.cashregister.BaseCashRegisterFragment
import com.appbytes.pharma_manager.presentation.cashregister.CashRegisterActivity
import com.appbytes.pharma_manager.presentation.util.ReceivePaymentType
import com.appbytes.pharma_manager.presentation.util.ReceivePaymentType.Companion.VENDOR_PAY
import com.appbytes.pharma_manager.presentation.util.processQueue
import kotlinx.android.synthetic.main.fragment_vendor_payment.*
import java.util.*


class VendorPaymentFragment : BaseCashRegisterFragment() {

    private val viewModel: PaymentViewModel by activityViewModels()

    private lateinit var datePicker : DatePickerDialog
    val calender = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_vendor_payment, container, false)
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
        paymentAdd.setOnClickListener {
            try {
                cacheState()
                if (viewModel.state.value?.type.equals(VENDOR_PAY)) {
                    if (viewModel.state.value?.supplier == null) {
                        throw Exception("Select a supplier")
                    }
                }
                viewModel.onTriggerEvent(PaymentEvents.NewPaymentCreate)
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

        paymentClear.setOnClickListener {
            clearWarning()
        }

//        paymentTypeTV.setOnClickListener {
//            paymentTypeTV.visibility = View.INVISIBLE
//            paymentSpinner.visibility = View.VISIBLE
//            paymentSpinner.performClick()
//        }

        val typeList = mutableListOf<String>()
        typeList.add(ReceivePaymentType.CASH_OUT)
        typeList.add(ReceivePaymentType.ELECTRICITY_BILL)
        typeList.add(ReceivePaymentType.RENT)
        typeList.add(ReceivePaymentType.SALARY)
        typeList.add(ReceivePaymentType.VENDOR_PAY)
        typeList.add(ReceivePaymentType.OTHER_EXPENSE)


        val kindAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            typeList.toTypedArray()
        )

        kindAdapter.setDropDownViewResource(
            android.R.layout
                .simple_spinner_dropdown_item
        )

        paymentTypeTV.setAdapter(kindAdapter)
        paymentTypeTV.setAdapter(kindAdapter)
        paymentTypeTV.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (typeList.get(position) == viewModel.state.value?.type) {
                    Log.d("AppDebug", "spinner return")
                    return
                }


                viewModel.onTriggerEvent(PaymentEvents.AddType(typeList.get(position)))
//                paymentTypeTV.setText(typeList.get(position))
//                paymentSpinner.visibility = View.INVISIBLE
//                paymentTypeTV.visibility = View.VISIBLE
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
//                paymentSpinner.visibility = View.INVISIBLE
//                paymentTypeTV.visibility = View.VISIBLE
            }

        })


        paymentSearch.setOnClickListener {
            (activity as CashRegisterActivity).navigatePaymentToSupplierFragment()
        }



        val dataPickerListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
//                2012-09-04 14:00:00+00:00
//                val date = dayOfMonth.toString() + "/" + (month + 1) + "/" + year
                val date = year.toString() + "-" + (month + 1) + "-" + dayOfMonth
                paymentDate.setText(date)
                Log.d(TAG, "Year " + year + " Month " + month + " Day " + dayOfMonth)
            }

        }


        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH)
        val date = calender.get(Calendar.DATE)
        val today = year.toString() + "-" + (month + 1) + "-" + date
        paymentDate.setText(today)

        paymentDate.setOnClickListener {
//            val builder = MaterialDatePicker.Builder.dateRangePicker()
//            val picker = builder.build()
//            picker.show(childFragmentManager, picker.toString())
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
                        viewModel.onTriggerEvent(PaymentEvents.OnRemoveHeadFromQueue)
                    }
                })

            Log.d(TAG, state.payment.toString())


            if (state.supplier != null) {
                paymentSearch.setText(state.supplier.agent_name)
            }

            if (state.type == ReceivePaymentType.VENDOR_PAY) {
                paymentSearch.visibility = View.VISIBLE
            }
            else {
                paymentSearch.visibility = View.GONE
//                if (state.customer != null) {
//                    viewModel.onTriggerEvent(PaymentEvents.AddSupplier(null))
//                }
            }
        })
    }


    fun cacheState() {
//        try {
            val date = paymentDate.text.toString()
//            val type = paymentTypeTV.text.toString()
        val type = viewModel.state.value?.type
            val amount = paymentAmount.text.toString().toFloat()
//            val balance = paymentBalance.text.toString().toFloat()
            val remark = paymentRemark1.text.toString() + " " + paymentRemark2.text.toString()


//            var result: String = ""
//            var lastIndex = date!!.lastIndex
//
//            while (lastIndex >= 0) {
//                result += date[lastIndex]
//                lastIndex--
//            }


        viewModel.onTriggerEvent(PaymentEvents.AddDate(date))
        viewModel.onTriggerEvent(PaymentEvents.AddType(type!!))
        viewModel.onTriggerEvent(PaymentEvents.AddAmount(amount))
//        viewModel.onTriggerEvent(PaymentEvents.AddBalance(balance))
        viewModel.onTriggerEvent(PaymentEvents.AddRemark(remark))

       /* val payment = Payment(
                pk = -1,
                room_id = -1,
                date = date.trim(),
                customer = -1,
                vendor = -1,
                type = type,
                total_amount = amount,
                balance = balance,
                remarks = remark,
                created_at = "",
                updated_at = "",
                customer_name = "",
                vendor_name = ""
            )
            viewModel.onTriggerEvent(PaymentEvents.CacheState(payment))*/
//        }
//        catch (e : Exception) {
//            MaterialDialog(requireContext())
//                .show{
//                    title(R.string.text_info)
//                    message(text = "Something is wrong")
//                    onDismiss {
//                    }
//                    cancelable(true)
//                }
//        }

    }



    override fun onDestroyView() {
        super.onDestroyView()
    }


    fun clearForm() {
        paymentDate.setText("")
//        paymentTypeTV.setText("")
        paymentAmount.text.clear()
        paymentBalance.text.clear()
        paymentRemark1.text.clear()
        paymentRemark2.text.clear()

        val payment = Payment(
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
        viewModel.onTriggerEvent(PaymentEvents.CacheState(payment))
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