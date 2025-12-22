package re.kr.icuh.icuhplatform.dto.file;

import re.kr.icuh.icuhplatform.domain.FileEntity;

public record FileResponse(
        Long id,
        String originalFilename,
        String extension,
        Long fileSize,
        String filePath,
        String downloadUrl
) {
    public static FileResponse fromEntity(FileEntity file) {
        return new FileResponse(
                file.getId(),
                file.getOriginalFilename(),
                file.getExtension(),
                file.getFileSize(),
                file.getFilePath(),
                "/api/v1/files/" + file.getId() + "/download"
        );
    }
}
