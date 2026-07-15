package com.watchdog.apm.detector.global;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

/**
 * Kafka Streams 활성화. bootstrap-servers/application-id/serde 는 application.yml 에서 주입.
 */
@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {
}
