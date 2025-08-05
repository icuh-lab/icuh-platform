package re.kr.icuh.icuhplatform.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import re.kr.icuh.icuhplatform.domain.Article;
import re.kr.icuh.icuhplatform.domain.ArticleStatus;
import re.kr.icuh.icuhplatform.domain.QArticle;

import java.util.List;

@Repository
public class ArticleQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ArticleQueryRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public List<Article> findApprovedArticles(String documentType, String  subjectDomain, String source, Pageable pageable) {
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

        return queryFactory
                .selectFrom(article)
                .leftJoin(article.documentType).fetchJoin()
                .leftJoin(article.subjectDomain).fetchJoin()
                .where(builder)
                .orderBy(article.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    public JPAQuery<Long> countApprovedArticles() {
        QArticle article = QArticle.article;

        return queryFactory
                .select(article.count())
                .from(article)
                .where(article.status.eq(ArticleStatus.APPROVED));
    }

}
