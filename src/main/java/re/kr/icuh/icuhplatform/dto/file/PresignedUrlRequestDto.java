package re.kr.icuh.icuhplatform.dto.file;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PresignedUrlRequestDto {
    private String uploadId;
    private String fileName;
    private int partNumber;
}
