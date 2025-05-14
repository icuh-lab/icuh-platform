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
	PRODUCT_LIST_GET_SUCCESS(HttpStatus.OK, "상품 리스트 조회 성공", true),
	PRODUCT_GET_SUCCESS(HttpStatus.OK, "상품 조회 성공", true),
	PRODUCT_UPDATE_SUCCESS(HttpStatus.OK, "상품 수정 성공", true),
	PRODUCT_DELETE_SUCCESS(HttpStatus.OK, "상품 삭제 성공", true),

	/**
	 * 201 Createed
	 */
	PRODUCT_CREATE_SUCCESS(HttpStatus.CREATED, "상품 생성 성공", true),
	;

	private final HttpStatus httpStatus;
	private final String message;
	private final boolean isSuccess;
}
