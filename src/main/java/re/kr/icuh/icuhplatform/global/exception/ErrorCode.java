package re.kr.icuh.icuhplatform.global.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {

	/**
	 * 400 Bad Request
	 */
	INVALID_SEARCH_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 검색 파라미터", false),
	INVALID_REQUEST_DATA(HttpStatus.BAD_REQUEST, "유효하지 않은 데이터", false),

	/**
	 * 401 Unauthorized
	 */
	UNAUTHORIZED_TEMP_PASSWORD(HttpStatus.UNAUTHORIZED, "임시 비밀번호 불일치", false),

	/**
	 * 404 Not Found
	 */
	ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글 없음", false),
	FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "파일 없음", false),
	FILE_NOT_EXIST(HttpStatus.NOT_FOUND, "업로드할 파일이 존재하지 않습니다.", false),
	FILE_READ_ERROR(HttpStatus.NOT_FOUND, "파일 읽기 중 오류가 발생했습니다.", false),
	FILE_METADATA_CREATE_FAIL(HttpStatus.NOT_FOUND, "존재하지 않는 상품 정보입니다.", false),
	FILE_UPLOAD_FAIL_CLIENT(HttpStatus.NOT_FOUND, "S3 업로드 실패 - 서비스 오류", false),
	FILE_UPLOAD_FAIL_SERVER(HttpStatus.NOT_FOUND, "S3 업로드 실패 - 클라이언트 오류", false),

	MULTIPART_TO_FILE_ERROR(HttpStatus.NOT_FOUND, "임시파일 변환 실패", false),

	/**
	 * 413 Payload Too Large
	 */
	FILE_SIZE_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, "업로드 파일 크기 초과", false),


	/**
	 * 415 Unsupported Media Type
	 */
	UNSUPPORTED_FILE_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 파일 확장자", false),

	/**
	 * 500 Internal Server Error
	 */
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.", false),
	;

	private final HttpStatus httpStatus;
	private final String message;
	private final boolean isSuccess;
}
