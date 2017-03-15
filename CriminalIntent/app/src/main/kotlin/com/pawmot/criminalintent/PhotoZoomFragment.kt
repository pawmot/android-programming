package com.pawmot.criminalintent

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import com.pawmot.criminalintent.util.PictureUtils
import kotlinx.android.synthetic.main.dialog_photo.view.*
import java.io.File

class PhotoZoomFragment : DialogFragment() {
    companion object Static {
        private val argPhotoFileName = "photoFileName"

        fun newInstance(file: File) : PhotoZoomFragment {
            val args = Bundle()
            args.putSerializable(argPhotoFileName, file.absolutePath)

            val fragment = PhotoZoomFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val filename = arguments.getSerializable(argPhotoFileName) as String

        val v = LayoutInflater.from(activity).inflate(R.layout.dialog_photo, null)

        val bmp = PictureUtils.getScaledBitmap(filename, activity)

        v.dialogPhotoImageView.setImageBitmap(bmp)

        return AlertDialog.Builder(activity)
                .setView(v)
                .setTitle("Crime Photo")
                .setPositiveButton(android.R.string.ok, { _, _ ->

                })
                .create()
    }
}