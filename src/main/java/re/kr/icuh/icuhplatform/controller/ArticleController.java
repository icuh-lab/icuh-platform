package re.kr.icuh.icuhplatform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import re.kr.icuh.icuhplatform.dto.article.*;
import re.kr.icuh.icuhplatform.dto.file.CreateArticleWithFilesRequest;
import re.kr.icuh.icuhplatform.global.common.ApiResponse;
import re.kr.icuh.icuhplatform.service.ArticleService;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping("/articles")
    public ApiResponse<Page<ArticleListResponse>> findArticles(
            ArticleRequest request,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ApiResponse.success(articleService.findArticles(request, pageable));
    }

    @GetMapping("/articles/{id}")
    public ApiResponse<ArticleDetailResponse> getArticle(@PathVariable Long id) {
        return ApiResponse.success(articleService.findArticleById(id));
    }

    @PostMapping("/articles")
    public ApiResponse<CreateArticleResponse> createArticle(@Valid @RequestBody CreateArticleRequest request) {
        return ApiResponse.success(articleService.createArticle(request));
    }

    @PostMapping("/articles/{id}")
    public ApiResponse<ArticleDetailResponse> modifyArticleStatus(@PathVariable Long id, @RequestBody ModifyArticleStatusRequest request) {
        return ApiResponse.success(articleService.modifyArticleStatus(id, request));
    }

    @PatchMapping("/articles/{id}")
    public ApiResponse<Void> updateArticle(@PathVariable Long id, @Valid @RequestBody UpdateArticleRequest request) {
        articleService.updateArticle(id, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/articles/{articleId}")
    public ApiResponse<Void> deleteArticle(@PathVariable Long articleId, @Valid @RequestBody DeleteArticleRequest request) {
        articleService.deleteArticle(articleId, request);
        return ApiResponse.success();
    }

    // 새로운 통합 API
    @PostMapping("/articles-with-files")
    public ApiResponse<CreateArticleResponse> createArticleWithFiles(@Valid @RequestBody CreateArticleWithFilesRequest request) {
        return ApiResponse.success(articleService.createArticleWithFiles(request));
    }
}
