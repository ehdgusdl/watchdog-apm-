package com.watchdog.apm.detector.detection.service;

import com.watchdog.apm.detector.detection.dto.response.Verdict;

/** 배포 전후 error rate 비교 규칙: SUSPECT ⟺ after ≥ before×3 AND after ≥ 2%. */
public final class RegressionJudge {

    public static final double SPIKE_MULTIPLIER = 3.0;   // 상대 변화 배수 (서비스 무관)
    public static final double NOISE_FLOOR = 0.02;       // 절대 노이즈 하한

    public Verdict judge(double beforeErrorRate, double afterErrorRate) {
        boolean spiked = afterErrorRate >= beforeErrorRate * SPIKE_MULTIPLIER;
        boolean aboveFloor = afterErrorRate >= NOISE_FLOOR;
        return (spiked && aboveFloor) ? Verdict.SUSPECT : Verdict.NORMAL;
    }
}
