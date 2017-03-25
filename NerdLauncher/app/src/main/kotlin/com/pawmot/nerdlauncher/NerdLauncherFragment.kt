package com.pawmot.nerdlauncher

import android.content.Intent
import android.content.Intent.ACTION_MAIN
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_nerd_launcher.*
import kotlinx.android.synthetic.main.list_item_activity.view.*

class NerdLauncherFragment : Fragment() {
    companion object Static {
        private val TAG = NerdLauncherFragment::class.simpleName

        fun newInstance(): NerdLauncherFragment {
            return NerdLauncherFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_nerd_launcher, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        fragmentNerdLauchnerRecyclerView.layoutManager = LinearLayoutManager(activity)
        setupAdapter()
    }

    private fun setupAdapter() {
        val startupIntent = Intent(Intent.ACTION_MAIN)
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val pm = activity.packageManager
        val activities = pm.queryIntentActivities(startupIntent, 0)
        activities.sortBy { a -> a.loadLabel(pm).toString().toLowerCase() }

        Log.i(TAG, "Found ${activities.size} activities.")
        fragmentNerdLauchnerRecyclerView.adapter = ActivityAdapter(activities)
    }

    private inner class ActivityHolder(private val v: View) : RecyclerView.ViewHolder(v) {
        private lateinit var resolveInfo: ResolveInfo

        fun bindActivity(resolveInfo: ResolveInfo) {
            this.resolveInfo = resolveInfo
            val pm = activity.packageManager

            val appName = resolveInfo.loadLabel(pm).toString()
            v.listItemActivityTextView.text = appName

            val icon = resolveInfo.loadIcon(pm)
            v.listItemActivityImageView.setImageDrawable(icon)

            v.setOnClickListener {
                val activityInfo = resolveInfo.activityInfo
                val intent = Intent(ACTION_MAIN)
                        .setClassName(activityInfo.packageName, activityInfo.name)
                        .addFlags(FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }

    private inner class ActivityAdapter(private val activities: List<ResolveInfo>) : RecyclerView.Adapter<ActivityHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ActivityHolder {
            val inflater = LayoutInflater.from(activity)
            val view = inflater.inflate(R.layout.list_item_activity, parent, false)
            return ActivityHolder(view)
        }

        override fun onBindViewHolder(holder: ActivityHolder?, position: Int) {
            holder?.bindActivity(activities[position])
        }

        override fun getItemCount(): Int {
            return activities.size
        }
    }
}