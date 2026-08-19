package re.kr.icuh.icuhplatform.file.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InitiateUploadRequestDto {
    @NotNull
    private String fileName;
    private String fileType;
    private Long fileSize;
}
