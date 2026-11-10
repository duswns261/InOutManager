package com.cret.inoutmanager.presentation.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import com.cret.inoutmanager.ui.theme.InOutManagerTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * 테라코타 브랜드 Light/Dark 팔레트의 주요 semantic role 쌍이 WCAG 대비 목표를 만족하는지 수치로 검증한다.
 * 일반 텍스트 전경/배경은 4.5:1, 의미 있는 비텍스트(경계·아이콘 등)는 3:1을 목표로 한다(Issue #39).
 *
 * 원천 토큰(`LightPrimary` 등)이 아니라 [InOutManagerTheme]을 실제로 컴포즈해 `MaterialTheme.colorScheme`에서
 * 값을 읽는다. 이렇게 해야 토큰 자체의 대비뿐 아니라 `Theme.kt`가 토큰을 role에 **올바르게 배선했는지**까지 함께
 * 검증된다.
 *
 * 단언은 반드시 JUnit [assertTrue]를 쓴다. Kotlin의 `assert`는 JVM assertion이 켜져 있을 때만 동작하는데
 * Android 런타임에서는 기본 비활성이라, 쓰면 아무것도 검증하지 않고 통과한다.
 */
class ThemeContrastTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private companion object {
        const val TEXT_MIN = 4.5
        const val NON_TEXT_MIN = 3.0
    }

    private fun channel(c: Float): Double {
        val d = c.toDouble()
        return if (d <= 0.03928) d / 12.92 else Math.pow((d + 0.055) / 1.055, 2.4)
    }

    private fun luminance(color: Color): Double =
        0.2126 * channel(color.red) + 0.7152 * channel(color.green) + 0.0722 * channel(color.blue)

    private fun contrast(a: Color, b: Color): Double {
        val hi = maxOf(luminance(a), luminance(b))
        val lo = minOf(luminance(a), luminance(b))
        return (hi + 0.05) / (lo + 0.05)
    }

    /** [InOutManagerTheme]이 실제로 적용하는 [ColorScheme]을 컴포지션에서 읽어온다. */
    private fun scheme(darkTheme: Boolean): ColorScheme {
        var captured: ColorScheme? = null
        composeTestRule.setContent {
            InOutManagerTheme(darkTheme = darkTheme) {
                captured = MaterialTheme.colorScheme
            }
        }
        composeTestRule.waitForIdle()
        return requireNotNull(captured) { "ColorScheme을 컴포지션에서 읽지 못했습니다" }
    }

    /**
     * [pairs]의 모든 role 쌍을 검사하고, 미달 항목을 한 번에 모아 실패시킨다.
     * 하나만 보고 멈추지 않아야 남은 미달 쌍을 다음 실행까지 기다리지 않는다.
     */
    private fun verifyAll(
        label: String,
        minRatio: Double,
        pairs: List<Triple<String, Color, Color>>,
    ) {
        val failures = pairs.mapNotNull { (name, fg, bg) ->
            val ratio = contrast(fg, bg)
            if (ratio >= minRatio) null else "$name: %.2f:1 < %.1f:1".format(ratio, minRatio)
        }
        assertTrue(
            "$label 대비 목표 미달 ${failures.size}건\n" + failures.joinToString("\n"),
            failures.isEmpty(),
        )
    }

    private fun textPairs(s: ColorScheme) = listOf(
        Triple("onPrimary/primary", s.onPrimary, s.primary),
        Triple("onSecondary/secondary", s.onSecondary, s.secondary),
        Triple("onBackground/background", s.onBackground, s.background),
        Triple("onSurface/surface", s.onSurface, s.surface),
        Triple("onSurfaceVariant/surface", s.onSurfaceVariant, s.surface),
        Triple("onSurfaceVariant/background", s.onSurfaceVariant, s.background),
        Triple("onPrimaryContainer/primaryContainer", s.onPrimaryContainer, s.primaryContainer),
        Triple("onSecondaryContainer/secondaryContainer", s.onSecondaryContainer, s.secondaryContainer),
        Triple("onError/error", s.onError, s.error),
        // primary를 표면 위 강조 텍스트(수량 등)로 쓰므로 surface 대비도 확인한다.
        Triple("primary/surface", s.primary, s.surface),
    )

    /** 경계·아이콘처럼 의미를 전달하는 비텍스트 요소. 이미지 선택 + 버튼의 아이콘·테두리가 여기에 해당한다. */
    private fun nonTextPairs(s: ColorScheme) = listOf(
        Triple("outline/surface", s.outline, s.surface),
        Triple("outline/background", s.outline, s.background),
        Triple("onSurfaceVariant/surfaceVariant", s.onSurfaceVariant, s.surfaceVariant),
    )

    @Test
    fun lightScheme_textRolePairs_meet_4_5() {
        verifyAll("Light 텍스트", TEXT_MIN, textPairs(scheme(darkTheme = false)))
    }

    @Test
    fun lightScheme_nonTextRolePairs_meet_3() {
        verifyAll("Light 비텍스트", NON_TEXT_MIN, nonTextPairs(scheme(darkTheme = false)))
    }

    @Test
    fun darkScheme_textRolePairs_meet_4_5() {
        verifyAll("Dark 텍스트", TEXT_MIN, textPairs(scheme(darkTheme = true)))
    }

    @Test
    fun darkScheme_nonTextRolePairs_meet_3() {
        verifyAll("Dark 비텍스트", NON_TEXT_MIN, nonTextPairs(scheme(darkTheme = true)))
    }
}
