package com.cret.inoutmanager.presentation.screenshot

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

/**
 * 컴포넌트 단위 Preview의 라이트·다크 쌍.
 * 배경과 전경이 같은 theme role 쌍에서 나오는지 확인하는 것이 목적이므로 두 모드를 항상 함께 기록한다.
 */
@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
annotation class LightDarkPreviews

/**
 * 전체 화면 Preview. 라이트·다크에 더해 큰 글꼴(`fontScale = 2f`)과 좁은 화면(`widthDp = 320`)을 포함해
 * 수동 확인 체크리스트의 접근성 항목을 자동 검증으로 옮긴다. 높이를 고정해 결과 이미지 크기를 안정화한다.
 */
@Preview(name = "Light", showBackground = true, widthDp = 360, heightDp = 640)
@Preview(name = "Dark", showBackground = true, widthDp = 360, heightDp = 640, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "LargeFont", showBackground = true, widthDp = 360, heightDp = 640, fontScale = 2f)
@Preview(name = "Narrow", showBackground = true, widthDp = 320, heightDp = 640)
annotation class ScreenPreviews

/**
 * Dialog Preview. 내용이 잘리지 않도록 화면보다 높이를 넉넉히 두고 동일한 접근성 조건을 적용한다.
 */
@Preview(name = "Light", showBackground = true, widthDp = 360, heightDp = 800)
@Preview(name = "Dark", showBackground = true, widthDp = 360, heightDp = 800, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "LargeFont", showBackground = true, widthDp = 360, heightDp = 800, fontScale = 2f)
@Preview(name = "Narrow", showBackground = true, widthDp = 320, heightDp = 800)
annotation class DialogPreviews
