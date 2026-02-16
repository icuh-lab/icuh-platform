package re.kr.icuh.icuhplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import re.kr.icuh.icuhplatform.domain.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long>, ArticleRepositoryCustom {
}
