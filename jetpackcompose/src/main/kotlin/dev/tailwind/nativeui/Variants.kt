package dev.tailwind.nativeui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

fun Modifier.at(breakpoint: Breakpoint, width: Float, transform: Modifier.() -> Modifier) =
    variant(breakpoint.matches(width), transform)

@Composable
fun Modifier.dark(transform: Modifier.() -> Modifier): Modifier =
    variant(LocalTWDarkTheme.current ?: isSystemInDarkTheme(), transform)

/** Use the same interaction source on the native control and the variant. */
@Composable
fun Modifier.pressed(source: androidx.compose.foundation.interaction.InteractionSource,
                     transform: Modifier.() -> Modifier): Modifier =
    variant(source.collectIsPressedAsState().value, transform)

@Composable
fun Modifier.focus(source: androidx.compose.foundation.interaction.InteractionSource,
                   transform: Modifier.() -> Modifier): Modifier =
    variant(source.collectIsFocusedAsState().value, transform)

@Composable
fun Modifier.hover(source: androidx.compose.foundation.interaction.InteractionSource,
                   transform: Modifier.() -> Modifier): Modifier =
    variant(source.collectIsHoveredAsState().value, transform)
