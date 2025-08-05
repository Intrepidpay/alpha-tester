package com.example.aichatassistant.data

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.aichatassistant.utils.CompatibilityUtils
import java.io.File
import java.io.InputStream
import java.io.OutputStream

class MediaRepository(private val context: Context) {

    fun saveImage(inputStream: InputStream): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveWithMediaStore(inputStream)
        } else {
            saveLegacy(inputStream)
        }
    }

    @TargetApi(Build.VERSION_CODES.Q)
    private fun saveWithMediaStore(inputStream: InputStream): Uri {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/WhatsAppAI")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?: throw Exception("Failed to create MediaStore entry")

        resolver.openOutputStream(uri)?.use { output ->
            inputStream.copyTo(output)
        }

        contentValues.clear()
        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(uri, contentValues, null, null)

        return uri
    }

    private fun saveLegacy(inputStream: InputStream): Uri {
        val storageDir = File(
            CompatibilityUtils.getStorageDir(context, Environment.DIRECTORY_PICTURES),
            "WhatsAppAI"
        ).apply { mkdirs() }

        val file = File(storageDir, "IMG_${System.currentTimeMillis()}.jpg")
        file.outputStream().use { output ->
            inputStream.copyTo(output)
        }

        return Uri.fromFile(file)
    }
}