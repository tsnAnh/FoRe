package dev.tsnanh.fore.util.extension

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

fun ContentResolver.getFilePath(uri: Uri): String {
    var name = ""

    val cursor = query(uri, null, null, null, null)
    cursor?.use {
        it.moveToFirst()
        name = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
    }

    return name
}

fun Uri?.toBitmap(context: Context): Bitmap? {
    return if (this != null) {
        try {
            val descriptor = context.contentResolver.openFileDescriptor(this, "r")

            val bitmap = BitmapFactory.decodeStream(FileInputStream(descriptor?.fileDescriptor))

            Bitmap.createScaledBitmap(bitmap, 224, 224, false)
        } catch (e: Exception) {
            null
        }
    } else {
        null
    }
}


