package com.pawmot.criminalintent.dal

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.pawmot.criminalintent.dal.CrimeDbSchema.CrimeTable

class CrimeBaseHelper(ctx: Context) : SQLiteOpenHelper(ctx, dbName, null, version) {
    companion object Companion {
        private val version = 1
        private val dbName = "crimeBase.db"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE ${CrimeTable.name}(${CrimeTable.Columns.uuid}, ${CrimeTable.Columns.title}, ${CrimeTable.Columns.date}, ${CrimeTable.Columns.solved})")

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}