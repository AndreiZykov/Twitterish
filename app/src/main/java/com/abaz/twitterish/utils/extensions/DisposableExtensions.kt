package com.abaz.twitterish.utils.extensions

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * @author: Anthony Busto
 * @date:   2019-09-01
 */

fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
    compositeDisposable.add(this)
}