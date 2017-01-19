package com.pawmot.criminalintent.model

import java.util.*

data class Crime(val id: UUID, var title: String) {
    companion object Factory {
        fun new() = Crime(UUID.randomUUID(), "")
    }
}