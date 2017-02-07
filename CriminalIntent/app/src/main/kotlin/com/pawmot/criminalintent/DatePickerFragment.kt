package com.pawmot.criminalintent

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.dialog_date.view.*
import java.util.*

class DatePickerFragment : DialogFragment() {
    companion object Companion {
        private val argDate = "date"

        val extraDate = "com.pawmot.criminalintent.date"

        fun newInstance(date: Date): DatePickerFragment {
            val args = Bundle()
            args.putSerializable(argDate, date)

            val fragment = DatePickerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val date = arguments.getSerializable(argDate) as Date

        val calendar = Calendar.getInstance()
        calendar.time = date
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val v = LayoutInflater.from(activity).inflate(R.layout.dialog_date, null)

        v.dialogDateDatePicker.init(year, month, day, null)

        return AlertDialog.Builder(activity)
                .setView(v)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, { dialog, which ->
                    val y = v.dialogDateDatePicker.year
                    val m = v.dialogDateDatePicker.month
                    val d = v.dialogDateDatePicker.dayOfMonth
                    val date = GregorianCalendar(y, m, d).time
                    sendResult(RESULT_OK, date)
                })
                .create()
    }

    private fun sendResult(resultCode: Int, date: Date) {
        if (targetFragment == null) {
            return
        }

        val intent = Intent()
        intent.putExtra(extraDate, date)

        targetFragment.onActivityResult(targetRequestCode, resultCode, intent)
    }
}