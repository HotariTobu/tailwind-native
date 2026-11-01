package dev.tailwind.nativeui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class TokensTest {
    @Test fun spacingUsesFourPointsPerUnit() {
        assertEquals(0f, TW.spacing(0).value, 0f)
        assertEquals(2f, TW.spacing(0.5).value, 0f)
        assertEquals(16f, TW.spacing(4).value, 0f)
        assertEquals(384f, TW.spacing(96).value, 0f)
    }
    @Test fun invalidSpacingIsRejected() {
        for (value in listOf(-1.0, Double.NaN, Double.POSITIVE_INFINITY, Double.MAX_VALUE, Float.MAX_VALUE.toDouble(), -Double.MIN_VALUE)) {
            assertThrows(IllegalArgumentException::class.java) { TW.spacing(value) }
        }
    }
    @Test fun breakpointBoundariesAreInclusive() {
        assertEquals(false, Breakpoint.Md.matches(767f))
        assertEquals(true, Breakpoint.Md.matches(768f))
    }
    @Test fun invalidOpacityIsRejected() {
        for (value in listOf(-1.0, 101.0, Double.NaN)) {
            assertThrows(IllegalArgumentException::class.java) {
                androidx.compose.ui.Modifier.opacityPercent(value)
            }
        }
    }
    @Test fun zeroBorderDoesNotAddHairline() {
        val modifier = androidx.compose.ui.Modifier
        assertEquals(modifier, modifier.border(Palette.black, width = 0f))
    }
    @Test fun numericScalesMatchReference() {
        assertEquals(listOf(0, 2, 4, 6, 8, 12, 16, 24, 32, 9999), TW.Radius.entries.map { it.points })
        assertEquals(listOf(16, 20, 24, 28, 28, 32, 36, 40, 48, 60, 72, 96, 128), TW.TextSize.entries.map { it.lineHeight })
        assertEquals((100..900 step 100).toList(), TW.Weight.entries.map { it.points })
        assertEquals(0.025f, TW.Tracking.Wide.em, 0f)
        assertEquals(320, TW.Container.Xs.points)
    }
    @Test fun shadowLayersMatchReference() {
        assertEquals(listOf(TW.ShadowLayer(0f, 4f, 6f, -1f, 0.1f),
            TW.ShadowLayer(0f, 2f, 4f, -2f, 0.1f)), TW.Shadow.Md.layers)
        assertEquals(emptyList<TW.ShadowLayer>(), TW.Shadow.None.layers)
        assertThrows(IllegalArgumentException::class.java) {
            TW.ShadowLayer(0f, 0f, -1f, 0f, 0.1f)
        }
    }
    @Test fun signedSpacingPreservesNegativeValues() {
        assertEquals(4.0, TW.spacingUnit, 0.0)
        assertEquals(-16f, TW.signedSpacing(-4).value, 0f)
    }
    @Test fun invalidGraphicsInputsAreRejected() {
        val modifier = androidx.compose.ui.Modifier
        val invalidOperations = listOf<() -> Unit>(
            { modifier.rotate(Double.NaN) },
            { modifier.scale(Double.POSITIVE_INFINITY) },
            { modifier.aspect(0f) },
            { modifier.aspect(Float.POSITIVE_INFINITY) },
            { modifier.translateX(Double.MAX_VALUE) },
            { modifier.opacityPercent(100.00000001) },
            { androidx.compose.ui.text.TextStyle.Default.tracking(Float.NaN) }
        )
        invalidOperations.forEach { operation ->
            assertThrows(IllegalArgumentException::class.java) { operation() }
        }
    }
    @Test fun shadowCornerSpreadMatchesSpecificationExamples() {
        assertEquals(27.5f, shadowCornerRadius(10f, 20f), 0f)
        assertEquals(0f, shadowCornerRadius(0f, 5f), 0f)
        assertEquals(8f, shadowCornerRadius(10f, -2f), 0f)
    }
    @Test fun defaultTokensMatchTailwind() {
        assertEquals(4, TW.Radius.Sm.points)
        assertEquals(8, TW.Radius.Lg.points)
        assertEquals(16, TW.TextSize.Base.points)
        assertEquals(30, TW.TextSize.Xxxl.points)
    }
}
