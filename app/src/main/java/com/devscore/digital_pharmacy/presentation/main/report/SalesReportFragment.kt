package com.devscore.digital_pharmacy.presentation.main.report

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.devscore.digital_pharmacy.R
import com.devscore.digital_pharmacy.business.datasource.network.inventory.InventoryApiService
import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.models.MedicineUnits
import com.devscore.digital_pharmacy.business.domain.models.Report
import com.devscore.digital_pharmacy.business.domain.util.StateMessageCallback
import com.devscore.digital_pharmacy.presentation.cashregister.receive.ReceiveEvents
import com.devscore.digital_pharmacy.presentation.inventory.BaseInventoryFragment
import com.devscore.digital_pharmacy.presentation.inventory.InventoryActivity
import com.devscore.digital_pharmacy.presentation.inventory.local.LocalAdapter
import com.devscore.digital_pharmacy.presentation.inventory.local.LocalMedicineEvents
import com.devscore.digital_pharmacy.presentation.inventory.local.LocalMedicineViewModel
import com.devscore.digital_pharmacy.presentation.main.BaseMainFragment
import com.devscore.digital_pharmacy.presentation.util.DateTime
import com.devscore.digital_pharmacy.presentation.util.ReceivePaymentType
import com.devscore.digital_pharmacy.presentation.util.TopSpacingItemDecoration
import com.devscore.digital_pharmacy.presentation.util.processQueue
import com.google.gson.Gson
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_local.*
import kotlinx.android.synthetic.main.fragment_sales_report.*
import kotlinx.android.synthetic.main.fragment_vendor_receive.*
import kotlinx.android.synthetic.main.inventory_details_dialog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class SalesReportFragment : BaseMainFragment(),
    ReportAdapter.Interaction {


    val ALL = "All"
    val INCOME = "Income"
    val EXPENSE = "Expense"
    val SALES = "Sales"
    val PURCHASES = "Purchases"
    val SALES_RETURN = "Sales Return"
    val PURCHASES_RETURN = "Purchases Return"
    val RECEIVE = "Receive"
    val PAYMENT = "Payment"
    val DUE = "Due"

    private var recyclerAdapter: ReportAdapter? = null // can leak memory so need to null
    private val viewModel: ReportViewModel by viewModels()


    private lateinit var datePicker : DatePickerDialog





    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_sales_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        initRecyclerView()
        initUIClick()
        subscribeObservers()
    }

    private fun initUIClick() {

        val typeList = mutableListOf<String>()
        typeList.add(ALL)
        typeList.add(INCOME)
        typeList.add(EXPENSE)
        typeList.add(SALES)
        typeList.add(PURCHASES)
        typeList.add(SALES_RETURN)
        typeList.add(PURCHASES_RETURN)
        typeList.add(RECEIVE)
        typeList.add(PAYMENT)
        typeList.add(DUE)


        val kindAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            typeList.toTypedArray()
        )

        kindAdapter.setDropDownViewResource(
            android.R.layout
                .simple_spinner_dropdown_item
        )

        reportSpinner.setAdapter(kindAdapter)
        reportSpinner.setAdapter(kindAdapter)
        reportSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (typeList.get(position) == viewModel.state.value?.query) {
                    Log.d("AppDebug", "spinner return")
                    return
                }


                viewModel.onTriggerEvent(ReportEvents.UpdateQuery(typeList.get(position)))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        })


        val fromDatePickerListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                val date = year.toString() + "-" + (month + 1) + "-" + dayOfMonth
                fromDateTvId.setText(dayOfMonth.toString() + " " + DateTime.getMonth(month + 1)+"," + " " + year)
                viewModel.onTriggerEvent(ReportEvents.UpdateStart(date))
                Log.d(TAG, "Year " + year + " Month " + month + " Day " + dayOfMonth)
            }

        }

        val toDatePickerListerner = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                val date = year.toString() + "-" + (month + 1) + "-" + dayOfMonth
                toDateTvId.setText(dayOfMonth.toString() + " " + DateTime.getMonth(month + 1)+"," + " " + year)
                viewModel.onTriggerEvent(ReportEvents.UpdateEnd(date))
                Log.d(TAG, "Year " + year + " Month " + month + " Day " + dayOfMonth)
            }

        }


        fromDateImgId.setOnClickListener {
            val calender = Calendar.getInstance()
            val year = calender.get(Calendar.YEAR)
            val monty = calender.get(Calendar.MONTH)
            val date = calender.get(Calendar.DATE)
            datePicker = DatePickerDialog(requireContext(),fromDatePickerListener, year, monty, date)
            datePicker.show()
        }

        toDateImgId.setOnClickListener {
            val calender = Calendar.getInstance()
            val year = calender.get(Calendar.YEAR)
            val monty = calender.get(Calendar.MONTH)
            val date = calender.get(Calendar.DATE)
            datePicker = DatePickerDialog(requireContext(),toDatePickerListerner, year, monty, date)
            datePicker.show()
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
                        viewModel.onTriggerEvent(ReportEvents.OnRemoveHeadFromQueue)
                    }
                })

            recyclerAdapter?.apply {
                submitList(reportList = state.reportList, state.isLoading, state.isQueryExhausted)
            }
        })
    }

    private  fun resetUI(){
//        uiCommunicationListener.hideSoftKeyboard()
//        focusableView.requestFocus()
    }

    private fun initRecyclerView(){
        salesReportRvId.apply {
            layoutManager = LinearLayoutManager(this@SalesReportFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerAdapter = ReportAdapter(this@SalesReportFragment)
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    Log.d(TAG, "onScrollStateChanged: exhausted? ${viewModel.state.value?.isQueryExhausted}")
                    if (
                        lastPosition == recyclerAdapter?.itemCount?.minus(1)
                        && viewModel.state.value?.isLoading == false
                        && viewModel.state.value?.isQueryExhausted == false
                    ) {
                        Log.d(TAG, "GlobalFragment: attempting to load next page...")
                        viewModel.onTriggerEvent(ReportEvents.NextPage)
                    }
                }
            })
            adapter = recyclerAdapter
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        recyclerAdapter = null
    }

    override fun onItemSelected(position: Int, item: Report) {
    }

    override fun onItemReturnSelected(position: Int, item: Report) {
    }

    override fun onItemDeleteSelected(position: Int, item: Report) {
    }

    override fun restoreListPosition() {
    }

    override fun nextPage() {
//        viewModel.onTriggerEvent(LocalMedicineEvents.NewLocalMedicineSearch)
    }
}