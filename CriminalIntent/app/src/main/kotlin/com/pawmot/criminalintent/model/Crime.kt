package com.pawmot.criminalintent.model

import java.util.*

data class Crime(var title: String) {
    val uuid = UUID.randomUUID()
    val date = Date()
    var solved = false

    companion object Factory {
        fun new() = Crime("")
    }
}