package com.abaz.twitterish.mvrx

import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.BuildConfig
import com.airbnb.mvrx.MvRxState

/**
 * @author: Anthony Busto
 * @date:   2019-08-28
 */
abstract class MvRxViewModel<S : MvRxState>(
    initialState: S,
    debugMode: Boolean = BuildConfig.DEBUG
) : BaseMvRxViewModel<S>(
    initialState = initialState,
    debugMode = debugMode
)