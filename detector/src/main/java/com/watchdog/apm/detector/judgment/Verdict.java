package com.watchdog.apm.detector.judgment;

/** 배포 회귀 판정 결과. */
public enum Verdict {
    /** 정상 — 배포가 회귀를 유발하지 않음. */
    NORMAL,
    /** 의심 — 배포 후 error rate 가 규칙을 넘음. alerts 로 발행 대상. */
    SUSPECT
}
