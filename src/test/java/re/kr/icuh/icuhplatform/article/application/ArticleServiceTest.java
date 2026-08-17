package re.kr.icuh.icuhplatform.article.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import re.kr.icuh.icuhplatform.article.dto.request.CreateArticleWithFilesRequest;
import re.kr.icuh.icuhplatform.article.infra.ArticleRepository;
import re.kr.icuh.icuhplatform.category.infra.DocumentTypeRepository;
import re.kr.icuh.icuhplatform.category.infra.SubjectDomainRepository;
import re.kr.icuh.icuhplatform.file.infra.FileRepository;
import re.kr.icuh.icuhplatform.global.common.BusinessException;
import re.kr.icuh.icuhplatform.global.common.ErrorCode;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private DocumentTypeRepository documentTypeRepository;

    @Mock
    private SubjectDomainRepository subjectDomainRepository;

    @InjectMocks
    private ArticleService articleService;

    @Test
    @DisplayName("문서 성격 검증 실패 시 실제 예외(DOCUMENT_TYPE_NOT_FOUND)가 전파되어야 하며 FILE_SIZE_EXCEEDED로 뭉개지지 않는다")
    void createArticleWithFiles_documentTypeNotFound_propagatesRealException() {
        // given
        when(documentTypeRepository.findByCode(anyString())).thenReturn(Optional.empty());

        CreateArticleWithFilesRequest request = new CreateArticleWithFilesRequest(
                null,            // title
                null,            // description
                null,            // author
                null,            // authorOrganization
                null,            // department
                null,            // tempPassword
                "INVALID_CODE",  // documentTypeCode
                null,            // subjectDomainCode
                null,            // source
                null             // completedFiles
        );

        // when & then
        assertThatThrownBy(() -> articleService.createArticleWithFiles(request))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(ErrorCode.DOCUMENT_TYPE_NOT_FOUND);

        BusinessException thrown = (BusinessException) org.assertj.core.api.Assertions.catchThrowable(
                () -> articleService.createArticleWithFiles(request));
        assertThat(thrown.getErrorCode()).isEqualTo(ErrorCode.DOCUMENT_TYPE_NOT_FOUND);
        assertThat(thrown.getErrorCode()).isNotEqualTo(ErrorCode.FILE_SIZE_EXCEEDED);
    }
}
