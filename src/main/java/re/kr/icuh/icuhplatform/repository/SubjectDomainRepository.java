package re.kr.icuh.icuhplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import re.kr.icuh.icuhplatform.domain.SubjectDomain;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectDomainRepository extends JpaRepository<SubjectDomain, Long> {
    Optional<SubjectDomain> findByCode(String code);

    List<SubjectDomain> findByStatus(String status);
}
