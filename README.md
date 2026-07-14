# watchdog-apm

> 실시간 메트릭 수집부터 이상 탐지, 알림, 자동 완화(auto-mitigation)까지 이어지는 이벤트 기반 APM 모니터링 시스템

Java 애플리케이션의 메트릭과 배포 이벤트를 수집해 Kafka 파이프라인으로 흘려보내고, 실시간으로 이상을 탐지하여 **Slack 알림**과 **Kubernetes 기반 자동 완화**를 수행합니다. 모든 메트릭은 ClickHouse에 장기 보관되어 Grafana로 시각화됩니다.

---

## 데이터 흐름

**1. 수집** — Java Agent(Spring Boot)가 OpenTelemetry로 메트릭을 계측해 gRPC로 OTel Collector에 보내고, Deploy Watcher가 K8s watch로 배포 이벤트를 감지합니다. 두 흐름은 모두 Kafka(`metrics + deploys`) 토픽으로 모입니다.

**2. 파이프라인** — 두 컨슈머 그룹이 Kafka를 병렬로 소비합니다.
- `detector` (Kafka Streams): 실시간으로 이상을 탐지해 `Kafka(alerts)` 토픽으로 알림을 발행
- `archiver` (ClickHouse): 메트릭을 장기 저장하고 Grafana로 시각화

**3. 대응** — `Kafka(alerts)`를 두 서비스가 소비합니다.
- Alert Service(Spring Boot): Slack으로 알림 통보
- Mitigation Service(Spring Boot): K8s API로 자동 완화 조치 수행

---

## 기술 스택

| 영역 | 기술 |
| --- | --- |
| 애플리케이션 | Spring Boot (Java) |
| 계측 / 수집 | OpenTelemetry (OTel Collector), gRPC |
| 이벤트 버스 | Apache Kafka |
| 스트림 처리 | Kafka Streams |
| 저장 / 분석 | ClickHouse |
| 시각화 | Grafana |
| 오케스트레이션 | Kubernetes (K8s watch, K8s API) |
| 알림 | Slack |

---

## 핵심 특징

- **이벤트 기반 파이프라인** — 수집·탐지·대응이 Kafka 토픽으로 느슨하게 결합되어 확장에 유연합니다.
- **탐지와 아카이빙 분리** — `detector`/`archiver` 컨슈머 그룹이 독립적으로 동작해 서로 영향을 주지 않습니다.
- **자동 완화(Auto-mitigation)** — 이상 탐지 시 사람 개입 없이 K8s API로 즉시 조치할 수 있습니다.
- **배포 연계 분석** — 배포 이벤트를 함께 수집해 성능 변화를 배포와 연관 지어 진단합니다.
