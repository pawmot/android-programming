package com.pawmot.photogallery

import android.content.Context
import android.preference.PreferenceManager

object QueryPreferences {
    private val prefSearchQuery = "searchQuery"
    private val prefLastResultId = "lastResultId"

    fun getStoredQuery(ctx: Context): String? {
        return getStringOrNull(ctx, prefSearchQuery)
    }

    fun setStoredQuery(ctx: Context, query: String?) {
        setString(ctx, prefSearchQuery, query)
    }

    fun getLastResultId(ctx: Context): String? {
        return getStringOrNull(ctx, prefLastResultId)
    }

    fun setLastResultId(ctx: Context, lastResultId: String?) {
        setString(ctx, prefLastResultId, lastResultId)
    }

    private fun getStringOrNull(ctx: Context, name: String): String? {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getString(name, null)
    }

    private fun setString(ctx: Context, name: String, value: String?) {
        PreferenceManager.getDefaultSharedPreferences(ctx)
                .edit()
                .putString(name, value)
                .apply()
    }
}