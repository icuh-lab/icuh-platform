package re.kr.icuh.icuhplatform.file.dto.request;

import jakarta.validation.constraints.NotNull;

public record CompletedFileUpload(
        @NotNull String originalFileName,
        @NotNull String storedFileName,
        @NotNull String filePath,
        @NotNull Long fileSize,
        @NotNull String extension
) {}
