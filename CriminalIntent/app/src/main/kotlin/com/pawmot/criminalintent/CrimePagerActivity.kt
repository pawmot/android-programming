package com.pawmot.criminalintent

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentStatePagerAdapter
import com.pawmot.criminalintent.model.CrimeLab
import kotlinx.android.synthetic.main.activity_crime_pager.*
import java.util.*

class CrimePagerActivity : FragmentActivity() {
    companion object Companion {
        private val extraCrimeId = "com.pawmot.criminalintent.crime_id"
        fun newIntent(ctx: Context, crimeId: UUID): Intent {
            val intent = Intent(ctx, CrimePagerActivity::class.java)
            intent.putExtra(extraCrimeId, crimeId)
            return intent
        }
    }

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

        activityCrimePagerViewPager.currentItem = lab.getCrimeIdx(crimeId) ?: 0
    }
}