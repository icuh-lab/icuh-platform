package re.kr.icuh.icuhplatform.article.dto.request;

public record ArticleRequest(
        String documentType,
        String subjectDomain,
        String source,
        String query
) {}
