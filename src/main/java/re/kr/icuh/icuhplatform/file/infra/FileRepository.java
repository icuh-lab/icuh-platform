package re.kr.icuh.icuhplatform.file.infra;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import re.kr.icuh.icuhplatform.file.domain.FileEntity;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
}
