package re.kr.icuh.icuhplatform.util;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import re.kr.icuh.icuhplatform.config.S3Config;
import re.kr.icuh.icuhplatform.dto.CreateAttachmentDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AttachmentStore {

    private final S3Config s3Config;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    public List<CreateAttachmentDto> storeFiles(List<MultipartFile> files) throws IOException {
        List<CreateAttachmentDto> storeFileResult = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                storeFileResult.add(storeFile(file));
            }
        }
        return storeFileResult;
    }

    public CreateAttachmentDto storeFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        String originalName = file.getOriginalFilename();
        String savedName = createStoreFileName(originalName);
        String extensionName = extractExtensionName(originalName);
        Integer size = getSize(file.getSize());

        // S3에 파일 업로드
        String savedPath = putS3(savedName, file.getInputStream(), file.getSize(), file.getContentType());

        return CreateAttachmentDto.builder()
                .originalName(originalName)
                .savedPath(savedPath)
                .savedName(savedName)
                .extensionName(extensionName)
                .size(size)
                .build();
    }

    private String putS3(String storeFileName, InputStream inputStream, long contentLength, String contentType) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);
        metadata.setContentType(contentType);

        PutObjectRequest request = new PutObjectRequest(bucket, storeFileName, inputStream, metadata).withCannedAcl(CannedAccessControlList.PublicRead);
        s3Config.amazonS3Client().putObject(request);

        return s3Config.amazonS3Client().getUrl(bucket, storeFileName).toString();
    }

    private String createStoreFileName(String originName) {
        String extensionName = extractExtensionName(originName);
        String uuid = UUID.randomUUID().toString();

        return uuid + "." + extensionName;
    }

    private String extractExtensionName(String originName) {
        int position = originName.lastIndexOf(".");

        return originName.substring(position + 1);
    }

    private Integer getSize(long size) {
        return Long.valueOf(size).intValue();
    }
}
