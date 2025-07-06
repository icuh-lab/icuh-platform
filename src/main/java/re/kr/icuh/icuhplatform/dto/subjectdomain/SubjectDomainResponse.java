package re.kr.icuh.icuhplatform.dto.subjectdomain;

import re.kr.icuh.icuhplatform.domain.SubjectDomain;

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
