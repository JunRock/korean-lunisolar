# korean-lunisolar

한국 음력(태음태양력)과 양력을 서로 바꾸는 Kotlin/JVM 라이브러리입니다.

- 한국천문연구원 음양력 자료로 만든 표를 내장해서 네트워크 없이 계산합니다.
- Kotlin 표준 라이브러리 말고는 의존하는 것이 없습니다.
- 윤달을 지원합니다.
- 지원 범위: 음력 1900년 1월 1일 ~ 2050년 11월 30일 (양력 1900-01-31 ~ 2051-01-12)

## 설치

```kotlin
dependencies {
    implementation("io.github.junrock:korean-lunisolar:0.0.2")
}
```

Java 17 이상, Kotlin은 2.0 이상에서 씁니다.

## 사용

```kotlin
import io.github.junrock.lunisolar.KoreanLunisolar
import io.github.junrock.lunisolar.LunarDate
import java.time.LocalDate

// 음력 → 양력
KoreanLunisolar.toSolar(2026, 10, 1)                       // 2026-11-09
KoreanLunisolar.toSolar(2017, 5, 1, isLeapMonth = true)    // 2017-06-24

// 양력 → 음력
KoreanLunisolar.toLunar(LocalDate.of(2026, 11, 9))         // 음력 2026년 10월 1일 (윤달 아님)

// 없는 날짜나 지원 범위 밖이면 예외 대신 null
KoreanLunisolar.toSolarOrNull(2026, 2, 31)                 // null

// 있는 음력 날짜인지 (그 달에 없는 날, 없는 윤달, 범위 밖이면 false)
KoreanLunisolar.isValidLunarDate(2026, 10, 30)             // true
KoreanLunisolar.isValidLunarDate(2026, 2, 31)              // false

// 그 음력 달의 날수 (29 또는 30)
KoreanLunisolar.daysInMonth(2026, 10)                      // 30

// 그 해의 윤달 (없으면 null)
KoreanLunisolar.leapMonth(2017)                            // 5
```

`toSolar`, `toLunar` 는 바꿀 수 없는 날짜에 `IllegalArgumentException` 을 던집니다.

## 정확도

음력은 공식으로 계산할 수 없고 한국천문연구원이 발표한 자료가 기준입니다.
이 라이브러리의 표는 그 자료에서 음력 달마다 양력 시작일과 날수를 받아 만들었습니다.
천문연 변환기는 양력 2050-12-31까지만 답하므로, 그 뒤 12일(양력 2051-01-01 ~ 01-12)은 천문연이 알려 주는 마지막 달의 날수(30일)로 계산한 값입니다.

테스트는 받은 자료 전체(`src/test/resources/kasi-lunar-months.csv`)와 대조합니다.

- 모든 음력 달의 초하루가 자료의 양력 날짜와 같은지
- 모든 음력 달의 날수가 자료와 같은지
- 지원 범위의 모든 날짜가 양력 → 음력 → 양력으로 그대로 돌아오는지

한국 음력은 중국 음력과 가끔 하루씩 다릅니다(예: 2027년·2028년 설날). 이 라이브러리는 한국 기준입니다.

## 표 다시 만들기

```bash
python3 tools/fetch_kasi.py > src/test/resources/kasi-lunar-months.csv   # 천문연 자료 받기
python3 tools/generate_table.py                                          # LunarYearData.kt 생성
./gradlew ktlintFormat                                                   # 생성한 파일 줄 맞춤
./gradlew test
```

## 라이선스

MIT
