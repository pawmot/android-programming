package com.pawmot.criminalintent.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.pawmot.criminalintent.dal.CrimeBaseHelper
import com.pawmot.criminalintent.dal.CrimeCursorWrapper
import com.pawmot.criminalintent.dal.CrimeDbSchema.CrimeTable
import java.util.*

class CrimeLab private constructor(ctx: Context) {
    companion object Singleton {
        private var instance: CrimeLab? = null

        fun instance(ctx: Context): CrimeLab {
            if (instance == null) {
                instance = CrimeLab(ctx)
            }

            return instance!!
        }

        private fun getContentValues(crime: Crime): ContentValues {
            val cv = ContentValues()
            cv.put(CrimeTable.Columns.uuid, crime.uuid.toString())
            cv.put(CrimeTable.Columns.title, crime.title)
            cv.put(CrimeTable.Columns.date, crime.date.time)
            cv.put(CrimeTable.Columns.solved, if(crime.solved) 1 else 0)

            return cv
        }
    }

    private val db: SQLiteDatabase = CrimeBaseHelper(ctx.applicationContext).writableDatabase

    fun addCrime(crime: Crime) {
        val cv = getContentValues(crime)
        db.insert(CrimeTable.name, null, cv)
    }

    fun updateCrime(crime: Crime) {
        val uuidStr = crime.uuid.toString()
        val cv = getContentValues(crime)

        db.update(CrimeTable.name, cv, "${CrimeTable.Columns.uuid} = ?", arrayOf(uuidStr))
    }

    fun getCrimes(): List<Crime> {
        val cursor = queryCrimes(null, null)

        val crimes = mutableListOf<Crime>()
        try {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                crimes.add(cursor.getCrime())
                cursor.moveToNext()
            }
        } finally {
            cursor.close()
        }

        return crimes
    }

    fun getCrime(id: UUID): Crime? {
        val cursor = queryCrimes(
                "${CrimeTable.Columns.uuid} = ?",
                arrayOf(id.toString()))

        try {
            if (cursor.count == 0) {
                return null
            }

            cursor.moveToFirst()
            return cursor.getCrime()
        } finally {
            cursor.close()
        }
    }

    fun removeCrime(crime: Crime) {
        db.delete(CrimeTable.name, "${CrimeTable.Columns.uuid} = ?", arrayOf(crime.uuid.toString()))
    }

    private fun queryCrimes(whereClause: String?, whereArgs: Array<String>?): CrimeCursorWrapper {
        val cursor = db.query(
                CrimeTable.name,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        )

        return CrimeCursorWrapper(cursor)
    }
}