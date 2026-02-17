import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Counter, Rate } from 'k6/metrics';
import { generateReport, extractMetrics } from './report-utils.js';

// VU별 성능 추적을 위한 Custom Metrics
const stage1Trend = new Trend('stage_100vu_duration', true);
const stage2Trend = new Trend('stage_200vu_duration', true);
const stage3Trend = new Trend('stage_300vu_duration', true);
const stage4Trend = new Trend('stage_400vu_duration', true);
const stage5Trend = new Trend('stage_500vu_duration', true);

// VU별 요청 수 추적
const vuRequestCounter = new Counter('vu_requests');

// 느린 요청 추적
const slowRequestRate = new Rate('slow_requests');

/**
 * Stress Test - 스트레스 테스트
 * 목적: 시스템 한계점 파악 및 병목 발견
 * VU: 100 → 200 → 300 → 400 → 500 (한계까지 증가)
 * Duration: 14분
 */
export const options = {
  summaryTrendStats: ['avg', 'min', 'med', 'max', 'p(90)', 'p(95)', 'p(99)', 'p(99.9)'],
  stages: [
    { duration: '2m', target: 100 },   // 2분간 100명
    { duration: '3m', target: 200 },   // 3분간 200명
    { duration: '3m', target: 300 },   // 3분간 300명 (한계 테스트)
    { duration: '2m', target: 400 },   // 2분간 400명 (기존 병목 지점)
    { duration: '2m', target: 500 },   // 2분간 500명 (병목 한계 탐색)
    { duration: '2m', target: 0 },     // 2분간 복구
  ],
  thresholds: {
    // 전체 API (Load 대비 3배 - 극한 상황)
    http_req_duration: ['p(95)<1500', 'p(99)<2500'],  // Load: 500/1000
    http_req_failed: ['rate<0.05'],  // Load: 0.01
    http_reqs: ['rate>10'],  // Load: 20

    // API별 상세 임계값 (Load 대비 3배)
    'http_req_duration{name:POST_login}': ['p(95)<1200', 'p(99)<1500'],  // Load: 400/500
    'http_req_duration{name:GET_today_problem}': ['p(95)<1200', 'p(99)<1800'],  // Load: 400/600
    'http_req_duration{name:GET_streak}': ['p(95)<900', 'p(99)<1200'],  // Load: 300/400

    // API별 에러율
    'http_req_failed{name:POST_login}': ['rate<0.02'],
    'http_req_failed{name:GET_today_problem}': ['rate<0.05'],
    'http_req_failed{name:GET_streak}': ['rate<0.05'],

    // VU별 단계 성능 임계값 (병목 지점 파악용)
    'stage_100vu_duration': ['p(95)<500', 'avg<250'],   // Load 100VU와 동일
    'stage_200vu_duration': ['p(95)<800', 'avg<400'],   // 1.6배
    'stage_300vu_duration': ['p(95)<1100', 'avg<550'],  // 2.2배
    'stage_400vu_duration': ['p(95)<1500', 'avg<750'],  // 3배
    'stage_500vu_duration': ['p(95)<1800', 'avg<900'],  // 3.6배
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const TEST_PASSWORD = 'TestPassword1@';

let authToken = null;

// 테스트 시작 시간 (VU별 단계 판단용)
let testStartTime = null;

// 테스트 시작 전 샘플 계정 로그인 검증
export function setup() {
  console.log('🔧 테스트 계정 로그인 검증 중...');
  console.log('📌 각 VU는 고유 계정 사용 (최대 VU 500 → perf_user_1~500)');

  // 샘플 계정만 검증
  const sampleUsers = [1, 200, 400, 500];
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
  const vuId = __VU; // k6의 VU ID (1부터 시작)
  const user = {
    loginId: `perf_user_${vuId}`,
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
        authToken = body.data.accessToken;
      }
    } catch (e) {}
  }
  return authToken;
}

export default function () {
  // 테스트 시작 시간 초기화
  if (!testStartTime) {
    testStartTime = Date.now();
  }

  // VU별 요청 수 카운트
  vuRequestCounter.add(1, { vu: __VU });

  if (!authToken) {
    login();
    if (!authToken) {
      sleep(0.5);
      return;
    }
  }

  const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${authToken}`,
  };

  // 현재 단계 판단 (테스트 시작 후 경과 시간 기준)
  const elapsedSec = (Date.now() - testStartTime) / 1000;
  let currentStage = null;
  if (elapsedSec < 120) {
    currentStage = stage1Trend;  // 0-2분: 100 VU
  } else if (elapsedSec < 300) {
    currentStage = stage2Trend;  // 2-5분: 200 VU
  } else if (elapsedSec < 480) {
    currentStage = stage3Trend;  // 5-8분: 300 VU
  } else if (elapsedSec < 600) {
    currentStage = stage4Trend;  // 8-10분: 400 VU
  } else if (elapsedSec < 720) {
    currentStage = stage5Trend;  // 10-12분: 500 VU
  }
  // Cool down (12-14분)은 측정 안 함

  // 빠른 연속 요청 (사용자가 빠르게 클릭하는 상황)
  // 메인 페이지 시뮬레이션: 오늘의 문제 + 스트릭 동시 조회
  for (let i = 0; i < 2; i++) {
    const todayStartTime = Date.now();

    const todayRes = http.get(`${BASE_URL}/v1/daily-problem/today`, {
      headers,
      tags: { name: 'GET_today_problem' },
    });

    const todayResponseTime = Date.now() - todayStartTime;

    // 느린 요청 추적
    slowRequestRate.add(todayResponseTime > 2000);

    // 단계별 응답 시간 기록
    if (currentStage) {
      currentStage.add(todayResponseTime);
    }

    check(todayRes, {
      '[Stress] 오늘의 문제 응답': (r) => r.status === 200 || r.status === 429 || r.status === 503,
    });

    // 같은 페이지에서 스트릭도 조회
    sleep(0.05);
    const streakStartTime = Date.now();

    const streakRes = http.get(`${BASE_URL}/v1/streaks`, {
      headers,
      tags: { name: 'GET_streak' },
    });

    const streakResponseTime = Date.now() - streakStartTime;

    slowRequestRate.add(streakResponseTime > 2000);

    if (currentStage) {
      currentStage.add(streakResponseTime);
    }

    check(streakRes, {
      '[Stress] 스트릭 응답': (r) => r.status === 200 || r.status === 429 || r.status === 503,
    });

    // 토큰 만료 시 재로그인
    if (todayRes.status === 401 || streakRes.status === 401) {
      authToken = null;
      break;
    }

    sleep(0.3);  // 300ms 간격
  }

  sleep(0.5);
}

export function teardown() {
  console.log('Stress Test 완료');
  console.log('⚠️ 어느 VU 수에서 성능 저하가 시작되었는지 확인하세요.');
}

// Report
const reportConfig = {
  title: 'STRESS TEST',
  testType: 'stress',
  sla: { p95: 1500, p99: 2500, errRate: 0.05, rps: 10 },
  stages: [
    { label: '100 VU', key: 'stage_100vu_duration' },
    { label: '200 VU', key: 'stage_200vu_duration' },
    { label: '300 VU', key: 'stage_300vu_duration' },
    { label: '400 VU', key: 'stage_400vu_duration' },
    { label: '500 VU', key: 'stage_500vu_duration' },
  ],
};

export function handleSummary(data) {
  const report = generateReport(data, reportConfig);
  const json = extractMetrics(data, reportConfig);
  return {
    'stdout': report,
    'k6-tests/results/stress-test-latest.json': JSON.stringify(json, null, 2),
  };
}
