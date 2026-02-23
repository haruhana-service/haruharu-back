package org.kwakmunsu.haruhana.domain.member.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.kwakmunsu.haruhana.UnitTestSupport;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.error.HaruHanaException;

class NicknameFilterUnitTest extends UnitTestSupport {

    private NicknameFilter nicknameFilter;

    @BeforeEach
    void setUp() throws IOException {
        nicknameFilter = new NicknameFilter();
        nicknameFilter.init();
    }

    @Test
    void 정상_닉네임은_검사를_통과한다() {
        assertThatCode(() -> nicknameFilter.validate("하루하나유저"))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {"fuck", "FUCK", "Fuck", "shit", "SHIT", "bitch"})
    void 영어_욕설이_포함된_닉네임은_검사에_걸린다(String nickname) {
        assertThatThrownBy(() -> nicknameFilter.validate(nickname))
                .isInstanceOf(HaruHanaException.class)
                .hasMessage(ErrorType.INVALID_NICKNAME.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"씨발닉네임", "개새끼user", "병신123", "창녀"})
    void 한국어_욕설이_포함된_닉네임은_검사에_걸린다(String nickname) {
        assertThatThrownBy(() -> nicknameFilter.validate(nickname))
                .isInstanceOf(HaruHanaException.class)
                .hasMessage(ErrorType.INVALID_NICKNAME.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"porn유저", "야동user", "섹스닉", "자지123"})
    void 성적_단어가_포함된_닉네임은_검사에_걸린다(String nickname) {
        assertThatThrownBy(() -> nicknameFilter.validate(nickname))
                .isInstanceOf(HaruHanaException.class)
                .hasMessage(ErrorType.INVALID_NICKNAME.getMessage());
    }

    @Test
    void 공백으로_구분된_욕설도_검사에_걸린다() {
        assertThatThrownBy(() -> nicknameFilter.validate("씨 발"))
                .isInstanceOf(HaruHanaException.class)
                .hasMessage(ErrorType.INVALID_NICKNAME.getMessage());
    }

    @Test
    void 대소문자_구분없이_영어_욕설을_감지한다() {
        assertThatThrownBy(() -> nicknameFilter.validate("FUCK_USER"))
                .isInstanceOf(HaruHanaException.class)
                .hasMessage(ErrorType.INVALID_NICKNAME.getMessage());
    }

}
