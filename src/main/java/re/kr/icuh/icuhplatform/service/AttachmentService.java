package re.kr.icuh.icuhplatform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import re.kr.icuh.icuhplatform.domain.Attachment;
import re.kr.icuh.icuhplatform.dto.CreateAttachmentDto;
import re.kr.icuh.icuhplatform.repository.AttachmentRepository;
import re.kr.icuh.icuhplatform.util.AttachmentStore;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentStore attachmentStore;
    private final AttachmentRepository attachmentRepository;

    public void createAttachment(MultipartFile attachmentDtos) throws IOException {

        CreateAttachmentDto attachmentDto = attachmentStore.storeFile(attachmentDtos);
        Attachment attachment = attachmentDto.toAttachment(attachmentDto);

        attachmentRepository.save(attachment);
    }
}
