package dev.tailwind.nativeui

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur as nativeBlur
import androidx.compose.ui.unit.dp

/** Native blur; Android's rendering API supports this effect starting at API 31. */
fun Modifier.blur(token: TW.Blur) = nativeBlur(token.points.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
fun Modifier.blur(points: Number): Modifier {
    val radius = points.toFloat()
    require(radius.isFinite() && radius >= 0) { "Blur radius must be finite and nonnegative" }
    return nativeBlur(radius.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
}
