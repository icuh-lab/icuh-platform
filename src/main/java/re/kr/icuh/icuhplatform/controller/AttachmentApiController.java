package re.kr.icuh.icuhplatform.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AttachmentApiController {

    @GetMapping("/test")
    public String test() {
        return "<h1> 안녕하세요 가뭄 정보 빅데이터 플랫폼에 오신 것을 환영합니다. </h1>";
    }
}
