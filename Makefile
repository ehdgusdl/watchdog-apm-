.PHONY: help infra-up infra-down infra-status build test

CLUSTER := watchdog

help:
	@echo "watchdog-apm"
	@echo "  make infra-up      kind 클러스터 생성 + 인프라 배포"
	@echo "  make infra-status  인프라 파드 상태 확인"
	@echo "  make infra-down    kind 클러스터 삭제"
	@echo "  make build         전체 모듈 빌드 (Gradle)"
	@echo "  make test          전체 모듈 테스트 (인프라 필요)"

infra-up:
	kind create cluster --config kind-cluster.yaml
	kubectl apply -f k8s/
	@echo "인프라 배포 완료. 'make infra-status' 로 파드 준비 상태를 확인하세요."

infra-status:
	kubectl get pods -n watchdog -o wide

infra-down:
	kind delete cluster --name $(CLUSTER)

build:
	./gradlew build -x test

test:
	./gradlew test
