package com.pawmot.photogallery

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

class ThumbnailDownloader<T>(private val responseHandler: Handler) : HandlerThread(TAG) {
    companion object Static {
        private val TAG = ThumbnailDownloader::class.simpleName
        private val messageDownload = 0
    }

    interface ThumbnailDownloadListener<T>{
        fun onThumbnailDownloaded(target: T, thumbnail: Bitmap)
    }

    private lateinit var requestHandler: Handler
    private val requestMap = ConcurrentHashMap<T, String>()
    var thumbnailDownloadListener: ThumbnailDownloadListener<T>? = null

    override fun onLooperPrepared() {
        requestHandler = object : Handler() {
            override fun handleMessage(msg: Message) {
                if (msg.what == messageDownload) {
                    @Suppress("UNCHECKED_CAST")
                    val target = msg.obj as T
                    Log.i(TAG, "Got a request for URL: ${requestMap[target]}")
                    handleRequest(target)
                }
            }
        }
    }

    fun queueThumbnail(target: T, url: String?) {
        Log.i(TAG, "Got a URL $url")

        if (url == null) {
            requestMap.remove(target)
        } else {
            requestMap.put(target, url)
            requestHandler.obtainMessage(messageDownload, target).sendToTarget()
        }
    }

    fun clearQueue() {
        requestHandler.removeMessages(messageDownload)
    }

    private fun handleRequest(target: T) {
        try {
            val url = requestMap[target]

            if (url == null) {
                return
            }

            val bitmapBytes = FlickrFetchr.getUrlBytes(url)
            val bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.size)
            Log.i(TAG, "Bitmap created")

            responseHandler.post({
                if (requestMap[target] != url) {
                    return@post
                }

                requestMap.remove(target)
                thumbnailDownloadListener?.onThumbnailDownloaded(target, bitmap)
            })
        } catch (ioe: IOException) {
            Log.e(TAG, "Error downloading image", ioe)
        }
    }
}