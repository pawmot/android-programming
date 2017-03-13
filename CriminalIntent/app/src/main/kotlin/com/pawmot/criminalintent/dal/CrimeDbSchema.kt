package com.pawmot.criminalintent.dal

object CrimeDbSchema {
    object CrimeTable {
        val name = "crimes"

        object Columns {
            val uuid = "uuid"
            val title = "title"
            val date = "date"
            val solved = "solved"
            val suspect = "suspect"
        }
    }
}