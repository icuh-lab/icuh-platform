package re.kr.icuh.icuhplatform.util;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import re.kr.icuh.icuhplatform.config.S3Config;
import re.kr.icuh.icuhplatform.dto.CreateAttachmentDto;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AttachmentStore {

    private final S3Config s3Config;

    @Value("${file.dir}")
    private String fileDir;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    public String getFullPath(String filename) {
        return fileDir + filename;
    }

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
        String savedPath = getFullPath(file.getOriginalFilename());
        String extensionName = extractExtensionName(originalName);
        Integer size = getSize(file.getSize());

        // 로컬에 파일 저장
        File localFile = new File(getFullPath(savedName));

        // S3에 파일 업로드
        String s3UploadFileName = putS3(savedName, localFile);

        return CreateAttachmentDto.builder()
                .originalName(originalName)
                .savedPath(s3UploadFileName)
                .savedName(savedName)
                .extensionName(extensionName)
                .size(size)
                .build();
    }

    private String putS3(String storeFileName, File localFile) {
        s3Config.amazonS3Client().putObject(new PutObjectRequest(bucket, storeFileName, localFile).withCannedAcl(CannedAccessControlList.PublicRead));
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
