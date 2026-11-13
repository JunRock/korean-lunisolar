package io.github.junrock.lunisolar

import java.time.LocalDate

/** [LunarYearData] 를 풀어 만든 음력 해 목록 — 어느 해를 볼지 찾아 주는 역할 */
internal object LunarTable {
    private const val BASE_YEAR = 1900
    private const val BIG_MONTH_DAYS = 30
    private const val SMALL_MONTH_DAYS = 29
    private const val LEAP_SHIFT = 13
    private const val COUNT_SHIFT = 17
    private const val FOUR_BITS = 0xF

    /** 음력 1900년 1월 1일의 양력 날짜 */
    val FIRST_EPOCH_DAY: Long = LocalDate.of(1900, 1, 31).toEpochDay()

    private val years: List<LunarYear> =
        buildList {
            var startEpochDay = FIRST_EPOCH_DAY
            LunarYearData.YEARS.forEachIndexed { index, packed ->
                add(LunarYear(BASE_YEAR + index, decode(packed), startEpochDay))
                startEpochDay += last().days
            }
        }

    private val yearStarts: LongArray = LongArray(years.size) { years[it].startEpochDay }

    /** 표에 든 마지막 음력 달의 말일 (음력 2050년 11월 30일) */
    val LAST_EPOCH_DAY: Long = years.last().let { it.startEpochDay + it.days - 1 }

    /** 그 음력 해. 지원 범위 밖이면 null */
    fun yearOf(year: Int): LunarYear? = years.getOrNull(year - BASE_YEAR)

    /** 그 양력 날짜가 속한 음력 해. 지원 범위 밖이면 null */
    fun yearContaining(epochDay: Long): LunarYear? {
        if (epochDay !in FIRST_EPOCH_DAY..LAST_EPOCH_DAY) return null
        val found = yearStarts.binarySearch(epochDay)
        return years[if (found >= 0) found else -found - 2]
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
