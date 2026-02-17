import http from 'k6/http';
import { check, sleep } from 'k6';
import { generateReport, extractMetrics } from './report-utils.js';

/**
 * Smoke Test - 스모크 테스트
 * 목적: 기본 기능 동작 여부 확인 (배포 전 헬스체크)
 * VU: 1~5명 (최소 부하)
 * Duration: 3분
 */
export const options = {
  summaryTrendStats: ['avg', 'min', 'med', 'max', 'p(90)', 'p(95)', 'p(99)', 'p(99.9)'],
  stages: [
    { duration: '10s', target: 2 },   // 1분간 2명
    { duration: '30s', target: 5 },   // 1분간 5명 유지
    { duration: '20s', target: 0 },   // 1분간 0명 (정리)
  ],
  thresholds: {
    // 기본 기능만 확인 (느슨한 임계값)
    http_req_duration: ['p(95)<300', 'p(99)<500'],
    http_req_failed: ['rate<0.01'],

    // API별 기본 동작 확인
    'http_req_duration{name:POST_login}': ['p(95)<200'],
    'http_req_duration{name:GET_today_problem}': ['p(95)<300'],
    'http_req_duration{name:GET_streak}': ['p(95)<200'],

    // 최소 요청 수 확인
    'http_reqs{name:GET_today_problem}': ['count>10'],
    'http_reqs{name:GET_streak}': ['count>10'],
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const TEST_PASSWORD = 'TestPassword1@';

// VU별 토큰 캐시 (각 VU는 독립적인 인스턴스를 가짐)
let token = null;

// 테스트 시작 전 계정 검증
export function setup() {
  console.log('🔧 Smoke Test 시작 - 기본 기능 검증');
  console.log('📌 VU 1~5 (최소 부하로 헬스체크)');

  // 샘플 계정 1개만 검증
  const user = { loginId: 'perf_user_1', password: TEST_PASSWORD };
  const loginRes = http.post(
    `${BASE_URL}/v1/auth/login`,
    JSON.stringify(user),
    { headers: { 'Content-Type': 'application/json' } }
  );

  if (loginRes.status === 200) {
    console.log('✅ 로그인 OK');
  } else {
    console.log(`❌ 로그인 실패 - ${loginRes.status}`);
    console.log('💡 힌트: SQL 스크립트로 계정을 먼저 생성하세요 (seed-test-data.sql)');
  }
}

// VU별 고유 계정으로 로그인
function login() {
  // 캐시된 토큰이 있으면 반환 (각 VU는 자신만의 token 변수를 가짐)
  if (token) {
    return token;
  }

  const user = {
    loginId: `perf_user_${__VU}`,
    password: TEST_PASSWORD
  };

  const res = http.post(
    `${BASE_URL}/v1/auth/login`,
    JSON.stringify(user),
    {
      headers: { 'Content-Type': 'application/json' },
      tags: { name: 'POST_login' },
    }
  );

  if (res.status === 200) {
    try {
      const body = JSON.parse(res.body);
      if (body.data && body.data.accessToken) {
        token = body.data.accessToken;
        return token;
      }
    } catch (e) {}
  }

  return null;
}

export default function () {
  const token = login();

  if (!token) {
    sleep(1);
    return;
  }

  const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`,
  };

  // 시나리오 1: 메인 페이지 조회 (오늘의 문제 + 스트릭)
  const todayRes = http.get(`${BASE_URL}/v1/daily-problem/today`, {
    headers,
    tags: { name: 'GET_today_problem' },
  });

  check(todayRes, {
    '[Smoke] 오늘의 문제 조회 성공': (r) => r.status === 200,
    '[Smoke] 응답에 데이터 포함': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.data !== undefined;
      } catch (e) {
        return false;
      }
    },
  });

  sleep(0.5);

  // 스트릭 조회
  const streakRes = http.get(`${BASE_URL}/v1/streaks`, {
    headers,
    tags: { name: 'GET_streak' },
  });

  check(streakRes, {
    '[Smoke] 스트릭 조회 성공': (r) => r.status === 200,
  });

  sleep(1);

  // 시나리오 2: 문제 목록 조회
  if (Math.random() < 0.3) {  // 30%
    const listRes = http.get(`${BASE_URL}/v1/daily-problem`, {
      headers,
      tags: { name: 'GET_problem_list' },
    });

    check(listRes, {
      '[Smoke] 문제 목록 조회 성공': (r) => r.status === 200,
    });

    sleep(1);
  }

  // 시나리오 3: 문제 상세 조회 (오늘의 문제 ID 사용)
  if (todayRes.status === 200) {
    try {
      const data = JSON.parse(todayRes.body);
      if (data.data && data.data.id) {
        const detailRes = http.get(`${BASE_URL}/v1/daily-problem/${data.data.id}`, {
          headers,
          tags: { name: 'GET_problem_detail' },
        });

        check(detailRes, {
          '[Smoke] 문제 상세 조회 성공': (r) => r.status === 200,
        });
      }
    } catch (e) {}
  }

  sleep(2);  // 2초 대기 (느긋하게)
}

export function teardown() {
  console.log('✅ Smoke Test 완료');
}

// Report
const reportConfig = {
  title: 'SMOKE TEST',
  testType: 'smoke',
  sla: { p95: 300, p99: 500, errRate: 0.01, rps: 1 },
  stages: [],
};

export function handleSummary(data) {
  const report = generateReport(data, reportConfig);
  return {
    'stdout': report,
  };
}
