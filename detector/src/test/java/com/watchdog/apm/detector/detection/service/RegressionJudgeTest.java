package com.watchdog.apm.detector.detection.service;

import com.watchdog.apm.detector.detection.dto.response.Verdict;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegressionJudgeTest {

    private final RegressionJudge judge = new RegressionJudge();

    @Test
    @DisplayName("4% → 5% : 배수 미달 → NORMAL")
    void belowMultiplier() {
        assertEquals(Verdict.NORMAL, judge.judge(0.04, 0.05));
    }

    @Test
    @DisplayName("0.01% → 0.03% : 3배지만 절대값 미달 → NORMAL")
    void belowFloor() {
        assertEquals(Verdict.NORMAL, judge.judge(0.0001, 0.0003));
    }

    @Test
    @DisplayName("0.5% → 5% : 3배 이상 AND 2% 이상 → SUSPECT")
    void spikeAboveFloor() {
        assertEquals(Verdict.SUSPECT, judge.judge(0.005, 0.05));
    }

    @Test
    @DisplayName("경계: 정확히 3배 AND 정확히 2% → SUSPECT")
    void exactBoundary() {
        assertEquals(Verdict.SUSPECT, judge.judge(0.02 / 3.0, 0.02));
    }

    @Test
    @DisplayName("0% → 2% : 무에서 하한 도달 → SUSPECT")
    void zeroBaselineToFloor() {
        assertEquals(Verdict.SUSPECT, judge.judge(0.0, 0.02));
    }

    @Test
    @DisplayName("0% → 1% : 하한 미달 → NORMAL")
    void zeroBaselineBelowFloor() {
        assertEquals(Verdict.NORMAL, judge.judge(0.0, 0.01));
    }
}
