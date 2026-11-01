package dev.tailwind.nativeui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Modifier
import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.Color
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.text.input.rememberTextFieldState
import kotlin.math.roundToInt
import androidx.compose.ui.platform.testTag
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.click
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31, 37])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class LayoutTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    @Test fun paddingAndSizingAffectLayout() {
        compose.setContent {
            Box(Modifier.testTag("box").p(2).w(10).h(5))
        }
        compose.onNodeWithTag("box").assertWidthIsEqualTo(56.dp).assertHeightIsEqualTo(36.dp)
    }

    @Test fun rowGapAffectsLayout() {
        compose.setContent {
            TWRow(modifier = Modifier.testTag("row"), gap = 3) {
                Box(Modifier.size(5))
                Box(Modifier.size(5))
            }
        }
        compose.onNodeWithTag("row").assertWidthIsEqualTo(52.dp).assertHeightIsEqualTo(20.dp)
    }

    @Test fun backgroundUsesGeneratedColor() {
        compose.setContent {
            Box(Modifier.testTag("color").size(5).bg(Palette.blue500))
        }
        val color = captureCenterColor("color")
        assertEquals(43f/255, color.red, 0.01f)
        assertEquals(127f/255, color.green, 0.01f)
        assertEquals(1f, color.blue, 0.01f)
    }

    @Test fun buttonPreservesNativeActions() {
        var clicks = 0
        compose.setContent {
            TWButton(onClick = { clicks++ }, modifier = Modifier.testTag("button").size(10)) {
                TWText("Save")
            }
        }
        compose.onNodeWithTag("button").assertIsEnabled().performClick()
        compose.runOnIdle { assertEquals(1, clicks) }
    }

    @Test fun disabledButtonDoesNotInvokeAction() {
        var clicks = 0
        compose.setContent {
            TWButton(onClick = { clicks++ }, enabled = false,
                modifier = Modifier.testTag("button").size(10)) { TWText("Save") }
        }
        compose.onNodeWithTag("button").assertIsNotEnabled().performTouchInput { click() }
        compose.runOnIdle { assertEquals(0, clicks) }
    }

    @Test fun plainInputRetainsEditing() {
        compose.setContent {
            val state = rememberTextFieldState()
            TWInput(state, Modifier.testTag("input").w(40))
        }
        compose.onNodeWithTag("input").performTextInput("Native")
        compose.onNodeWithTag("input").assertTextEquals("Native")
    }

    @Test fun explicitDarkThemeControlsVariants() {
        compose.setContent {
            TWPreflight(dark = true) {
                Box(Modifier.testTag("dark").size(5).dark { bg(Palette.white) })
            }
        }
        val pixel = captureCenterColor("dark")
        assertEquals(1f, pixel.red, 0.01f)
        assertEquals(1f, pixel.green, 0.01f)
        assertEquals(1f, pixel.blue, 0.01f)
    }


    @Test fun gridUsesEqualColumnsAndExplicitGap() {
        compose.setContent {
            TWGrid(columns = 2, gap = 2, modifier = Modifier.w(32).h(20)) {
                items(4) { index -> Box(Modifier.h(5).testTag("cell$index")) }
            }
        }
        compose.onNodeWithTag("cell0").assertWidthIsEqualTo(60.dp)
        val first = compose.onNodeWithTag("cell0").getUnclippedBoundsInRoot()
        val second = compose.onNodeWithTag("cell1").getUnclippedBoundsInRoot()
        val third = compose.onNodeWithTag("cell2").getUnclippedBoundsInRoot()
        assertEquals(68.dp, second.left - first.left)
        assertEquals(28.dp, third.top - first.top)
    }

    @Test fun nativeScrollCanRevealOffscreenContent() {
        compose.setContent {
            TWScroll(Modifier.h(20).w(20)) {
                repeat(5) { index -> Box(Modifier.size(10).testTag("item$index")) }
            }
        }
        compose.onNodeWithTag("item4").performScrollTo().assertIsDisplayed()
    }

    @Test fun physicalLeftPaddingStaysLeftInRTL() {
        compose.setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Box(Modifier.testTag("outer").pl(3)) { Box(Modifier.size(5).testTag("inner")) }
            }
        }
        val outer = compose.onNodeWithTag("outer").getUnclippedBoundsInRoot()
        val inner = compose.onNodeWithTag("inner").getUnclippedBoundsInRoot()
        assertEquals(12.dp, inner.left - outer.left)
    }

    @Test fun translationUsesPhysicalDirectionInRTL() {
        compose.setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Box(Modifier.testTag("outer").w(20).h(10)) {
                    // Measure the translated content, rather than the outer offset wrapper.
                    Box(Modifier.translateX(1).size(5).testTag("inner"))
                }
            }
        }
        val outer = compose.onNodeWithTag("outer").getUnclippedBoundsInRoot()
        val inner = compose.onNodeWithTag("inner").getUnclippedBoundsInRoot()
        assertEquals(64.dp, inner.left - outer.left)
    }

    @Test fun nativePointerDrivesPressedVariant() {
        compose.setContent {
            val source = remember { MutableInteractionSource() }
            TWButton(onClick = {}, interactionSource = source,
                modifier = Modifier.testTag("pressed").size(10).bg(Palette.blue500)
                    .pressed(source) { bg(Palette.red500) }) {}
        }
        compose.onNodeWithTag("pressed").performTouchInput { down(center) }
        val pressed = captureCenterColor("pressed")
        assertEquals(Palette.red500.red, pressed.red, 0.01f)
        assertEquals(Palette.red500.green, pressed.green, 0.01f)
        compose.onNodeWithTag("pressed").performTouchInput { up() }
        val released = captureCenterColor("pressed")
        assertEquals(Palette.blue500.red, released.red, 0.01f)
        assertEquals(Palette.blue500.green, released.green, 0.01f)
    }

    @Test fun boxShadowPaintsOutsideWithoutFillingInterior() {
        compose.setContent {
            Box(Modifier.testTag("shadowCanvas").size(30).bg(Palette.white)) {
                Box(Modifier.absoluteOffset(40.dp, 40.dp).size(10).shadow(TW.Shadow.Md))
            }
        }
        val bounds = compose.onNodeWithTag("shadowCanvas").fetchSemanticsNode().boundsInWindow
        val density = compose.activity.resources.displayMetrics.density
        val outside = captureColorAt(Offset(bounds.left + 38 * density, bounds.top + 60 * density))
        assertEquals(true, outside.red < 1f)
        assertEquals(outside.red, outside.green, 0.01f)
        val inside = captureColorAt(Offset(bounds.left + 60 * density, bounds.top + 60 * density))
        assertEquals(1f, inside.red, 0f)
        compose.onNodeWithTag("shadowCanvas").assertWidthIsEqualTo(120.dp).assertHeightIsEqualTo(120.dp)
    }

    @Test fun shadowNoneAddsNoPaint() {
        compose.setContent {
            Box(Modifier.testTag("shadowCanvas").size(30).bg(Palette.white)) {
                Box(Modifier.absoluteOffset(40.dp, 40.dp).size(10).shadow(TW.Shadow.None))
            }
        }
        assertEquals(Color.White, captureCenterColor("shadowCanvas"))
    }

    @Test fun hardShadowUsesPhysicalOffsetAndExplicitColor() {
        compose.setContent {
            Box(Modifier.testTag("shadowCanvas").size(30).bg(Palette.white)) {
                Box(Modifier.absoluteOffset(40.dp, 40.dp).size(10).shadow(
                    layers = listOf(TW.ShadowLayer(4f, 0f, 0f, 0f, 1f)), color = Palette.blue500))
            }
        }
        val bounds = compose.onNodeWithTag("shadowCanvas").fetchSemanticsNode().boundsInWindow
        val density = compose.activity.resources.displayMetrics.density
        assertEquals(Palette.blue500, captureColorAt(Offset(bounds.left + 82 * density, bounds.top + 60 * density)))
        assertEquals(Color.White, captureCenterColor("shadowCanvas"))
    }

    private fun captureCenterColor(tag: String): Color =
        captureColorAt(compose.onNodeWithTag(tag).fetchSemanticsNode().boundsInWindow.center)

    // PixelCopy under Robolectric/API 37 can return a stale window buffer.
    // Render the actual view tree with native graphics for deterministic pixel assertions.
    private fun captureColorAt(center: Offset): Color = compose.runOnIdle {
        val view = compose.activity.window.decorView
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        try {
            view.draw(Canvas(bitmap))
            Color(bitmap.getPixel(center.x.roundToInt(), center.y.roundToInt()))
        } finally {
            bitmap.recycle()
        }
    }
}
