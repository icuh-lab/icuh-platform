package re.kr.icuh.icuhplatform.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import re.kr.icuh.icuhplatform.domain.*;
import re.kr.icuh.icuhplatform.dto.article.ArticleListResponse;
import re.kr.icuh.icuhplatform.dto.article.ArticleResponse;
import re.kr.icuh.icuhplatform.dto.article.CreateArticleRequest;
import re.kr.icuh.icuhplatform.global.exception.BusinessException;
import re.kr.icuh.icuhplatform.global.exception.ErrorCode;
import re.kr.icuh.icuhplatform.repository.ArticleRepository;
import re.kr.icuh.icuhplatform.repository.DocumentTypeRepository;
import re.kr.icuh.icuhplatform.repository.SubjectDomainRepository;

import java.util.ArrayList;
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

    private final FileStorageService fileStorageService;
    private final ArticleRepository articleRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final SubjectDomainRepository subjectDomainRepository;
    private final JPAQueryFactory queryFactory;
    private final RestClient.Builder builder;

    public void createArticle(CreateArticleRequest request, List<MultipartFile> files) {
        validateFiles(files);

        DocumentType documentType = documentTypeRepository.findById(request.documentTypeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASSIFICATION_NOT_FOUND));

        SubjectDomain subjectDomain = subjectDomainRepository.findById(request.subjectDomainId())
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
                .documentType(documentType)
                .subjectDomain(subjectDomain)
                .source(request.source())
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

    public List<ArticleListResponse> findArticles(String documentType, String  subjectDomain, String source) {

        QArticle qArticle = QArticle.article;
        BooleanBuilder builder = new BooleanBuilder();

        QDocumentType qDocumentType = qArticle.documentType;
        QSubjectDomain qSubjectDomain = qArticle.subjectDomain;

        if (documentType != null) {
            builder.and(qArticle.documentType.eq(qDocumentType));
        }

        if (subjectDomain != null) {
            builder.and(qArticle.subjectDomain.eq(qSubjectDomain));
        }

        if (source != null) {
            builder.and(qArticle.source.eq(source));
        }

        List<Article> articles = queryFactory
                .selectFrom(qArticle)
                .where(builder)
                .fetch();

        List<ArticleListResponse> articleResponses = new ArrayList<>();

        for (Article article : articles) {
            articleResponses.add(ArticleListResponse.fromEntity(article));
        }

        return articleResponses;
    }

    public ArticleResponse findArticleById(Long id) {

        QArticle qArticle = QArticle.article;
        BooleanBuilder builder = new BooleanBuilder();

        if (id != null) {
            builder.and(qArticle.id.eq(id));
        }

        Article article = queryFactory
                .selectFrom(qArticle)
                .where(builder)
                .fetchOne();


        return ArticleResponse.fromEntity(article);
    }
}
