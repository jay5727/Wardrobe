package com.jay.nearbysample.utilities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import androidx.exifinterface.media.ExifInterface
import java.io.IOException
import java.io.InputStream
import java.util.*

object Utility {

    fun getRandomNumberFrom(min: Int, max: Int): Int {
        val foo = Random()
        return foo.nextInt(max + 1 - min) + min
    }

    fun getScaledBitmap(
        context: Context,
        uri: Uri
    ): Bitmap? {
        var bitmap: Bitmap? = null
        var inputStream: InputStream?
        return try {
            val mContentResolver = context.contentResolver
            inputStream = mContentResolver.openInputStream(uri)

            val opt = BitmapFactory.Options()
            opt.inJustDecodeBounds = true
            BitmapFactory.decodeStream(inputStream, null, opt)
            inputStream!!.close()
            inputStream = mContentResolver.openInputStream(uri)
            bitmap = BitmapFactory.decodeStream(inputStream)
            bitmap = rotateImageIfRequired(context, bitmap, uri)
            inputStream!!.close()
            bitmap
        } catch (e: IOException) {
            bitmap
        }
    }

    @Throws(IOException::class)
    fun rotateImageIfRequired(
        context: Context,
        bitmap: Bitmap?,
        selectedImage: Uri
    ): Bitmap? {
        val input =
            context.contentResolver.openInputStream(selectedImage)
        val ei: ExifInterface
        ei =
            if (Build.VERSION.SDK_INT > 23) ExifInterface(input!!) else ExifInterface(selectedImage.path!!)

        return when (ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flipImage(
                bitmap,
                horizontal = true,
                vertical = false
            )
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> flipImage(
                bitmap,
                horizontal = false,
                vertical = true
            )
            else -> bitmap
        }
    }

    private fun flipImage(bitmap: Bitmap?, horizontal: Boolean, vertical: Boolean): Bitmap {
        val matrix = Matrix()
        matrix.preScale((if (horizontal) -1 else 1).toFloat(), (if (vertical) -1 else 1).toFloat())
        return Bitmap.createBitmap(bitmap!!, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun rotateImage(img: Bitmap?, degree: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedImg =
            Bitmap.createBitmap(img!!, 0, 0, img.width, img.height, matrix, true)
        img.recycle()
        return rotatedImg
    }
}