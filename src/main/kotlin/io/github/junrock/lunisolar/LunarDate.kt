package io.github.junrock.lunisolar

/**
 * 음력 날짜
 *
 * @property isLeapMonth 윤달이면 true
 */
data class LunarDate(
    val year: Int,
    val month: Int,
    val day: Int,
    val isLeapMonth: Boolean = false,
)
