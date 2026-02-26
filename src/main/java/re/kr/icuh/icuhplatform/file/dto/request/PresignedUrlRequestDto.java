package re.kr.icuh.icuhplatform.file.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PresignedUrlRequestDto {
    private String uploadId;
    private String fileName;
    private int partNumber;
}
