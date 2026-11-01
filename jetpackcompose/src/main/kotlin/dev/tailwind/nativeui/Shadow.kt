package dev.tailwind.nativeui

import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.clipPath
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

/** Tailwind outer box-shadow masks are cached per size/token, including on Android API 21. */
fun Modifier.shadow(token: TW.Shadow, color: Color = Color.Black, radius: TW.Radius = TW.Radius.None) =
    shadow(layers = token.layers, color = color, radius = radius)

fun Modifier.shadow(layers: List<TW.ShadowLayer>, color: Color = Color.Black,
                    radius: TW.Radius = TW.Radius.None) = drawWithCache {
    val corner = if (radius == TW.Radius.Full) size.minDimension / 2 else radius.points * density
    val outline = Path().apply { addRoundRect(RoundRect(Rect(Offset.Zero, size), CornerRadius(corner))) }
    val masks = layers.asReversed().mapNotNull { layer ->
        val spread = layer.spread * density
        if (size.width + spread * 2 <= 0 || size.height + spread * 2 <= 0) return@mapNotNull null
        val blur = layer.blur * density / 2
        val padding = ceil(blur * 4 + abs(spread) + 1).toInt()
        val bitmap = Bitmap.createBitmap(ceil(size.width).toInt() + padding * 2,
            ceil(size.height).toInt() + padding * 2, Bitmap.Config.ARGB_8888)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = android.graphics.Color.BLACK
            // Android converts its legacy radius to sigma as radius * 0.57735 + 0.5.
            // Invert that mapping so the CSS blur token corresponds to sigma = blur / 2.
            if (blur > 0) maskFilter = BlurMaskFilter(max(0.001f, (blur - 0.5f) / 0.57735f), BlurMaskFilter.Blur.NORMAL)
        }
        val canvas = android.graphics.Canvas(bitmap)
        val shadowCorner = if (radius == TW.Radius.Full) min(size.width + spread * 2, size.height + spread * 2) / 2
            else shadowCornerRadius(corner, spread)
        canvas.drawRoundRect(RectF(padding - spread, padding - spread,
            padding + size.width + spread, padding + size.height + spread), shadowCorner, shadowCorner, paint)
        Triple(bitmap.asImageBitmap(), Offset(layer.x * density - padding, layer.y * density - padding), layer.opacity)
    }
    onDrawBehind {
        clipPath(outline, ClipOp.Difference) {
            masks.forEach { (image, offset, opacity) ->
                drawImage(image, topLeft = offset, alpha = opacity, colorFilter = ColorFilter.tint(color))
            }
        }
    }
}

// CSS Backgrounds 3, Shadow Shape: keep sharp corners sharp under positive spread.
internal fun shadowCornerRadius(radius: Float, spread: Float): Float {
    val ratio = if (spread > 0 && radius < spread) radius / spread - 1 else 0f
    val adjusted = if (spread > 0 && radius < spread) spread * (1 + ratio * ratio * ratio) else spread
    return max(0f, radius + adjusted)
}
