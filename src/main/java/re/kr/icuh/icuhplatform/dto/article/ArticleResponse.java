package re.kr.icuh.icuhplatform.dto.article;

import re.kr.icuh.icuhplatform.domain.Article;
import re.kr.icuh.icuhplatform.domain.FileEntity;
import re.kr.icuh.icuhplatform.domain.FileStatus;
import re.kr.icuh.icuhplatform.dto.documenttype.DocumentTypeResponse;
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
        List<FileResponseWithDownloadInfo> files
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
                        .filter(file -> file.getStatus() == FileStatus.APPROVED)
                        .map(FileResponseWithDownloadInfo::fromEntity)
                        .collect(Collectors.toList())
        );
    }
}

// 파일 다운로드 정보가 포함된 FileResponse 클래스
record FileResponseWithDownloadInfo(
        Long id,
        String originalFilename,
        String extension,
        Long fileSize,
        String filePath,
        String downloadUrl
) {
    public static FileResponseWithDownloadInfo fromEntity(FileEntity file) {
        return new FileResponseWithDownloadInfo(
                file.getId(),
                file.getOriginalFilename(),
                file.getExtension(),
                file.getFileSize(),
                file.getFilePath(),
                "/api/v1/files/" + file.getId() + "/download"
        );
    }
}

