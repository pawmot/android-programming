package com.pawmot.photogallery

import android.app.AlarmManager
import android.app.AlarmManager.ELAPSED_REALTIME
import android.app.IntentService
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_NO_CREATE
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.SystemClock
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.NotificationCompat
import android.util.Log

class PollService : IntentService(TAG) {
    companion object Static {
        private val TAG = PollService::class.simpleName

        // FIXME: set to 15 minutes
        private val pollIntervalMs = 60 * 1000L

        fun newIntent(ctx: Context): Intent {
            return Intent(ctx, PollService::class.java)
        }

        fun setServiceAlarm(ctx: Context, isOn: Boolean) {
            val intent = newIntent(ctx)
            val pendingIntent = PendingIntent.getService(ctx, 0, intent, 0)

            val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (isOn) {
                alarmManager.setInexactRepeating(ELAPSED_REALTIME, SystemClock.elapsedRealtime(), pollIntervalMs, pendingIntent)
            } else {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        }

        fun isServiceAlarmOn(ctx: Context): Boolean {
            val intent = newIntent(ctx)
            val pendingIntent = PendingIntent.getService(ctx, 0, intent, FLAG_NO_CREATE)
            return pendingIntent != null
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        if (!isConnectedToInternet()) {
            return
        }

        val query = QueryPreferences.getStoredQuery(this)
        val lastResultId = QueryPreferences.getLastResultId(this)

        val items =
                if (query == null)
                    FlickrFetchr.fetchRecentPhotos()
                else
                    FlickrFetchr.searchPhotos(query)

        if (items.isEmpty()) {
            return
        }

        val resultId = items.first().id

        if (resultId == lastResultId) {
            Log.i(TAG, "Got an old result: $resultId")
        } else {
            Log.i(TAG, "Got a new result: $resultId")

            postNotification()
        }

        QueryPreferences.setLastResultId(this, resultId)
    }

    private fun isConnectedToInternet(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // TODO: verify that it does not throw when network is actually unavailable
        return cm.activeNetworkInfo.isAvailable && cm.activeNetworkInfo.isConnected
    }

    private fun postNotification() {
        val resources = resources
        val intent = PhotoGalleryActivity.newIntent(this)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val notification = NotificationCompat.Builder(this)
                .setTicker(resources.getString(R.string.new_pictures_title))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(resources.getString(R.string.new_pictures_title))
                .setContentText(resources.getString(R.string.new_pictures_text))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

        NotificationManagerCompat.from(this).notify(0, notification)
    }
}