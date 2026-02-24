package re.kr.icuh.icuhplatform.dto;

import re.kr.icuh.icuhplatform.domain.DocumentType;

public record DocumentTypesResponse(
    String code,
    String name,
    String enName
) {
    public static DocumentTypesResponse from(DocumentType documentType) {
        return new DocumentTypesResponse(
                documentType.getCode(),
                documentType.getName(),
                documentType.getEnName()
        );
    }
}
