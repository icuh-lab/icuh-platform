package re.kr.icuh.icuhplatform.dto;

import lombok.*;
import re.kr.icuh.icuhplatform.domain.Attachment;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CreateAttachmentDto {

    private String originalName;
    private String savedPath;
    private String savedName;
    private String extensionName;
    private Integer size;
    private LocalDateTime createdAt;

    @Builder
    public CreateAttachmentDto(String originalName, String savedPath, String savedName, String extensionName, Integer size) {
        this.originalName = originalName;
        this.savedPath = savedPath;
        this.savedName = savedName;
        this.extensionName = extensionName;
        this.size = size;
    }

    public Attachment toAttachment(CreateAttachmentDto createAttachmentDto) {
        return Attachment.builder()
                .originalName(createAttachmentDto.getOriginalName())
                .savedPath(createAttachmentDto.getSavedPath())
                .savedName(createAttachmentDto.getSavedName())
                .extensionName(createAttachmentDto.getExtensionName())
                .size(createAttachmentDto.getSize())
                .createdAt(createAttachmentDto.getCreatedAt())
                .build();
    }
}
