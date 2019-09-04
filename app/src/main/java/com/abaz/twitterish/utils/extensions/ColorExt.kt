package com.abaz.twitterish.utils.extensions

import android.graphics.Color
import com.github.ivbaranov.mli.MaterialLetterIcon

fun MaterialLetterIcon.colorById(id: Long) {
    shapeColor = runCatching {
        var resColor = Color.BLACK
        val arrayId =
            context.resources.getIdentifier("material_colors", "array", context.packageName)
        if (arrayId != 0) {
            val colors = context.resources.obtainTypedArray(arrayId)
            val index = (id % colors.length()).toInt()
            resColor = colors.getColor(index, Color.BLACK)
            colors.recycle()
        }
        resColor
    }.getOrDefault(Color.BLACK)
}