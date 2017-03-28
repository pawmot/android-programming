package com.pawmot.photogallery

import android.net.Uri
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection.HTTP_OK
import java.net.URL
import javax.net.ssl.HttpsURLConnection

object FlickrFetchr {
    private val TAG = FlickrFetchr::class.simpleName
    private val apiKey = "0a8de1c6e11ec7e061aea7aa1a5709c4"

    private val fetchRecentsMethod = "flickr.photos.getRecent"
    private val searchMethod = "flickr.photos.search"
    private val endpoint = Uri
            .parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("api_key", apiKey)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("extras", "url_s")
            .build()

    fun getUrlBytes(urlStr: String): ByteArray {
        val url = URL(urlStr)
        val conn = url.openConnection() as HttpsURLConnection

        try {
            val out = ByteArrayOutputStream()
            val input = conn.inputStream

            if (conn.responseCode != HTTP_OK) {
                throw IOException("${conn.responseMessage}: with $urlStr")
            }

            val buffer = ByteArray(1024)
            var bytesRead = input!!.read(buffer)
            while (bytesRead > 0) {
                out.write(buffer, 0, bytesRead)
                bytesRead = input.read(buffer)
            }

            out.close()
            return out.toByteArray()
        } finally {
            conn.disconnect()
        }
    }

    fun getUrlString(urlStr: String): String {
        return String(getUrlBytes(urlStr))
    }

    fun fetchRecentPhotos(): List<GalleryItem> {
        val url = buildUrl(fetchRecentsMethod)
        return downloadGalleryItems(url)
    }

    fun searchPhotos(query: String): List<GalleryItem> {
        val url = buildUrl(searchMethod, query)
        return downloadGalleryItems(url)
    }

    private fun buildUrl(method: String, query: String? = null): String {
        val uriBuilder = endpoint
                .buildUpon()
                .appendQueryParameter("method", method)

        if (method == searchMethod) {
            uriBuilder.appendQueryParameter("text", query)
        }

        return uriBuilder.build().toString()
    }

    // FIXME: paging
    fun downloadGalleryItems(url: String): List<GalleryItem> {
        val items = mutableListOf<GalleryItem>()

        try {
            val json = getUrlString(url)
            Log.i(TAG, "Received JSON: $json")
            // FIXME: consider using Gson
            val obj = JSONObject(json)
            parseItems(items, obj)
        } catch (je: JSONException) {
            Log.e(TAG, "Failed to parse JSON", je)
        } catch (ioe: IOException) {
            Log.e(TAG, "Failed to fetch items", ioe)
        }

        return items
    }

    private fun parseItems(items: MutableList<GalleryItem>, obj: JSONObject) {
        val photosObj = obj.getJSONObject("photos")
        val photoJArray = photosObj.getJSONArray("photo")

        for (i in 0 until photoJArray.length()) {
            val photoJObj = photoJArray.getJSONObject(i)

            if (!photoJObj.has("url_s")) {
                continue
            }

            val id = photoJObj.getString("id")
            val caption = photoJObj.getString("title")
            val url = photoJObj.getString("url_s")

            items.add(GalleryItem(caption, id, url))
        }
    }
}