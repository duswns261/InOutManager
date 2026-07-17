package com.cret.inoutmanager.domain.repository

import java.io.File

/**
 * 제품 이미지 파일의 임시/영구 저장을 다루는 SDK 독립적인 계약입니다.
 * 구현체는 [com.cret.inoutmanager.data.image] 패키지로 격리합니다.
 */
interface ProductImageStorage {

    /** CameraX 촬영 출력 대상으로 사용할 관리 대상 임시 파일 경로를 생성합니다. */
    fun createTemporaryFile(): File

    /** 임시 파일을 영구 저장 위치로 확정하고, 확정된 파일을 반환합니다. */
    fun commit(temporaryFile: File): File

    /**
     * 이 저장소가 관리하는 임시/영구 디렉터리 안의 파일만 삭제합니다.
     * 관리 대상 밖의 파일이 전달되면 아무 동작도 하지 않습니다.
     */
    fun delete(file: File)
}
