package re.kr.icuh.icuhplatform.article.dto.response;

import re.kr.icuh.icuhplatform.article.domain.Article;
import re.kr.icuh.icuhplatform.category.dto.response.DocumentTypeResponse;
import re.kr.icuh.icuhplatform.category.dto.response.SubjectDomainResponse;
import re.kr.icuh.icuhplatform.file.dto.response.FileResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record ArticleDetailResponse(
        Long id,
        String title,
        String description,
        String author,
        String authorOrganization,
        String department,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Integer views,
        DocumentTypeResponse classification,
        SubjectDomainResponse serviceType,
        List<FileResponse> files
) {
    public static ArticleDetailResponse of(Article article) {
        return new ArticleDetailResponse(
                article.getId(),
                article.getTitle(),
                article.getDescription(),
                article.getAuthor(),
                article.getAuthorOrganization(),
                article.getDepartment(),
                article.getCreatedAt(),
                article.getUpdatedAt(),
                article.getViews(),
                DocumentTypeResponse.fromEntity(article.getDocumentType()),
                SubjectDomainResponse.fromEntity(article.getSubjectDomain()),
                article.getFiles().stream()
                        .map(FileResponse::fromEntity)
                        .collect(Collectors.toList())
        );
    }
}
