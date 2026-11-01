package dev.tailwind.nativeui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun TWStack(modifier: Modifier = Modifier, alignment: Alignment = Alignment.TopStart,
            content: @Composable BoxScope.() -> Unit) {
    Box(modifier, contentAlignment = alignment, content = content)
}

@Composable
fun TWGrid(columns: Int, modifier: Modifier = Modifier, gap: Number = 0, content: LazyGridScope.() -> Unit) {
    require(columns > 0) { "Grid columns must be positive" }
    LazyVerticalGrid(columns = GridCells.Fixed(columns), modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(TW.spacing(gap)),
        verticalArrangement = Arrangement.spacedBy(TW.spacing(gap)), content = content)
}

enum class ScrollAxis { Vertical, Horizontal }

/** Native scrolling without a Material scrollbar. Direct children form a zero-gap row/column. */
@Composable
fun TWScroll(modifier: Modifier = Modifier, axis: ScrollAxis = ScrollAxis.Vertical,
             state: ScrollState = rememberScrollState(), content: @Composable () -> Unit) {
    if (axis == ScrollAxis.Vertical) {
        Column(modifier.verticalScroll(state)) { content() }
    } else {
        Row(modifier.horizontalScroll(state), verticalAlignment = Alignment.Top) { content() }
    }
}
