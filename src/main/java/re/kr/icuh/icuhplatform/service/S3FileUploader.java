package re.kr.icuh.icuhplatform.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import re.kr.icuh.icuhplatform.common.util.FileUtils;
import re.kr.icuh.icuhplatform.domain.FileMetadata;
import re.kr.icuh.icuhplatform.dto.CreateAttachmentDto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3FileUploader {

    private final FileUtils fileUtils;
    private final AmazonS3Client amazonS3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    public List<CreateAttachmentDto> storeAttachments(List<MultipartFile> files) throws IOException {
        List<CreateAttachmentDto> storeFileResult = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                storeFileResult.add(storeAttachment(file));
            }
        }
        return storeFileResult;
    }

    public CreateAttachmentDto storeAttachment(MultipartFile file) throws IOException {
        FileMetadata metadata = fileUtils.createFileMetadata(file);

        // S3에 파일 업로드
        String savedPath = null;
        try {
            savedPath = putS3(metadata.getSavedName(), file.getInputStream(), file.getSize(), file.getContentType());
        } catch (Exception e) {
            rollbackS3(metadata.getSavedName());
            throw new IOException("[AttachmentStore][storeFile] 파일 업로드 중 오류 발생", e);
        }

        return CreateAttachmentDto.builder()
                .originalName(metadata.getOriginalName())
                .savedPath(savedPath)
                .savedName(metadata.getSavedName())
                .extensionName(metadata.getExtensionName())
                .size(metadata.getSize())
                .build();
    }

    public String uploadLargeAttachment(File file) {

        String fileName = createFileName(file.getName());

        // 1단계: Multipart Upload 초기화
        InitiateMultipartUploadRequest initiateRequest = new InitiateMultipartUploadRequest(bucket, fileName)
                .withCannedACL(CannedAccessControlList.PublicRead);

        InitiateMultipartUploadResult initResult = amazonS3Client.initiateMultipartUpload(initiateRequest);
        String uploadId = initResult.getUploadId();

        log.info("Multipart Upload 시작 - uploadId: {}", uploadId);

        try {
            // 2단계: 파일을 청크로 분할하여 업로드
            List<PartETag> partETags = new ArrayList<>();
            long contentLength = file.length();
            long partSize = 5 * 1024 * 1024; // 5MB

            long filePosition = 0;
            int partNumber = 1;

            while (filePosition < contentLength) {
                long partLength = Math.min(partSize, contentLength - filePosition);

                try (InputStream inputStream = new FileInputStream(file)) {
                    inputStream.skip(filePosition);

                    UploadPartRequest uploadRequest = new UploadPartRequest()
                            .withBucketName(bucket)
                            .withKey(fileName)
                            .withUploadId(uploadId)
                            .withPartNumber(partNumber)
                            .withInputStream(inputStream)
                            .withPartSize(partLength);

                    UploadPartResult uploadResult = amazonS3Client.uploadPart(uploadRequest);
                    partETags.add(uploadResult.getPartETag());

                    log.info("Part {} 업로드 완료 - size: {}", partNumber, partLength);
                }

                filePosition += partLength;
                partNumber++;
            }

            // 3단계: Multipart Upload 완료
            CompleteMultipartUploadRequest completeRequest = new CompleteMultipartUploadRequest(
                    bucket,
                    fileName,
                    uploadId,
                    partETags
            );

            amazonS3Client.completeMultipartUpload(completeRequest);

            String fileUrl = amazonS3Client.getUrl(bucket, fileName).toString();
            log.info("Multipart Upload 완료 - URL: {}", fileUrl);

            return fileUrl;

        } catch (Exception e) {
            // 4단계: 오류 발생 시 업로드 중단
            log.error("Multipart Upload 실패", e);
            amazonS3Client.abortMultipartUpload(new AbortMultipartUploadRequest(
                    bucket,
                    fileName,
                    uploadId
            ));
            throw new RuntimeException("파일 업로드 실패", e);
        }

    }

    private String putS3(String storeFileName, InputStream inputStream, long contentLength, String contentType) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);
        metadata.setContentType(contentType);

        PutObjectRequest request = new PutObjectRequest(bucket, storeFileName, inputStream, metadata).withCannedAcl(CannedAccessControlList.PublicRead);
        amazonS3Client.putObject(request);

        return amazonS3Client.getUrl(bucket, storeFileName).toString();
    }

    private void rollbackS3(String savedName) {
        try {
            amazonS3Client.deleteObject(bucket, savedName);
        } catch (Exception e) {
            log.error("[AttachmentStore][rollbackS3] S3 롤백 실패: {}", savedName, e);
        }
    }

    // 파일 이름 생성 메소드
    private String createFileName(String originalFileName) {
        return UUID.randomUUID().toString() + "_" + originalFileName;
    }

}
