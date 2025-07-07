package re.kr.icuh.icuhplatform.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import re.kr.icuh.icuhplatform.domain.Article;
import re.kr.icuh.icuhplatform.domain.DocumentType;
import re.kr.icuh.icuhplatform.domain.QArticle;
import re.kr.icuh.icuhplatform.domain.SubjectDomain;
import re.kr.icuh.icuhplatform.dto.article.ArticleListResponse;
import re.kr.icuh.icuhplatform.dto.article.ArticleResponse;
import re.kr.icuh.icuhplatform.dto.article.CreateArticleRequest;
import re.kr.icuh.icuhplatform.global.exception.BusinessException;
import re.kr.icuh.icuhplatform.global.exception.ErrorCode;
import re.kr.icuh.icuhplatform.repository.ArticleRepository;
import re.kr.icuh.icuhplatform.repository.DocumentTypeRepository;
import re.kr.icuh.icuhplatform.repository.SubjectDomainRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final FileStorageService fileStorageService;
    private final ArticleRepository articleRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final SubjectDomainRepository subjectDomainRepository;
    private final JPAQueryFactory queryFactory;

    public void createArticle(CreateArticleRequest request, List<MultipartFile> files) {
        validateFiles(files);
        DocumentType documentType = validateDocumentType(request.documentTypeId());
        SubjectDomain subjectDomain = validateSubjectDomain(request.subjectDomainId());


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

    public List<ArticleListResponse> findArticles(String documentType, String  subjectDomain, String source) {
        QArticle article = QArticle.article;
        BooleanBuilder builder = new BooleanBuilder();

        if (documentType != null) {
            builder.and(article.documentType.name.eq(documentType));
        }

        if (subjectDomain != null) {
            builder.and(article.subjectDomain.name.eq(subjectDomain));
        }

        if (source != null) {
            builder.and(article.source.eq(source));
        }

        List<Article> articles = queryFactory
                .selectFrom(article)
                .leftJoin(article.documentType).fetchJoin()
                .leftJoin(article.subjectDomain).fetchJoin()
                .where(builder)
                .fetch();

        return articles.stream()
                .map(ArticleListResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public ArticleResponse findArticleById(Long id) {
        if (!articleRepository.findById(id).isPresent()) {
            throw new BusinessException(ErrorCode.ARTICLE_NOT_FOUND);
        }

        return ArticleResponse.fromEntity(articleRepository.findById(id).get());
    }


    private void validateFiles(List<MultipartFile> files) {
        files.forEach(file -> {
            if (file == null || file.isEmpty()) {
                throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
            }

            String fileName = file.getOriginalFilename();
            if (fileName != null && (fileName.endsWith(".exe") || fileName.endsWith(".dmg"))) {
                throw new BusinessException(ErrorCode.UNSUPPORTED_FILE_TYPE);
            }
        });
    }

    private DocumentType validateDocumentType(Long documentTypeId) {
        return documentTypeRepository.findById(documentTypeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DOCUMENT_TYPE_NOT_FOUND));
    }

    private SubjectDomain validateSubjectDomain(Long subjectDomainId) {
        return subjectDomainRepository.findById(subjectDomainId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SUBJECT_DOMAIN_NOT_FOUND));
    }
}
