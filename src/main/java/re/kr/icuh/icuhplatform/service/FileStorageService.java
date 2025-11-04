package re.kr.icuh.icuhplatform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import re.kr.icuh.icuhplatform.domain.*;
import re.kr.icuh.icuhplatform.global.exception.BusinessException;
import re.kr.icuh.icuhplatform.global.exception.ErrorCode;
import re.kr.icuh.icuhplatform.global.util.FileUtils;
import re.kr.icuh.icuhplatform.repository.FileEditRequestRepository;
import re.kr.icuh.icuhplatform.repository.FileRepository;

import java.io.File;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final FileUtils fileUtils;
    private final S3FileUploader s3FileUploader;
    private final FileRepository fileRepository;
    private final FileEditRequestRepository fileEditRequestRepository;


    public void uploadLargeFiles(List<MultipartFile> files, Article article) {
        for (MultipartFile file : files) {
            uploadLargeFile(file, article);
        }
    }

    private void uploadLargeFile(MultipartFile multipartFile, Article article) {
        File tempFile = null;
        log.info("uploadLargeFile - multipartFile: {}", multipartFile.getOriginalFilename());

        try {
            FileMetadata metadata = fileUtils.createFileMetadata(multipartFile);
            tempFile = fileUtils.convertToTempFile(multipartFile);

            if (tempFile == null) {
                log.info("uploadLargeFile - tempFile is null");
            }

            log.info("uploadLargeFile - tempFile: {}", tempFile.getAbsoluteFile());

            String fileUrl = s3FileUploader.uploadFile(tempFile, metadata.getSavedName());
            saveFileMetadataToFileEntity(article, metadata, fileUrl);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FILE_SIZE_EXCEEDED, e.getMessage());
        } finally {
            fileUtils.deleteTempFile(tempFile);
        }
    }


    private void saveFileMetadataToFileEntity(Article article, FileMetadata fileMetadata, String fileUrl) {

        FileEntity fileEntity = FileEntity.builder()
                .article(article)
                .originalFilename(fileMetadata.getOriginalName())
                .storedFilename(fileMetadata.getSavedName())
                .filePath(fileUrl)
                .fileSize(fileMetadata.getSize())
                .extension(fileMetadata.getExtensionName())
                .build();

        fileRepository.save(fileEntity);
    }


    public void updateLargeFiles(List<MultipartFile> files, ArticleEditRequest articleEditRequest) {
        for (MultipartFile file : files) {
            updateLargeFile(file, articleEditRequest);
        }
    }

    private void updateLargeFile(MultipartFile multipartFile, ArticleEditRequest articleEditRequest) {
        File tempFile = null;

        try {
            FileMetadata metadata = fileUtils.createFileMetadata(multipartFile);
            tempFile = fileUtils.convertToTempFile(multipartFile);

            String fileUrl = s3FileUploader.uploadFile(tempFile, metadata.getSavedName());
            updateFileMetadataToFileEntity(articleEditRequest, metadata, fileUrl);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FILE_SIZE_EXCEEDED, e.getMessage());
        } finally {
            fileUtils.deleteTempFile(tempFile);
        }
    }

    private void updateFileMetadataToFileEntity(ArticleEditRequest articleEditRequest, FileMetadata fileMetadata, String fileUrl) {

        FileEditRequest fileEditRequest = FileEditRequest.builder()
                .articleEditRequest(articleEditRequest)
                .originalFilename(fileMetadata.getOriginalName())
                .storedFilename(fileMetadata.getSavedName())
                .filePath(fileUrl)
                .fileSize(fileMetadata.getSize())
                .extension(fileMetadata.getExtensionName())
                .build();

        fileEditRequestRepository.save(fileEditRequest);

    }



}
