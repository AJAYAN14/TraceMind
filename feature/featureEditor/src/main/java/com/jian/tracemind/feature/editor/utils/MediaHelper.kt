package com.jian.tracemind.feature.editor.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object MediaHelper {
    fun copyImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val imagesDir = File(context.filesDir, "images")
            if (!imagesDir.exists()) {
                imagesDir.mkdirs()
            }
            val fileName = "img_${UUID.randomUUID()}.jpg"
            val destFile = File(imagesDir, fileName)
            
            val outputStream = FileOutputStream(destFile)
            inputStream.copyTo(outputStream)
            
            inputStream.close()
            outputStream.close()
            
            destFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
