package com.pawmot.criminalintent

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity

class CrimeActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crime)

        val fm = supportFragmentManager
        var fragment: Fragment? = fm.findFragmentById(R.id.fragmentContainer)

        if (fragment == null) {
            fragment = CrimeFragment()
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit()
        }
    }
}
