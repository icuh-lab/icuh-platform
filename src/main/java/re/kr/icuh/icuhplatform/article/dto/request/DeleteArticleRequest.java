package re.kr.icuh.icuhplatform.article.dto.request;

public record DeleteArticleRequest(
        String reason,
        String password
) {
}
