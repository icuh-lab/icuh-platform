package re.kr.icuh.icuhplatform.dto.article;

public record DeleteArticleRequest(
        String reason,
        String password
) {
}
