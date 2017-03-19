package com.pawmot.beatbox

import android.content.Context
import android.media.AudioManager.STREAM_MUSIC
import android.media.SoundPool
import android.util.Log
import java.io.IOException

class BeatBox(ctx: Context) {
    companion object Static {
        private val tag = "BeatBox"
        private val soundsFolder = "sample_sounds"
        private val maxNumberOfSounds = 5
    }

    private val assets = ctx.assets
    private val soundPool = SoundPool(maxNumberOfSounds, STREAM_MUSIC, 0)
    lateinit var sounds: List<Sound>

    init {
        loadSounds()
    }

    fun play(sound: Sound) {
        val id = sound.soundId
        if (id != null) soundPool.play(id, 1.0f, 1.0f, 1, 0, 1.0f)
    }

    fun release() {
        soundPool.release()
    }

    private fun loadSounds() {
        try {
            val soundNames = assets.list(soundsFolder)
            Log.i(tag, "Found ${soundNames.size} sounds")

            sounds = soundNames.map {
                val assetPath = "$soundsFolder/$it"
                Sound(assetPath)
            }

            sounds.forEach { load(it) }
        } catch (ioe: IOException) {
            Log.e(tag, "Could not list assets", ioe)
        }
    }

    private fun load(sound: Sound) {
        try {
            val afd = assets.openFd(sound.assetPath)
            val id = soundPool.load(afd, 1)
            sound.soundId = id
        } catch (ioe: IOException) {
            Log.e(tag, "Could not load sound ${sound.assetPath}", ioe)
        }
    }
}