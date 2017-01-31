package com.pawmot.criminalintent.model

import android.content.Context
import java.util.*

class CrimeLab private constructor(private val ctx: Context) {
    companion object Singleton {
        private var instance: CrimeLab? = null

        fun instance(ctx: Context): CrimeLab {
            if (instance == null) {
                instance = CrimeLab(ctx)
            }

            return instance!!
        }
    }

    private val crimes = mutableListOf<Crime>()

    init {
        for (i in 1..100) {
            val crime = Crime("Crime #$i")
            crime.solved = (i%2 == 0)
            crimes.add(crime)
        }
    }

    fun getCrimes(): List<Crime> {
        return crimes
    }

    fun getCrime(id: UUID) : Crime? {
        return crimes.find { it.uuid == id }
    }
}