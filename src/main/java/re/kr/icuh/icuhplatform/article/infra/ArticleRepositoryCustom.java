package re.kr.icuh.icuhplatform.article.infra;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import re.kr.icuh.icuhplatform.article.domain.Article;
import re.kr.icuh.icuhplatform.article.dto.request.ArticleRequest;

public interface ArticleRepositoryCustom {

    Page<Article> findApprovedArticles(ArticleRequest request, Pageable pageable);
}
