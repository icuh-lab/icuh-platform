package re.kr.icuh.icuhplatform.dto.article;

public record CreateArticleResponse(
        Long articleId
) {
    public static CreateArticleResponse of(Long articleId) {
        return new CreateArticleResponse(articleId);
    }
}
