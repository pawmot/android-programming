package com.pawmot.criminalintent

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.pawmot.criminalintent.model.Crime
import com.pawmot.criminalintent.model.CrimeLab
import com.pawmot.criminalintent.ui.CrimeRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_crime_list.*

class CrimeListFragment : Fragment() {

    companion object Companion {
        private val savedSubtitleVisible = "subtitle"
    }

    private var adapter: CrimeRecyclerViewAdapter? = null
    private var subtitleVisible: Boolean = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_crime_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        crimeRecyclerView.layoutManager = LinearLayoutManager(activity)

        val lab = CrimeLab.instance(activity)

        adapter = CrimeRecyclerViewAdapter(lab.getCrimes())
        crimeRecyclerView.adapter = adapter

        if (savedInstanceState != null) {
            subtitleVisible = savedInstanceState.getBoolean(savedSubtitleVisible)
        }
    }

    override fun onResume() {
        super.onResume()
        val lab = CrimeLab.instance(activity)
        adapter?.setCrimes(lab.getCrimes())
        adapter?.notifyDataSetChanged()
        updateSubtitle()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)

        val subtitleItem = menu.findItem(R.id.menuItemShowSubtitle)
        if (subtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle)
        } else {
            subtitleItem.setTitle(R.string.show_subtitle)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menuItemNewCrime -> {
                createNewCrime()
                return true
            }
            R.id.menuItemShowSubtitle -> {
                subtitleVisible = !subtitleVisible
                activity.invalidateOptionsMenu()
                updateSubtitle()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun createNewCrime() {
        val crime = Crime("")
        CrimeLab.instance(activity).addCrime(crime)
        val intent = CrimePagerActivity.newIntent(activity, crime.uuid)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean(savedSubtitleVisible, subtitleVisible)
    }

    private fun updateSubtitle() {
        val subtitle = if (subtitleVisible) {
            val lab = CrimeLab.instance(activity)
            val crimeCount = lab.getCrimes().size
            resources.getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount)
        } else {
            null
        }

        val activity = activity as AppCompatActivity
        activity.supportActionBar?.subtitle = subtitle
    }
}