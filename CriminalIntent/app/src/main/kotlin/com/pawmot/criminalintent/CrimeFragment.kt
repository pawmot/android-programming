package com.pawmot.criminalintent

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.content.pm.PackageManager.MATCH_DEFAULT_ONLY
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import android.provider.MediaStore.EXTRA_OUTPUT
import android.support.v4.app.Fragment
import android.support.v4.app.ShareCompat
import android.support.v4.content.FileProvider
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pawmot.criminalintent.model.Crime
import com.pawmot.criminalintent.model.CrimeLab
import com.pawmot.criminalintent.util.PictureUtils
import com.pawmot.criminalintent.util.TextWatcherChangeOnly
import kotlinx.android.synthetic.main.fragment_crime.*
import kotlinx.android.synthetic.main.view_camera_and_title.*
import java.io.File
import java.util.*

class CrimeFragment : Fragment() {
    companion object Companion {
        private val tag = CrimeFragment::class.simpleName
        private val argCrimeId = "crime_id"
        private val dateDialogName = "DialogDate"
        private val photoDialogName = "DialogPhoto"
        private val requestDate = 0
        private val requestContact = 1
        private val requestPhoto = 2
        fun newInstance(crimeId: UUID): CrimeFragment {
            val args = Bundle()
            args.putSerializable(argCrimeId, crimeId)

            val fragment = CrimeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var crime: Crime
    private var photoFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val crimeId = arguments.getSerializable(argCrimeId) as UUID
        crime = CrimeLab.instance(activity).getCrime(crimeId) ?: Crime.new()
        photoFile = CrimeLab.instance(activity).getPhotoFile(crime)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_crime, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        crimeTitle.setText(crime.title)
        crimeTitle.addTextChangedListener(TextWatcherChangeOnly { crime.title = it })

        crimeDate.text = formatDate(crime.date)
        crimeDate.setOnClickListener { _ ->
            val fm = fragmentManager
            val dialog = DatePickerFragment.newInstance(crime.date)
            dialog.setTargetFragment(this, requestDate)
            dialog.show(fm, dateDialogName)
        }

        crimeSolved.isChecked = crime.solved
        crimeSolved.setOnCheckedChangeListener { _, checked -> crime.solved = checked }

        val pickContact = Intent(ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        chooseSuspect.setOnClickListener { _ ->
            startActivityForResult(pickContact, requestContact)
        }

        if (crime.suspect != null) {
            chooseSuspect.text = crime.suspect
        }

        val packageManager = activity.packageManager
        if (packageManager.resolveActivity(pickContact, MATCH_DEFAULT_ONLY) == null) {
            chooseSuspect.isEnabled = false
        }

        sendCrimeReport.setOnClickListener { _ ->
            val intent = ShareCompat.IntentBuilder.from(activity)
                    .setType("text/plain")
                    .setText(getCrimeReport())
                    .setSubject(getString(R.string.crime_report_subject))
                    .setChooserTitle(R.string.send_report)
                    .createChooserIntent()
            startActivity(intent)
        }

        val captureImage = Intent(ACTION_IMAGE_CAPTURE)
        val canTakePhoto = photoFile != null && captureImage.resolveActivity(packageManager) != null
        crimeCamera.isEnabled = canTakePhoto

        if (canTakePhoto) {
            val uri = FileProvider.getUriForFile(this.activity, "com.pawmot.criminalintent.fileprovider", photoFile)
            captureImage.putExtra(EXTRA_OUTPUT, uri)
        }

        crimeCamera.setOnClickListener { _ ->
            startActivityForResult(captureImage, requestPhoto)
        }

        updatePhotoView()

        crimePhoto.setOnClickListener { _ ->
            val fm = fragmentManager
            val dialog = PhotoZoomFragment.newInstance(photoFile!!)
            dialog.show(fm, photoDialogName)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_OK) {
            return
        }

        if (requestCode == requestDate) {
            val date = data?.getSerializableExtra(DatePickerFragment.extraDate) as Date
            crime.date = date
            crimeDate.text = formatDate(crime.date)
        } else if (requestCode == requestContact && data != null) {
            val contactUri = data.data
            val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
            val cursor = activity.contentResolver.query(contactUri, queryFields, null, null, null)
            cursor.use { c ->
                if (c.count == 0) {
                    return
                }

                c.moveToFirst()
                val suspect = c.getString(0)
                crime.suspect = suspect
                chooseSuspect.text = suspect
            }
        } else if (requestCode == requestPhoto) {
            updatePhotoView()
        }
    }

    override fun onPause() {
        super.onPause()

        CrimeLab.instance(activity).updateCrime(crime)
    }

    private fun formatDate(date: Date): String {
        return DateFormat.format("EEEE, MMM d, yyyy", date).toString()
    }

    private fun getCrimeReport(): String {
        val solvedString =
                if (crime.solved) {
                    getString(R.string.crime_report_solved)
                } else {
                    getString(R.string.crime_report_unsolved)
                }

        val dateFormat = "EEE, MMM dd"
        val dateString = DateFormat.format(dateFormat, crime.date).toString()

        var suspect = crime.suspect
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect)
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect)
        }

        val report = getString(R.string.crime_report, crime.title, dateString, solvedString, suspect)
        return report
    }

    private fun updatePhotoView() {
        if (photoFile == null || !photoFile!!.exists()) {
            crimePhoto.setImageDrawable(null)
        } else {
            val bmp = PictureUtils.getScaledBitmap(photoFile!!.path, activity)
            crimePhoto.setImageBitmap(bmp)
        }
    }
}