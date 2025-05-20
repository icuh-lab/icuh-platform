package re.kr.icuh.icuhplatform.dto.article;

import jakarta.validation.constraints.NotNull;

public record CreateArticleRequest(
        @NotNull String title,
        @NotNull String description,
        @NotNull String author,
        @NotNull String authorOrganization,
        @NotNull String department,
        @NotNull String tempPassword,
        @NotNull Long classificationId,
        @NotNull Long serviceTypeId
) {}
