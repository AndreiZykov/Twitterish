package com.abaz.twitterish.screen

import com.abaz.twitterish.R
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder

class ProgressItem : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) = Unit
    override fun getLayout(): Int = R.layout.progress_bar_item
}