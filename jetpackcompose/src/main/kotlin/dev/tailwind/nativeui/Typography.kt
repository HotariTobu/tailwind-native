package dev.tailwind.nativeui

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.em

fun TextStyle.italicText() = copy(fontStyle = FontStyle.Italic)
fun TextStyle.underlineText() = copy(textDecoration = TextDecoration.Underline)
fun TextStyle.lineThrough() = copy(textDecoration = TextDecoration.LineThrough)
fun TextStyle.tracking(value: Float): TextStyle {
    require(value.isFinite()) { "Tracking must be finite" }
    return copy(letterSpacing = value.em)
}
fun TextStyle.leading(value: Float): TextStyle {
    require(value.isFinite() && value > 0) { "Line height must be positive and finite" }
    return copy(lineHeight = value.em)
}
fun TextStyle.textCenter() = copy(textAlign = TextAlign.Center)
fun TextStyle.textStart() = copy(textAlign = TextAlign.Start)
fun TextStyle.textEnd() = copy(textAlign = TextAlign.End)

fun TextStyle.tracking(token: TW.Tracking) = tracking(token.em)
fun TextStyle.leading(token: TW.Leading) = leading(token.em)
fun TextStyle.font(weight: TW.Weight) = copy(fontWeight = weight.native)
