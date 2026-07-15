# 수집 (Collection)

고객사 앱의 메트릭을 우리 서버로 가져오는 단계. **핵심: 고객사 코드는 안 건드린다.**

## 고객사가 하는 일 3개

```
1. 에이전트 설치     → 앱 실행에 -javaagent 한 줄
2. API 키 발급 받기  → 우리 대시보드에서 복사
3. 엔드포인트 설정   → 설정에 endpoint + api-key 두 줄
```

이러면 고객사 앱이 **우리 서버로 gRPC Push 방식으로 메트릭을 쏜다.** 앱 코드 수정은 0.

실행 예시:
```bash
java -javaagent:opentelemetry-javaagent.jar \
     -Dotel.exporter.otlp.endpoint=<우리 주소>:4317 \
     -Dotel.service.name=customers-service \
     -jar app.jar
```

## 용어 정리 (헷갈리기 쉬움)

- **Push** = 에이전트가 능동적으로 "보낸다" (vs Pull = 우리가 긁어감/scrape) → **우리는 Push**
- **OTLP** = OpenTelemetry의 데이터 전송 규격 (**무엇을** 보내나)
- **gRPC** = 그 OTLP를 실어 나르는 방식 (**어떻게** 보내나), 포트 `4317`

→ 한 덩어리로 **"OTLP over gRPC"** 로 보면 된다.

## 왜 gRPC가 기본값인가

메트릭·트레이스는 **대량·고빈도**로 쏟아지는 데이터라, 여기에 맞는 전송 방식이 gRPC다.

- **바이너리(Protobuf) 직렬화** → JSON보다 작고 빠름 (같은 데이터를 더 적은 바이트로)
- **HTTP/2 기반** → 커넥션 하나로 여러 요청 다중화·재사용 (매번 새 연결 안 함)
- **스트리밍 친화적** → 끊임없이 흐르는 텔레메트리에 적합

참고로 OTLP는 HTTP로도 실을 수 있다(포트 `4318`). HTTP/JSON은 사람이 읽기 쉽고 프록시·방화벽 통과가 쉬워 그 경우에만 쓰고, **성능 기본값은 gRPC**다.

| | 포트 | 특징 |
|---|---|---|
| **gRPC** | 4317 | 기본. 빠름·효율적 (바이너리) |
| HTTP | 4318 | 방화벽 친화적 (JSON) |

## 우리 프로젝트에서는

- **1번(에이전트)** ✅ petclinic에 `-javaagent` 부착
- **2번(API 키)** ❌ 단일 고객이라 불필요 (멀티 테넌시 = 범위 밖)
- **3번(endpoint)** ✅ 우리 로컬 Collector(`localhost:4317`)로 지정

우리 Collector는 이미 gRPC(4317)·HTTP(4318) 둘 다 열려 있음(`k8s/otel-collector.yaml`). 그래서 에이전트를 4317로 지정하면 그대로 gRPC Push로 들어온다.
