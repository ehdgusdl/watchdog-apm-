# 로컬 인프라 (kind + Kubernetes)

호스트에서 실행되는 Spring 서비스가 붙을 로컬 인프라를 kind 클러스터 위에 올립니다.

| 구성 요소 | 이미지 | 호스트 접근 |
| --- | --- | --- |
| Kafka (KRaft, 단일 노드) | `apache/kafka:3.9.0` | `localhost:9092` |
| ClickHouse | `clickhouse/clickhouse-server:24.8` | HTTP `localhost:8123`, native `localhost:9000` |
| Grafana | `grafana/grafana:11.3.0` | `localhost:3000` (admin / admin) |
| OTel Collector | `ghcr.io/open-telemetry/opentelemetry-collector-releases/opentelemetry-collector-contrib:0.156.0` | gRPC `localhost:4317`, HTTP `localhost:4318` |

모두 `watchdog` 네임스페이스에 배포되며, 개발용이라 **영속성이 없습니다**(`emptyDir`). 클러스터를 지우면 데이터도 사라집니다.

## 사전 준비

```bash
brew install kind kubectl   # 이미 설치돼 있으면 생략
```

## 올리기 / 내리기

```bash
# 루트에서 Makefile 사용
make infra-up      # 클러스터 생성 + 매니페스트 적용
make infra-status  # 파드 상태 확인
make infra-down    # 클러스터 삭제

# 또는 직접 (kind 설정은 저장소 루트의 kind-cluster.yaml)
kind create cluster --config kind-cluster.yaml
kubectl apply -f k8s/
kind delete cluster --name watchdog
```

## 연결 정보 (호스트 → 클러스터)

kind 의 `extraPortMappings` 로 NodePort 를 호스트 포트에 매핑했습니다. Spring 서비스의 `application.yml` 기본값이 이 주소를 가리킵니다.

| 서비스 | application.yml 설정 | 접근 주소 |
| --- | --- | --- |
| Kafka | `spring.kafka.bootstrap-servers` | `localhost:9092` |
| OTel Collector | `management.otlp.metrics.export.url` | `localhost:4318` / `localhost:4317` |
| ClickHouse | (archiver) | `localhost:8123` |
| Grafana | (대시보드) | http://localhost:3000 |

## 참고

- **Kafka 리스너**: 내부(`INTERNAL:9094`)는 `kafka.watchdog.svc.cluster.local:9094`, 외부(`EXTERNAL:9092`)는 `localhost:9092` 로 advertise 됩니다. OTel Collector 는 내부 리스너로, 호스트의 Spring 앱은 외부 리스너로 붙습니다.
- **deploy-watcher / mitigation-service** 는 fabric8 kubernetes-client 로 이 kind 클러스터의 API 서버에 접근합니다(로컬 kubeconfig 자동 사용).
- 프로덕션이 아니므로 복제본 1, 인증 최소화, 리소스 여유 설정입니다.
