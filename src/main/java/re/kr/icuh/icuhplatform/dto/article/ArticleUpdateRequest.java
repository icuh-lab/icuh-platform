package re.kr.icuh.icuhplatform.dto.article;

public record ArticleUpdateRequest(
        String title,
        String description,
        String tempPassword
) {
}
