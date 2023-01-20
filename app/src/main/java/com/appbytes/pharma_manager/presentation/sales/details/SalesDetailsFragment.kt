package com.appbytes.pharma_manager.presentation.sales.details

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.appbytes.pharma_manager.business.domain.models.SalesOrderMedicine
import com.appbytes.pharma_manager.business.domain.util.StateMessageCallback
import com.appbytes.pharma_manager.presentation.sales.BaseSalesFragment
import com.appbytes.pharma_manager.presentation.sales.SalesActivity
import com.appbytes.pharma_manager.presentation.util.*
import com.appbytes.pharma_manager.presentation.util.SalesPdfWorker.Companion.Progress
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_sales_details.*
import kotlinx.coroutines.*
import java.io.File
import java.util.*

import com.appbytes.pharma_manager.R


@AndroidEntryPoint
class SalesDetailsFragment : BaseSalesFragment(), SalesDetailsAdapter.Interaction, OnCompleteCallback{


    private var recyclerAdapter: SalesDetailsAdapter? = null // can leak memory so need to null
    private val viewModel: SalesDetailsViewModel by viewModels()
//    private val viewModel : SalesCardViewModel by activityViewModels()
//    private val shareViewModel : SalesReturnViewModel by activityViewModels()
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
        return inflater.inflate(R.layout.fragment_sales_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        initRecyclerView()
        initUIClick()
        subscribeObservers()
        Log.d(TAG, "SalesPayNowFragment ViewModel " + viewModel.toString())
    }

    private fun initUIClick() {

        salesOrderDelete.setOnClickListener {
            if (viewModel.state.value?.order != null) {
                viewModel.onTriggerEvent(SalesDetailsEvents.DeleteOrder)
            }
            else {
                Toast.makeText(context, "Loading order...", Toast.LENGTH_SHORT).show()
            }
        }

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this){
//            viewModel.state.value = SalesCardState()
            findNavController().popBackStack()
            Log.d(TAG, "Fragment On Back Press Callback call")
        }

        createSalesOrder.setOnClickListener {
//            findNavController().navigate(R.id.action_salesDetailsFragment_to_salesCartFragment)
            val bundle = bundleOf("pk" to viewModel.state.value?.pk)
            Log.d(TAG, bundle.toString() + "             " + viewModel.state.value?.pk)
            findNavController().navigate(R.id.action_salesDetailsFragment_to_salesPayNowFragment3, bundle)
        }

        createSalesOrderReturn.setOnClickListener {
//            shareViewModel.onTriggerEvent(SalesReturnEvents.OrderDetails(viewModel.state.value?.order?.pk!!))
            (activity as SalesActivity).navigateSalesToSalesReturn(pk!!)
        }



        orderDetailsId.setText("# Order Id " + pk)

        printButton.setOnClickListener {
            try {
                if (uiCommunicationListener.isStoragePermissionGranted()) {
                    createPDF()
                }
            }
            catch (e : Exception) {
                e.printStackTrace()
                uiCommunicationListener.displayProgressBar(false)
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }




        shareButton.setOnClickListener {
            try {
                if (uiCommunicationListener.isStoragePermissionGranted()) {
                    sharePdf()
                }
            }
            catch (e : Exception) {
                e.printStackTrace()
                uiCommunicationListener.displayProgressBar(false)
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sharePdf() {
        val constraints = Constraints.Builder().setRequiresCharging(false).build()
        val inputData = Data.Builder()
            .putInt("pk", pk!!)
            .putBoolean("share", true)
            .build()

        val pdfWorker : WorkRequest =
            OneTimeWorkRequestBuilder<SalesPdfWorker>()
                .setConstraints(constraints)
                .setInputData(inputData)
                .build()

        WorkManager
            .getInstance(requireContext())
            .enqueue(pdfWorker)

        WorkManager.getInstance()
            .getWorkInfoByIdLiveData(pdfWorker.id)
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer { workInfo: WorkInfo? ->
                if (workInfo != null) {
                    val progress = workInfo.progress.getInt(Progress, 0)
                    if (progress < 100) {
                        uiCommunicationListener.displayProgressBar(true)
                    }
                    else {
                        uiCommunicationListener.displayProgressBar(false)
                    }
                    if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                        val path = workInfo.outputData.getString("path")
                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                        uiCommunicationListener.displayProgressBar(false)
                        Log.d(TAG, path.toString())
                        val uri = Uri.parse(path)
                        Log.d(TAG, "Print PDF from $uri")
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "application/pdf"
                        shareIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                        requireContext().startActivity(Intent.createChooser(shareIntent, "Share Invoice"))



//                        try {
//                            val file = File(path)
//                            Log.d(TAG, "File " + file.toString())
//                            if (file.exists()) {
//                                Log.d(TAG, "File Exist")
//                                printPdf(file)
//                            }
//                        }
//                        catch (e : Exception) {
//                            e.printStackTrace()
//                        }
                    }
                }
            })

        Log.d(TAG, "Create Pdf")
    }

    private fun createPDF() {
        val constraints = Constraints.Builder().setRequiresCharging(false).build()
        val inputData = Data.Builder()
            .putInt("pk", pk!!)
            .putBoolean("share", false)
            .build()

        val pdfWorker : WorkRequest =
            OneTimeWorkRequestBuilder<SalesPdfWorker>()
                .setConstraints(constraints)
                .setInputData(inputData)
                .build()

        WorkManager
            .getInstance(requireContext())
            .enqueue(pdfWorker)

        WorkManager.getInstance()
            .getWorkInfoByIdLiveData(pdfWorker.id)
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer { workInfo: WorkInfo? ->
                if (workInfo != null) {
                    val progress = workInfo.progress.getInt(Progress, 0)
                    if (progress < 100) {
                        uiCommunicationListener.displayProgressBar(true)
                    }
                    else {
                        uiCommunicationListener.displayProgressBar(false)
                    }
                    if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                        val path = workInfo.outputData.getString("path")
                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                        uiCommunicationListener.displayProgressBar(false)
                        Log.d(TAG, path.toString())
                        val file = File(path)
                        Log.d(TAG, "File " + file.toString())
                        if (file.exists()) {
                            Log.d(TAG, "File Exist")
                            printPdf(file)
                        }
                    }
                }
            })

        Log.d(TAG, "Create Pdf")
    }
    private fun printPdf(file: File) {
        val printManager = requireContext().getSystemService(Context.PRINT_SERVICE) as PrintManager
        try {
            val printAdapter = PdfDocumentAdapter(context,file.path )
            printManager.print("Document", printAdapter, PrintAttributes.Builder().build());
        }
        catch (e : Exception)
        {
            e.printStackTrace()
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
                        viewModel.onTriggerEvent(SalesDetailsEvents.OnRemoveHeadFromQueue)
                    }
                })

            if (state.order != null) {
                recyclerAdapter?.apply {
                    submitList(list = state.order?.sales_oder_medicines)
                }

                salesPaymentItemCount.setText("Items : " + state.order?.sales_oder_medicines?.size.toString())
                salesPaymentTotal.setText("Total : ৳" + state.order?.total_after_discount.toString())
                totalItem.setText("Total Item :  " + state.order.sales_oder_medicines?.size)

                if (state.order?.status == 0) {
                    createSalesOrder.visibility = View.VISIBLE
                    salesOrderDelete.visibility = View.VISIBLE
                }
                else {
                    createSalesOrder.visibility = View.GONE
                    salesOrderDelete.visibility = View.GONE
                    createSalesOrderReturn.visibility = View.VISIBLE
                }
                
                orderNo.setText("#Order No : " + state.order.pk)
            }

            if (state.order?.customer != null) {
                if (state.order?.customer_name != null) {
                    salesPaymentSearchView.setText("        " + state.order?.customer_name)
                }
                else {
                    salesPaymentSearchView.setText("        Customer has no name")
                }
            }
            else {
                salesPaymentSearchView.setText("      " + "Walk-In Customer")
            }

//            if (state.deleted) {
//                (activity as SalesActivity).onBackPressed()
//            }

//            totalItem.setText("Total Item :  " + state.order.sales_oder_medicines?.size)

        })
    }

    private  fun resetUI(){
        uiCommunicationListener.hideSoftKeyboard()
    }

    private fun initRecyclerView(){
        salesPaymentRvId.apply {
            layoutManager = LinearLayoutManager(this@SalesDetailsFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator)
            addItemDecoration(topSpacingDecorator)
            recyclerAdapter = SalesDetailsAdapter(this@SalesDetailsFragment)
            adapter = recyclerAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvent(SalesDetailsEvents.OrderDetails(pk!!))
    }


    override fun onDestroyView() {
        super.onDestroyView()
        recyclerAdapter = null
        uiCommunicationListener.displayProgressBar(false)
    }


    override fun onItemDelete(item: SalesOrderMedicine) {
    }



    fun pdfDialog() {
        MaterialDialog(requireContext())
            .show{
                title(R.string.are_you_sure)
                message(text = "Cart item will be dismiss")
                positiveButton(R.string.text_ok){
//                    viewModel.state.value = SalesCardState()
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

    override fun delete() {
        findNavController().popBackStack()
    }
}