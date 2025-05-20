package re.kr.icuh.icuhplatform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import re.kr.icuh.icuhplatform.domain.Article;
import re.kr.icuh.icuhplatform.domain.Classification;
import re.kr.icuh.icuhplatform.domain.ServiceType;
import re.kr.icuh.icuhplatform.dto.article.CreateArticleRequest;
import re.kr.icuh.icuhplatform.global.exception.BusinessException;
import re.kr.icuh.icuhplatform.global.exception.ErrorCode;
import re.kr.icuh.icuhplatform.repository.ArticleRepository;
import re.kr.icuh.icuhplatform.repository.ClassificationRepository;
import re.kr.icuh.icuhplatform.repository.ServiceTypeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {

    /**
     * 1. CreateArticleRequest가 넘어온다.
     * 2. CreateArticleReqeust 안에 Article과 List<Multipart> files에 대한 유효성 검사를 진행한다.
     * 3. files 유효성이 끝나면 s3를 통해 업로드 된다.
     * 3-1. S3 업로드 실패가 된다면 전부 rollback
     * 4. 3번 스텝이 끝나면 files -> 변환 -> fileEntity DB에 저장, CreateArticle -> 변환 -> article DB에 저장
     */

    private final ArticleRepository articleRepository;
    private final FileStorageService fileStorageService;
    private final ClassificationRepository classificationRepository;
    private final ServiceTypeRepository serviceTypeRepository;

    public void createArticle(CreateArticleRequest request, List<MultipartFile> files) {
        validateFiles(files);

        Classification classification = classificationRepository.findById(request.classificationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASSIFICATION_NOT_FOUND));

        ServiceType serviceType = serviceTypeRepository.findById(request.serviceTypeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SERVICE_TYPE_NOT_FOUND));


        Article article = Article.builder()
                .title(request.title())
                .description(request.description())
                .author(request.author())
                .authorOrganization(request.authorOrganization())
                .department(request.department())
                .tempPassword(request.tempPassword())
                .views(0)
                .status(Article.ArticleStatus.ACTIVE)
                .classification(classification)
                .serviceType(serviceType)
                .build();

        Article savedArticle = articleRepository.save(article);

        fileStorageService.uploadLargeFiles(files, savedArticle);
    }


    private void validateFiles(List<MultipartFile> files) {
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
            }

            if (file.getOriginalFilename().endsWith(".exe") || file.getOriginalFilename().endsWith(".dmg")) {
                throw new BusinessException(ErrorCode.UNSUPPORTED_FILE_TYPE);
            }
        }
    }
}
