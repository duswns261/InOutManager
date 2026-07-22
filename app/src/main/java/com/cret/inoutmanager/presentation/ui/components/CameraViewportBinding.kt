package com.cret.inoutmanager.presentation.ui.components

/**
 * `PreviewView`의 레이아웃 크기와 display rotation을 나타내는 순수 값 객체입니다.
 * CameraX 타입에 의존하지 않아 unit test에서 바인딩 준비 여부와 rebind 조건을 검증할 수 있습니다.
 */
internal data class CameraBindKey(
    val width: Int,
    val height: Int,
    val rotation: Int,
)

/**
 * width/height가 모두 0보다 커야 `PreviewView.getViewPort()`가 layout 기준의 유효한 값을 반환합니다.
 * 이 조건을 통과하기 전에는 bind를 시도하지 않고 camera ready 상태를 false로 유지합니다.
 */
internal fun isCameraBindReady(width: Int, height: Int): Boolean = width > 0 && height > 0

/**
 * 이전에 bind한 key와 현재 key가 다르면 오래된 ViewPort/rotation으로 촬영하지 않도록 다시 bind해야 합니다.
 * `previous`가 없으면(최초 bind) 항상 true입니다.
 */
internal fun shouldRebindCamera(previous: CameraBindKey?, current: CameraBindKey): Boolean =
    previous != current
