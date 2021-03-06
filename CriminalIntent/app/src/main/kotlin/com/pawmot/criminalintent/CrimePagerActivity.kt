package com.pawmot.criminalintent

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.pawmot.criminalintent.model.Crime
import com.pawmot.criminalintent.model.CrimeLab
import kotlinx.android.synthetic.main.activity_crime_pager.*
import java.util.*

class CrimePagerActivity : AppCompatActivity(), CrimeFragment.Callbacks {

    companion object Companion {
        private val extraCrimeId = "com.pawmot.criminalintent.crime_id"
        fun newIntent(ctx: Context, crimeId: UUID): Intent {
            val intent = Intent(ctx, CrimePagerActivity::class.java)
            intent.putExtra(extraCrimeId, crimeId)
            return intent
        }
    }

    private lateinit var idxToCrime: (Int) -> Crime

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crime_pager)

        val crimeId = intent.getSerializableExtra(extraCrimeId) as UUID

        val lab = CrimeLab.instance(this)
        val crimes = lab.getCrimes()
        val fm = supportFragmentManager
        activityCrimePagerViewPager.adapter = object : FragmentStatePagerAdapter(fm) {
            override fun getItem(position: Int): Fragment {
                val crime = crimes[position]
                return CrimeFragment.newInstance(crime.uuid)
            }

            override fun getCount(): Int {
                return crimes.size
            }
        }

        activityCrimePagerViewPager.currentItem = getCrimeIdx(crimes, crimeId) ?: 0

        idxToCrime = { crimes[it] }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_crime_pager, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.menuItemDeleteCrime -> {
                CrimeLab.instance(this).removeCrime(idxToCrime(activityCrimePagerViewPager.currentItem))
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCrimeUpdate(crime: Crime) {
    }

    private fun getCrimeIdx(crimes: List<Crime>, id: UUID): Int? {
        crimes.forEachIndexed { i, c ->
            if (c.uuid == id) {
                return i
            }
        }
        return null
    }
}