package com.pawmot.criminalintent

import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import java.util.*

class CrimeActivity : SingleFragmentActivity() {
    companion object Companion {
        private val extraCrimeId = "com.pawmot.criminalintent.crime_id"
        fun newIntent(ctx: Context, crimeId: UUID): Intent {
            val intent = Intent(ctx, CrimeActivity::class.java)
            intent.putExtra(extraCrimeId, crimeId)
            return intent
        }
    }

    override fun createFragment(): Fragment {
        val crimeId = intent.getSerializableExtra(extraCrimeId) as UUID
        return CrimeFragment.newInstance(crimeId)
    }
}
