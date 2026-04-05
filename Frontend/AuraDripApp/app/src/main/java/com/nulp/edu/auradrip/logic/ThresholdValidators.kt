package com.nulp.edu.auradrip.logic

object ThresholdValidators {

    fun irrigationThresholdValidate(startValue: Int, stopValue: Int): Boolean {
        if (startValue >= stopValue) return false
        else return true
    }
}