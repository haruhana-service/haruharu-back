import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Counter, Rate } from 'k6/metrics';
import { generateReport, extractMetrics } from './report-utils.js';

// VU별 성능 추적을 위한 Custom Metrics
const stage1Trend = new Trend('stage_10vu_duration', true);
const stage2Trend = new Trend('stage_100vu_duration', true);
const stage3Trend = new Trend('stage_250vu_duration', true);

// VU별 요청 수 추적 ⭐ NEW
const vuRequestCounter = new Counter('vu_requests');

// API별 상세 메트릭 ⭐ NEW
const slowRequestRate = new Rate('slow_requests');  // p95 > 1000ms인 요청

/**
 * Load Test - 부하 테스트
 * 목적: 예상 트래픽 처리 능력 검증
 * VU: 10 → 100 → 250 (점진적 증가)
 * Duration: 15분
 */
export const options = {
  summaryTrendStats: ['avg', 'min', 'med', 'max', 'p(90)', 'p(95)', 'p(99)', 'p(99.9)'],
  stages: [
    { duration: '2m', target: 10 },   // Warm up: 2분간 10명
    { duration: '5m', target: 100 },  // 증가: 5분간 100명
    { duration: '5m', target: 250 },  // Peak: 5분간 250명
    { duration: '3m', target: 0 },    // Cool down: 3분간 0명
  ],
  thresholds: {
    // 전체 API (목표치 상향 조정 ⭐)
    http_req_duration: ['p(95)<500', 'p(99)<1000'],
    http_req_failed: ['rate<0.01'],
    http_reqs: ['rate>20'],

    // API별 상세 임계값 (목표치 상향 ⭐)
    'http_req_duration{name:POST_login}': ['p(95)<400', 'p(99)<500'],
    'http_req_duration{name:GET_today_problem}': ['p(95)<400', 'p(99)<600'],
    'http_req_duration{name:GET_problem_detail}': ['p(95)<400', 'p(99)<800'],
    'http_req_duration{name:GET_streak}': ['p(95)<300', 'p(99)<400'],
    'http_req_duration{name:GET_problem_list}': ['p(95)<500', 'p(99)<700'],
    'http_req_duration{name:POST_submission}': ['p(95)<500', 'p(99)<1000'],

    // API별 에러율
    'http_req_failed{name:POST_login}': ['rate<0.01'],
    'http_req_failed{name:GET_today_problem}': ['rate<0.01'],
    'http_req_failed{name:GET_streak}': ['rate<0.01'],
    'http_req_failed{name:GET_problem_list}': ['rate<0.01'],
    'http_req_failed{name:POST_submission}': ['rate<0.01'],

    // API별 요청 수 (비율 반영: 오늘의 문제+스트릭 85%, 목록 10%, 제출 5%)
    'http_reqs{name:GET_today_problem}': ['count>300'],
    'http_reqs{name:GET_streak}': ['count>300'],
    'http_reqs{name:GET_problem_list}': ['count>20'],
    'http_reqs{name:POST_submission}': ['count>10'],
    'http_reqs{name:GET_problem_detail}': ['count>0'],
    'http_reqs{name:POST_login}': ['count>0'],

    // VU별 단계 성능 임계값 (병목 지점 파악용 ⭐)
    'stage_10vu_duration': ['p(95)<200', 'avg<100'],    // 10VU: 전체 p95의 40%
    'stage_100vu_duration': ['p(95)<400', 'avg<200'],   // 100VU: 전체 p95의 80%
    'stage_250vu_duration': ['p(95)<600', 'avg<300'],   // 250VU: 전체 p95보다 여유
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const TEST_PASSWORD = 'TestPassword1@';

// VU별 토큰 캐시 (각 VU는 독립적인 인스턴스를 가짐)
let token = null;

// 테스트 시작 시간 (VU별 단계 판단용)
let testStartTime = null;

// 테스트 시작 전 샘플 계정 로그인 검증
export function setup() {
  console.log('🔧 테스트 계정 로그인 검증 중...');
  console.log('📌 각 VU는 고유 계정 사용 (VU 1 → perf_user_1, VU 2 → perf_user_2, ...)');

  // 샘플 계정 3개만 검증 (모든 계정 검증은 비효율적)
  const sampleUsers = [1, 100, 250];
  let allSuccess = true;

  sampleUsers.forEach((userId) => {
    const user = { loginId: `perf_user_${userId}`, password: TEST_PASSWORD };
    const loginRes = http.post(
      `${BASE_URL}/v1/auth/login`,
      JSON.stringify(user),
      { headers: { 'Content-Type': 'application/json' } }
    );

    if (loginRes.status === 200) {
      console.log(`✅ 로그인 OK: ${user.loginId}`);
    } else {
      console.log(`❌ 로그인 실패: ${user.loginId} - ${loginRes.status}`);
      allSuccess = false;
    }

    sleep(0.3);
  });

  if (!allSuccess) {
    console.log('\n❌ 일부 샘플 계정 로그인 실패');
    console.log('💡 힌트: SQL 스크립트로 계정을 먼저 생성하세요 (seed-test-data.sql)');
  } else {
    console.log('\n✅ 샘플 계정 검증 완료');
  }
}

// VU별 고유 계정으로 로그인
function login() {
  // 캐시된 토큰이 있으면 반환 (각 VU는 자신만의 token 변수를 가짐)
  if (token) {
    return token;
  }

  // VU ID 기반 계정 생성
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
  // 테스트 시작 시간 초기화
  if (!testStartTime) {
    testStartTime = Date.now();
  }

  // VU별 요청 수 카운트 ⭐ NEW
  vuRequestCounter.add(1, { vu: __VU });

  // VU별 고유 계정 로그인
  const token = login();

  if (!token) {
    sleep(1);
    return;
  }

  const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`,
  };

  // 현재 단계 판단 (테스트 시작 후 경과 시간 기준)
  const elapsedSec = (Date.now() - testStartTime) / 1000;
  let currentStage = null;
  let stageName = 'cooldown';
  if (elapsedSec < 120) {
    currentStage = stage1Trend;  // 0-2분: 10 VU
    stageName = 'stage1';
  } else if (elapsedSec < 420) {
    currentStage = stage2Trend;  // 2-7분: 100 VU
    stageName = 'stage2';
  } else if (elapsedSec < 720) {
    currentStage = stage3Trend;  // 7-12분: 250 VU
    stageName = 'stage3';
  }
  // Cool down (12-15분)은 측정 안 함

  // 사용자 행동 시뮬레이션
  const action = Math.random();

  if (action < 0.5) {
    // 50% - 메인 페이지: 오늘의 문제 + 스트릭 조회 (같은 페이지)
    const todayStartTime = Date.now();

    const todayRes = http.get(`${BASE_URL}/v1/daily-problem/today`, {
      headers,
      tags: { name: 'GET_today_problem' },
    });

    const todayResponseTime = Date.now() - todayStartTime;
    check(todayRes, {
      '[메인 페이지] 오늘의 문제 성공': (r) => r.status === 200,
    });

    slowRequestRate.add(todayResponseTime > 1000);
    if (currentStage) {
      currentStage.add(todayResponseTime);
    }

    // 같은 페이지에서 스트릭도 함께 조회
    sleep(0.1);  // 거의 동시 요청
    const streakStartTime = Date.now();
    const streakRes = http.get(`${BASE_URL}/v1/streaks`, {
      headers,
      tags: { name: 'GET_streak' },
    });

    const streakResponseTime = Date.now() - streakStartTime;
    check(streakRes, {
      '[메인 페이지] 스트릭 성공': (r) => r.status === 200,
    });

    slowRequestRate.add(streakResponseTime > 1000);
    if (currentStage) {
      currentStage.add(streakResponseTime);
    }

    sleep(Math.random() * 2 + 1);  // 1~3초

  } else if (action < 0.85) {
    // 35% - 메인 페이지 + 문제 상세 조회
    const todayStartTime = Date.now();
    const todayRes = http.get(`${BASE_URL}/v1/daily-problem/today`, {
      headers,
      tags: { name: 'GET_today_problem' },
    });

    const todayResponseTime = Date.now() - todayStartTime;
    slowRequestRate.add(todayResponseTime > 1000);
    if (currentStage) {
      currentStage.add(todayResponseTime);
    }

    // 스트릭 조회 (같은 페이지)
    sleep(0.1);
    const streakStartTime = Date.now();
    const streakRes = http.get(`${BASE_URL}/v1/streaks`, {
      headers,
      tags: { name: 'GET_streak' },
    });

    const streakResponseTime = Date.now() - streakStartTime;
    slowRequestRate.add(streakResponseTime > 1000);
    if (currentStage) {
      currentStage.add(streakResponseTime);
    }

    // 문제 상세 조회
    if (todayRes.status === 200) {
      try {
        const data = JSON.parse(todayRes.body);
        if (data.data && data.data.id) {
          sleep(1);
          const detailStartTime = Date.now();
          const detailRes = http.get(`${BASE_URL}/v1/daily-problem/${data.data.id}`, {
            headers,
            tags: { name: 'GET_problem_detail' },
          });
          const detailResponseTime = Date.now() - detailStartTime;
          check(detailRes, {
            '[상세] 문제 상세 성공': (r) => r.status === 200,

          });

          slowRequestRate.add(detailResponseTime > 1000);
          if (currentStage) {
            currentStage.add(detailResponseTime);
          }
        }
      } catch (e) {}
    }
    sleep(Math.random() * 2 + 1);

  } else if (action < 0.95) {
    // 10% - 문제 목록 조회 (과거 문제 찾아보기)
    const listStartTime = Date.now();
    const listRes = http.get(`${BASE_URL}/v1/daily-problem`, {
      headers,
      tags: { name: 'GET_problem_list' },
    });

    const listResponseTime = Date.now() - listStartTime;
    check(listRes, {
      '[목록] 문제 목록 성공': (r) => r.status === 200,
    });

    slowRequestRate.add(listResponseTime > 1000);
    if (currentStage) {
      currentStage.add(listResponseTime);
    }

    sleep(Math.random() * 2 + 1);

  } else {
    // 5% - 문제 제출
    const todayRes = http.get(`${BASE_URL}/v1/daily-problem/today`, {
      headers,
      tags: { name: 'GET_today_problem' },
    });

    if (todayRes.status === 200) {
      try {
        const data = JSON.parse(todayRes.body);
        if (data.data && data.data.id) {
          sleep(2);  // 문제 풀이 시간
          const submitStartTime = Date.now();
          const submitRes = http.post(
            `${BASE_URL}/v1/daily-problem/${data.data.id}/submissions`,
            JSON.stringify({ userAnswer: `LoadTest Answer ${Date.now()}` }),
            {
              headers,
              tags: { name: 'POST_submission' },
            }
          );

          const submitResponseTime = Date.now() - submitStartTime;
          check(submitRes, {
            '[제출] 답안 제출 성공': (r) => r.status === 200,
          });

          slowRequestRate.add(submitResponseTime > 1000);
          if (currentStage) {
            currentStage.add(submitResponseTime);
          }
        }
      } catch (e) {}
    }
    sleep(Math.random() * 3 + 2);  // 2~5초
  }
}

export function teardown() {
  console.log('Load Test 완료');
}

// Report
const reportConfig = {
  title: 'LOAD TEST',
  testType: 'load',
  sla: { p95: 500, p99: 1000, errRate: 0.01, rps: 20 },
  stages: [
    { label: ' 10 VU', key: 'stage_10vu_duration' },
    { label: '100 VU', key: 'stage_100vu_duration' },
    { label: '250 VU', key: 'stage_250vu_duration' },
  ],
};

export function handleSummary(data) {
  const report = generateReport(data, reportConfig);
  const json = extractMetrics(data, reportConfig);
  return {
    'stdout': report,
    'k6-tests/results/load-test-latest.json': JSON.stringify(json, null, 2),
  };
}
