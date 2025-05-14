package re.kr.icuh.icuhplatform.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import re.kr.icuh.icuhplatform.global.exception.ErrorCode;
import re.kr.icuh.icuhplatform.global.exception.S3Exception;
import re.kr.icuh.icuhplatform.global.util.FileUtils;
import re.kr.icuh.icuhplatform.domain.Attachment;
import re.kr.icuh.icuhplatform.domain.FileMetadata;
import re.kr.icuh.icuhplatform.dto.CreateAttachmentDto;
import re.kr.icuh.icuhplatform.repository.FileStorageRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final FileUtils fileUtils;
    private final S3FileUploader s3FileUploader;
    private final FileStorageRepository fileStorageRepository;

    public void createAttachment(List<MultipartFile> file) throws IOException {

        for (MultipartFile multipartFile : file) {
            validateNull(multipartFile);
            validateExtension(multipartFile);
        }

        List<CreateAttachmentDto> attachmentDtos = s3FileUploader.storeAttachments(file);

        for (CreateAttachmentDto attachmentDto : attachmentDtos) {
            Attachment attachment = attachmentDto.toAttachment(attachmentDto);
            fileStorageRepository.save(attachment);
        }
    }

    public void uploadLargeFile(MultipartFile multipartFile) throws FileUploadException {
        validateNull(multipartFile);
        validateExtension(multipartFile);

        File tempFile = null;

        try {
            tempFile = convertToTempFile(multipartFile);
            FileMetadata metadata = createFileMetadata(multipartFile);
            String fileUrl = uploadToS3(tempFile);
            saveFileMetadata(metadata, fileUrl);
        } catch (Exception e) {
            throw new S3Exception(ErrorCode.FILE_READ_ERROR);
        } finally {
            fileUtils.deleteTempFile(tempFile);
        }
    }

    private File convertToTempFile(MultipartFile multipartFile) {
        try {
            return fileUtils.convertToTempFile(multipartFile);
        } catch (IOException | RuntimeException e) {
            throw new S3Exception(ErrorCode.MULTIPART_TO_FILE_ERROR);
        }
    }

    private FileMetadata createFileMetadata(MultipartFile multipartFile) {
        try {
            return fileUtils.createFileMetadata(multipartFile);
        } catch (IllegalArgumentException e) {
            throw new S3Exception(ErrorCode.FILE_METADATA_CREATE_FAIL);
        }
    }

    private String uploadToS3(File tempFile) {
        try {
            return s3FileUploader.uploadLargeAttachment(tempFile);
        } catch (AmazonServiceException ase) {
            throw new S3Exception(ErrorCode.FILE_UPLOAD_FAIL_SERVER);
        } catch (SdkClientException sce) {
            throw new S3Exception(ErrorCode.FILE_UPLOAD_FAIL_CLIENT);
        }
    }

    private void saveFileMetadata(FileMetadata fileMetadata, String fileUrl) {
        CreateAttachmentDto dto = CreateAttachmentDto.builder()
            .originalName(fileMetadata.getOriginalName())
            .savedPath(fileUrl)
            .savedName(fileMetadata.getSavedName())
            .extensionName(fileMetadata.getExtensionName())
            .size(fileMetadata.getSize())
            .build();

        Attachment attachment = dto.toAttachment(dto);
        fileStorageRepository.save(attachment);
    }

    private void validateNull(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new S3Exception(ErrorCode.FILE_NOT_EXIST);
        }
    }

    private void validateExtension(MultipartFile multipartFile) {
        if (multipartFile.getOriginalFilename().endsWith(".exe") || multipartFile.getOriginalFilename().endsWith(".dmg")) {
            throw new S3Exception(ErrorCode.NOT_SUPPORT_EXTENSION);
        }
    }
}
