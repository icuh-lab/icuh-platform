package re.kr.icuh.icuhplatform.dto.article;

public record ArticleRequest(
        String documentType,
        String subjectDomain,
        String source,
        String query
) {}
