package com.pawmot.photogallery

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_photo_gallery.*

class PhotoGalleryFragment : Fragment() {
    companion object Static {
        private val TAG = PhotoGalleryFragment::class.simpleName
        fun newInstance(): PhotoGalleryFragment {
            return PhotoGalleryFragment()
        }
    }

    private var items: List<GalleryItem> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        FetchItemsTask().execute()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_photo_gallery, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        fragmentPhotoGalleryRecyclerView.layoutManager = GridLayoutManager(activity, resources.getInteger(R.integer.colNum))
        setupAdapter()
    }

    private fun setupAdapter() {
        if (isAdded) {
            fragmentPhotoGalleryRecyclerView.adapter = PhotoAdapter(items)
        }
    }

    inner private class PhotoHolder(private val tv: TextView) : RecyclerView.ViewHolder(tv) {

        fun bindGalleryItem(item: GalleryItem) {
            tv.text = item.toString()
        }
    }

    inner private class PhotoAdapter(private val items: List<GalleryItem>) : RecyclerView.Adapter<PhotoHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PhotoHolder {
            return PhotoHolder(TextView(activity))
        }

        override fun onBindViewHolder(holder: PhotoHolder?, position: Int) {
            val item = items[position]
            holder?.bindGalleryItem(item)
        }

        override fun getItemCount(): Int {
            return items.size
        }
    }

    inner private class FetchItemsTask : AsyncTask<Void, Void, List<GalleryItem>>() {

        override fun doInBackground(vararg params: Void?): List<GalleryItem> {
            return FlickrFetchr.fetchItems()
        }

        override fun onPostExecute(result: List<GalleryItem>) {
            items = result
            setupAdapter()
        }
    }
}
