package re.kr.icuh.icuhplatform.dto.file;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CompleteUploadRequestDto {
    private String uploadId;
    private String fileName;
    private List<PartETagDto> parts;
    private Long articleId;
    private Long fileSize;
    private String originFileName;
}
