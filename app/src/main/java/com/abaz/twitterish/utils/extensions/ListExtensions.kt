package com.abaz.twitterish.utils.extensions

/**
 * @author: Anthony Busto
 * @date:   2019-08-29
 */

fun <T> List<T>.upsert(value: T, finder: (T) -> Boolean) = indexOfFirst(finder).let { index ->
    if (index >= 0) copy(index, value) else this + value
}

fun <T> List<T>.copy(i: Int, value: T): List<T> = toMutableList().apply { set(i, value) }

fun <T> List<T>.add(value: T, i: Int = 0): List<T> = toMutableList().apply { add(i, value) }

inline fun <T> List<T>.delete(filter: (T) -> Boolean): List<T> = toMutableList().apply { removeAt(indexOfFirst(filter)) }
