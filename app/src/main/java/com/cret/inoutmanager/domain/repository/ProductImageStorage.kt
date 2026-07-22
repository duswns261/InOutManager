package com.cret.inoutmanager.domain.repository

import java.io.File
import java.io.InputStream

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
     * 외부 [input] 스트림(예: Photo Picker가 반환한 content Uri)을 관리 대상 임시 파일로 복사합니다.
     * 복사된 파일이 비어 있거나 이미지로 decode할 수 없으면 부분 파일을 삭제하고 예외를 던집니다.
     * 호출자는 [input]을 열고 닫을 책임을 그대로 유지합니다.
     */
    fun importTemporaryFile(input: InputStream): File

    /**
     * [file]이 이 저장소가 관리하는 경로 안에 있고 실제로 decode 가능한 이미지인지 판정합니다.
     * 관리 대상 밖의 파일, 존재하지 않는 파일, decode할 수 없는 파일은 false를 반환합니다.
     */
    fun isUsableManagedImage(file: File): Boolean

    /**
     * 이 저장소가 관리하는 임시/영구 디렉터리 안의 파일만 삭제합니다.
     * 관리 대상 밖의 파일이 전달되면 아무 동작도 하지 않습니다.
     */
    fun delete(file: File)
}
