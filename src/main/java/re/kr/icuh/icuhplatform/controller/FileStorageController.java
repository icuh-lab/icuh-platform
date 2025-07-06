package re.kr.icuh.icuhplatform.controller;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import re.kr.icuh.icuhplatform.dto.article.ArticleListResponse;
import re.kr.icuh.icuhplatform.dto.article.CreateArticleRequest;
import re.kr.icuh.icuhplatform.global.common.ApiResponse;
import re.kr.icuh.icuhplatform.service.ArticleService;

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

    @GetMapping("/articles")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<List<ArticleListResponse>>> findArticles() {

        List<ArticleListResponse> articles = articleService.findArticles();
        ApiResponse<List<ArticleListResponse>> response = new ApiResponse<>().success(articles);

        return ResponseEntity.ok(response);
    }
}
