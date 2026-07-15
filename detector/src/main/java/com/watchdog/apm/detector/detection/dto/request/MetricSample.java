package com.watchdog.apm.detector.detection.dto.request;

/**
 * 한 시점의 서비스별 요청 집계 (metrics 스트림의 정제된 입력).
 * error rate 를 미리 계산하지 않고 원시 카운트로 들고 있어, 윈도우에서
 * sum(errors)/sum(total) 로 볼륨 가중 집계가 가능하다.
 *
 * @param service     서비스 이름 (OTel service.name)
 * @param timestampMs 이벤트 시각 (epoch millis)
 * @param total       구간 내 전체 요청 수
 * @param errors      구간 내 5xx 요청 수
 */
public record MetricSample(String service, long timestampMs, long total, long errors) {
}
