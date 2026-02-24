package re.kr.icuh.icuhplatform.dto;

import re.kr.icuh.icuhplatform.domain.SubjectDomain;

public record SubjectDomainsResponse(
    String code,
    String name,
    String enName
) {
    public static SubjectDomainsResponse from(SubjectDomain subjectDomain) {
        return new SubjectDomainsResponse(
                subjectDomain.getCode(),
                subjectDomain.getName(),
                subjectDomain.getEnName()
        );
    }
}
