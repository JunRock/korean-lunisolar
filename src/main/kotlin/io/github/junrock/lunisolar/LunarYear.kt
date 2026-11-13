package io.github.junrock.lunisolar

/**
 * 음력 한 해 — 달 목록과 그해 1월 1일의 양력 날짜(epoch day)
 *
 * 한 해 안에서 음력 날짜와 양력 날짜를 오가는 계산을 맡음
 */
internal class LunarYear(
    val year: Int,
    private val months: List<LunarMonth>,
    val startEpochDay: Long,
) {
    /** 이 해에 표가 가진 날수 */
    val days: Int = months.sumOf { it.days }

    /** 윤달이 몇 월 뒤에 오는지. 없으면 null */
    val leapMonth: Int? = months.firstOrNull { it.isLeap }?.month

    fun daysIn(
        month: Int,
        isLeap: Boolean,
    ): Int? = months.firstOrNull { it.month == month && it.isLeap == isLeap }?.days

    /** 음력 날짜 → 양력 epoch day. 이 해에 없는 날짜면 null */
    fun epochDayOf(
        month: Int,
        day: Int,
        isLeap: Boolean,
    ): Long? {
        var offset = 0
        for (lunarMonth in months) {
            if (lunarMonth.month == month && lunarMonth.isLeap == isLeap) {
                return if (day in 1..lunarMonth.days) startEpochDay + offset + (day - 1) else null
            }
            offset += lunarMonth.days
        }
        return null
    }

    /** 양력 epoch day → 음력 날짜. 이 해에 속한 날이어야 함 */
    fun dateAt(epochDay: Long): LunarDate {
        var remaining = (epochDay - startEpochDay).toInt()
        for (lunarMonth in months) {
            if (remaining < lunarMonth.days) return LunarDate(year, lunarMonth.month, remaining + 1, lunarMonth.isLeap)
            remaining -= lunarMonth.days
        }
        error("음력 ${year}년에 속하지 않는 날짜: $epochDay")
    }
}
