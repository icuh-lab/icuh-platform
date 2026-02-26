package re.kr.icuh.icuhplatform.category.dto.response;

import re.kr.icuh.icuhplatform.category.domain.SubjectDomain;

public record SubjectDomainResponse(
        Long id,
        String name
) {
    public static SubjectDomainResponse fromEntity(SubjectDomain subjectDomain) {
        return new SubjectDomainResponse(
                subjectDomain.getId(),
                subjectDomain.getName()
        );
    }
}
