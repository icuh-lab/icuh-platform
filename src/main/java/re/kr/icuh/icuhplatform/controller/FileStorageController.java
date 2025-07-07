package re.kr.icuh.icuhplatform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import re.kr.icuh.icuhplatform.dto.article.ArticleListResponse;
import re.kr.icuh.icuhplatform.dto.article.ArticleResponse;
import re.kr.icuh.icuhplatform.dto.article.CreateArticleRequest;
import re.kr.icuh.icuhplatform.global.common.ApiResponse;
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

    @GetMapping("/articles")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<List<ArticleListResponse>>> findArticles(@RequestParam(required = false) String documentType, @RequestParam(required = false) String subjectDomain, @RequestParam(required = false) String source) {

        List<ArticleListResponse> articles = articleService.findArticles(documentType, subjectDomain, source);
        ApiResponse<List<ArticleListResponse>> response = new ApiResponse<>().success(articles);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/articles/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<ArticleResponse>> getArticle(@PathVariable Long id) {

        ArticleResponse article = articleService.findArticleById(id);
        ApiResponse<ArticleResponse> response = new ApiResponse<>().success(article);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/articles/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteArticle(@PathVariable Long id, @RequestParam String tempPassword) {

        articleService.deleteArticle(id, tempPassword);

        return "delete Ok";
    }
}
