package com.watchdog.apm.detector.detection.dto.request;

/** deploys 스트림 입력: 배포 이벤트(before/after 경계). ROLLBACK 은 판정 제외(루프 방지). */
public record DeployEvent(String service, long timestampMs, String revision, Trigger trigger) {

    public enum Trigger { DEPLOY, ROLLBACK }

    public boolean isRollback() {
        return trigger == Trigger.ROLLBACK;
    }
}
