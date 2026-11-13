package io.github.junrock.lunisolar

import java.time.LocalDate

/**
 * 한국 음력(태음태양력) <-> 양력 변환
 *
 * 한국천문연구원 음양력 자료로 만든 표를 내장 → 네트워크 없이 계산
 * 지원 범위는 [MIN_SOLAR] ~ [MAX_SOLAR] (음력 1900년 1월 1일 ~ 2050년 11월 30일)
 * 범위 밖의 해·달·날짜를 물으면 OrNull 함수와 [daysInMonth], [leapMonth] 는 null 을 돌려줌
 */
object KoreanLunisolar {
    /** 변환할 수 있는 가장 이른 양력 날짜 (음력 1900년 1월 1일) */
    @JvmField
    val MIN_SOLAR: LocalDate = LocalDate.ofEpochDay(LunarTable.FIRST_EPOCH_DAY)

    /** 변환할 수 있는 가장 늦은 양력 날짜 (음력 2050년 11월 30일) */
    @JvmField
    val MAX_SOLAR: LocalDate = LocalDate.ofEpochDay(LunarTable.LAST_EPOCH_DAY)

    /**
     * 음력 → 양력
     *
     * @throws IllegalArgumentException 없는 날짜거나 지원 범위 밖일 때
     */
    @JvmStatic
    @JvmOverloads
    fun toSolar(
        year: Int,
        month: Int,
        day: Int,
        isLeapMonth: Boolean = false,
    ): LocalDate =
        requireNotNull(toSolarOrNull(year, month, day, isLeapMonth)) {
            "변환할 수 없는 음력 날짜: $year-$month-$day" + if (isLeapMonth) " (윤달)" else ""
        }

    @JvmStatic
    fun toSolar(lunar: LunarDate): LocalDate = toSolar(lunar.year, lunar.month, lunar.day, lunar.isLeapMonth)

    /** 음력 → 양력. 없는 날짜거나 지원 범위 밖이면 null */
    @JvmStatic
    @JvmOverloads
    fun toSolarOrNull(
        year: Int,
        month: Int,
        day: Int,
        isLeapMonth: Boolean = false,
    ): LocalDate? = LunarTable.yearOf(year)?.epochDayOf(month, day, isLeapMonth)?.let(LocalDate::ofEpochDay)

    /**
     * 양력 → 음력
     *
     * @throws IllegalArgumentException 지원 범위 밖일 때
     */
    @JvmStatic
    fun toLunar(solar: LocalDate): LunarDate = requireNotNull(toLunarOrNull(solar)) { "지원 범위($MIN_SOLAR ~ $MAX_SOLAR) 밖의 양력 날짜: $solar" }

    /** 양력 → 음력. 지원 범위 밖이면 null */
    @JvmStatic
    fun toLunarOrNull(solar: LocalDate): LunarDate? = solar.toEpochDay().let { LunarTable.yearContaining(it)?.dateAt(it) }

    /** 그 음력 달의 날수(29 또는 30). 없는 달이거나 지원 범위 밖이면 null */
    @JvmStatic
    @JvmOverloads
    fun daysInMonth(
        year: Int,
        month: Int,
        isLeapMonth: Boolean = false,
    ): Int? = LunarTable.yearOf(year)?.daysIn(month, isLeapMonth)

    /** 그 음력 해의 윤달이 몇 월 뒤에 오는지. 윤달이 없거나 지원 범위 밖의 해면 null */
    @JvmStatic
    fun leapMonth(year: Int): Int? = LunarTable.yearOf(year)?.leapMonth
}
