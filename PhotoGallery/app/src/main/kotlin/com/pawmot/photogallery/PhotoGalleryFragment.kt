package com.pawmot.photogallery

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.pawmot.photogallery.ThumbnailDownloader.ThumbnailDownloadListener
import kotlinx.android.synthetic.main.fragment_photo_gallery.*
import kotlinx.android.synthetic.main.gallery_item.view.*

class PhotoGalleryFragment : Fragment() {
    companion object Static {
        private val TAG = PhotoGalleryFragment::class.simpleName
        fun newInstance(): PhotoGalleryFragment {
            return PhotoGalleryFragment()
        }
    }

    private var items: List<GalleryItem> = arrayListOf()
    private lateinit var thumbnailDownloader: ThumbnailDownloader<PhotoHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setHasOptionsMenu(true)
        updateItems()

        val responseHandler = Handler()
        thumbnailDownloader = ThumbnailDownloader(responseHandler)
        thumbnailDownloader.start()
        thumbnailDownloader.looper
        Log.i(TAG, "Background thread started")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_photo_gallery, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        fragmentPhotoGalleryRecyclerView.layoutManager = GridLayoutManager(activity, resources.getInteger(R.integer.colNum))
        setupAdapter()

        thumbnailDownloader.thumbnailDownloadListener = object : ThumbnailDownloadListener<PhotoHolder> {
            override fun onThumbnailDownloaded(target: PhotoHolder, thumbnail: Bitmap) {
                val drawable = BitmapDrawable(resources, thumbnail)
                target.bindDrawable(drawable)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        thumbnailDownloader.clearQueue()
    }

    override fun onDestroy() {
        super.onDestroy()
        thumbnailDownloader.quit()
        Log.i(TAG, "Background thread destroyed")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_photo_gallery, menu)

        val searchView = menu.findItem(R.id.menuItemSearch).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Log.d(TAG, "QueryTextSubmit $query")
                QueryPreferences.setStoredQuery(activity, query)
                updateItems()
                // FIXME: hide keyboard
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                Log.d(TAG, "QueryTextChange $newText")
                return false
            }

        })

        searchView.setOnSearchClickListener({
            val query = QueryPreferences.getStoredQuery(activity)
            searchView.setQuery(query, false)
        })

        val toggleItem = menu.findItem(R.id.menuItemTogglePolling)
        if (PollService.isServiceAlarmOn(activity)) {
            toggleItem.setTitle(R.string.stop_polling)
        } else {
            toggleItem.setTitle(R.string.start_polling)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuItemClear -> {
                QueryPreferences.setStoredQuery(activity, null)
                updateItems()
                true
            }
            R.id.menuItemTogglePolling -> {
                val shouldStartAlarm = !PollService.isServiceAlarmOn(activity)
                PollService.setServiceAlarm(activity, shouldStartAlarm)
                activity.invalidateOptionsMenu()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateItems() {
        val query = QueryPreferences.getStoredQuery(activity)
        FetchItemsTask(query).execute()
    }

    private fun setupAdapter() {
        if (isAdded) {
            fragmentPhotoGalleryRecyclerView.adapter = PhotoAdapter(items)
        }
    }

    inner private class PhotoHolder(v: View) : RecyclerView.ViewHolder(v) {

        private val imageView = v.fragmentPhotoGalleryImageView

        fun bindDrawable(drawable: Drawable) {
            imageView.setImageDrawable(drawable)
        }
    }

    inner private class PhotoAdapter(private val items: List<GalleryItem>) : RecyclerView.Adapter<PhotoHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PhotoHolder {
            val view = LayoutInflater.from(activity).inflate(R.layout.gallery_item, parent, false)
            return PhotoHolder(view)
        }

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            val item = items[position]
            // FIXME: Implement caching and preloading (10 prev & 10 next)
            thumbnailDownloader.queueThumbnail(holder, item.url)
        }

        override fun getItemCount(): Int {
            return items.size
        }
    }

    inner private class FetchItemsTask(private val query: String?) : AsyncTask<Void, Void, List<GalleryItem>>() {

        override fun doInBackground(vararg params: Void?): List<GalleryItem> {
            // FIXME: when clearing it does not work, investigate.
            // FIXME: until this is done, show a loading indicator
            return if (query == null) FlickrFetchr.fetchRecentPhotos() else FlickrFetchr.searchPhotos(query)
        }

        override fun onPostExecute(result: List<GalleryItem>) {
            items = result
            setupAdapter()
        }
    }
}
