package com.pawmot.criminalintent

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pawmot.criminalintent.model.Crime
import com.pawmot.criminalintent.model.CrimeLab
import com.pawmot.criminalintent.util.TextWatcherChangeOnly
import kotlinx.android.synthetic.main.fragment_crime.*
import java.util.*

class CrimeFragment : Fragment() {
    companion object Companion {
        private val argCrimeId = "crime_id"
        private val dateDialogName = "DialogDate"
        private val requestDate = 0
        fun newInstance(crimeId: UUID): CrimeFragment {
            val args = Bundle()
            args.putSerializable(argCrimeId, crimeId)

            val fragment = CrimeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var crime: Crime

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val crimeId = arguments.getSerializable(argCrimeId) as UUID
        crime = CrimeLab.instance(activity).getCrime(crimeId) ?: Crime.new()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_crime, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        crimeTitle.setText(crime.title)
        crimeTitle.addTextChangedListener(TextWatcherChangeOnly { crime.title = it })

        crimeDate.text = formatDate(crime.date)
        crimeDate.setOnClickListener { v ->
            val fm = fragmentManager
            val dialog = DatePickerFragment.newInstance(crime.date)
            dialog.setTargetFragment(this, requestDate)
            dialog.show(fm, dateDialogName)
        }

        crimeSolved.isChecked = crime.solved
        crimeSolved.setOnCheckedChangeListener { compoundButton, checked -> crime.solved = checked }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_OK) {
            return
        }

        if (requestCode == requestDate) {
            val date = data?.getSerializableExtra(DatePickerFragment.extraDate) as Date
            crime.date = date
            crimeDate.text = formatDate(crime.date)
        }
    }

    private fun formatDate(date: Date): String {
        return DateFormat.format("EEEE, MMM d, yyyy", date).toString()
    }
}