package com.liverowing.android.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import java.io.IOException
import android.os.Build

@Throws(IOException::class)
fun Bitmap.rotateImageIfRequired(context: Context, uri: Uri): Bitmap {

    val input = context.getContentResolver().openInputStream(uri)
    val ei: ExifInterface
    if (Build.VERSION.SDK_INT > 23)
        ei = ExifInterface(input)
    else
        ei = ExifInterface(uri.getPath())
    val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)

    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> return this.rotateImage(90F)
        ExifInterface.ORIENTATION_ROTATE_180 -> return this.rotateImage(180F)
        ExifInterface.ORIENTATION_ROTATE_270 -> return this.rotateImage(270F)
        else -> return this
    }
}

fun Bitmap.rotateImage(degree: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degree)
    val rotatedImg = Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
    this.recycle()
    return rotatedImg
}

fun Bitmap.getResizedBitmap(maxSize: Int): Bitmap {
    var width = this.width
    var height = this.height

    val bitmapRatio = width.toFloat() / height.toFloat()
    if (bitmapRatio > 0) {
        width = maxSize
        height = (width / bitmapRatio).toInt()
    } else {
        height = maxSize
        width = (height * bitmapRatio).toInt()
    }
    return Bitmap.createScaledBitmap(this, width, height, true)
}