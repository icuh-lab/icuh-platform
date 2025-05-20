package re.kr.icuh.icuhplatform.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import re.kr.icuh.icuhplatform.domain.*;
import re.kr.icuh.icuhplatform.dto.CreateAttachmentDto;
import re.kr.icuh.icuhplatform.global.exception.BusinessException;
import re.kr.icuh.icuhplatform.global.exception.ErrorCode;
import re.kr.icuh.icuhplatform.global.util.FileUtils;
import re.kr.icuh.icuhplatform.repository.ExtensionRepository;
import re.kr.icuh.icuhplatform.repository.FileRepository;
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
    private final FileRepository fileRepository;
    private final ExtensionRepository extensionRepository;


    public void createFile(List<MultipartFile> file) throws IOException {

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

    public void uploadLargeFiles(List<MultipartFile> files, Article article) {
        for (MultipartFile file : files) {
            uploadLargeFile(file, article);
        }
    }

    private void uploadLargeFile(MultipartFile multipartFile, Article article) {
        File tempFile = null;

        try {
            tempFile = convertToTempFile(multipartFile);
            FileMetadata metadata = createFileMetadata(multipartFile);
            String fileUrl = uploadToS3(tempFile);
            saveFileMetadataToFileEntity(metadata, fileUrl, article);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FILE_SIZE_EXCEEDED, e.getMessage());
        } finally {
            fileUtils.deleteTempFile(tempFile);
        }
    }

    private File convertToTempFile(MultipartFile multipartFile) {
        try {
            return fileUtils.convertToTempFile(multipartFile);
        } catch (IOException | RuntimeException e) {
            throw new BusinessException(ErrorCode.UNSUPPORTED_FILE_TYPE, e.getMessage());
        }
    }

    private FileMetadata createFileMetadata(MultipartFile multipartFile) {
        try {
            return fileUtils.createFileMetadata(multipartFile);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.UNSUPPORTED_FILE_TYPE, e.getMessage());
        }
    }

    private String uploadToS3(File tempFile) {
        try {
            return s3FileUploader.uploadLargeAttachment(tempFile);
        } catch (AmazonServiceException ase) {
            throw new BusinessException(ErrorCode.UNSUPPORTED_FILE_TYPE, ase.getMessage());
        } catch (SdkClientException sce) {
            throw new BusinessException(ErrorCode.UNSUPPORTED_FILE_TYPE, sce.getMessage());
        }
    }

    private void saveFileMetadataToFileEntity(FileMetadata fileMetadata, String fileUrl, Article article) {

        // Extension 가져오기
        Extension extension = extensionRepository.findByName(fileMetadata.getExtensionName())
                .orElseThrow(() -> new BusinessException(ErrorCode.UNSUPPORTED_FILE_TYPE));

        FileEntity fileEntity = FileEntity.builder()
                .article(article)
                .originalFilename(fileMetadata.getOriginalName())
                .storedFilename(fileMetadata.getSavedName())
                .filePath(fileUrl)
                .fileSize(fileMetadata.getSize())
                .extension(extension)
                .build();

        fileRepository.save(fileEntity);
    }

    private void validateNull(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }
    }

    private void validateExtension(MultipartFile multipartFile) {
        if (multipartFile.getOriginalFilename().endsWith(".exe") || multipartFile.getOriginalFilename().endsWith(".dmg")) {
            throw new BusinessException(ErrorCode.UNSUPPORTED_FILE_TYPE);
        }
    }
}
