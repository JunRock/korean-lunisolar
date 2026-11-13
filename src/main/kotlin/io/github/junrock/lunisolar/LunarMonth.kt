package io.github.junrock.lunisolar

/** 음력 한 달 — 윤달은 같은 번호의 평달 바로 뒤에 옴 */
internal data class LunarMonth(
    val month: Int,
    val isLeap: Boolean,
    val days: Int,
)
