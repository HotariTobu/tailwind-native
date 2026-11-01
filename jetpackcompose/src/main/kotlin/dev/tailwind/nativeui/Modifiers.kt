package dev.tailwind.nativeui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate as nativeRotate
import androidx.compose.ui.draw.scale as nativeScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

fun Modifier.p(value: Number) = padding(TW.spacing(value))
fun Modifier.px(value: Number) = padding(horizontal = TW.spacing(value))
fun Modifier.py(value: Number) = padding(vertical = TW.spacing(value))
fun Modifier.pt(value: Number) = padding(top = TW.spacing(value))
fun Modifier.pb(value: Number) = padding(bottom = TW.spacing(value))
fun Modifier.ps(value: Number) = padding(start = TW.spacing(value))
fun Modifier.pe(value: Number) = padding(end = TW.spacing(value))
fun Modifier.w(value: Number) = width(TW.spacing(value))
fun Modifier.h(value: Number) = height(TW.spacing(value))
fun Modifier.size(value: Number) = size(TW.spacing(value))
fun Modifier.wFull() = fillMaxWidth()
fun Modifier.hFull() = fillMaxHeight()
fun Modifier.minW(value: Number) = widthIn(min = TW.spacing(value))
fun Modifier.maxW(value: Number) = widthIn(max = TW.spacing(value))
fun Modifier.minH(value: Number) = heightIn(min = TW.spacing(value))
fun Modifier.maxH(value: Number) = heightIn(max = TW.spacing(value))
fun Modifier.bg(color: Color) = background(color)
fun Modifier.rounded(radius: TW.Radius = TW.Radius.Sm) = clip(radius.shape())
fun Modifier.border(color: Color, width: Float = 1f, radius: TW.Radius = TW.Radius.None): Modifier {
    require(width.isFinite() && width >= 0) { "Border width must be finite and nonnegative" }
    return if (width == 0f) this else border(width.dp, color, radius.shape())
}
fun Modifier.opacityPercent(value: Number): Modifier {
    val percent = finiteNumber(value)
    require(percent in 0.0..100.0) { "Opacity must be between 0 and 100" }
    return alpha((percent / 100).toFloat())
}
fun Modifier.overflowHidden() = clipToBounds()
fun Modifier.rotate(value: Number) = nativeRotate((finiteNumber(value) % 360).toFloat())
fun Modifier.scale(value: Number) = nativeScale(nativeFloat(finiteNumber(value) / 100))
fun Modifier.translateX(value: Number) = absoluteOffset(x = TW.signedSpacing(value))
fun Modifier.translateY(value: Number) = absoluteOffset(y = TW.signedSpacing(value))
fun Modifier.aspect(ratio: Float): Modifier {
    require(ratio.isFinite() && ratio > 0) { "Aspect ratio must be finite and positive" }
    return aspectRatio(ratio)
}
inline fun Modifier.variant(condition: Boolean, transform: Modifier.() -> Modifier): Modifier =
    if (condition) transform() else this

fun Modifier.maxW(token: TW.Container) = widthIn(max = token.points.dp)

private fun TW.Radius.shape() = if (this == TW.Radius.Full) RoundedCornerShape(percent = 50)
    else RoundedCornerShape(points.dp)

fun Modifier.pl(value: Number) = absolutePadding(left = TW.spacing(value))
fun Modifier.pr(value: Number) = absolutePadding(right = TW.spacing(value))
