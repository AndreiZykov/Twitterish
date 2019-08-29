package com.abaz.twitterish.utils.extensions

import android.view.View

/**
 * @author: Anthony Busto
 * @date:   2019-08-29
 */

fun View.showIf(predicate: Boolean, defaultNotShow: Int = View.GONE) {
    visibility = if(predicate) View.VISIBLE else defaultNotShow
}

fun View.hideIf(predicate: Boolean, defaultNotShow: Int = View.GONE) {
    visibility = if(predicate) defaultNotShow else  View.VISIBLE
}

fun View.showOrHide(predicate: Boolean) {
    if(predicate) show() else hide()
}

fun View.showOrGone(predicate: Boolean) {
    if(predicate) show() else gone()
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}