package re.kr.icuh.icuhplatform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import re.kr.icuh.icuhplatform.domain.Article;
import re.kr.icuh.icuhplatform.domain.FileEntity;
import re.kr.icuh.icuhplatform.domain.FileStatus;
import re.kr.icuh.icuhplatform.dto.file.CompleteUploadRequestDto;
import re.kr.icuh.icuhplatform.dto.file.CompleteUploadResponseDto;
import re.kr.icuh.icuhplatform.global.exception.BusinessException;
import re.kr.icuh.icuhplatform.global.exception.ErrorCode;
import re.kr.icuh.icuhplatform.global.util.FileUtils;
import re.kr.icuh.icuhplatform.repository.ArticleRepository;
import re.kr.icuh.icuhplatform.repository.FileRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final FileUtils fileUtils;
    private final FileRepository fileRepository;
    private final ArticleRepository articleRepository;

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
