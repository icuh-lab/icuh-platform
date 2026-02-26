package re.kr.icuh.icuhplatform.category.dto.response;

import re.kr.icuh.icuhplatform.category.domain.DocumentType;

public record DocumentTypeResponse(
        Long id,
        String name
) {
    public static DocumentTypeResponse fromEntity(DocumentType documentType) {
        return new DocumentTypeResponse(
                documentType.getId(),
                documentType.getName()
        );
    }
}
