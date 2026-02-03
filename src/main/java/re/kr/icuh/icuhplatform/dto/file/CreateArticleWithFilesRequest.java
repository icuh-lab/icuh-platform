package re.kr.icuh.icuhplatform.dto.file;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateArticleWithFilesRequest(
        @NotNull String title,
        @NotNull String description,
        @NotNull String author,
        @NotNull String authorOrganization,
        @NotNull String department,
        @NotNull String tempPassword,
        @NotNull Long documentTypeId,
        @NotNull Long subjectDomainId,
        @NotNull String source,
        @NotNull List<CompletedFileUpload> completedFiles
) {
    public record CompletedFileUpload(
            @NotNull String uploadId,           // /generate-upload-id에서 받은 uploadId
            @NotNull String s3Key,              // /generate-upload-id에서 받은 s3Key
            @NotNull String s3Location,         // /complete-upload에서 받은 location
            @NotNull String originalFileName,   // 원본 파일명
            @NotNull Long fileSize,             // 파일 크기 (바이트)
            String contentType
    ) {}
}
