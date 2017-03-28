package com.pawmot.photogallery

import android.content.Context
import android.preference.PreferenceManager

object QueryPreferences {
    private val prefSearchQuery = "searchQuery"

    fun getStoredQuery(ctx: Context): String? {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getString(prefSearchQuery, null)
    }

    fun setStoredQuery(ctx: Context, query: String?) {
        PreferenceManager.getDefaultSharedPreferences(ctx)
                .edit()
                .putString(prefSearchQuery, query)
                .apply()
    }
}