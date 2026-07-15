package com.watchdog.apm.detector.judgment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 판정 규칙 검증. detector/CLAUDE.md 의 테스트 케이스 중 순수 로직 해당분.
 * (워밍업 30초 제외 / rollback 제외는 스트림 토폴로지 레벨에서 별도 검증)
 */
class RegressionJudgeTest {

    private final RegressionJudge judge = new RegressionJudge();

    @Test
    @DisplayName("4% → 5% : 배수 미달(3배 안 됨) → NORMAL")
    void belowMultiplier() {
        assertEquals(Verdict.NORMAL, judge.judge(0.04, 0.05));
    }

    @Test
    @DisplayName("0.01% → 0.03% : 3배지만 절대값 미달(2% 미만) → NORMAL")
    void belowFloor() {
        assertEquals(Verdict.NORMAL, judge.judge(0.0001, 0.0003));
    }

    @Test
    @DisplayName("0.5% → 5% : 3배 이상 AND 2% 이상 → SUSPECT")
    void spikeAboveFloor() {
        assertEquals(Verdict.SUSPECT, judge.judge(0.005, 0.05));
    }

    @Test
    @DisplayName("경계: 정확히 3배 AND 정확히 2% → SUSPECT (>= 포함)")
    void exactBoundary() {
        assertEquals(Verdict.SUSPECT, judge.judge(0.02 / 3.0, 0.02));
    }

    @Test
    @DisplayName("0% → 2% : 무에서 하한 도달 → SUSPECT")
    void zeroBaselineToFloor() {
        assertEquals(Verdict.SUSPECT, judge.judge(0.0, 0.02));
    }

    @Test
    @DisplayName("0% → 1% : 무에서 급증이나 하한 미달 → NORMAL")
    void zeroBaselineBelowFloor() {
        assertEquals(Verdict.NORMAL, judge.judge(0.0, 0.01));
    }
}
