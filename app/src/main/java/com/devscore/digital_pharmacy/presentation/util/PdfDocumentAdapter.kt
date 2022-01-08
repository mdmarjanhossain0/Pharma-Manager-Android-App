package com.devscore.digital_pharmacy.presentation.util

import android.content.Context
import android.print.PrintDocumentAdapter.WriteResultCallback

import android.os.ParcelFileDescriptor

import android.print.PrintDocumentInfo

import android.os.Bundle
import android.os.CancellationSignal
import android.print.PageRange

import android.print.PrintDocumentAdapter.LayoutResultCallback

import android.print.PrintAttributes

import android.print.PrintDocumentAdapter
import java.io.*
import java.lang.Exception


class PdfDocumentAdapter(ctxt: Context?, pathName: String) :
    PrintDocumentAdapter() {
    var context: Context? = null
    var pathName = ""
    override fun onLayout(
        printAttributes: PrintAttributes,
        printAttributes1: PrintAttributes,
        cancellationSignal: CancellationSignal,
        layoutResultCallback: LayoutResultCallback,
        bundle: Bundle?
    ) {
        if (cancellationSignal.isCanceled()) {
            layoutResultCallback.onLayoutCancelled()
        } else {
            val builder = PrintDocumentInfo.Builder(" file name")
            builder.setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                .build()
            layoutResultCallback.onLayoutFinished(
                builder.build(),
                printAttributes1 != printAttributes
            )
        }
    }

    override fun onWrite(
        pageRanges: Array<PageRange?>?,
        parcelFileDescriptor: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal,
        writeResultCallback: WriteResultCallback
    ) {
        var `in`: InputStream? = null
        var out: OutputStream? = null
        try {
            val file = File(pathName)
            `in` = FileInputStream(file)
            out = FileOutputStream(parcelFileDescriptor.fileDescriptor)
            val buf = ByteArray(16384)
            var size: Int
            while (`in`.read(buf).also { size = it } >= 0
                && !cancellationSignal.isCanceled()) {
                out.write(buf, 0, size)
            }
            if (cancellationSignal.isCanceled()) {
                writeResultCallback.onWriteCancelled()
            } else {
                writeResultCallback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
            }
        } catch (e: Exception) {
            writeResultCallback.onWriteFailed(e.message)
            e.printStackTrace()
        } finally {
            try {
                `in`!!.close()
                out!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    init {
        context = ctxt
        this.pathName = pathName
    }
}