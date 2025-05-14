package re.kr.icuh.icuhplatform.global.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum SuccessCode {

	/**
	 * 200 OK
	 */
	ARTICLE_LIST_GET_SUCCESS(HttpStatus.OK, "게시글 리스트 조회 성공", true),
	ARTICLE_GET_SUCCESS(HttpStatus.OK, "게시글 조회 성공", true),
	ARTICLE_UPDATE_SUCCESS(HttpStatus.OK, "게시글 수정 성공", true),
	ARTICLE_DELETE_SUCCESS(HttpStatus.OK, "게시글 삭제 성공", true),

	/**
	 * 201 Createed
	 */
	ARTICLE_CREATE_SUCCESS(HttpStatus.CREATED, "게시글 생성 완료", true),
	;

	private final HttpStatus httpStatus;
	private final String message;
	private final boolean isSuccess;
}
