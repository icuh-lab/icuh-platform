package re.kr.icuh.icuhplatform.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import re.kr.icuh.icuhplatform.domain.Article;
import re.kr.icuh.icuhplatform.domain.ArticleStatus;
import re.kr.icuh.icuhplatform.domain.QArticle;
import re.kr.icuh.icuhplatform.dto.article.ArticleRequest;

import java.util.List;

@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Article> findApprovedArticles(ArticleRequest request, Pageable pageable) {
        QArticle article = QArticle.article;
        BooleanBuilder builder = new BooleanBuilder();

        if (request.documentType() != null) {
            builder.and(article.documentType.enName.eq(request.documentType()));
        }

        if (request.subjectDomain() != null) {
            builder.and(article.subjectDomain.enName.eq(request.subjectDomain()));
        }

        if (request.source() != null) {
            builder.and(article.source.eq(request.source()));
        }

        if (request.query() != null) {
            builder.and(article.title.containsIgnoreCase(request.query()));
        }

        // 승인 상태 조건을 묶어서 처리
        builder.andAnyOf(
                article.status.eq(ArticleStatus.APPROVED),
                article.status.eq(ArticleStatus.UPDATED_APPROVED)
        );

        return jpaQueryFactory
                .selectFrom(article)
                .leftJoin(article.documentType).fetchJoin()
                .leftJoin(article.subjectDomain).fetchJoin()
                .where(builder)
                .orderBy(article.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public JPAQuery<Long> countApprovedArticles() {
        QArticle article = QArticle.article;
        BooleanBuilder builder = new BooleanBuilder();

        builder.or(article.status.eq(ArticleStatus.APPROVED));
        builder.or(article.status.eq(ArticleStatus.UPDATED_APPROVED));

        return jpaQueryFactory
                .select(article.count())
                .from(article)
                .where(builder);
    }
}
