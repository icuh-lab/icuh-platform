package re.kr.icuh.icuhplatform.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import re.kr.icuh.icuhplatform.dto.CreateAttachmentDto;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttachmentStoreTest {

    @Mock
    private AmazonS3Client amazonS3Client;

    @InjectMocks
    private AttachmentStore attachmentStore;

    @BeforeEach
    void setUp() {
        attachmentStore = new AttachmentStore(amazonS3Client);
        ReflectionTestUtils.setField(attachmentStore, "bucket", "test-bucket");
    }

    @Test
    void 단일_파일_업로드_성공시_CreateAttachmentDto를_반환한다() throws Exception {
        // Given
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                "test-image.png",
                "image/png",
                "FakeImageData".getBytes()
        );

        // Mock S3 응답
        when(amazonS3Client.putObject(any(PutObjectRequest.class)))
                .thenReturn(null); // putObject는 void를 반환하므로 null을 반환

        when(amazonS3Client.getUrl(anyString(), anyString()))
                .thenReturn(new URL("http://test-bucket.s3.amazonaws.com/test-file.png"));


        // When
        CreateAttachmentDto result = attachmentStore.storeAttachment(mockMultipartFile);

        // Then
        assertNotNull(result);
        assertEquals("test-image.png", result.getOriginalName());
        assertTrue(result.getSavedPath().contains("http"));
        assertEquals("png", result.getExtensionName());
    }

    @Test
    void 복수_파일_업로드_성공시_CreateAttachmentDto_리스트를_반환한다() throws Exception {
        // Given
        MockMultipartFile mockMultipartFile1 = new MockMultipartFile(
                "file1",
                "test-image1.png",
                "image/png",
                "FakeImageData1".getBytes()
        );

        MockMultipartFile mockMultipartFile2 = new MockMultipartFile(
                "file2",
                "test-document.pdf",
                "application/pdf",
                "FakeDocumentData".getBytes()
        );

        List<MultipartFile> multipartFiles = Arrays.asList(mockMultipartFile1, mockMultipartFile2);

        // Mock S3 응답
        when(amazonS3Client.putObject(any(PutObjectRequest.class)))
                .thenReturn(null);

        when(amazonS3Client.getUrl(anyString(), anyString()))
                .thenReturn(
                        new URL("http://test-bucket.s3.amazonaws.com/test-file1.png"),
                        new URL("http://test-bucket.s3.amazonaws.com/test-file2.pdf")
                );

        // When
        List<CreateAttachmentDto> results = attachmentStore.storeAttachments(multipartFiles);

        // Then
        assertNotNull(results);
        assertEquals(2, results.size());

        // 첫 번째 파일 검증
        CreateAttachmentDto firstResult = results.get(0);
        assertEquals("test-image1.png", firstResult.getOriginalName());
        assertTrue(firstResult.getSavedPath().contains("http"));
        assertEquals("png", firstResult.getExtensionName());

        // 두 번째 파일 검증
        CreateAttachmentDto secondResult = results.get(1);
        assertEquals("test-document.pdf", secondResult.getOriginalName());
        assertTrue(secondResult.getSavedPath().contains("http"));
        assertEquals("pdf", secondResult.getExtensionName());
    }

    @Test
    void 파일_업로드_실패시_IOException이_발생한다() throws Exception {
        // Given
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                "test-image.png",
                "image/png",
                "FakeImageData".getBytes()
        );

        // S3 업로드 실패 시나리오 모킹
        when(amazonS3Client.putObject(any(PutObjectRequest.class)))
                .thenThrow(new RuntimeException("S3 upload failed"));

        // Then
        assertThrows(IOException.class, () -> {
            // When
            attachmentStore.storeAttachment(mockMultipartFile);
        });
    }

    @Test
    void 파일_업로드_실패시_S3_롤백이_수행된다() throws Exception {
        // Given
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                "test-image.png",
                "image/png",
                "FakeImageData".getBytes()
        );

        // S3 업로드 실패 시나리오 모킹
        when(amazonS3Client.putObject(any(PutObjectRequest.class)))
                .thenThrow(new RuntimeException("S3 upload failed"));

        // When & Then
        assertThrows(IOException.class, () -> {
            attachmentStore.storeAttachment(mockMultipartFile);
        });

        // 롤백 메소드 호출 확인
        verify(amazonS3Client, times(1)).deleteObject(anyString(), anyString());
    }


}