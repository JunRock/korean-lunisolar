"""천문연 자료 CSV 로 LunarYearData.kt 를 만듦.

사용: python3 tools/generate_table.py
입력: src/test/resources/kasi-lunar-months.csv (tools/fetch_kasi.py 결과)
출력: src/main/kotlin/io/github/junrock/lunisolar/LunarYearData.kt
"""
import csv
import datetime
from collections import defaultdict
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
SOURCE = ROOT / "src/test/resources/kasi-lunar-months.csv"
TARGET = ROOT / "src/main/kotlin/io/github/junrock/lunisolar/LunarYearData.kt"

years = defaultdict(list)
with SOURCE.open() as file:
    for row in csv.DictReader(file):
        years[int(row["lunar_year"])].append(
            (int(row["lunar_month"]), int(row["is_leap"]), datetime.date.fromisoformat(row["solar_date"]), int(row["days"]))
        )

# 자료 자체 검증: 달이 빈틈없이 이어져야 함 (앞 달 시작일 + 날수 = 다음 달 시작일)
flat = [month for year in sorted(years) for month in years[year]]
for previous, current in zip(flat, flat[1:]):
    assert previous[2] + datetime.timedelta(days=previous[3]) == current[2], (previous, current)
assert sorted(years) == list(range(1900, 2051)), "빠진 해가 있음"
assert flat[0][2] == datetime.date(1900, 1, 31)
# 마지막 달은 이어지는 달이 없어 위 검사에 안 걸림 → 값을 직접 확인 (응답이 비면 표가 조용히 짧아짐)
assert len(flat) == 1867 and flat[-1] == (11, 0, datetime.date(2050, 12, 14), 30), flat[-1]

lines = []
for year in sorted(years):
    months = years[year]
    assert all(days in (29, 30) for _, _, _, days in months)
    leaps = [month for month, is_leap, _, _ in months if is_leap]
    assert len(leaps) <= 1
    packed = sum(1 << position for position, (_, _, _, days) in enumerate(months) if days == 30)
    packed |= (leaps[0] if leaps else 0) << 13
    packed |= len(months) << 17
    lines.append(f"        0x{packed:06X}, // {year}")
lines[-1] = lines[-1].replace(", //", " //")

TARGET.write_text(
    "package io.github.junrock.lunisolar\n\n"
    "/**\n"
    " * 한국천문연구원 음양력 자료로 만든 표 — tools/generate_table.py 가 생성, 직접 고치지 말 것\n"
    " *\n"
    " * 음력 1900년부터 한 해에 정수 하나 (형식은 [LunarTable] 참고)\n"
    " */\n"
    "internal object LunarYearData {\n"
    "    val YEARS: IntArray = intArrayOf(\n" + "\n".join(lines) + "\n    )\n}\n"
)
print(f"{len(years)}년, {len(flat)}개 달 → {TARGET.relative_to(ROOT)}")
