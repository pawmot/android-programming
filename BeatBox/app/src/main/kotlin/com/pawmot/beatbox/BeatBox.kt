package com.pawmot.beatbox

import android.content.Context
import android.util.Log
import java.io.IOException

class BeatBox(ctx: Context) {
    companion object Static {
        private val tag = "BeatBox"
        private val soundsFolder = "sample_sounds"
    }

    private val assets = ctx.assets
    lateinit var sounds: List<Sound>

    init {
        loadSounds()
    }

    private fun loadSounds() {
        try {
            val soundNames = assets.list(soundsFolder)
            Log.i(tag, "Found ${soundNames.size} sounds")

            sounds = soundNames.map {
                val assetPath = "$soundsFolder/$it"
                Sound(assetPath)
            }
        } catch (ioe: IOException) {
            Log.e(tag, "Could not list assets", ioe)
        }
    }
}