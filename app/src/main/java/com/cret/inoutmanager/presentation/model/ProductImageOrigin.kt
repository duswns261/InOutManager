package com.cret.inoutmanager.presentation.model

/**
 * 선택된 임시 제품 이미지의 출처입니다.
 * 카메라 저장 실패에만 기존 촬영 Analytics/Crashlytics reporting을 적용하기 위해
 * Presentation 계층에서만 사용하며 Domain/Product/Room에는 저장하지 않습니다.
 */
enum class ProductImageOrigin {
    CAMERA,
    PICKER,
}
