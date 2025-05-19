package re.kr.icuh.icuhplatform.dto.extension;

import re.kr.icuh.icuhplatform.domain.Extension;

public record ExtensionResponse(
        Long id,
        String name
) {
    public static ExtensionResponse fromEntity(Extension extension) {
        return new ExtensionResponse(
                extension.getId(),
                extension.getName()
        );
    }
}
