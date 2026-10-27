package com.example.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileOutputStream

object ImageSaver {

    private const val TAG = "ImageSaver"

    /**
     * Saves the AI hairstyle transformation result to the user's device photo gallery.
     * Complies with Android 10+ scoped storage policies.
     */
    fun saveBitmapToGallery(context: Context, bitmap: Bitmap, styleName: String): Uri? {
        val sanitizedName = styleName.replace(Regex("[^a-zA-Z0-9]"), "_")
        val filename = "HairStyleAI_${sanitizedName}_${System.currentTimeMillis()}.jpg"

        return try {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/HairStyleAI")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }

            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (imageUri != null) {
                resolver.openOutputStream(imageUri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(imageUri, contentValues, null, null)
                }
                Log.d(TAG, "Successfully saved hairstyle transformation to $imageUri")
            }
            imageUri
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save image to gallery", e)
            null
        }
    }

    /**
     * Creates a temporary cache file for sharing without permanently storing the photo.
     */
    fun getShareableCacheFile(context: Context, bitmap: Bitmap): File? {
        return try {
            val cacheDir = File(context.cacheDir, "shared_looks").apply { mkdirs() }
            val file = File(cacheDir, "makeover_preview.jpg")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            file
        } catch (e: Exception) {
            Log.e(TAG, "Failed creating shareable cache file", e)
            null
        }
    }
}
