package com.pawmot.nerdlauncher

import android.os.Bundle
import android.support.v4.app.Fragment

class NerdLauncherActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return NerdLauncherFragment.newInstance()
    }
}
