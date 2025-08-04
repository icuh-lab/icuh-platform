package re.kr.icuh.icuhplatform.global.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SuccessCode {

	/**
	 * 200 OK
	 */
	ARTICLE_LIST_GET_SUCCESS(HttpStatus.OK, "ARTICLE_LIST_GET_SUCCESS", "게시글 리스트 조회 성공"),
	ARTICLE_GET_SUCCESS(HttpStatus.OK, "ARTICLE_GET_SUCCESS", "게시글 조회 성공"),
	ARTICLE_UPDATE_PENDING(HttpStatus.OK, "ARTICLE_UPDATE_PENDING", "게시글 수정 요청 완료"),
	ARTICLE_UPDATE_SUCCESS(HttpStatus.OK, "ARTICLE_UPDATE_SUCCESS", "게시글 수정 성공"),
	ARTICLE_DELETE_SUCCESS(HttpStatus.OK, "ARTICLE_DELETE_SUCCESS", "게시글 삭제 성공"),

	/**
	 * 201 Createed
	 */
	ARTICLE_CREATE_SUCCESS(HttpStatus.CREATED, "ARTICLE_CREATE_SUCCESS", "게시글 생성 완료");

	private final HttpStatus status;
	private final String code;
	private final String message;

	SuccessCode(HttpStatus status, String code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}
}
