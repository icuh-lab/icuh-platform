package re.kr.icuh.icuhplatform.dto.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PartETagDto {
    private int partNumber;

    @JsonProperty("eTag")
    private String etag;
}
