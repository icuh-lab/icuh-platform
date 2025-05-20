package re.kr.icuh.icuhplatform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import re.kr.icuh.icuhplatform.dto.article.CreateArticleRequest;
import re.kr.icuh.icuhplatform.service.ArticleService;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FileStorageController {

    private final ArticleService articleService;

    @PostMapping("/articles")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<String> createArticle(@RequestPart @Valid CreateArticleRequest request, @RequestPart List<MultipartFile> files) throws IOException {

        articleService.createArticle(request, files);

        return ResponseEntity.ok("파일 업로드 성공");
    }
}
