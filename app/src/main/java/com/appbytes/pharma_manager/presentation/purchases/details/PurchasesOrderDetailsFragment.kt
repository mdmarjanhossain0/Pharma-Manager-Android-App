package com.appbytes.pharma_manager.presentation.purchases.details

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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.appbytes.pharma_manager.business.domain.models.PurchasesOrderMedicine
import com.appbytes.pharma_manager.business.domain.util.StateMessageCallback
import com.appbytes.pharma_manager.presentation.purchases.PurchasesActivity
import com.appbytes.pharma_manager.presentation.purchases.purchasesreturn.PurchasesReturnViewModel
import com.appbytes.pharma_manager.presentation.sales.BaseSalesFragment
import com.appbytes.pharma_manager.presentation.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_sales_details.*
import java.io.File



import com.appbytes.pharma_manager.R

@AndroidEntryPoint
class PurchasesOrderDetailsFragment : BaseSalesFragment(), PurchasesDetailsAdapter.Interaction, OnCompleteCallback{


    private var recyclerAdapter: PurchasesDetailsAdapter? = null // can leak memory so need to null
//    private val viewModel : PurchasesCartViewModel by activityViewModels()
    private val viewModel : OrderDetailsViewModel by viewModels()
    private val shareViewModel : PurchasesReturnViewModel by activityViewModels()
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







//        val callback = requireActivity().onBackPressedDispatcher.addCallback(this){
//            viewModel.state.value = OrderDetailsState()
//            findNavController().popBackStack()
//            Log.d(TAG, "Fragment On Back Press Callback call")
//        }


        createSalesOrder.setOnClickListener {
//            findNavController().navigate(R.id.action_purchasesOrderDetailsFragment_to_purchasesCartFragment)
            val bundle = bundleOf("pk" to viewModel.state.value?.pk)
            findNavController().navigate(R.id.action_purchasesOrderDetailsFragment_to_purchasesPaymentFragment2, bundle)
        }

        createSalesOrderReturn.setOnClickListener {
//            shareViewModel.onTriggerEvent(PurchasesReturnEvents.OrderDetails(viewModel.state.value?.order?.pk!!))
            (activity as PurchasesActivity).navigatePurchasesToPurchasesReturnFragment(viewModel.state.value?.pk!!)
        }

        salesOrderDelete.setOnClickListener {
            viewModel.onTriggerEvent(OrderDetailsEvents.DeleteOrder)
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
            OneTimeWorkRequestBuilder<PurchasesPdfWorker>()
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
                    val progress = workInfo.progress.getInt(SalesPdfWorker.Progress, 0)
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
            OneTimeWorkRequestBuilder<PurchasesPdfWorker>()
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
                    val progress = workInfo.progress.getInt(SalesPdfWorker.Progress, 0)
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
        viewModel.state.observe(viewLifecycleOwner, { state ->

            uiCommunicationListener.displayProgressBar(state.isLoading)

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(OrderDetailsEvents.OnRemoveHeadFromQueue)
                    }
                })

            if (state.order != null) {
                recyclerAdapter?.apply {
                    submitList(list = state.order?.purchases_order_medicines)
                }

                salesPaymentItemCount.setText("Items : " + state.order?.purchases_order_medicines?.size.toString())
                salesPaymentTotal.setText("Total : ৳" + state.order?.total_after_discount.toString())

                if (state.order?.vendor != null) {
                    if (state.order?.company != null) {
                        salesPaymentSearchView.setText("        " + state.order?.company)
                    }
                    else {
                        salesPaymentSearchView.setText("        Supplier has no name")
                    }
                }
                else {
                    salesPaymentSearchView.setText("      " + "Walk-In Supplier")
                }
                orderNo.setText("#Order No : " + state.order.pk)

                if (state.order?.status == 0) {
                    createSalesOrder.visibility = View.VISIBLE
                    salesOrderDelete.visibility = View.VISIBLE
                }
                else {
                    createSalesOrder.visibility = View.GONE
                    salesOrderDelete.visibility = View.GONE
                    createSalesOrderReturn.visibility = View.VISIBLE
                }

                totalItem.setText("Total Item :  " + state.order?.purchases_order_medicines?.size)
            }



//            if (state.deleted) {
////                findNavController().navigate(R.id.action_purchasesOrderDetailsFragment_to_purchaseFragment)
//                (activity as PurchasesActivity).onBackPressed()
//            }

        })
    }

    private  fun resetUI(){
        uiCommunicationListener.hideSoftKeyboard()
    }

    private fun initRecyclerView(){
        salesPaymentRvId.apply {
            layoutManager = LinearLayoutManager(this@PurchasesOrderDetailsFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator)
            addItemDecoration(topSpacingDecorator)
            recyclerAdapter = PurchasesDetailsAdapter(this@PurchasesOrderDetailsFragment)
            adapter = recyclerAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onTriggerEvent(OrderDetailsEvents.OrderDetails(pk!!))
    }


    override fun onDestroyView() {
        super.onDestroyView()
        recyclerAdapter = null
    }


    override fun onItemDelete(item: PurchasesOrderMedicine) {
    }



    fun backPressWarning() {
        MaterialDialog(requireContext())
            .show{
                title(R.string.are_you_sure)
                message(text = "Cart item will be dismiss")
                positiveButton(R.string.text_ok){
                    viewModel.state.value = OrderDetailsState()
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