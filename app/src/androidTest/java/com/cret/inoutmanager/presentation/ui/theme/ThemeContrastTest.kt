package com.cret.inoutmanager.presentation.ui.theme

import androidx.compose.ui.graphics.Color
import com.cret.inoutmanager.ui.theme.DarkBackground
import com.cret.inoutmanager.ui.theme.DarkError
import com.cret.inoutmanager.ui.theme.DarkOnBackground
import com.cret.inoutmanager.ui.theme.DarkOnError
import com.cret.inoutmanager.ui.theme.DarkOnPrimary
import com.cret.inoutmanager.ui.theme.DarkOnPrimaryContainer
import com.cret.inoutmanager.ui.theme.DarkOnSecondary
import com.cret.inoutmanager.ui.theme.DarkOnSecondaryContainer
import com.cret.inoutmanager.ui.theme.DarkOnSurface
import com.cret.inoutmanager.ui.theme.DarkOnSurfaceVariant
import com.cret.inoutmanager.ui.theme.DarkOutline
import com.cret.inoutmanager.ui.theme.DarkPrimary
import com.cret.inoutmanager.ui.theme.DarkPrimaryContainer
import com.cret.inoutmanager.ui.theme.DarkSecondary
import com.cret.inoutmanager.ui.theme.DarkSecondaryContainer
import com.cret.inoutmanager.ui.theme.DarkSurface
import com.cret.inoutmanager.ui.theme.LightBackground
import com.cret.inoutmanager.ui.theme.LightError
import com.cret.inoutmanager.ui.theme.LightOnBackground
import com.cret.inoutmanager.ui.theme.LightOnError
import com.cret.inoutmanager.ui.theme.LightOnPrimary
import com.cret.inoutmanager.ui.theme.LightOnPrimaryContainer
import com.cret.inoutmanager.ui.theme.LightOnSecondary
import com.cret.inoutmanager.ui.theme.LightOnSecondaryContainer
import com.cret.inoutmanager.ui.theme.LightOnSurface
import com.cret.inoutmanager.ui.theme.LightOnSurfaceVariant
import com.cret.inoutmanager.ui.theme.LightOutline
import com.cret.inoutmanager.ui.theme.LightPrimary
import com.cret.inoutmanager.ui.theme.LightPrimaryContainer
import com.cret.inoutmanager.ui.theme.LightSecondary
import com.cret.inoutmanager.ui.theme.LightSecondaryContainer
import com.cret.inoutmanager.ui.theme.LightSurface
import org.junit.Test

/**
 * 테라코타 브랜드 Light/Dark 팔레트의 주요 semantic role 쌍이 WCAG 대비 목표를 만족하는지 수치로 검증한다.
 * 일반 텍스트 전경/배경은 4.5:1, 의미 있는 비텍스트(경계 등)는 3:1을 목표로 한다(Issue #39).
 */
class ThemeContrastTest {

    private fun channel(c: Float): Double {
        val d = c.toDouble()
        return if (d <= 0.03928) d / 12.92 else Math.pow((d + 0.055) / 1.055, 2.4)
    }

    private fun luminance(color: Color): Double =
        0.2126 * channel(color.red) + 0.7152 * channel(color.green) + 0.0722 * channel(color.blue)

    private fun contrast(a: Color, b: Color): Double {
        val l1 = luminance(a)
        val l2 = luminance(b)
        val hi = maxOf(l1, l2)
        val lo = minOf(l1, l2)
        return (hi + 0.05) / (lo + 0.05)
    }

    private fun assertText(name: String, fg: Color, bg: Color) {
        val r = contrast(fg, bg)
        assert(r >= 4.5) { "$name 대비 ${"%.2f".format(r)} < 4.5:1 (일반 텍스트 목표)" }
    }

    private fun assertNonText(name: String, fg: Color, bg: Color) {
        val r = contrast(fg, bg)
        assert(r >= 3.0) { "$name 대비 ${"%.2f".format(r)} < 3.0:1 (비텍스트 목표)" }
    }

    @Test
    fun lightScheme_textRolePairs_meet_4_5() {
        assertText("Light onPrimary/primary", LightOnPrimary, LightPrimary)
        assertText("Light onSecondary/secondary", LightOnSecondary, LightSecondary)
        assertText("Light onBackground/background", LightOnBackground, LightBackground)
        assertText("Light onSurface/surface", LightOnSurface, LightSurface)
        assertText("Light onSurfaceVariant/surface", LightOnSurfaceVariant, LightSurface)
        assertText("Light onSurfaceVariant/background", LightOnSurfaceVariant, LightBackground)
        assertText("Light onPrimaryContainer/primaryContainer", LightOnPrimaryContainer, LightPrimaryContainer)
        assertText("Light onSecondaryContainer/secondaryContainer", LightOnSecondaryContainer, LightSecondaryContainer)
        assertText("Light onError/error", LightOnError, LightError)
        // primary를 표면 위 강조 텍스트(수량 등)로 쓰므로 surface 대비도 확인
        assertText("Light primary/surface", LightPrimary, LightSurface)
    }

    @Test
    fun lightScheme_nonTextRolePairs_meet_3() {
        assertNonText("Light outline/surface", LightOutline, LightSurface)
    }

    @Test
    fun darkScheme_textRolePairs_meet_4_5() {
        assertText("Dark onPrimary/primary", DarkOnPrimary, DarkPrimary)
        assertText("Dark onSecondary/secondary", DarkOnSecondary, DarkSecondary)
        assertText("Dark onBackground/background", DarkOnBackground, DarkBackground)
        assertText("Dark onSurface/surface", DarkOnSurface, DarkSurface)
        assertText("Dark onSurfaceVariant/surface", DarkOnSurfaceVariant, DarkSurface)
        assertText("Dark onPrimaryContainer/primaryContainer", DarkOnPrimaryContainer, DarkPrimaryContainer)
        assertText("Dark onSecondaryContainer/secondaryContainer", DarkOnSecondaryContainer, DarkSecondaryContainer)
        assertText("Dark onError/error", DarkOnError, DarkError)
        assertText("Dark primary/surface", DarkPrimary, DarkSurface)
    }

    @Test
    fun darkScheme_nonTextRolePairs_meet_3() {
        assertNonText("Dark outline/surface", DarkOutline, DarkSurface)
    }
}
