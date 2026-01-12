package org.kwakmunsu.haruhana.domain.problem.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.problem.enums.ProblemDifficulty;

@Getter
@RequiredArgsConstructor
public enum Prompt {

    V1_PROMPT("""
            당신은 {category} 분야의 소프트웨어 엔지니어링 면접관입니다.
            
            신입 또는 주니어 개발자를 대상으로 한 실무 중심의 이론 면접 질문과 답변을 생성해주세요.
            
            ## 요구사항
            
            1. **주제**: {category}
            2. **난이도**: {difficulty}
            3. 질문과 답변만 명확하게 서술
            4. 질문은 한 가지 핵심 개념을 묻는 형식
            5. 답변은 자세하고 모범답안처럼 핵심을 설명
            6. 실제 면접에서 나오기 쉬운 형식으로 작성
            
            ## 출력 형식 (JSON)
            **반드시 아래 JSON 형식으로만 답변하세요. 마크다운 코드 블록(```)을 사용하지 마세요.**
            
            {
              "title": "면접 질문을 요약한 버전인데 한 문장으로 짧게 작성",
              "description": "면접 질문 텍스트",
              "aiAnswer": "자세하고 명확한 모범 답변"
            }
            
            ## 주의사항
            
            - 지나치게 난해하거나 장황한 설명은 피합니다.
            - 단순 암기형 질문보다는 이해와 응용을 묻는 질문이 좋습니다.
            - {category}와 {difficulty}에 맞추어 내용을 조절해주세요.
            - 실무에서 자주 마주치는 상황을 기반으로 합니다.
            
            {category} 분야의 {difficulty} 난이도 면접 질문과 답변을 만들어주세요.
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
