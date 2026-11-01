package consumer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import dev.tailwind.nativeui.*

@Composable fun Example() {
    TWPreflight {
        TWColumn(Modifier.p(6), gap = 4) {
            TWText("Welcome", style = TextStyle.Default.text(TW.TextSize.Xl).font(FontWeight.Bold))
            TWButton(onClick = {}, modifier = Modifier.rounded(TW.Radius.Lg).bg(Palette.blue500).px(4).py(2)) {
                TWText("Continue", style = TextStyle.Default.text(Palette.white))
            }
        }
    }
}
