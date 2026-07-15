package com.watchdog.apm.detector.detection.dto.response;

/** 배포 회귀 판정 결과. SUSPECT 는 alerts 로 발행 대상. */
public enum Verdict {
    NORMAL,
    SUSPECT
}
