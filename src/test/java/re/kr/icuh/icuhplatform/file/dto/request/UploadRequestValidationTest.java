package re.kr.icuh.icuhplatform.file.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("업로드 요청 DTO 필수값 검증")
class UploadRequestValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        validatorFactory.close();
    }

    @Test
    @DisplayName("InitiateUploadRequestDto - fileName이 null이면 제약 위반이 발생한다")
    void initiateUpload_fileNameNull_hasViolation() {
        InitiateUploadRequestDto request = new InitiateUploadRequestDto();
        request.setFileName(null);
        request.setFileType("image/png");
        request.setFileSize(100L);

        Set<ConstraintViolation<InitiateUploadRequestDto>> violations = validator.validate(request);

        assertThat(violations).hasSizeGreaterThanOrEqualTo(1);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("fileName");
    }

    @Test
    @DisplayName("InitiateUploadRequestDto - 필수값이 모두 채워지면 제약 위반이 없다")
    void initiateUpload_allPopulated_noViolation() {
        InitiateUploadRequestDto request = new InitiateUploadRequestDto();
        request.setFileName("stored.png");
        request.setFileType("image/png");
        request.setFileSize(100L);

        Set<ConstraintViolation<InitiateUploadRequestDto>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("CompleteUploadRequestDto - fileName이 null이면 제약 위반이 발생한다")
    void completeUpload_fileNameNull_hasViolation() {
        CompleteUploadRequestDto request = new CompleteUploadRequestDto(
                "upload-id",
                null,
                List.of(new PartETagDto(1, "etag")),
                1L,
                100L,
                "origin.png",
                "COMPLETED"
        );

        Set<ConstraintViolation<CompleteUploadRequestDto>> violations = validator.validate(request);

        assertThat(violations).hasSizeGreaterThanOrEqualTo(1);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("fileName");
    }

    @Test
    @DisplayName("CompleteUploadRequestDto - originFileName이 null이면 제약 위반이 발생한다")
    void completeUpload_originFileNameNull_hasViolation() {
        CompleteUploadRequestDto request = new CompleteUploadRequestDto(
                "upload-id",
                "stored.png",
                List.of(new PartETagDto(1, "etag")),
                1L,
                100L,
                null,
                "COMPLETED"
        );

        Set<ConstraintViolation<CompleteUploadRequestDto>> violations = validator.validate(request);

        assertThat(violations).hasSizeGreaterThanOrEqualTo(1);
        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("originFileName");
    }

    @Test
    @DisplayName("CompleteUploadRequestDto - 필수값이 모두 채워지면 제약 위반이 없다")
    void completeUpload_allPopulated_noViolation() {
        CompleteUploadRequestDto request = new CompleteUploadRequestDto(
                "upload-id",
                "stored.png",
                List.of(new PartETagDto(1, "etag")),
                1L,
                100L,
                "origin.png",
                "COMPLETED"
        );

        Set<ConstraintViolation<CompleteUploadRequestDto>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }
}
