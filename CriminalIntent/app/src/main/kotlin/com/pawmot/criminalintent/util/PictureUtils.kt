package com.pawmot.criminalintent.util

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point

object PictureUtils {
    // FIXME: use implementation from https://developer.android.com/training/camera/photobasics.html#TaskScalePhoto
    fun getScaledBitmap(path: String, destWidth: Int, destHeight: Int): Bitmap? {
        var opts = BitmapFactory.Options()
        opts.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, opts)

        val srcWidth = opts.outWidth
        val srcHeight = opts.outHeight

        var inSampleSize = 1L
        if (srcHeight > destHeight || srcWidth > destWidth) {
            if (srcWidth > srcHeight) {
                inSampleSize = Math.round((srcHeight / destHeight).toDouble())
            } else {
                inSampleSize = Math.round((srcWidth / destWidth).toDouble())
            }
        }

        opts = BitmapFactory.Options()
        opts.inSampleSize = inSampleSize.toInt()

        return BitmapFactory.decodeFile(path, opts)
    }

    fun getScaledBitmap(path: String, activity: Activity): Bitmap? {
        val size = Point()
        activity.windowManager.defaultDisplay.getSize(size)
        return getScaledBitmap(path, size.x, size.y)
    }
}