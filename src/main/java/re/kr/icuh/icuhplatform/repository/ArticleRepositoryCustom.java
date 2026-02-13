package re.kr.icuh.icuhplatform.repository;

import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Pageable;
import re.kr.icuh.icuhplatform.domain.Article;
import re.kr.icuh.icuhplatform.dto.article.ArticleRequest;

import java.util.List;

public interface ArticleRepositoryCustom {

    List<Article> findApprovedArticles(ArticleRequest request, Pageable pageable);

    JPAQuery<Long> countApprovedArticles();
}
