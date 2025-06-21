package com.vanphong.foodnfit.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream

object ImageUtils {
    fun resizeImage(
        uri: Uri,
        context: Context,
        maxWidth: Int = 1024,
        quality: Int = 80
    ): File? {
        return try {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)

            val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
            val targetHeight = (maxWidth / aspectRatio).toInt()
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, maxWidth, targetHeight, true)

            val file = File(context.cacheDir, "resized_image_${System.currentTimeMillis()}.jpg")
            val out = FileOutputStream(file)
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
            out.flush()
            out.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}