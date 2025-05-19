package re.kr.icuh.icuhplatform.dto.classification;

import re.kr.icuh.icuhplatform.domain.Classification;

public record ClassificationResponse(
        Long id,
        String name
) {
    public static ClassificationResponse fromEntity(Classification classification) {
        return new ClassificationResponse(
                classification.getId(),
                classification.getName()
        );
    }
}
