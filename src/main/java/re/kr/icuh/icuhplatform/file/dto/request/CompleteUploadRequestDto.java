package re.kr.icuh.icuhplatform.file.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CompleteUploadRequestDto {
    private String uploadId;
    @NotNull
    private String fileName;
    private List<PartETagDto> parts;
    private Long articleId;
    private Long fileSize;
    @NotNull
    private String originFileName;
    private String fileStatus;
}
