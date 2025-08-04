package re.kr.icuh.icuhplatform.dto.article;

import re.kr.icuh.icuhplatform.domain.Article;
import re.kr.icuh.icuhplatform.domain.FileStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record ArticleListResponse(
        Long id,
        String title,
        String authorOrganization,
        LocalDateTime updatedAt,
        Integer views,
        List<String> extensions,
        String documentType,
        String subjectDomain,
        String source
) {
    public static ArticleListResponse fromEntity(Article article) {
        return new ArticleListResponse(
                article.getId(),
                article.getTitle(),
                article.getAuthorOrganization(),
                article.getUpdatedAt(),
                article.getViews(),
                article.getFiles().stream()
                        .filter(file -> file.getStatus() == FileStatus.APPROVED)
                        .map(file -> file.getExtension())
                        .collect(Collectors.toList()),
                article.getDocumentType().getEnName(),
                article.getSubjectDomain().getEnName(),
                article.getSource()
        );
    }
}
