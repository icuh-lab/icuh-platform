package re.kr.icuh.icuhplatform.dto.documenttype;

import re.kr.icuh.icuhplatform.domain.DocumentType;

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
