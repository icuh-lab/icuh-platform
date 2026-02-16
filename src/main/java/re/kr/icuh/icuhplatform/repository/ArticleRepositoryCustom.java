package re.kr.icuh.icuhplatform.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import re.kr.icuh.icuhplatform.domain.Article;
import re.kr.icuh.icuhplatform.dto.article.ArticleRequest;

public interface ArticleRepositoryCustom {

    Page<Article> findApprovedArticles(ArticleRequest request, Pageable pageable);
}
