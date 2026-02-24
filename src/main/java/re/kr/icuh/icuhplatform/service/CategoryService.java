package re.kr.icuh.icuhplatform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import re.kr.icuh.icuhplatform.dto.CategoryResponse;
import re.kr.icuh.icuhplatform.dto.DocumentTypesResponse;
import re.kr.icuh.icuhplatform.dto.SubjectDomainsResponse;
import re.kr.icuh.icuhplatform.repository.DocumentTypeRepository;
import re.kr.icuh.icuhplatform.repository.SubjectDomainRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final DocumentTypeRepository documentTypeRepository;
    private final SubjectDomainRepository subjectDomainRepository;

    public CategoryResponse getCategories() {
        List<DocumentTypesResponse> documentTypesResponses = documentTypeRepository.findByStatus("ACTIVE")
                .stream()
                .map(DocumentTypesResponse::from)
                .toList();

        List<SubjectDomainsResponse> subjectDomainsResponses = subjectDomainRepository.findByStatus("ACTIVE")
                .stream()
                .map(SubjectDomainsResponse::from)
                .toList();

        return new CategoryResponse(documentTypesResponses, subjectDomainsResponses);
    }
}
