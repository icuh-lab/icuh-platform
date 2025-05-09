package re.kr.icuh.icuhplatform.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import re.kr.icuh.icuhplatform.service.AttachmentService;
import re.kr.icuh.icuhplatform.util.AttachmentStore;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AttachmentApiController {

    private final AttachmentService attachmentService;
    private final AttachmentStore attachmentStore;

    @GetMapping("/test")
    public String test() {
        return "<h1> 안녕하세요 가뭄 정보 빅데이터 플랫폼에 오신 것을 환영합니다. </h1>";
    }

    @PostMapping("/attachments")
    public String attachments(@RequestPart("attachment") List<MultipartFile> file) throws IOException {

        attachmentService.createAttachment(file);

        return "ok";
    }

    @PostMapping("/large-attachment")
    public ResponseEntity<String> largeAttachments(@RequestParam("attachment") MultipartFile multipartFile) throws Exception {

        try {
            // MultipartFile을 임시 File로 변환
            File file = File.createTempFile("temp_", multipartFile.getOriginalFilename());
            multipartFile.transferTo(file);

            // S3에 Multipart Upload 실행
            String fileUrl = attachmentStore.uploadLargeAttachment(file);

            // 임시 파일 삭제
            if (!file.delete()) {
                log.warn("임시 파일 삭제 실패: {}", file.getPath());
            }

            return ResponseEntity.ok(fileUrl);

        } catch (Exception e) {
            log.error("파일 업로드 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 업로드 중 오류가 발생했습니다.");
        }
    }
}
