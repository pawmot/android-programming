package com.pawmot.criminalintent.model

class CrimePhotoFilename(private val crime: Crime) {
    fun get(): String {
        return "IMG_${crime.uuid}.jpg"
    }
}