package com.watchdog.apm.detector.detection.dto.request;

/** metrics 스트림 입력: 서비스별 요청 집계(원시 카운트). error rate = errors/total. */
public record MetricSample(String service, long timestampMs, long total, long errors) {
}
