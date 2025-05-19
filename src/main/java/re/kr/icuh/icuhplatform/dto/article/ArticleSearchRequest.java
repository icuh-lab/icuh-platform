package re.kr.icuh.icuhplatform.dto.article;

public record ArticleSearchRequest(
        Long classificationId,
        Long serviceTypeId,
        Long extensionId,
        String keyword,
        Integer page,
        Integer size
) {
    public ArticleSearchRequest {
        if (page == null) page = 0;
        if (size == null) size = 10;
    }
}
