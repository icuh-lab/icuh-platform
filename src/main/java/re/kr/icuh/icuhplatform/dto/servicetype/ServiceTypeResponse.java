package re.kr.icuh.icuhplatform.dto.servicetype;

import re.kr.icuh.icuhplatform.domain.ServiceType;

public record ServiceTypeResponse(
        Long id,
        String name
) {
    public static ServiceTypeResponse fromEntity(ServiceType serviceType) {
        return new ServiceTypeResponse(
                serviceType.getId(),
                serviceType.getName()
        );
    }
}
