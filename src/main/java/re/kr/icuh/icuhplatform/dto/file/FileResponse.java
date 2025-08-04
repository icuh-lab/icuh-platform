package re.kr.icuh.icuhplatform.dto.file;

import re.kr.icuh.icuhplatform.domain.FileEntity;

import java.time.LocalDateTime;

public record FileResponse(
        Long id,
        String originalFilename,
        String filePath,
        Long fileSize,
        String extension,
        LocalDateTime createdAt
) {
    public static FileResponse fromEntity(FileEntity file) {
        return new FileResponse(
                file.getId(),
                file.getOriginalFilename(),
                file.getFilePath(),
                file.getFileSize(),
                file.getExtension(),
                file.getCreatedAt()
        );
    }
}
