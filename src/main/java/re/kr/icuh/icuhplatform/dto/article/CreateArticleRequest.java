package re.kr.icuh.icuhplatform.dto.article;

public record CreateArticleRequest(
        String title,
        String description,
        String author,
        String authorOrganization,
        String department,
        String tempPassword,
        Long classificationId,
        Long serviceTypeId
) {}
