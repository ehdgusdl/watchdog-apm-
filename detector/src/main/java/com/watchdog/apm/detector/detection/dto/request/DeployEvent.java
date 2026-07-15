package com.watchdog.apm.detector.detection.dto.request;

/**
 * 배포 이벤트 (deploys 스트림 입력). 판정의 before/after 경계이자 트리거.
 *
 * @param service     배포된 서비스 이름
 * @param timestampMs 배포 시각 (epoch millis) — before/after 윈도우의 기준선
 * @param revision    배포 리비전 (롤백 대상 식별용)
 * @param trigger     배포 원인. ROLLBACK 은 판정에서 제외(무한 루프 방지)
 */
public record DeployEvent(String service, long timestampMs, String revision, Trigger trigger) {

    public enum Trigger {
        /** 일반 배포 — 판정 대상 */
        DEPLOY,
        /** 우리 Mitigation 이 만든 롤백 — 판정 제외 */
        ROLLBACK
    }

    public boolean isRollback() {
        return trigger == Trigger.ROLLBACK;
    }
}
