package io.github.junrock.lunisolar

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class KoreanLunisolarTest {
    private data class OfficialMonth(
        val year: Int,
        val month: Int,
        val isLeap: Boolean,
        val solar: LocalDate,
        val days: Int,
    )

    // 한국천문연구원에서 받은 음력 달별 자료 (양력 시작일·날수)
    private val officialMonths: List<OfficialMonth> =
        checkNotNull(javaClass.getResourceAsStream("/kasi-lunar-months.csv")).bufferedReader().readLines().drop(1).map { line ->
            val (year, month, isLeap, solar, days) = line.split(",")
            OfficialMonth(year.toInt(), month.toInt(), isLeap == "1", LocalDate.parse(solar), days.toInt())
        }

    @Test
    fun `모든 음력 달의 초하루가 천문연 자료의 양력 날짜와 같다`() {
        officialMonths.forEach {
            assertEquals(it.solar, KoreanLunisolar.toSolar(it.year, it.month, 1, it.isLeap), "$it")
            assertEquals(LunarDate(it.year, it.month, 1, it.isLeap), KoreanLunisolar.toLunar(it.solar), "$it")
        }
    }

    @Test
    fun `모든 음력 달의 날수가 천문연 자료와 같다`() {
        officialMonths.forEach {
            assertEquals(it.days, KoreanLunisolar.daysInMonth(it.year, it.month, it.isLeap), "$it")
        }
    }

    @Test
    fun `지원 범위의 모든 날짜가 양력에서 음력으로 갔다가 그대로 돌아온다`() {
        var solar = KoreanLunisolar.MIN_SOLAR
        while (!solar.isAfter(KoreanLunisolar.MAX_SOLAR)) {
            assertEquals(solar, KoreanLunisolar.toSolar(KoreanLunisolar.toLunar(solar)))
            solar = solar.plusDays(1)
        }
    }

    @Test
    fun `알려진 날짜를 맞게 바꾼다`() {
        // 설날 — 2027·2028년은 한국과 중국이 하루 다른 해
        assertEquals(LocalDate.of(2026, 2, 17), KoreanLunisolar.toSolar(2026, 1, 1))
        assertEquals(LocalDate.of(2027, 2, 7), KoreanLunisolar.toSolar(2027, 1, 1))
        assertEquals(LocalDate.of(2028, 1, 27), KoreanLunisolar.toSolar(2028, 1, 1))
        // 추석
        assertEquals(LocalDate.of(2026, 9, 25), KoreanLunisolar.toSolar(2026, 8, 15))
        // 윤달
        assertEquals(LunarDate(2017, 5, 1, isLeapMonth = true), KoreanLunisolar.toLunar(LocalDate.of(2017, 6, 24)))
        assertEquals(5, KoreanLunisolar.leapMonth(2017))
        assertNull(KoreanLunisolar.leapMonth(2026))
    }

    @Test
    fun `없는 날짜와 범위 밖 날짜는 바꾸지 않는다`() {
        val smallMonth = officialMonths.first { it.days == 29 }
        assertNull(KoreanLunisolar.toSolarOrNull(smallMonth.year, smallMonth.month, 30, smallMonth.isLeap))
        assertNull(KoreanLunisolar.toSolarOrNull(2026, 1, 1, isLeapMonth = true))
        assertNull(KoreanLunisolar.toSolarOrNull(2026, 13, 1))
        assertNull(KoreanLunisolar.toSolarOrNull(1899, 1, 1))
        assertNull(KoreanLunisolar.toSolarOrNull(2051, 1, 1))
        assertNull(KoreanLunisolar.toLunarOrNull(KoreanLunisolar.MIN_SOLAR.minusDays(1)))
        assertNull(KoreanLunisolar.toLunarOrNull(KoreanLunisolar.MAX_SOLAR.plusDays(1)))
        assertFailsWith<IllegalArgumentException> { KoreanLunisolar.toSolar(2026, 2, 31) }
    }
}
