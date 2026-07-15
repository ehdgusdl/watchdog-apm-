# load-target — 가짜 고객사 앱 (테스트 픽스처)

수집·판정 파이프라인을 검증하기 위한 부하 대상. **제품 코드 아님** (루트 4개 모듈과 분리, `settings.gradle` 미포함).

- 엔드포인트: `GET /api/work` (포트 8090)
- `ERROR_RATE` = 500 응답 비율 (판정의 error rate 원천)
- `LATENCY_MS` = 응답 지연
- 앱에 OTel 의존성 없음 — 에이전트는 `-javaagent`로 외부 부착 (고객사 조건과 동일)

## 빌드
```bash
../gradlew -p . build          # 또는 루트에서: ./gradlew -p load-target build
```

## 실행 (에이전트 부착 = 고객사 시나리오)
```bash
ERROR_RATE=0.0 LATENCY_MS=50 \
java -javaagent:opentelemetry-javaagent.jar \
     -Dotel.exporter.otlp.endpoint=http://localhost:4317 \
     -Dotel.service.name=load-target \
     -Dotel.metrics.exporter=otlp \
     -jar build/libs/load-target-0.0.1-SNAPSHOT.jar
```
> 에이전트 JAR은 OpenTelemetry 릴리스에서 받는다(우리가 안 만듦). endpoint는 우리 Collector.
> 시나리오 값은 **환경변수**로 준다(`ERROR_RATE`/`LATENCY_MS`). `-DERROR_RATE`(시스템 프로퍼티 대문자)는 Spring `error.rate`에 바인딩되지 않으니, `-D`로 줄 땐 `-Derror.rate=`.

## 판정 시나리오 만들기
재빌드 없이 **환경변수만 바꿔 재실행**하면 됨:
```bash
ERROR_RATE=0.005  # 정상 (before)
ERROR_RATE=0.05   # 회귀 (after) → detector SUSPECT 유발
```

## 부하 (k6)
```bash
k6 run --vus 20 --duration 60s script.js   # /api/work 를 반복 호출
```
