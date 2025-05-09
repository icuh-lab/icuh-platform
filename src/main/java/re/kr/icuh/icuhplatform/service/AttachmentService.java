package re.kr.icuh.icuhplatform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import re.kr.icuh.icuhplatform.common.FileMetadata;
import re.kr.icuh.icuhplatform.common.FileUtils;
import re.kr.icuh.icuhplatform.domain.Attachment;
import re.kr.icuh.icuhplatform.dto.CreateAttachmentDto;
import re.kr.icuh.icuhplatform.repository.AttachmentRepository;
import re.kr.icuh.icuhplatform.util.S3FileUploader;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final FileUtils fileUtils;
    private final S3FileUploader s3FileUploader;
    private final AttachmentRepository attachmentRepository;

    public void createAttachment(List<MultipartFile> file) throws IOException {

        List<CreateAttachmentDto> attachmentDtos = s3FileUploader.storeAttachments(file);

        for (CreateAttachmentDto attachmentDto : attachmentDtos) {
            Attachment attachment = attachmentDto.toAttachment(attachmentDto);
            attachmentRepository.save(attachment);
        }
    }

    public void uploadLargeFile(MultipartFile multipartFile) throws FileUploadException {

        File tempFile = null;
        try {

            tempFile = fileUtils.convertToTempFile(multipartFile);
            String fileUrl = s3FileUploader.uploadLargeAttachment(tempFile);

            FileMetadata metadata = fileUtils.createFileMetadata(multipartFile);
            CreateAttachmentDto dto = CreateAttachmentDto.builder()
                    .originalName(metadata.getOriginalName())
                    .savedPath(fileUrl)
                    .savedName(metadata.getSavedName())
                    .extensionName(metadata.getExtensionName())
                    .size(metadata.getSize())
                    .build();

            Attachment attachment = dto.toAttachment(dto);
            attachmentRepository.save(attachment);

        } catch (Exception e) {
            log.error("파일 업로드 실패", e);
            throw new FileUploadException("파일 업로드 중 오류가 발생했습니다.", e);
        } finally {
            if (tempFile != null) {
                fileUtils.deleteTempFile(tempFile);
            }
        }
    }
}
