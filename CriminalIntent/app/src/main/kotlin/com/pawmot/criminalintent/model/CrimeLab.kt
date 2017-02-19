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

    fun addCrime(crime: Crime) {
        crimes.add(crime)
    }

    fun getCrimes(): List<Crime> {
        return crimes
    }

    fun getCrime(id: UUID) : Crime? {
        return crimes.find { it.uuid == id }
    }

    fun getCrimeIdx(id: UUID): Int? {
        crimes.forEachIndexed { i, c ->
            if (c.uuid == id) {
                return i
            }
        }
        return null
    }

    fun removeCrimeAt(idx: Int) {
        crimes.removeAt(idx)
    }
}