"""한국천문연구원 음양력 변환에서 음력 달별 자료(양력 시작일·달 길이·윤달 여부)를 받아 CSV 로 저장.

사용: python3 tools/fetch_kasi.py > src/test/resources/kasi-lunar-months.csv
"""
import json
import sys
import time
import urllib.request

START_YEAR, END_YEAR = 1900, 2050


def fetch(year, month):
    url = f"https://astro.kasi.re.kr/life/lunc?yyyy={year}&mm={month:02d}&dd=01"
    for attempt in range(5):
        try:
            with urllib.request.urlopen(url, timeout=20) as response:
                return json.loads(response.read().decode())
        except Exception:
            time.sleep(1 + attempt)
    raise RuntimeError(f"조회 실패 {year}-{month}")


print("lunar_year,lunar_month,is_leap,solar_date,days")
for year in range(START_YEAR, END_YEAR + 1):
    for month in range(1, 13):
        rows = fetch(year, month)
        # 평달 먼저, 윤달 다음 순서로 기록
        for row in sorted(rows, key=lambda r: r["LUNC_LEAP_MM"] == "윤"):
            leap = 1 if row["LUNC_LEAP_MM"] == "윤" else 0
            solar = f'{row["SOLC_YYYY"]}-{row["SOLC_MM"]}-{row["SOLC_DD"]}'
            print(f'{year},{month},{leap},{solar},{row["LUNC_EN_DD"]}')
        time.sleep(0.03)
    print(f"{year} 완료", file=sys.stderr, flush=True)
