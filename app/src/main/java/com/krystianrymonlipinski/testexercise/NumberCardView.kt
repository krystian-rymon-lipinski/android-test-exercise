package com.krystianrymonlipinski.testexercise

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.card.MaterialCardView

class NumberCardView(context: Context, attrs: AttributeSet) : MaterialCardView(context, attrs) {

    companion object {
        private val STATE_IS_SELECTED = intArrayOf(R.attr.isSelected)
    }

    private var isSelected = false

    fun setIsSelected(isSelected: Boolean) {
        this.isSelected = isSelected
        refreshDrawableState()
    }

    fun getIsSelected() = isSelected

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isSelected) {
            mergeDrawableStates(drawableState, STATE_IS_SELECTED)
        }
        return drawableState

    }
}