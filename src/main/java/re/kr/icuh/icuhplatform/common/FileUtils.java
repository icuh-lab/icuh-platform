package re.kr.icuh.icuhplatform.common;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class FileUtils {

    /**
     * 파일 저장을 위한 이름을 생성합니다.
     * UUID를 사용하여 고유한 파일명을 생성합니다.
     */
    public String createStoreFileName(String originName) {
        String extensionName = extractExtensionName(originName);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + extensionName;
    }

    /**
     * 파일명에서 확장자를 추출합니다.
     */
    public String extractExtensionName(String originName) {
        int position = originName.lastIndexOf(".");
        return position > -1 ? originName.substring(position + 1) : "";
    }

    /**
     * 파일 크기를 Integer로 변환합니다.
     */
    public Integer convertToIntegerSize(long size) {
        if (size > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("File size is too large to convert to Integer");
        }
        return Long.valueOf(size).intValue();
    }

    /**
     * MultipartFile을 임시 File로 변환합니다.
     */
    public File convertToTempFile(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("temp_", file.getOriginalFilename());
        file.transferTo(tempFile);
        return tempFile;
    }

    /**
     * 임시 파일을 삭제합니다.
     */
    public boolean deleteTempFile(File file) {
        return file.delete();
    }

    /**
     * 파일 메타데이터를 생성합니다.
     */
    public FileMetadata createFileMetadata(MultipartFile file) {
        return FileMetadata.builder()
                .originalName(file.getOriginalFilename())
                .savedName(createStoreFileName(file.getOriginalFilename()))
                .extensionName(extractExtensionName(file.getOriginalFilename()))
                .size(convertToIntegerSize(file.getSize()))
                .contentType(file.getContentType())
                .build();
    }

}
