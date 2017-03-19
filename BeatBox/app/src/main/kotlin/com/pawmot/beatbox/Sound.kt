package com.pawmot.beatbox

class Sound(val assetPath: String) {
    val name: String
    var soundId: Int? = null

    init {
        val comps = assetPath.split("/")
        val filename = comps.last()
        name = filename.replace(".wav", "")
    }
}