package re.kr.icuh.icuhplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import re.kr.icuh.icuhplatform.domain.Attachment;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDto {

    private String originName;
    private String savedName;

//    public Attachment toAttachment(CreateAttachmentDto createAttachmentDto) {
//        return Attachment.builder()
//                .originalName(createAttachmentDto.getOriginalName())
//                .savedPath(createAttachmentDto.getSavedPath())
//                .savedName(createAttachmentDto.getSavedName())
//                .extensionName(createAttachmentDto.getExtensionName())
//                .size(createAttachmentDto.getSize())
//                .createdAt(createAttachmentDto.getCreatedAt())
//                .build();
//    }
}
