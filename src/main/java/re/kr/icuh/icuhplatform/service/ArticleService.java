package re.kr.icuh.icuhplatform.service;

import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import re.kr.icuh.icuhplatform.domain.Article;
import re.kr.icuh.icuhplatform.domain.ArticleStatus;
import re.kr.icuh.icuhplatform.domain.DocumentType;
import re.kr.icuh.icuhplatform.domain.SubjectDomain;
import re.kr.icuh.icuhplatform.dto.article.*;
import re.kr.icuh.icuhplatform.global.exception.BusinessException;
import re.kr.icuh.icuhplatform.global.exception.ErrorCode;
import re.kr.icuh.icuhplatform.repository.ArticleQueryRepository;
import re.kr.icuh.icuhplatform.repository.ArticleRepository;
import re.kr.icuh.icuhplatform.repository.DocumentTypeRepository;
import re.kr.icuh.icuhplatform.repository.SubjectDomainRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final SubjectDomainRepository subjectDomainRepository;
    private final ArticleQueryRepository articleQueryRepository;

    @Transactional(readOnly = true)
    public Page<ArticleListResponse> findArticles(ArticleRequest request, Pageable pageable) {
        List<Article> articles = articleQueryRepository.findApprovedArticles(request, pageable);
        JPAQuery<Long> countQuery = articleQueryRepository.countApprovedArticles();

        List<ArticleListResponse> articleListResponses = articles.stream()
                .map(ArticleListResponse::fromEntity)
                .collect(Collectors.toList());

        return PageableExecutionUtils.getPage(articleListResponses, pageable, countQuery::fetchOne);
    }

    @Transactional
    public CreateArticleResponse createArticle(CreateArticleRequest request) {
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
                .isDeleted(false)
                .deletedAt(null)
                .build();

        Article savedArticleId = articleRepository.save(article);

        return CreateArticleResponse.of(savedArticleId.getId());
    }

    @Transactional
    public ArticleDetailResponse findArticleById(Long id) {
        Article savedArticle = findSavedArticle(id);
        savedArticle.increaseViews();

        return ArticleDetailResponse.of(savedArticle);
    }

    @Transactional
    public ArticleDetailResponse modifyArticleStatus(Long id, ModifyArticleStatusRequest request) {
        Article article = findSavedArticle(id);
        validatePassword(article, request.password());

        return ArticleDetailResponse.of(article);
    }

    @Transactional
    public void updateArticle(Long id, UpdateArticleRequest request) {
        Article savedArticle = findSavedArticle(id);
        savedArticle.updateContent(request);
    }

    @Transactional
    public void deleteArticle(Long articleId, DeleteArticleRequest request) {
        Article savedArticle = findSavedArticle(articleId);
        validatePassword(savedArticle, request.password());

        savedArticle.delete();
    }

    private static void validatePassword(Article article, String password) {
        if (!article.validatePassword(password)) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }
    }

    private Article findSavedArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ARTICLE_NOT_FOUND));
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
