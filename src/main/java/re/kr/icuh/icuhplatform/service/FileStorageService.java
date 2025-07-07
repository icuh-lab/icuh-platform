package re.kr.icuh.icuhplatform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import re.kr.icuh.icuhplatform.domain.Article;
import re.kr.icuh.icuhplatform.domain.Extension;
import re.kr.icuh.icuhplatform.domain.FileEntity;
import re.kr.icuh.icuhplatform.domain.FileMetadata;
import re.kr.icuh.icuhplatform.global.exception.BusinessException;
import re.kr.icuh.icuhplatform.global.exception.ErrorCode;
import re.kr.icuh.icuhplatform.global.util.FileUtils;
import re.kr.icuh.icuhplatform.repository.ExtensionRepository;
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
    private final ExtensionRepository extensionRepository;


    public void uploadLargeFiles(List<MultipartFile> files, Article article) {
        for (MultipartFile file : files) {
            uploadLargeFile(file, article);
        }
    }

    private void uploadLargeFile(MultipartFile multipartFile, Article article) {
        File tempFile = null;

        try {
            FileMetadata metadata = fileUtils.createFileMetadata(multipartFile);
            tempFile = fileUtils.convertToTempFile(multipartFile);

            String fileUrl = s3FileUploader.uploadFile(tempFile, metadata.getSavedName());
            saveFileMetadataToFileEntity(metadata, fileUrl, article);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FILE_SIZE_EXCEEDED, e.getMessage());
        } finally {
            fileUtils.deleteTempFile(tempFile);
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

}
