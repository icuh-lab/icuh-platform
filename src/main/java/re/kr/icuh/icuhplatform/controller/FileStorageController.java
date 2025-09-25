package re.kr.icuh.icuhplatform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import re.kr.icuh.icuhplatform.dto.article.*;
import re.kr.icuh.icuhplatform.global.common.ApiResponse;
import re.kr.icuh.icuhplatform.global.common.SuccessCode;
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
    public ResponseEntity<ApiResponse<?>> createArticle(@RequestPart @Valid CreateArticleRequest request,
                                                @RequestPart List<MultipartFile> files) throws IOException {

        articleService.createArticle(request, files);

        return ResponseEntity.ok(ApiResponse.created(SuccessCode.ARTICLE_CREATE_SUCCESS));
    }

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

    @PostMapping("/articles/{id}")
    public ResponseEntity<ApiResponse<ArticleResponse>> requestArticleUpdate(@PathVariable Long id, @RequestBody RequestStatusChange request) {
        return ResponseEntity.ok(ApiResponse.success(articleService.requestArticleUpdate(id, request)));
    }

    @PatchMapping("/articles/{id}")
    public ResponseEntity<ApiResponse<?>> updateArticle(@PathVariable Long id, @RequestPart @Valid UpdateArticleRequest request, @RequestPart List<MultipartFile> files) throws IOException {
        articleService.updateArticle(id, request, files);

        return ResponseEntity.ok(ApiResponse.success(SuccessCode.ARTICLE_UPDATE_PENDING));
    }

    @DeleteMapping("/articles/{id}")
    public ResponseEntity<ApiResponse<?>> requestArticleDelete(@PathVariable Long id, @RequestBody RequestStatusChange request) {
        articleService.requestArticleDelete(id, request);

        return ResponseEntity.ok(ApiResponse.success(SuccessCode.ARTICLE_DELETE_PENDING));
    }
}
