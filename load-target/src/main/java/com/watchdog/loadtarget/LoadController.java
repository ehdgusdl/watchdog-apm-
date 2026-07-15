package com.watchdog.loadtarget;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 가짜 고객사 앱의 유일한 엔드포인트.
 * 에러율/지연을 환경변수로 조절해 판정 시나리오(예: 0.5% -> 5%)를 재배포만으로 만든다.
 *   ERROR_RATE=0.05  LATENCY_MS=50  로 실행
 */
@RestController
class LoadController {

    @Value("${error.rate:0.0}")
    double errorRate;

    @Value("${latency.ms:50}")
    long latencyMs;

    @GetMapping("/api/work")
    String work() throws InterruptedException {
        Thread.sleep(latencyMs);
        // 스택트레이스 로그 스팸 없이 깨끗한 500 반환 (부하 중 수천 건이라 중요)
        if (ThreadLocalRandom.current().nextDouble() < errorRate) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "injected");
        }
        return "ok";
    }
}
