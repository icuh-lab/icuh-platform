package re.kr.icuh.icuhplatform.dto.file;

import lombok.Data;

@Data
public class InitiateUploadRequestDto {
    private String fileName;
    private String fileType;
}
