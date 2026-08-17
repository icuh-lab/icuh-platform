package re.kr.icuh.icuhplatform.article.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArticleTest {

    private Article createArticle() {
        return Article.builder()
                .title("제목")
                .description("내용")
                .author("작성자")
                .authorOrganization("작성기관")
                .department("부서")
                .tempPassword("password")
                .views(0)
                .status(ArticleStatus.PENDING)
                .documentType(null)
                .subjectDomain(null)
                .source("출처")
                .isDeleted(false)
                .deletedAt(null)
                .build();
    }

    @Test
    @DisplayName("delete() 호출 시 status가 DELETED_PENDING으로 변경되고 isDeleted와 deletedAt이 함께 세팅된다")
    void delete_소프트삭제_표식이_함께_세팅된다() {
        // given
        Article article = createArticle();

        // when
        article.delete();

        // then
        assertThat(article.getStatus()).isEqualTo(ArticleStatus.DELETED_PENDING);
        assertThat(article.getIsDeleted()).isTrue();
        assertThat(article.getDeletedAt()).isNotNull();
    }
}
