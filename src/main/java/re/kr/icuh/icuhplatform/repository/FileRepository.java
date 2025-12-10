package re.kr.icuh.icuhplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import re.kr.icuh.icuhplatform.domain.Article;
import re.kr.icuh.icuhplatform.domain.FileEntity;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    FileEntity findByArticle(Article article);
}
