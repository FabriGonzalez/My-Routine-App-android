package com.myroutine.app.data.model

sealed class Measure {
    data class Weight(val kg: Double) : Measure()
    data class Time(val seconds: Int) : Measure()
}