package re.kr.icuh.icuhplatform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import re.kr.icuh.icuhplatform.domain.*;
import re.kr.icuh.icuhplatform.dto.file.CompleteUploadRequestDto;
import re.kr.icuh.icuhplatform.dto.file.CompleteUploadResponseDto;
import re.kr.icuh.icuhplatform.global.exception.BusinessException;
import re.kr.icuh.icuhplatform.global.exception.ErrorCode;
import re.kr.icuh.icuhplatform.global.util.FileUtils;
import re.kr.icuh.icuhplatform.repository.ArticleEditRequestRepository;
import re.kr.icuh.icuhplatform.repository.ArticleRepository;
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
    private final ArticleRepository articleRepository;
    private final ArticleEditRequestRepository articleEditRequestRepository;


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

    @Transactional
    public void createFileMetaData(CompleteUploadRequestDto request, String location) {
        Article savedArticle = articleRepository.findById(request.getArticleId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ARTICLE_NOT_FOUND));

        FileEntity fileEntity = FileEntity.builder()
                .article(savedArticle)
                .originalFilename(request.getOriginFileName())
                .storedFilename(request.getFileName())
                .filePath(location)
                .fileSize(request.getFileSize())
                .extension(fileUtils.extractExtensionName(request.getOriginFileName()))
                .status(FileStatus.PENDING)
                .build();

        fileRepository.save(fileEntity);
    }

    @Transactional
    public CompleteUploadResponseDto updateFileMetaData(CompleteUploadRequestDto request, String location) {
        // article id로 게시글을 찾고, 해당 게시글의 pending_update 컬럼에 값이 있다면 해당 게시글은 업데이트 대기 중인 상태
        return CompleteUploadResponseDto.builder()
                .originalFileName(request.getOriginFileName())
                .storedFileName(request.getFileName())
                .filePath(location)
                .fileSize(request.getFileSize())
                .extension(fileUtils.extractExtensionName(request.getOriginFileName()))
                .build();
    }

}
