package com.pawmot.criminalintent.dal

import android.database.Cursor
import android.database.CursorWrapper
import com.pawmot.criminalintent.dal.CrimeDbSchema.CrimeTable.Columns
import com.pawmot.criminalintent.model.Crime
import java.util.*

class CrimeCursorWrapper(cursor: Cursor) : CursorWrapper(cursor) {
    fun getCrime(): Crime {
        val uuidString = getString(getColumnIndex(Columns.uuid))
        val title = getString(getColumnIndex(Columns.title))
        val date = getLong(getColumnIndex(Columns.date))
        val isSolved = getInt(getColumnIndex(Columns.solved))
        val suspect = getString(getColumnIndex(Columns.suspect))

        val crime = Crime.new()
        crime.uuid = UUID.fromString(uuidString)
        crime.title = title
        crime.date = Date(date)
        crime.solved = isSolved != 0
        crime.suspect = suspect

        return crime
    }
}