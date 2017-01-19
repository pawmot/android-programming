package com.pawmot.criminalintent.util

import android.text.Editable
import android.text.TextWatcher

class TextWatcherChangeOnly(private val onChangeHandler: (String) -> Unit) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        onChangeHandler(s.toString())
    }

    override fun afterTextChanged(s: Editable?) {}
}