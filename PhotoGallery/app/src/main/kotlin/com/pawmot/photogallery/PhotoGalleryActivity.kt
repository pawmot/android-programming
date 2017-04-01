package com.pawmot.photogallery

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment

class PhotoGalleryActivity : SingleFragmentActivity() {
    companion object Static {
        fun newIntent(ctx: Context): Intent {
            return Intent(ctx, PhotoGalleryActivity::class.java)
        }
    }

    override fun createFragment(): Fragment {
        return PhotoGalleryFragment.newInstance()
    }
}
