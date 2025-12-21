package re.kr.icuh.icuhplatform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import re.kr.icuh.icuhplatform.dto.article.*;
import re.kr.icuh.icuhplatform.global.common.ApiResponse;
import re.kr.icuh.icuhplatform.global.common.SuccessCode;
import re.kr.icuh.icuhplatform.service.ArticleService;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping("/articles")
    public ResponseEntity<ApiResponse<Page<ArticleListResponse>>> findArticles(@RequestParam(required = false) String documentType,
                                                                               @RequestParam(required = false) String subjectDomain,
                                                                               @RequestParam(required = false) String source,
                                                                               @RequestParam(required = false) String query,
                                                                               @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(ApiResponse.success(articleService.findArticles(documentType, subjectDomain, source, query, pageable)));
    }

    @GetMapping("/articles/{id}")
    public ResponseEntity<ApiResponse<ArticleResponse>> getArticle(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(articleService.findArticleById(id)));
    }

    @PostMapping("/articles")
    public ResponseEntity<ApiResponse<CreateArticleResponse>> createArticle(@Valid @RequestBody CreateArticleRequest request) {
        return ResponseEntity.ok(ApiResponse.success(articleService.createArticle(request)));
    }

    @PostMapping("/articles/{id}")
    public ResponseEntity<ApiResponse<ArticleResponse>> requestArticleUpdate(@PathVariable Long id, @RequestBody RequestStatusChange request) {
        return ResponseEntity.ok(ApiResponse.success(articleService.requestArticleUpdate(id, request)));
    }

    @PatchMapping("/articles/{id}")
    public ResponseEntity<ApiResponse<?>> updateArticle(@PathVariable Long id, @Valid @RequestBody UpdateArticleRequest request) {
        return ResponseEntity.ok(ApiResponse.success(articleService.updateArticle(id, request)));
    }

    @DeleteMapping("/articles/{id}")
    public ResponseEntity<ApiResponse<?>> requestArticleDelete(@PathVariable Long id, @RequestBody RequestStatusChange request) {
        articleService.requestArticleDelete(id, request);

        return ResponseEntity.ok(ApiResponse.success(SuccessCode.ARTICLE_DELETE_PENDING));
    }
}
