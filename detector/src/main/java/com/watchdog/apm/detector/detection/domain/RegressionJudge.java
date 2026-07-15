package com.watchdog.apm.detector.detection.domain;

/**
 * 배포 전후(before/after) error rate 를 비교해 회귀를 판정하는 순수 로직.
 *
 * <p>확정 규칙:
 * <pre>
 *   SUSPECT  ⟺  (after ≥ before × 3)  AND  (after ≥ 0.02)
 * </pre>
 *
 * <ul>
 *   <li>{@code before}/{@code after} = error rate = 5xx / 전체 요청 (0.0 ~ 1.0)</li>
 *   <li>{@code 3배}(SPIKE_MULTIPLIER) = 상대 변화량 — 서비스 무관하게 통함</li>
 *   <li>{@code 2%}(NOISE_FLOOR) = 노이즈 하한 — 저수준 3배 급증(예 0.1%→0.3%) 억제</li>
 * </ul>
 *
 * <p>절대 수준 임계값(예 "5% 넘으면 이상")은 서비스마다 달라 쓰지 않는다.
 * 통계 검정/분산도 쓰지 않는다(표본 부족·설명가능성).
 *
 * <p>참고: 워밍업(배포 직후 30초 제외)과 rollback 배포 제외는 이 순수 로직이 아니라
 * 상위 스트림(service)에서 처리한다. 여기 들어오는 before/after 는 이미 정제된 값이다.
 */
public final class RegressionJudge {

    /** 상대 변화 배수. after 가 before 의 이 배수 이상이어야 의심. */
    public static final double SPIKE_MULTIPLIER = 3.0;

    /** 절대 노이즈 하한. after 가 이 값 미만이면 배수와 무관하게 정상. */
    public static final double NOISE_FLOOR = 0.02;

    public Verdict judge(double beforeErrorRate, double afterErrorRate) {
        boolean spiked = afterErrorRate >= beforeErrorRate * SPIKE_MULTIPLIER;
        boolean aboveFloor = afterErrorRate >= NOISE_FLOOR;
        return (spiked && aboveFloor) ? Verdict.SUSPECT : Verdict.NORMAL;
    }
}
