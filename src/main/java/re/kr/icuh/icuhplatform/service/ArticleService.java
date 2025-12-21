package re.kr.icuh.icuhplatform.service;

import com.querydsl.jpa.impl.JPAQuery;
import jakarta.validation.Valid;
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
    public Page<ArticleListResponse> findArticles(String documentType, String  subjectDomain, String source, String query, Pageable pageable) {
        List<Article> articles = articleQueryRepository.findApprovedArticles(documentType, subjectDomain, source, query, pageable);
        JPAQuery<Long> countQuery = articleQueryRepository.countApprovedArticles();

        List<ArticleListResponse> articleListResponses = articles.stream()
                .map(ArticleListResponse::fromEntity)
                .collect(Collectors.toList());

        return PageableExecutionUtils.getPage(articleListResponses, pageable, countQuery::fetchOne);
    }

    @Transactional
    public Long createArticle(CreateArticleRequest request) {
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

        return savedArticleId.getId();
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

    @Transactional
    public ArticleResponse requestArticleUpdate(Long id, RequestStatusChange request) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ARTICLE_NOT_FOUND));

        if (!article.validatePassword(request.password())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        return ArticleResponse.fromEntity(article);
    }

    @Transactional
    public Long updateArticle(Long id, @Valid UpdateArticleRequest request) {
        DocumentType documentType = validateDocumentType(request.documentTypeId());
        SubjectDomain subjectDomain = validateSubjectDomain(request.subjectDomainId());

        // 기존에 작성되어 있던 내용은 그대로 가져오고, 새로 작성되는 내용만 덮어쓴다. 패스워드는 그전에 사용했던 값을 그대로 사용
        Article savedArticle = articleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ARTICLE_NOT_FOUND));

        // UpdateArticleRequest 객체를 json 형식으로 컬럼에 집어넣어야함
        savedArticle.setPendingUpdate(request);

        return savedArticle.getId();
    }

    @Transactional
    public void requestArticleDelete(Long id, RequestStatusChange request) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ARTICLE_NOT_FOUND));

        if (!article.validatePassword(request.password())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        article.softDelete();
        article.getFiles().stream()
                .forEach(file -> file.softDelete());
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
