package com.cret.inoutmanager.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.window.Dialog

/**
 * 다이얼로그 내용을 별도 윈도우가 아니라 현재 컴포지션 안에 그릴지 여부입니다.
 *
 * Compose의 [Dialog]는 내용을 별도 윈도우에 렌더링하기 때문에, Preview 기반 스크린샷 도구(layoutlib)가
 * 그 내용을 캡처하지 못하고 빈 이미지를 만든다. 스크린샷 테스트에서만 이 값을 `true`로 제공해
 * 같은 내용을 인라인으로 그려 기준 이미지를 남긴다.
 *
 * 앱 실행 경로는 기본값 `false`를 그대로 사용하므로 다이얼로그 동작과 표시 방식이 달라지지 않는다.
 */
internal val LocalRenderDialogContentInline = staticCompositionLocalOf { false }

/**
 * [LocalRenderDialogContentInline]이 `false`(기본값)이면 기존과 동일하게 [content]를 [Dialog] 윈도우로
 * 감싸고, `true`이면 [onDismissRequest] 없이 현재 컴포지션 안에 바로 그립니다.
 *
 * 인라인 경로는 [Dialog]의 scrim과 화면 중앙 배치를 거치지 않으므로, 스크린샷은 다이얼로그가 실제로
 * 표시되는 위치가 아니라 다이얼로그의 **내용**만 담는다.
 */
@Composable
internal fun DialogContainer(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (LocalRenderDialogContentInline.current) {
        content()
    } else {
        Dialog(onDismissRequest = onDismissRequest) { content() }
    }
}
