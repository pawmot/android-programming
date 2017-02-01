package com.pawmot.criminalintent

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pawmot.criminalintent.model.CrimeLab
import com.pawmot.criminalintent.ui.CrimeAdapter
import kotlinx.android.synthetic.main.fragment_crime_list.*

class CrimeListFragment : Fragment() {
    private var adapter: CrimeAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_crime_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        crimeRecyclerView.layoutManager = LinearLayoutManager(activity)

        val lab = CrimeLab.instance(activity)

        adapter = CrimeAdapter(lab.getCrimes())
        crimeRecyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        adapter?.notifyDataSetChanged()
    }
}