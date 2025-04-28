package re.kr.icuh.icuhplatform.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import re.kr.icuh.icuhplatform.service.AttachmentService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AttachmentApiController {

    private final AttachmentService attachmentService;

    @GetMapping("/test")
    public String test() {
        return "<h1> 안녕하세요 가뭄 정보 빅데이터 플랫폼에 오신 것을 환영합니다. </h1>";
    }

    @PostMapping("/attachments")
    public String attachments(@RequestPart("attachment") List<MultipartFile> file) throws IOException {

        attachmentService.createAttachment(file);

        return "ok";
    }
}
