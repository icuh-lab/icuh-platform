package re.kr.icuh.icuhplatform.global.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ErrorCode 규약 검증: 각 상수의 {@code code} 문자열은 enum 상수 이름과 동일하게 유지한다.
 * code를 이름에서 수동으로 복제하는 구조라, 복붙으로 인한 불일치(드리프트)를 이 불변식으로 막는다.
 */
class ErrorCodeTest {

    @DisplayName("각 ErrorCode의 code 문자열은 enum 상수 이름과 일치한다")
    @ParameterizedTest(name = "{0}")
    @EnumSource(ErrorCode.class)
    void code는_enum_이름과_일치한다(ErrorCode errorCode) {
        // then
        assertThat(errorCode.getCode()).isEqualTo(errorCode.name());
    }
}
