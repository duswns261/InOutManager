package com.cret.inoutmanager.presentation.ui.search

import com.cret.inoutmanager.domain.model.Product
import java.text.Normalizer
import java.util.Locale

/** 완성형 한글 음절의 시작 코드포인트. */
private const val HANGUL_SYLLABLE_BASE = 0xAC00

/** 완성형 한글 음절의 끝 코드포인트. */
private const val HANGUL_SYLLABLE_LAST = 0xD7A3

/** 초성 21 * 중성 28 = 588. 완성형 음절 하나에서 초성 인덱스를 구하는 나눗수. */
private const val HANGUL_SYLLABLES_PER_CHOSUNG = 21 * 28

/** 유니코드 초성 자모(Choseong Jamo, `ᄀ`~`ᄒ`)의 시작 코드포인트. 완성형 음절과 결합 자모 입력 모두 대응하기 위해 필요하다. */
private const val CHOSUNG_JAMO_BASE = 0x1100

/**
 * 현대 한글 초성 19자를 호환 자모(Hangul Compatibility Jamo, 문자 입력에서 실제로 널리 쓰이는 형태)로 나열한 표.
 * 완성형 음절에서 추출한 초성 인덱스, 그리고 사용자가 입력한 초성 자모(Choseong Jamo)를 모두 이 표현으로 정규화해 비교한다.
 */
private val CHOSUNG_COMPAT_TABLE = charArrayOf(
    'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ',
    'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ',
)

/** 검색어와 제품명을 substring 비교에 쓸 수 있도록 정규화한다: 유니코드 NFC 정규화 + `Locale.ROOT` 소문자 변환. */
private fun normalizeForSubstring(value: String): String =
    Normalizer.normalize(value, Normalizer.Form.NFC).lowercase(Locale.ROOT)

/**
 * [c]가 초성으로 해석 가능하면 호환 자모 표현으로 정규화해 반환하고, 아니면 `null`을 반환한다.
 * 호환 자모 초성(`ㄱ` 등)은 그대로, 결합용 초성 자모(`ᄀ` 등)는 같은 표의 대응 문자로 변환한다.
 */
private fun normalizeChosungChar(c: Char): Char? {
    if (c in CHOSUNG_COMPAT_TABLE) return c
    val jamoIndex = c.code - CHOSUNG_JAMO_BASE
    return CHOSUNG_COMPAT_TABLE.getOrNull(jamoIndex)
}

/** [query]가 초성으로만 구성돼 있으면 호환 자모로 정규화한 초성 시퀀스를 반환하고, 하나라도 초성이 아니면 `null`을 반환한다. */
private fun toChosungQueryOrNull(query: String): String? {
    val normalized = Normalizer.normalize(query, Normalizer.Form.NFC)
    val builder = StringBuilder(normalized.length)
    for (c in normalized) {
        val chosung = normalizeChosungChar(c) ?: return null
        builder.append(chosung)
    }
    return builder.toString()
}

/** [name]에서 완성형 한글 음절의 초성만 뽑아 호환 자모 시퀀스로 이어붙인다. 한글이 아닌 문자는 건너뛴다. */
private fun extractChosungSequence(name: String): String {
    val normalized = Normalizer.normalize(name, Normalizer.Form.NFC)
    val builder = StringBuilder()
    for (c in normalized) {
        val code = c.code
        if (code in HANGUL_SYLLABLE_BASE..HANGUL_SYLLABLE_LAST) {
            val chosungIndex = (code - HANGUL_SYLLABLE_BASE) / HANGUL_SYLLABLES_PER_CHOSUNG
            builder.append(CHOSUNG_COMPAT_TABLE[chosungIndex])
        }
    }
    return builder.toString()
}

/**
 * [productName]이 [rawQuery]와 일치하는지 판정한다.
 * [rawQuery]가 초성으로만 구성돼 있으면 초성 부분 문자열 비교를, 그 외에는 일반 부분 문자열(대소문자 무시) 비교를 한다.
 */
fun matchesProductSearchQuery(productName: String, rawQuery: String): Boolean {
    val query = rawQuery.trim()
    if (query.isEmpty()) return true

    val chosungQuery = toChosungQueryOrNull(query)
    return if (chosungQuery != null) {
        extractChosungSequence(productName).contains(chosungQuery)
    } else {
        normalizeForSubstring(productName).contains(normalizeForSubstring(query))
    }
}

/** 수량 내림차순, 동률이면 id 오름차순으로 안정 정렬하는 공통 기준. */
val ProductDisplayOrder: Comparator<Product> =
    compareByDescending<Product> { it.quantity }.thenBy { it.id }

/**
 * [products]를 변경하지 않고, [query]가 공백이면 전체 목록을, 아니면 [matchesProductSearchQuery]를 만족하는
 * 항목만 걸러 새 목록으로 반환한다. 반환값은 항상 [ProductDisplayOrder] 기준으로 정렬돼 있다.
 */
fun filterAndSortProducts(products: List<Product>, query: String): List<Product> {
    val trimmed = query.trim()
    val filtered = if (trimmed.isEmpty()) {
        products
    } else {
        products.filter { matchesProductSearchQuery(it.name, trimmed) }
    }
    return filtered.sortedWith(ProductDisplayOrder)
}
