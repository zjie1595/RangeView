package com.zj.widget

interface RangeListener {

    fun onStartTrackingTouch(rangeView: RangeView)

    fun onStopTrackingTouch(rangeView: RangeView)

    fun onRangeChangeListener(
        rangeView: RangeView,
        startValue: Float,
        endValue: Float,
        changingValue: Float,
        isStartValueChanging: Boolean,
        isFromUser: Boolean
    )
}