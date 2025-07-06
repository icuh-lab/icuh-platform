package re.kr.icuh.icuhplatform.dto.article;

import re.kr.icuh.icuhplatform.domain.Article;
import re.kr.icuh.icuhplatform.domain.FileEntity;
import re.kr.icuh.icuhplatform.dto.documenttype.DocumentTypeResponse;
import re.kr.icuh.icuhplatform.dto.file.FileResponse;
import re.kr.icuh.icuhplatform.dto.subjectdomain.SubjectDomainResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record ArticleResponse(
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
    public static ArticleResponse fromEntity(Article article) {
        return new ArticleResponse(
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
                        .filter(file -> file.getStatus() == FileEntity.FileStatus.ACTIVE)
                        .map(FileResponse::fromEntity)
                        .collect(Collectors.toList())
        );
    }
}
