package com.cret.inoutmanager.ui.theme

import androidx.compose.ui.graphics.Color

// 앱 아이콘 상단부의 테라코타를 원천색으로 삼아, 라이트·다크 두 모드의 Material 3 semantic role을
// 명시적으로 정의한다. 색값은 WCAG 대비(일반 텍스트 4.5:1, 큰 텍스트·의미 있는 비텍스트 3:1)를 만족하도록
// 선정했다(`ThemeContrastTest` 참고). 다크 모드는 단순 반전이 아니라 어두운 중립 표면 + 조정된 테라코타 tonal.

// ── Light ─────────────────────────────────────────────
val LightPrimary = Color(0xFFA85A38)            // 버튼·강조. onPrimary(흰색) 대비 5.02:1
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFEFE1D3)   // 칩·약한 강조 표면
val LightOnPrimaryContainer = Color(0xFF5A2E16) // 대비 8.92:1
val LightSecondary = Color(0xFF8B5E3C)          // 보조 강조(어두운 테라코타). onSecondary 대비 5.58:1
val LightOnSecondary = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFFEFE1D3)
val LightOnSecondaryContainer = Color(0xFF5A2E16)
val LightBackground = Color(0xFFF8F4EE)         // 브랜드 크림 배경
val LightOnBackground = Color(0xFF231A14)       // 대비 15.59:1
val LightSurface = Color(0xFFFFFFFF)
val LightOnSurface = Color(0xFF231A14)          // 대비 17.08:1
val LightSurfaceVariant = Color(0xFFEFE6DC)
val LightOnSurfaceVariant = Color(0xFF5F5349)   // 보조 텍스트. 흰색 위 7.45:1, 크림 위 6.8:1
val LightSurfaceContainer = Color(0xFFF3EDE5)
val LightSurfaceContainerHigh = Color(0xFFEDE6DC)
val LightError = Color(0xFFBA1A1A)              // onError(흰색) 대비 6.46:1
val LightOnError = Color(0xFFFFFFFF)
val LightOutline = Color(0xFF837468)            // 경계. 표면 대비 4.5:1
val LightOutlineVariant = Color(0xFFD5C7BA)

// ── Dark ──────────────────────────────────────────────
val DarkPrimary = Color(0xFFE8A87C)             // 밝은 테라코타. 표면 위 8.28:1
val DarkOnPrimary = Color(0xFF4A2410)           // 버튼 위 어두운 글자. 대비 6.66:1
val DarkPrimaryContainer = Color(0xFF6E3F23)
val DarkOnPrimaryContainer = Color(0xFFF5D9C8)  // 대비 6.52:1
val DarkSecondary = Color(0xFFD8B49A)
val DarkOnSecondary = Color(0xFF3A2416)         // 대비 7.56:1
val DarkSecondaryContainer = Color(0xFF53402F)
val DarkOnSecondaryContainer = Color(0xFFF5D9C8)
val DarkBackground = Color(0xFF1A1512)          // 어두운 따뜻한 중립
val DarkOnBackground = Color(0xFFEDE0D6)        // 대비 14.0:1
val DarkSurface = Color(0xFF221C18)
val DarkOnSurface = Color(0xFFEDE0D6)           // 대비 13.02:1
val DarkSurfaceVariant = Color(0xFF3A322C)
val DarkOnSurfaceVariant = Color(0xFFCFC0B4)    // 대비 9.5:1
val DarkSurfaceContainer = Color(0xFF2A231E)
val DarkSurfaceContainerHigh = Color(0xFF352D27)
val DarkError = Color(0xFFFFB4AB)               // 표면 위 9.92:1
val DarkOnError = Color(0xFF690005)             // 대비 7.72:1
val DarkOutline = Color(0xFF9C8D80)             // 경계. 표면 대비 5.24:1
val DarkOutlineVariant = Color(0xFF4F453D)
