package re.kr.icuh.icuhplatform.global.exception;

public class S3Exception extends RuntimeException{

	public S3Exception(ErrorCode errorCode) {
		super(errorCode.getMessage());
	}
}
