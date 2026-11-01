package dev.tailwind.nativeui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

val LocalTWDarkTheme = staticCompositionLocalOf<Boolean?> { null }
val LocalTWTextStyle = staticCompositionLocalOf { TextStyle(color = Color.Black, fontSize = 16.sp) }

/** An explicit base text environment, independent of Material typography and colors. */
@Composable
fun TWPreflight(dark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val foreground = if (dark) Color.White else Color.Black
    CompositionLocalProvider(
        LocalTWDarkTheme provides dark,
        LocalTWTextStyle provides TextStyle(color = foreground, fontSize = 16.sp)
    ) {
        content()
    }
}

/** Foundation primitives carry no Material background, padding, border or minimum size. */
@Composable
fun TWText(text: String, modifier: Modifier = Modifier, style: TextStyle = TextStyle.Default,
           maxLines: Int = Int.MAX_VALUE, overflow: TextOverflow = TextOverflow.Clip, softWrap: Boolean = true) {
    require(maxLines > 0) { "Line clamp must be positive" }
    BasicText(text, modifier, style = LocalTWTextStyle.current.merge(style),
        maxLines = maxLines, overflow = overflow, softWrap = softWrap)
}
fun TextStyle.text(size: TW.TextSize) = copy(fontSize = size.native)
fun TextStyle.text(color: Color) = copy(color = color)
fun TextStyle.font(weight: FontWeight) = copy(fontWeight = weight)

@Composable
fun TWButton(onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true,
             interactionSource: MutableInteractionSource? = null,
             content: @Composable BoxScope.() -> Unit) {
    Box(modifier.clickable(interactionSource = interactionSource, indication = null,
        enabled = enabled, role = Role.Button, onClick = onClick), content = content)
}
@Composable
fun TWInput(state: TextFieldState, modifier: Modifier = Modifier,
            enabled: Boolean = true, readOnly: Boolean = false,
            lineLimits: TextFieldLineLimits = TextFieldLineLimits.SingleLine,
            style: TextStyle = TextStyle.Default,
            interactionSource: MutableInteractionSource? = null,
            keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
            onKeyboardAction: KeyboardActionHandler? = null,
            inputTransformation: InputTransformation? = null,
            outputTransformation: OutputTransformation? = null) {
    val textStyle = LocalTWTextStyle.current.merge(style)
    BasicTextField(state = state, modifier = modifier, enabled = enabled, readOnly = readOnly,
        lineLimits = lineLimits, textStyle = textStyle, cursorBrush = SolidColor(textStyle.color),
        interactionSource = interactionSource, keyboardOptions = keyboardOptions,
        onKeyboardAction = onKeyboardAction, inputTransformation = inputTransformation,
        outputTransformation = outputTransformation)
}
@Composable
fun TWRow(modifier: Modifier = Modifier, gap: Number = 0,
          alignment: Alignment.Vertical = Alignment.CenterVertically,
          content: @Composable RowScope.() -> Unit) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(TW.spacing(gap)),
        verticalAlignment = alignment, content = content)
}
@Composable
fun TWColumn(modifier: Modifier = Modifier, gap: Number = 0,
             alignment: Alignment.Horizontal = Alignment.Start,
             content: @Composable ColumnScope.() -> Unit) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(TW.spacing(gap)),
        horizontalAlignment = alignment, content = content)
}
