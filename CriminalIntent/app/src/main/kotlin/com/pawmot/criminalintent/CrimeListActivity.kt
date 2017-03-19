package com.pawmot.criminalintent

import android.support.v4.app.Fragment
import com.pawmot.criminalintent.model.Crime
import kotlinx.android.synthetic.main.activity_twopane.*

class CrimeListActivity : SingleFragmentActivity(), CrimeListFragment.Callbacks, CrimeFragment.Callbacks {
    override fun createFragment(): Fragment {
        return CrimeListFragment()
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_masterdetail
    }

    override fun onCrimeSelected(crime: Crime) {
        if (detailFragmentContainer == null) {
            val intent = CrimePagerActivity.newIntent(this, crime.uuid)
            startActivity(intent)
        } else {
            val detail = CrimeFragment.newInstance(crime.uuid)

            supportFragmentManager.beginTransaction()
                    .replace(R.id.detailFragmentContainer, detail)
                    .commit()
        }
    }

    override fun onCrimeUpdate(crime: Crime) {
        val listFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as CrimeListFragment
        listFragment.updateUI()
    }
}