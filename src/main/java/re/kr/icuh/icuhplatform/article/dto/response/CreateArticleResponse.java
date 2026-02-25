package re.kr.icuh.icuhplatform.article.dto.response;

public record CreateArticleResponse(
        Long articleId
) {
    public static CreateArticleResponse of(Long articleId) {
        return new CreateArticleResponse(articleId);
    }
}
