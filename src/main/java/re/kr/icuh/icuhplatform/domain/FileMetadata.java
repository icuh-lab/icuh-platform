package re.kr.icuh.icuhplatform.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileMetadata {

    private String originalName;    // 원본 파일명
    private String savedName;       // 저장된 파일명 (UUID)
    private String extensionName;   // 파일 확장자
    private Integer size;           // 파일 크기
    private String contentType;     // 파일 타입 (MIME type)

    @Builder
    public FileMetadata(String originalName, String savedName,
                        String extensionName, Integer size, String contentType) {
        this.originalName = originalName;
        this.savedName = savedName;
        this.extensionName = extensionName;
        this.size = size;
        this.contentType = contentType;
    }

    /**
     * 파일의 전체 경로명을 생성합니다.
     */
    public String getFullPath() {
        return String.format("%s.%s", savedName, extensionName);
    }

    /**
     * 파일 크기를 MB 단위로 변환합니다.
     */
    public double getSizeInMB() {
        return size / (1024.0 * 1024.0);
    }

    /**
     * 파일이 이미지인지 확인합니다.
     */
    public boolean isImage() {
        return contentType != null && contentType.startsWith("image/");
    }

    /**
     * 파일이 PDF인지 확인합니다.
     */
    public boolean isPDF() {
        return "pdf".equalsIgnoreCase(extensionName) ||
                "application/pdf".equals(contentType);
    }

}
