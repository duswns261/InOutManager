package com.cret.inoutmanager.domain.usecase

import java.io.File

/**
 * 관리 대상 이미지 파일 정리(cleanup)가 실패했음을 나타냅니다. 이 예외 자체를 던지지 않고
 * 원래 실패의 [Throwable.addSuppressed]로 붙여, cleanup 실패가 원래 오류를 가리거나
 * 대체하지 않으면서도 관찰 가능하게 합니다.
 */
class ProductImageCleanupFailedException(file: File) :
    Exception("Failed to delete managed image file: ${file.absolutePath}")
