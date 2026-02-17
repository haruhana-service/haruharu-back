#!/bin/bash

# k6 테스트 실행 스크립트
# 사용법: ./run-test.sh smoke|load|stress|spike [BASE_URL]

set -e

TEST_TYPE=$1
BASE_URL=${2:-"https://api.haruharu.online"}

if [ -z "$TEST_TYPE" ]; then
  echo "사용법: ./run-test.sh <test-type> [BASE_URL]"
  echo ""
  echo "테스트 타입:"
  echo "  smoke   - 기본 동작 확인 (1분)"
  echo "  load    - 부하 테스트 (15분)"
  echo "  stress  - 스트레스 테스트 (14분)"
  echo "  spike   - 급증 테스트 (6분)"
  echo ""
  echo "예시: ./run-test.sh smoke https://api.haruharu.online"
  exit 1
fi

# 테스트 스크립트 파일 확인
SCRIPT_FILE="scripts/${TEST_TYPE}-test.js"
if [ ! -f "$SCRIPT_FILE" ]; then
  echo "❌ 테스트 스크립트를 찾을 수 없습니다: $SCRIPT_FILE"
  exit 1
fi

echo "🚀 k6 테스트 시작"
echo "   타입: ${TEST_TYPE}-test"
echo "   URL: ${BASE_URL}"
echo ""
echo "💡 Grafana 대시보드에서 실시간 모니터링:"
echo "   http://localhost:3000 (SSH 터널링 필요)"
echo ""

# Docker로 k6 실행 (간소화 버전)
docker run --rm -i \
  -e BASE_URL="${BASE_URL}" \
  -v "$(pwd)/scripts:/scripts" \
  grafana/k6:latest run \
  "/scripts/${TEST_TYPE}-test.js"

echo ""
echo "✅ 테스트 완료"
echo "📊 Spring Boot 메트릭: http://localhost:3001 (Grafana)"
