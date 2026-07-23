package com.cret.inoutmanager.presentation.ui.search

import com.cret.inoutmanager.domain.model.Product
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductSearchTest {

    // ---- matchesProductSearchQuery: 일반 부분 문자열 ----

    @Test
    fun `matches when query is substring at the start of the name`() {
        assertTrue(matchesProductSearchQuery("갤럭시 S24", "갤럭시"))
    }

    @Test
    fun `matches when query is substring in the middle of the name`() {
        assertTrue(matchesProductSearchQuery("삼성 갤럭시 S24", "갤럭시"))
    }

    @Test
    fun `matches when query is substring at the end of the name`() {
        assertTrue(matchesProductSearchQuery("아이폰 15", "15"))
    }

    @Test
    fun `matches ignoring english case`() {
        assertTrue(matchesProductSearchQuery("iPhone 15 Pro", "iphone"))
        assertTrue(matchesProductSearchQuery("iPhone 15 Pro", "PRO"))
    }

    @Test
    fun `matches mixed korean english and digit query`() {
        assertTrue(matchesProductSearchQuery("갤럭시 S24 Ultra", "s24"))
    }

    @Test
    fun `does not match when query is not contained in the name`() {
        assertFalse(matchesProductSearchQuery("갤럭시 S24", "아이폰"))
    }

    @Test
    fun `blank query matches everything`() {
        assertTrue(matchesProductSearchQuery("갤럭시 S24", ""))
        assertTrue(matchesProductSearchQuery("갤럭시 S24", "   "))
    }

    // ---- matchesProductSearchQuery: 한글 초성 ----

    @Test
    fun `matches compatibility jamo chosung query against chosung subsequence`() {
        // "갤럭시" 초성은 ㄱㄹㅅ. "ㄱㄹ"은 그 부분 문자열이다.
        assertTrue(matchesProductSearchQuery("갤럭시 S24", "ㄱㄹ"))
    }

    @Test
    fun `matches combining choseong jamo query the same as compatibility jamo`() {
        // U+1100(ᄀ), U+1105(ᄅ) — 결합용 초성 자모 입력도 호환 자모와 동일하게 정규화돼야 한다.
        val combiningChosungQuery = "${'ᄀ'}${'ᄅ'}"
        assertTrue(matchesProductSearchQuery("갤럭시 S24", combiningChosungQuery))
    }

    @Test
    fun `matches chosung query spanning across a space in the middle of the name`() {
        // "아이폰 15" 초성은 ㅇㅇㅍ (공백은 초성 추출에서 건너뛴다).
        assertTrue(matchesProductSearchQuery("아이폰 15", "ㅇㅇㅍ"))
    }

    @Test
    fun `does not match chosung query that is not a subsequence of the name chosung`() {
        assertFalse(matchesProductSearchQuery("갤럭시 S24", "ㅍㅍ"))
    }

    @Test
    fun `mixed chosung and non-chosung query falls back to plain substring matching`() {
        // 초성 문자와 완성형 문자가 섞이면 초성 전용 검색어가 아니므로 일반 substring 규칙을 적용한다.
        // "갤ㄹ"은 "갤럭시 S24"에 그대로 포함되지 않으므로 불일치해야 한다.
        assertFalse(matchesProductSearchQuery("갤럭시 S24", "갤ㄹ"))
    }

    // ---- filterAndSortProducts / ProductDisplayOrder ----

    @Test
    fun `filterAndSortProducts with blank query returns full list sorted by common order`() {
        val products = listOf(
            Product(id = 1, name = "A", location = "L", quantity = 5),
            Product(id = 2, name = "B", location = "L", quantity = 10),
        )

        val result = filterAndSortProducts(products, "  ")

        assertEquals(listOf(2, 1), result.map { it.id })
    }

    @Test
    fun `filterAndSortProducts excludes non-matching products`() {
        val products = listOf(
            Product(id = 1, name = "갤럭시 S24", location = "L", quantity = 5),
            Product(id = 2, name = "아이폰 15", location = "L", quantity = 10),
        )

        val result = filterAndSortProducts(products, "아이폰")

        assertEquals(listOf(2), result.map { it.id })
    }

    @Test
    fun `sort orders by quantity descending`() {
        val products = listOf(
            Product(id = 1, name = "A", location = "L", quantity = 3),
            Product(id = 2, name = "B", location = "L", quantity = 9),
            Product(id = 3, name = "C", location = "L", quantity = 6),
        )

        val result = filterAndSortProducts(products, "")

        assertEquals(listOf(2, 3, 1), result.map { it.id })
    }

    @Test
    fun `sort breaks quantity ties by id ascending`() {
        val products = listOf(
            Product(id = 3, name = "A", location = "L", quantity = 7),
            Product(id = 1, name = "B", location = "L", quantity = 7),
            Product(id = 2, name = "C", location = "L", quantity = 7),
        )

        val result = filterAndSortProducts(products, "")

        assertEquals(listOf(1, 2, 3), result.map { it.id })
    }

    @Test
    fun `filterAndSortProducts does not mutate the original list`() {
        val original = mutableListOf(
            Product(id = 1, name = "갤럭시", location = "L", quantity = 3),
            Product(id = 2, name = "아이폰", location = "L", quantity = 9),
        )
        val originalSnapshot = original.toList()

        val result = filterAndSortProducts(original, "아이폰")

        assertEquals(originalSnapshot, original)
        assertNotSame(original, result)
    }
}
