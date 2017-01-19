package com.pawmot.criminalintent

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pawmot.criminalintent.model.Crime
import com.pawmot.criminalintent.util.TextWatcherChangeOnly
import kotlinx.android.synthetic.main.fragment_crime.*

class CrimeFragment : Fragment() {
    private lateinit var crime: Crime

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime.new()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_crime, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        crimeTitle.addTextChangedListener(TextWatcherChangeOnly { s -> crime.title = s })
    }
}