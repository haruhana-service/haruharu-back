import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Counter, Rate } from 'k6/metrics';
import { generateReport, extractMetrics } from './report-utils.js';

// VU별 성능 추적을 위한 Custom Metrics
const baselineTrend = new Trend('baseline_duration', true);      // 일반 부하 (10 VU)
const spikeTrend = new Trend('spike_duration', true);             // 급증 (300 VU)
const recoveryTrend = new Trend('recovery_duration', true);       // 복구 (10 VU)

// VU별 요청 수 추적
const vuRequestCounter = new Counter('vu_requests');

// 느린 요청 추적 (p95 > 2000ms인 요청)
const slowRequestRate = new Rate('slow_requests');

/**
 * Spike Test - 스파이크 테스트
 * 목적: 갑작스러운 트래픽 증가 시 대응력 및 복구 능력 확인
 * VU: 10 → 300 (30초 만에 급증)
 * Duration: 6분
 */
export const options = {
  summaryTrendStats: ['avg', 'min', 'med', 'max', 'p(90)', 'p(95)', 'p(99)', 'p(99.9)'],
  stages: [
    { duration: '1m', target: 10 },    // 0-1분: 기준선 (10 VU)
    { duration: '30s', target: 300 },  // 1-1.5분: 급증 (10 → 300 VU in 30s)
    { duration: '2m', target: 300 },   // 1.5-3.5분: 피크 유지 (300 VU)
    { duration: '1m', target: 10 },    // 3.5-4.5분: 복구 (300 → 10 VU)
    { duration: '1m30s', target: 0 },  // 4.5-6분: 정리
  ],
  thresholds: {
    // 전체 API (Load 대비 3배 - 극한 상황)
    http_req_duration: ['p(95)<1500', 'p(99)<2500'],
    http_req_failed: ['rate<0.05'],
    http_reqs: ['rate>10'],

    // API별 상세 임계값
    'http_req_duration{name:POST_login}': ['p(95)<1200', 'p(99)<1500'],
    'http_req_duration{name:GET_today_problem}': ['p(95)<1200', 'p(99)<1800'],
    'http_req_duration{name:GET_streak}': ['p(95)<900', 'p(99)<1200'],

    // API별 에러율 (극한 상황)
    'http_req_failed{name:POST_login}': ['rate<0.02'],
    'http_req_failed{name:GET_today_problem}': ['rate<0.05'],
    'http_req_failed{name:GET_streak}': ['rate<0.05'],

    // 단계별 성능 임계값 (병목 지점 파악용)
    'baseline_duration': ['p(95)<500', 'avg<250'],     // 기준선: Load와 동일
    'spike_duration': ['p(95)<1500', 'avg<750'],       // 스파이크: Load 대비 3배
    'recovery_duration': ['p(95)<600', 'avg<300'],     // 복구: 기준선 근처로 복구되어야 함
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
  console.log('🔧 Spike Test 계정 로그인 검증 중...');
  console.log('📌 각 VU는 고유 계정 사용 (최대 VU 300 → perf_user_1~300)');

  // 샘플 계정만 검증
  const sampleUsers = [1, 100, 300];
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

  // VU별 요청 수 카운트
  vuRequestCounter.add(1, { vu: __VU });

  // VU별 고유 계정 로그인
  const token = login();

  if (!token) {
    sleep(0.5);
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

  if (elapsedSec < 60) {
    // 0-1분: 기준선 (10 VU)
    currentStage = baselineTrend;
    stageName = 'baseline';
  } else if (elapsedSec < 210) {
    // 1-3.5분: 스파이크 (급증 + 피크 유지)
    currentStage = spikeTrend;
    stageName = 'spike';
  } else if (elapsedSec < 270) {
    // 3.5-4.5분: 복구
    currentStage = recoveryTrend;
    stageName = 'recovery';
  }
  // 4.5분 이후는 측정 안 함

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
      '[Spike] 오늘의 문제 응답': (r) => r.status === 200 || r.status === 429 || r.status === 503,
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
      '[Spike] 스트릭 응답': (r) => r.status === 200 || r.status === 429 || r.status === 503,
    });

    // 토큰 만료 시 재로그인
    if (todayRes.status === 401 || streakRes.status === 401) {
      tokenCache[`vu_${__VU}`] = null;
      break;
    }

    sleep(0.3);  // 300ms 간격
  }

  sleep(0.5);
}

export function teardown() {
  console.log('✅ Spike Test 완료');
  console.log('⚠️ 급증 시 에러율과 복구 시간을 확인하세요.');
  console.log('💡 복구 단계 응답 시간이 기준선과 비슷하면 안정적입니다.');
}

// Report
const reportConfig = {
  title: 'SPIKE TEST',
  testType: 'spike',
  sla: { p95: 1500, p99: 2500, errRate: 0.05, rps: 10 },
  stages: [
    { label: '기준선 (10 VU)', key: 'baseline_duration' },
    { label: '스파이크 (300 VU)', key: 'spike_duration' },
    { label: '복구 (10 VU)', key: 'recovery_duration' },
  ],
};

export function handleSummary(data) {
  const report = generateReport(data, reportConfig);
  const json = extractMetrics(data, reportConfig);
  return {
    'stdout': report,
    'k6-tests/results/spike-test-latest.json': JSON.stringify(json, null, 2),
  };
}
