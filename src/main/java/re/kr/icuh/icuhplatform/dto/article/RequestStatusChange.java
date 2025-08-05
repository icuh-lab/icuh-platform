package re.kr.icuh.icuhplatform.dto.article;

public record RequestStatusChange (
    String password,
    String reason,
    String type
) {}
