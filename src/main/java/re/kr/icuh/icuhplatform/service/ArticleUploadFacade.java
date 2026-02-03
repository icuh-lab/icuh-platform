package re.kr.icuh.icuhplatform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import re.kr.icuh.icuhplatform.dto.article.CreateArticleRequest;
import re.kr.icuh.icuhplatform.dto.file.CompleteUploadRequestDto;

@Service
@RequiredArgsConstructor
public class ArticleUploadFacade {

    private final ArticleService articleService;
    private final FileService fileService;

    @Transactional
    public void createFullArticle(CreateArticleRequest request, CompleteUploadRequestDto fileRequest, String location) {
        articleService.createArticle(request);
        fileService.createFileEntity(fileRequest, location);
    }
}
