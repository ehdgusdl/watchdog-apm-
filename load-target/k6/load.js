// load-target 부하 스크립트.
// k6는 "트래픽 양"만 만든다. 5xx 비율은 앱(ERROR_RATE)이 정한다.
//
// 사용:
//   ② 관통:      RATE=50 DURATION=3m  k6 run load.js
//   ③ 시나리오:  RATE=50 DURATION=12m k6 run load.js   (도중 앱을 ERROR_RATE 올려 재기동)
import http from 'k6/http';

const RATE = Number(__ENV.RATE || 50);           // 초당 요청 수
const DURATION = __ENV.DURATION || '3m';
const TARGET = __ENV.TARGET || 'http://localhost:8090/api/work';

export const options = {
  scenarios: {
    steady: {
      executor: 'constant-arrival-rate',         // 응답이 느려져도 목표 rate 유지
      rate: RATE,
      timeUnit: '1s',
      duration: DURATION,
      preAllocatedVUs: Math.max(20, RATE),
      maxVUs: Math.max(50, RATE * 2),
    },
  },
};

export default function () {
  http.get(TARGET);                              // 500은 앱이 반환 (k6가 주입 X)
}
