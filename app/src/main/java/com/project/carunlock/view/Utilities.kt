package com.project.carunlock.view

import android.graphics.Color

internal fun generateRandomNumbers(
    base: Int = 1,
    maxSize: Int = 8,
    diversity: Int = 30
): List<Float> {
    val size = (1..maxSize).random()
    val numbers = (1..size).map { (1..diversity).random() }
    return numbers.map { it.toFloat() / numbers.sum() * base }
}

internal fun generateRandomColor(): Int {
    fun component() = (0..255).random()
    return Color.rgb(component(), component(), component())
}