package re.kr.icuh.icuhplatform.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import re.kr.icuh.icuhplatform.dto.CategoryResponse;
import re.kr.icuh.icuhplatform.service.CategoryService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/article-categories")
    public CategoryResponse getCategory() {
        return categoryService.getCategories();
    }
}
