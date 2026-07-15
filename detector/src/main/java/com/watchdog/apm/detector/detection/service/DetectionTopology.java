package com.watchdog.apm.detector.detection.service;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Kafka Streams 토폴로지.
 * 현재는 연결 확인용 — metrics/deploys 토픽을 소비해 로그만 찍는다.
 * 다음 단계에서 여기에 판정 로직(윈도우 집계 → before/after → RegressionJudge → alerts)을 채운다.
 */
@Component
public class DetectionTopology {

    private static final Logger log = LoggerFactory.getLogger(DetectionTopology.class);

    @Autowired
    public void build(StreamsBuilder builder,
                      @Value("${watchdog.kafka.metrics-topic}") String metricsTopic,
                      @Value("${watchdog.kafka.deploys-topic}") String deploysTopic) {

        Consumed<String, String> asString = Consumed.with(Serdes.String(), Serdes.String());

        builder.stream(metricsTopic, asString)
                .peek((k, v) -> log.info("[metrics] {} bytes", v == null ? 0 : v.length()));

        builder.stream(deploysTopic, asString)
                .peek((k, v) -> log.info("[deploys] {}", v));
    }
}
