package io.github.junrock.lunisolar

import java.time.LocalDate

/** [LunarYearData] 를 풀어 해마다 달 목록과 양력 시작일을 계산해 둠 */
internal object LunarTable {
    private const val BASE_YEAR = 1900
    private const val BIG_MONTH_DAYS = 30
    private const val SMALL_MONTH_DAYS = 29
    private const val LEAP_SHIFT = 13
    private const val COUNT_SHIFT = 17
    private const val FOUR_BITS = 0xF

    /** 음력 1900년 1월 1일의 양력 날짜 */
    val BASE_EPOCH_DAY: Long = LocalDate.of(1900, 1, 31).toEpochDay()

    /** 한국천문연구원 자료가 제공되는 마지막 양력 날짜 */
    val MAX_EPOCH_DAY: Long = LocalDate.of(2050, 12, 31).toEpochDay()

    private val months: List<List<LunarMonth>> = LunarYearData.YEARS.map(::decode)

    private val yearStarts: LongArray =
        LongArray(months.size).also { starts ->
            var epochDay = BASE_EPOCH_DAY
            months.forEachIndexed { index, yearMonths ->
                starts[index] = epochDay
                epochDay += yearMonths.sumOf { it.days }
            }
        }

    fun monthsOf(year: Int): List<LunarMonth>? = months.getOrNull(year - BASE_YEAR)

    fun yearStart(year: Int): Long = yearStarts[year - BASE_YEAR]

    /** 그 양력 날짜가 속한 음력 해 — 호출 전에 지원 범위 안인지 확인해야 함 */
    fun yearContaining(epochDay: Long): Int {
        val found = yearStarts.binarySearch(epochDay)
        val index = if (found >= 0) found else -found - 2
        return BASE_YEAR + index
    }

    /**
     * 한 해를 담은 정수 풀기
     * - 0~12 비트: 순서대로 n 번째 달이 30일이면 1
     * - 13~16 비트: 윤달이 붙는 달 번호 (없으면 0)
     * - 17~20 비트: 자료가 있는 달 수
     */
    private fun decode(packed: Int): List<LunarMonth> {
        val leapAfter = (packed shr LEAP_SHIFT) and FOUR_BITS
        val count = (packed shr COUNT_SHIFT) and FOUR_BITS
        var month = 0
        var leapUsed = false
        return (0 until count).map { position ->
            val isLeap = leapAfter != 0 && month == leapAfter && !leapUsed
            if (isLeap) leapUsed = true else month++
            val days = if ((packed shr position) and 1 == 1) BIG_MONTH_DAYS else SMALL_MONTH_DAYS
            LunarMonth(month, isLeap, days)
        }
    }
}
