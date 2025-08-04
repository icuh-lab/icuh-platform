package re.kr.icuh.icuhplatform.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
                .status(ArticleStatus.PENDING)
                .documentType(documentType)
                .subjectDomain(subjectDomain)
                .source(request.source())
                .build();

        Article savedArticle = articleRepository.save(article);

        fileStorageService.uploadLargeFiles(files, savedArticle);
    }

    public Page<ArticleListResponse> findArticles(String documentType, String  subjectDomain, String source, Pageable pageable) {
        QArticle article = QArticle.article;
        BooleanBuilder builder = new BooleanBuilder();

        if (documentType != null) {
            builder.and(article.documentType.enName.eq(documentType));
        }

        if (subjectDomain != null) {
            builder.and(article.subjectDomain.enName.eq(subjectDomain));
        }

        if (source != null) {
            builder.and(article.source.eq(source));
        }

        builder.and(article.status.eq(ArticleStatus.APPROVED));

        List<Article> articles = queryFactory
                .selectFrom(article)
                .leftJoin(article.documentType).fetchJoin()
                .leftJoin(article.subjectDomain).fetchJoin()
                .where(builder)
                .orderBy(article.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(article.count())
                .from(article);

        List<ArticleListResponse> articleListResponses = articles.stream()
                .map(ArticleListResponse::fromEntity)
                .collect(Collectors.toList());

        return PageableExecutionUtils.getPage(articleListResponses, pageable, countQuery::fetchOne);
    }

    @Transactional
    public ArticleResponse findArticleById(Long id) {
        if (!articleRepository.findById(id).isPresent()) {
            throw new BusinessException(ErrorCode.ARTICLE_NOT_FOUND);
        }

        Article article = articleRepository.findById(id).get();
        article.increaseViews();

        return ArticleResponse.fromEntity(article);
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
