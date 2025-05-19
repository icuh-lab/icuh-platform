package re.kr.icuh.icuhplatform.dto.file;

import re.kr.icuh.icuhplatform.domain.FileEntity;
import re.kr.icuh.icuhplatform.dto.extension.ExtensionResponse;

import java.time.LocalDateTime;

public record FileResponse(
        Long id,
        String originalFilename,
        String filePath,
        Long fileSize,
        LocalDateTime createdAt,
        ExtensionResponse extension,
        String downloadUrl
) {
    public static FileResponse fromEntity(FileEntity file) {
        return new FileResponse(
                file.getId(),
                file.getOriginalFilename(),
                file.getFilePath(),
                file.getFileSize(),
                file.getCreatedAt(),
                ExtensionResponse.fromEntity(file.getExtension()),
                "/api/files/" + file.getId() + "/download"
        );
    }
}
