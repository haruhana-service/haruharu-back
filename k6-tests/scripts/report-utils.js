/**
 * k6 Performance Test Report Generator
 * load, stress, smoke 테스트에서 공유하는 리포트 모듈
 */

function fmt(ms) {
  if (ms == null) return '    N/A';
  if (ms >= 10000) return `${(ms / 1000).toFixed(0)}s`.padStart(7);
  if (ms >= 1000) return `${(ms / 1000).toFixed(1)}s`.padStart(7);
  return `${Math.round(ms)}ms`.padStart(7);
}

function collectApiMetrics(metrics) {
  const apis = [];
  for (const [name, metric] of Object.entries(metrics)) {
    if (!name.includes('{name:') || !name.includes('http_req_duration')) continue;
    const apiName = name.match(/name:([^}]+)/)?.[1];
    if (!apiName) continue;

    const countMetric = metrics[`http_reqs{name:${apiName}}`];
    const errorMetric = metrics[`http_req_failed{name:${apiName}}`];

    apis.push({
      name: apiName,
      count: countMetric?.values.count || 0,
      avg: metric.values.avg,
      p50: metric.values.med,
      p95: metric.values['p(95)'],
      p99: metric.values['p(99)'],
      max: metric.values.max,
      errRate: (errorMetric?.values.rate || 0) * 100,
    });
  }
  return apis.sort((a, b) => b.count - a.count);
}

/**
 * 테스트 결과 리포트 생성 (콘솔 출력용)
 *
 * @param {Object} data - k6 handleSummary data
 * @param {Object} config
 * @param {string} config.title - 'LOAD TEST' | 'STRESS TEST' | 'SMOKE TEST'
 * @param {Object} config.sla - { p95, p99, errRate, rps }
 * @param {Array}  config.stages - [{ label, key }]
 */
export function generateReport(data, config) {
  const m = data.metrics;
  const dur = m.http_req_duration.values;
  const wait = m.http_req_waiting.values;
  const totalReqs = m.http_reqs.values.count;
  const rps = m.http_reqs.values.rate;
  const errRate = m.http_req_failed.values.rate * 100;
  const maxVU = m.vus?.values.max || 0;
  const mins = (data.state.testRunDurationMs / 60000).toFixed(0);
  const date = new Date().toISOString().slice(0, 10);

  // TPS 계산 (성공한 트랜잭션만)
  const successReqs = totalReqs * (1 - m.http_req_failed.values.rate);
  const durationSec = data.state.testRunDurationMs / 1000;
  const tps = successReqs / durationSec;

  const thresholds = data.thresholds || {};
  const failedThresholds = [];
  for (const [name, result] of Object.entries(thresholds)) {
    if (!result.ok) failedThresholds.push(name);
  }
  const allPassed = failedThresholds.length === 0;
  const verdict = allPassed ? 'PASS' : 'FAIL';
  const sla = config.sla;

  let o = '\n';

  // Header
  o += '════════════════════════════════════════════════════════════════\n';
  o += `  ${config.title} REPORT\n`;
  o += `  ${date}  |  ${mins}min  |  Max ${maxVU} VU\n`;
  o += '════════════════════════════════════════════════════════════════\n\n';

  // 1. SLA
  o += `── 1. SLA 달성 여부 ──────────────────────────────── ${verdict}\n\n`;

  const checks = [
    { name: 'p95 응답 시간', val: `${Math.round(dur['p(95)'])}ms`, target: `< ${sla.p95}ms`, pass: dur['p(95)'] < sla.p95 },
    { name: 'p99 응답 시간', val: `${Math.round(dur['p(99)'])}ms`, target: `< ${sla.p99}ms`, pass: dur['p(99)'] < sla.p99 },
    { name: '에러율', val: `${errRate.toFixed(2)}%`, target: `< ${(sla.errRate * 100).toFixed(0)}%`, pass: errRate / 100 < sla.errRate },
    { name: '처리량(RPS)', val: `${rps.toFixed(1)} req/s`, target: `> ${sla.rps} req/s`, pass: rps > sla.rps },
    { name: '처리량(TPS)', val: `${tps.toFixed(1)} txn/s`, target: `> ${sla.rps} txn/s`, pass: tps > sla.rps },
  ];

  o += '  지표             측정값           목표            결과\n';
  o += '  ──────────────────────────────────────────────────────────\n';
  checks.forEach(c => {
    o += `  ${c.name.padEnd(16)} ${c.val.padEnd(16)} ${c.target.padEnd(15)} ${c.pass ? 'PASS' : 'FAIL'}\n`;
  });
  o += `\n  총 요청: ${totalReqs.toLocaleString()}건\n\n`;

  // 2. Response Time Distribution
  o += '── 2. 응답 시간 분포 ─────────────────────────────────────\n\n';
  o += `  모든 API 요청 ${totalReqs.toLocaleString()}건을 응답 시간 순으로 정렬한 백분위\n\n`;
  o += '           avg      p50      p90      p95      p99      max\n';
  o += `  전체 ${fmt(dur.avg)} ${fmt(dur.med)} ${fmt(dur['p(90)'])} ${fmt(dur['p(95)'])} ${fmt(dur['p(99)'])} ${fmt(dur.max)}\n`;
  o += `  서버 ${fmt(wait.avg)} ${fmt(wait.med)} ${fmt(wait['p(90)'])} ${fmt(wait['p(95)'])} ${fmt(wait['p(99)'])} ${fmt(wait.max)}\n\n`;
  o += '  전체 = 네트워크 포함 (k6 측정)  |  서버 = 네트워크 제외\n\n';

  // 3. API Performance
  o += '── 3. API별 성능 ─────────────────────────────────────────\n\n';
  const apis = collectApiMetrics(m);

  o += '  API                     Reqs      p50      p95      p99   Err%\n';
  o += '  ────────────────────────────────────────────────────────────────\n';
  apis.forEach(api => {
    o += `  ${api.name.padEnd(22)} ${String(api.count).padStart(6)} ${fmt(api.p50)} ${fmt(api.p95)} ${fmt(api.p99)} ${api.errRate.toFixed(2).padStart(6)}\n`;
  });
  o += '\n';

  // 4. Scalability (stages가 있는 경우만)
  if (config.stages && config.stages.length > 0) {
    o += '── 4. 부하 구간별 성능 변화 ──────────────────────────────\n\n';

    const stageResults = [];
    config.stages.forEach(s => {
      const sv = m[s.key]?.values;
      if (sv) stageResults.push({ label: s.label, p95: sv['p(95)'], p99: sv['p(99)'] });
    });

    if (stageResults.length > 0) {
      o += '  구간         p95        변화율      판정\n';
      o += '  ─────────────────────────────────────────────\n';

      stageResults.forEach((s, i) => {
        let change, judge;
        if (i === 0) {
          change = '    ─    ';
          judge = '기준값';
        } else {
          const rate = ((s.p95 - stageResults[i - 1].p95) / stageResults[i - 1].p95) * 100;
          change = `${rate >= 0 ? '+' : ''}${rate.toFixed(0)}%`.padStart(9);
          judge = rate > 100 ? '병목 의심' : '정상';
        }
        o += `  ${s.label}  ${fmt(s.p95)}   ${change}     ${judge}\n`;
      });

      if (stageResults.length >= 2) {
        const rates = [];
        for (let i = 1; i < stageResults.length; i++) {
          rates.push(((stageResults[i].p95 - stageResults[i - 1].p95) / stageResults[i - 1].p95) * 100);
        }
        const maxRate = Math.max(...rates);
        const lastRate = rates[rates.length - 1];
        o += '\n';
        if (lastRate > rates[0] * 1.5 && lastRate > 50) {
          o += '  분석: 후반 구간에서 성능 저하 가속화 → 병목 존재\n';
          o += '  제안: Grafana에서 HikariCP Pending, CPU 확인\n';
        } else if (maxRate > 100) {
          o += `  분석: 일부 구간에서 급격한 성능 저하 (최대 ${maxRate.toFixed(0)}%)\n`;
        } else {
          o += `  분석: ${maxVU} VU까지 선형 저하 → 병목 없음\n`;
        }
      }
    }
    o += '\n';
  }

  // 5. Warnings
  const warningSection = config.stages && config.stages.length > 0 ? '5' : '4';
  o += `── ${warningSection}. 주의 사항 ──────────────────────────────────────────\n\n`;

  let hasWarnings = false;
  apis.forEach(api => {
    const warnings = [];
    if (api.p95 > sla.p95) warnings.push(`p95 ${Math.round(api.p95)}ms (목표 < ${sla.p95}ms)`);
    if (api.errRate > sla.errRate * 100) warnings.push(`에러율 ${api.errRate.toFixed(2)}%`);
    if (api.max > 5000) warnings.push(`최대 ${Math.round(api.max)}ms`);
    if (warnings.length > 0) {
      hasWarnings = true;
      o += `  ${api.name}:\n`;
      warnings.forEach(w => o += `    - ${w}\n`);
    }
  });

  if (failedThresholds.length > 0) {
    hasWarnings = true;
    o += '\n  실패한 임계값:\n';
    failedThresholds.forEach(name => o += `    - ${name}\n`);
  }

  if (!hasWarnings) {
    o += '  없음\n';
  }
  o += '\n';

  // Footer
  o += '════════════════════════════════════════════════════════════════\n';
  o += `  ${verdict} - ${config.title} ${allPassed ? '통과' : '실패'} (${maxVU} VU)\n`;
  o += '════════════════════════════════════════════════════════════════\n\n';

  return o;
}

/**
 * 테스트 결과를 JSON으로 추출 (Before/After 비교용)
 */
export function extractMetrics(data, config) {
  const m = data.metrics;
  const dur = m.http_req_duration.values;
  const wait = m.http_req_waiting.values;

  // TPS 계산
  const totalReqs = m.http_reqs.values.count;
  const successReqs = totalReqs * (1 - m.http_req_failed.values.rate);
  const durationSec = data.state.testRunDurationMs / 1000;
  const tps = successReqs / durationSec;

  const result = {
    testType: config.testType,
    date: new Date().toISOString(),
    maxVU: m.vus?.values.max || 0,
    durationSec: Math.round(durationSec),
    totalRequests: totalReqs,
    rps: parseFloat(m.http_reqs.values.rate.toFixed(2)),
    tps: parseFloat(tps.toFixed(2)),
    errorRate: parseFloat((m.http_req_failed.values.rate * 100).toFixed(2)),
    responseTime: {
      avg: Math.round(dur.avg),
      p50: Math.round(dur.med),
      p90: Math.round(dur['p(90)']),
      p95: Math.round(dur['p(95)']),
      p99: Math.round(dur['p(99)']),
      max: Math.round(dur.max),
    },
    serverTime: {
      avg: Math.round(wait.avg),
      p95: Math.round(wait['p(95)']),
      p99: Math.round(wait['p(99)']),
    },
    apis: {},
    stages: {},
  };

  const apis = collectApiMetrics(m);
  apis.forEach(api => {
    result.apis[api.name] = {
      count: api.count,
      p50: Math.round(api.p50),
      p95: Math.round(api.p95),
      p99: Math.round(api.p99),
      errorRate: parseFloat(api.errRate.toFixed(2)),
    };
  });

  if (config.stages) {
    config.stages.forEach(s => {
      const sv = m[s.key]?.values;
      if (sv) {
        result.stages[s.label.trim()] = {
          avg: Math.round(sv.avg),
          p95: Math.round(sv['p(95)']),
          p99: Math.round(sv['p(99)']),
        };
      }
    });
  }

  return result;
}
