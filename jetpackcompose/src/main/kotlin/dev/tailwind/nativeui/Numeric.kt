package dev.tailwind.nativeui

internal fun finiteNumber(value: Number): Double = value.toDouble().also {
    require(it.isFinite()) { "Utility values must be finite" }
}
internal fun nativeFloat(value: Double): Float = value.toFloat().also {
    require(it.isFinite()) { "Utility value exceeds the native numeric range" }
}
