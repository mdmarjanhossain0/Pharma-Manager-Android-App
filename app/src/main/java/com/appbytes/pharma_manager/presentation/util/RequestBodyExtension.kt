package com.appbytes.beautywallpaper.util.extentions

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream

/**
 * Write this to a file of [fileUri].
 *
 * This method is suspended, thus can be cancelled by parent.
 * \
 * @param onProgress will be invoked when the progress has been updated.
 */
suspend fun ResponseBody.writeToFile(fileUri: String,
                                     onProgress: ((Int) -> Unit)?): File = withContext(Dispatchers.IO) {
    val body = this@writeToFile
    val fileToSave = File(fileUri)

    val inputStream = body.byteStream()
    val outputStream = FileOutputStream(fileToSave)

    inputStream.useWith(outputStream) { `is`, os ->
        val buffer = ByteArray(2048)

        val fileSize = body.contentLength()
        var fileSizeDownloaded: Long = 0

        var progressToReport = 0

        while (true) {
            if (!isActive) {
//                Pasteur.info("DownloadService", "cancel on not active")
                throw CancellationException("Cancelled inside while")
            }

            val read = `is`.read(buffer)
            if (read == -1) {
                break
            }

            os.write(buffer, 0, read)
            fileSizeDownloaded += read.toLong()

            val progress = (fileSizeDownloaded / fileSize.toDouble() * 100).toInt()
            if (progress - progressToReport >= 5) {
                progressToReport = progress
                onProgress?.invoke(progressToReport)
            }
        }
    }
    return@withContext fileToSave
}