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
) {}
