package re.kr.icuh.icuhplatform.dto.article;

public record ArticleSearchRequest(
        String documentType,
        String subjectDomain,
        String source,
        String query
) {}
