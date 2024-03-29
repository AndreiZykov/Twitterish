package com.abaz.twitterish.utils

import android.text.Editable
import android.text.TextWatcher

class TextChangedListener(val onTextChanged: (String) -> Unit) : TextWatcher {
    override fun afterTextChanged(s: Editable) { onTextChanged( s.toString()) }
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}