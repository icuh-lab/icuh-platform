package re.kr.icuh.icuhplatform.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUpload;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import re.kr.icuh.icuhplatform.service.FileStorageService;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FileStorageController {

    private final FileStorageService fileStorageService;

    @GetMapping("/test")
    @ResponseStatus(value = HttpStatus.OK)
    public String test() {
        return "<h1> 안녕하세요 가뭄 정보 빅데이터 플랫폼에 오신 것을 환영합니다. </h1>";
    }

    @PostMapping("/attachments")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<String> attachments(@RequestPart("attachment") List<MultipartFile> file) throws IOException {
        fileStorageService.createAttachment(file);

        return ResponseEntity.ok("파일 업로드 성공");
    }

    @PostMapping("/large-attachment")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<String> largeAttachments(@RequestParam("attachment") MultipartFile multipartFile) throws Exception {
        fileStorageService.uploadLargeFile(multipartFile);

        return ResponseEntity.ok("대용량 파일 업로드 성공");
    }
}
