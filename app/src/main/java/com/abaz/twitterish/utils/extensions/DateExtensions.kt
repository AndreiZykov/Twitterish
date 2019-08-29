package com.abaz.twitterish.utils.extensions

import java.util.*

/**
 * @author: Anthony Busto
 * @date:   2019-08-28
 */

fun Date.difference(another: Date) = (another.time - time)
    .let { if(it > 0) it else 0 }

//fun Date.differenceStringFormatted(another: Date) : String {
//
//    val
//}