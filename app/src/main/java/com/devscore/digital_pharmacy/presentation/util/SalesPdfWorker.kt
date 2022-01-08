package com.devscore.digital_pharmacy.presentation.util

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.print.PrintAttributes
import android.print.PrintManager
import android.util.Log
import androidx.core.content.FileProvider
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.devscore.digital_pharmacy.business.datasource.cache.AppDatabase
import com.devscore.digital_pharmacy.business.datasource.cache.account.AccountDao
import com.devscore.digital_pharmacy.business.datasource.cache.account.toAccount
import com.devscore.digital_pharmacy.business.datasource.cache.auth.AuthTokenDao
import com.devscore.digital_pharmacy.business.datasource.cache.auth.toAuthToken
import com.devscore.digital_pharmacy.business.datasource.cache.customer.toCustomer
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.toLocalMedicine
import com.devscore.digital_pharmacy.business.datasource.cache.purchases.toPurchasesOrder
import com.devscore.digital_pharmacy.business.datasource.cache.sales.SalesDao
import com.devscore.digital_pharmacy.business.datasource.cache.sales.toSalesOder
import com.devscore.digital_pharmacy.business.datasource.cache.supplier.toSupplier
import com.devscore.digital_pharmacy.business.datasource.datastore.AppDataStore
import com.devscore.digital_pharmacy.business.domain.models.Account
import com.devscore.digital_pharmacy.business.domain.models.AuthToken
import com.devscore.digital_pharmacy.business.domain.models.SalesOrder
import com.devscore.digital_pharmacy.business.interactors.customer.CreateFailureCustomerInteractor
import com.devscore.digital_pharmacy.business.interactors.inventory.local.AddFailureMedicineInteractor
import com.devscore.digital_pharmacy.business.interactors.purchases.CreateFailurePurchasesOrderInteractor
import com.devscore.digital_pharmacy.business.interactors.sales.CreateFailureSalesInteractor
import com.devscore.digital_pharmacy.business.interactors.supplier.CreateFailureSuppllierInteractor
import com.devscore.digital_pharmacy.presentation.session.SessionManager
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.VerticalAlignment
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@HiltWorker
class SalesPdfWorker @AssistedInject
constructor(
    @Assisted
    context: Context,
    @Assisted
    parameters: WorkerParameters,
    var sessionManager : SessionManager,
    val appDataStoreManager : AppDataStore,
    val accountDao: AccountDao,
    val authTokenDao: AuthTokenDao,
    val salesDao: SalesDao
) : CoroutineWorker(context, parameters) {

    val TAG = "PdfWorker"

    companion object {
        const val Progress = "progress"
        private const val delayDuration = 1L
    }

    override suspend fun doWork(): Result {
        val firstUpdate = workDataOf(Progress to 0)
        val lastUpdate = workDataOf(Progress to 100)


        setProgress(firstUpdate)
        val pk = inputData.getInt("pk", -1)
        if (pk > 0) {
            val order = salesDao.getSalesOrder(pk).toSalesOder()
            val account = accountDao.searchByEmail(appDataStoreManager.readValue(DataStoreKeys.PREVIOUS_AUTH_USER)!!)?.toAccount()
            val path = createPDF(order, account!!)
            setProgress(lastUpdate)
            val outputData = workDataOf("path" to path)
            return Result.success(outputData)
        }
        else {
            setProgress(lastUpdate)
            return Result.failure()
        }
    }


    private fun createPDF(order : SalesOrder, account : Account) : String {

        val calender = Calendar.getInstance()
        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH)
        val date = calender.get(Calendar.DATE)
        val today = year.toString() + "-" + (month + 1) + "-" + date

        // Create a file
        val path = applicationContext.externalCacheDir?.absolutePath
        val file = File(path, "Invoice.pdf")
        val outpurStream = FileOutputStream(file)

        // Create a blank pdf with document
        val pdfWriter = PdfWriter(file)
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument)
        pdfDocument.defaultPageSize = PageSize.A4









        val shopName = Paragraph(account.shop_name)
        shopName.setTextAlignment(TextAlignment.CENTER)
        shopName.setBold()
        shopName.setFontSize(51f)
        document.add(shopName)
        document.add(Paragraph("\n \n \n"))

        val dataTableColumn = floatArrayOf(200f, 120f, 120f, 120f)
        val dataTable = Table(dataTableColumn)

//        table.addCell(Cell().add(Paragraph("Test")))
//        table.addCell(Cell().add(Paragraph("Test")))
//        table.addCell(Cell().add(Paragraph("Test")))
//        table.addCell(Cell().add(Paragraph("Test")))
//
//        table.addCell(Cell().add(Paragraph("Test")))
//        table.addCell(Cell().add(Paragraph("Test")))

        // First Row
        if (order.customer == null) {
            val customerName = Paragraph("Customer Name : Walk-In Customer").setTextAlignment(
                TextAlignment.LEFT)
            val cell = Cell(1, 4).setBorder(Border.NO_BORDER)
            cell.add(customerName)
            dataTable.addCell(cell)
        }
        else {
            val customerName = Paragraph("Customer Name : " + order.customer_name).setTextAlignment(
                TextAlignment.LEFT)
            val cell = Cell(1, 4).setBorder(Border.NO_BORDER)
            cell.add(customerName)
            dataTable.addCell(cell)
        }

        // Second Row
        val orderId = Paragraph("#Order Id : " + order.pk).setTextAlignment(TextAlignment.LEFT)
        val orderIdCell = Cell(1, 2).setBorder(Border.NO_BORDER)
        orderIdCell.add(orderId)
        dataTable.addCell(orderIdCell)
//        orderInfoTable.addCell(Cell().add(Paragraph("")))
        if (order.created_at != null) {
            val timeZone = TimeZone.getTimeZone("Asia/Dhaka")
            val dateFormatter = SimpleDateFormat("dd/MM/yyyy")
            val sourceFormat = SimpleDateFormat("dd/MM/yyyy")
            val destFormat = SimpleDateFormat("dd/MM/yyyy")
            sourceFormat.timeZone = timeZone
            val convertedDate = sourceFormat.parse(order.created_at)
            val newDate = destFormat.format(convertedDate)
            val orderDate = Paragraph("Date : " + newDate).setTextAlignment(TextAlignment.RIGHT)
            val orderDateCell = Cell(1, 2).setBorder(Border.NO_BORDER)
            orderDateCell.add(orderDate)
            dataTable.addCell(orderDateCell)
        }
        else {
            val orderDate = Paragraph("Date : ").setTextAlignment(TextAlignment.RIGHT)
            val orderDateCell = Cell(1, 2).setBorder(Border.NO_BORDER)
            orderDateCell.add(orderDate)
            dataTable.addCell(orderDateCell)
        }




        // Data Table Header
        val brandName = Paragraph("Brand Name")
        brandName.setBold()
        brandName.setTextAlignment(TextAlignment.CENTER)
        dataTable.addCell(Cell().add(brandName))

        val unit = Paragraph("Unit")
        unit.setBold()
        unit.setTextAlignment(TextAlignment.CENTER)
        dataTable.addCell(Cell().add(unit))

        val mrp = Paragraph("MRP")
        mrp.setBold()
        mrp.setTextAlignment(TextAlignment.CENTER)
        dataTable.addCell(Cell().add(mrp))

        val subTotal = Paragraph("Sub Total")
        subTotal.setBold()
        subTotal.setTextAlignment(TextAlignment.CENTER)
        dataTable.addCell(Cell().add(subTotal))


        // Data Table
        for (item in order.sales_oder_medicines!!) {
            dataTable.addCell(Cell().add(Paragraph(item.brand_name).setTextAlignment(TextAlignment.LEFT)))
            dataTable.addCell(Cell().add(Paragraph( item.quantity.toString() + "   (" + item.unit_name + ") "))).setTextAlignment(
                TextAlignment.CENTER)
            dataTable.addCell(Cell().add(Paragraph((item.mrp.toString() + " ৳ ").toString()))).setTextAlignment(
                TextAlignment.CENTER).setPaddingRight(20f)
            dataTable.addCell(Cell().add(Paragraph(item.amount.toString() + " ৳ "))).setTextAlignment(
                TextAlignment.CENTER).setPaddingRight(20f)
        }






        // Data Table Total
        val itemCount = Paragraph("Item : " + order.sales_oder_medicines?.size).setTextAlignment(
            TextAlignment.LEFT)
        val itemCountCell = Cell(1, 2).setBorder(Border.NO_BORDER)
        itemCountCell.add(itemCount)
        dataTable.addCell(itemCountCell)

        val total = Paragraph("Total : ").setTextAlignment(TextAlignment.RIGHT)
        val totalCell = Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT)
        totalCell.add(total)
        dataTable.addCell(totalCell)

        val totalAmount = Paragraph(order.total_amount.toString() + " ৳ ").setTextAlignment(
            TextAlignment.CENTER)
        val totalAmountCell = Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER)
        totalAmountCell.add(totalAmount)
        dataTable.addCell(totalAmountCell)

        val paid = Paragraph("Paid : " + order.paid_amount + " ৳ ").setTextAlignment(TextAlignment.LEFT)
        val paidCell = Cell(1, 2).setBorder(Border.NO_BORDER)
        paidCell.add(paid)
        dataTable.addCell(paidCell)

        val discount = Paragraph("Discount : ").setTextAlignment(TextAlignment.RIGHT)
        val discountCell = Cell().setBorder(Border.NO_BORDER)
        discountCell.add(discount)
        dataTable.addCell(discountCell)
        if (order.is_discount_percent) {
            val discountAmount = Paragraph((order.discount.toString() + " % ").toString()).setTextAlignment(
                TextAlignment.CENTER)
            val discountAmountCell = Cell().setBorder(Border.NO_BORDER).setTextAlignment(
                TextAlignment.CENTER)
            discountAmountCell.add(discountAmount)
            dataTable.addCell(discountAmountCell)
        }
        else {
            val discountAmount = Paragraph((order.discount.toString() + " ৳ ").toString()).setTextAlignment(
                TextAlignment.CENTER)
            val discountAmountCell = Cell().setBorder(Border.NO_BORDER).setTextAlignment(
                TextAlignment.CENTER)
            discountAmountCell.add(discountAmount)
            dataTable.addCell(discountAmountCell)
        }








        val totalAfterDiscount = Paragraph("Total After Discount : ").setTextAlignment(TextAlignment.RIGHT)
        val totalAfterDiscountCell = Cell(1, 3).setBorder(Border.NO_BORDER).setTextAlignment(
            TextAlignment.CENTER)
        totalAfterDiscountCell.add(totalAfterDiscount)
        dataTable.addCell(totalAfterDiscountCell)

        val totalAfterDiscountAmount = Paragraph(order.total_after_discount.toString()).setTextAlignment(
            TextAlignment.CENTER)
        val totalAfterDiscountAmountCell = Cell().setBorder(Border.NO_BORDER).setTextAlignment(
            TextAlignment.CENTER)
        totalAfterDiscountAmountCell.add(totalAfterDiscountAmount)
        dataTable.addCell(totalAfterDiscountAmountCell)


        document.add(dataTable)




        document.add(Paragraph("\n \n \n"))
        stampPageFooter(document, today)
        document.close()
        Log.d(TAG, "Create Pdf")
//        printPdf(file)
        if (inputData.getBoolean("share", false)) {
            return FileProvider.getUriForFile(applicationContext, applicationContext.packageName + ".fileprovider", file).toString()
        }
        return Uri.fromFile(file).path!!
    }

    private fun printPdf(file: File) {
        val printManager = applicationContext.getSystemService(Context.PRINT_SERVICE) as PrintManager
        try {
            val printAdapter = PdfDocumentAdapter(applicationContext,file.path )
            printManager.print("Document", printAdapter, PrintAttributes.Builder().build());
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun stampPageFooter(doc : Document, name : String) {
        val numberOfPages = doc.pdfDocument.numberOfPages
        val pdfDocument = doc.pdfDocument
        for (i in 1..numberOfPages){
            val page = pdfDocument.getPage(i)
            val pageSize = page.getPageSize()
            var pageX = pageSize.getRight() - doc.getRightMargin() - 40
            var pageY = pageSize.getBottom() + 30
            // Write x of y to the right bottom
            val p = Paragraph (String.format("Page %s of %s", i, numberOfPages))
            doc.showTextAligned(
                p,
                pageX,
                pageY,
                i,
                TextAlignment.LEFT,
                VerticalAlignment.BOTTOM,
                0f
            )
            // write name to the left
            pageX = pageSize.getLeft() + doc.getLeftMargin()
            pageY = pageSize.getBottom() + 30
            val para = Paragraph ("Print : " + name).setMarginTop(10f)
            doc.showTextAligned(
                para,
                pageX,
                pageY,
                i,
                TextAlignment.LEFT,
                VerticalAlignment.BOTTOM,
                0f
            )

//            pageX = pageSize.getLeft() + doc.getLeftMargin() + 50
//            pageY = pageSize.getBottom() + 30
//            val from = Paragraph ("Digital Pharmacy").setMarginTop(10f)
//            doc.showTextAligned(
//                from,
//                pageX,
//                pageY,
//                i,
//                TextAlignment.CENTER,
//                VerticalAlignment.BOTTOM,
//                0f
//            )
        }
    }
}