package org.kwakmunsu.haruhana.domain.problem.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.problem.enums.ProblemDifficulty;

@Getter
@RequiredArgsConstructor
public enum Prompt {

    V1_PROMPT("""
            당신은 {category} 분야의 시니어 소프트웨어 엔지니어링 면접관입니다.

            신입~주니어 개발자를 대상으로 한 실무 중심의 기술 면접 질문과 답변을 생성해주세요.

            ## 요구사항

            1. **주제**: {category}
            2. **난이도**: {difficulty}
            3. **질문**: 한 가지 핵심 개념을 명확하게 묻는 형식
            4. **답변**: 구조화되고 가독성 높은 마크다운 형식으로 작성
            5. 실제 면접에서 자주 나오는 실용적인 질문
            6. 단순 암기보다는 이해와 적용을 중심으로

            ## 답변 작성 가이드 (aiAnswer 필드)

            답변은 **반드시 마크다운 문법**을 사용하여 가독성 높게 작성하세요:

            - **주요 개념 설명**: 핵심 개념을 명확하게 정의
            - **구체적인 예시**: 코드나 실무 상황 예시 포함
            - **추가 설명**: 장단점, 사용 시기, 주의사항 등

            ### 마크다운 포맷팅 규칙:
            - 제목: `## 제목`, `### 소제목` 사용
            - 강조: **볼드체**로 중요 키워드 강조
            - 리스트: `-` 또는 `1.`로 항목 나열
            - 코드: 인라인 코드는 `코드`, 코드 블록은 ```언어 형식
            - 구분: `---`로 섹션 구분 (필요시)

            ## 출력 형식 (JSON)

            **반드시 아래 JSON 형식으로만 답변하세요. JSON 외부에 마크다운 코드 블록(```)을 사용하지 마세요.**

            {
              "title": "질문을 10자 이내로 요약한 제목 (예: REST API란?)",
              "description": "구체적이고 명확한 면접 질문 (1-2문장)",
              "aiAnswer": "마크다운 형식의 구조화된 모범 답변 (제목, 볼드, 리스트, 코드 블록 등 활용)"
            }

            ## 주의사항

            - 난이도에 맞는 적절한 깊이로 설명 (초급: 기본 개념, 중급: 실무 활용, 고급: 최적화/트레이드오프)
            - 지나치게 장황하거나 이론적인 설명은 지양
            - 실무에서 실제로 활용 가능한 내용 중심
            - 코드 예시는 간결하고 이해하기 쉽게
            - JSON 문자열이므로 줄바꿈은 \\n 으로, 따옴표는 \\" 로 이스케이프 처리

            {category} 분야의 {difficulty} 난이도 면접 질문과 답변을 생성해주세요.
            """
    );

    private final String template;

    /**
     * 카테고리와 난이도를 주입하여 프롬프트 생성
     */
    public String generate(String categoryName, ProblemDifficulty difficulty) {
        return template
                .replace("{category}", categoryName)
                .replace("{difficulty}", translateDifficulty(difficulty));
    }

    /**
     * 난이도를 한글로 변환
     */
    private String translateDifficulty(ProblemDifficulty difficulty) {
        return switch (difficulty) {
            case EASY -> "초급 (기본 개념과 간단한 사용법)";
            case MEDIUM -> "중급 (실무 적용과 심화 개념)";
            case HARD -> "고급 (최적화, 설계, 트레이드오프)";
        };
    }

}