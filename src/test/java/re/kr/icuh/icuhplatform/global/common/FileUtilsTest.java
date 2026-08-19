package re.kr.icuh.icuhplatform.global.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class FileUtilsTest {

    private final FileUtils fileUtils = new FileUtils();

    @Test
    @DisplayName("null 파일명의 확장자는 빈 문자열로 처리한다 (NPE 없음)")
    void 확장자_추출은_null_입력에서_빈_문자열을_반환한다() {
        assertThat(fileUtils.extractExtensionName(null)).isEmpty();
    }

    @Test
    @DisplayName("null 파일명으로 저장명을 생성해도 예외를 던지지 않는다")
    void 저장명_생성은_null_입력에서_예외를_던지지_않는다() {
        assertThatCode(() -> fileUtils.createStoreFileName(null))
                .doesNotThrowAnyException();
    }
}
