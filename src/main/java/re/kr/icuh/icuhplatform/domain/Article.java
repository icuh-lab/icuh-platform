package re.kr.icuh.icuhplatform.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.testcontainers.shaded.com.google.common.hash.Hashing;
import re.kr.icuh.icuhplatform.dto.NewFileRequestJsonConverter;
import re.kr.icuh.icuhplatform.dto.UpdateArticleRequestJsonConverter;
import re.kr.icuh.icuhplatform.dto.article.UpdateArticleRequest;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "articles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "author")
    private String author;

    @Column(name = "author_organization")
    private String authorOrganization;

    @Column(name = "department")
    private String department;

    @Column(name = "temp_password")
    private String tempPassword;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "views")
    private Integer views;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ArticleStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_type_id")
    private DocumentType documentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_domain_id")
    private SubjectDomain subjectDomain;

    @Column(name = "source")
    private String source;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "pending_update")
    @Convert(converter = UpdateArticleRequestJsonConverter.class)
    private UpdateArticleRequest pendingUpdate;

    @Column(name = "pending_file_update")
    @Convert(converter = NewFileRequestJsonConverter.class)
    private List<UpdateArticleRequest.NewFileRequest> pendingFileUpdate;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FileEntity> files = new ArrayList<>();

    @Builder
    public Article(String title, String description, String author, String authorOrganization, String department, String tempPassword, Integer views, ArticleStatus status, DocumentType documentType, SubjectDomain subjectDomain, String source, Boolean isDeleted, LocalDateTime deletedAt) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.authorOrganization = authorOrganization;
        this.department = department;
        this.tempPassword = sha256Encode(tempPassword);
        this.views = views == null ? 0 : views;
        this.status = status == null ? ArticleStatus.PENDING : status;
        this.documentType = documentType;
        this.subjectDomain = subjectDomain;
        this.source = source;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
        this.pendingUpdate = null;
        this.pendingFileUpdate = null;
    }

    public String sha256Encode(String tempPassword) {
        return Hashing.sha256().hashString(tempPassword, StandardCharsets.UTF_8).toString();
    }

    // 소프트 삭제 메서드
    public void softDelete() {
        this.status = ArticleStatus.DELETED_PENDING;
        this.updatedAt = LocalDateTime.now();
    }

    // 조회수 증가 메서드
    public void increaseViews() {
        this.views++;
    }

    // 비밀번호 검증 메서드
    public boolean validatePassword(String password) {
        // 실제 구현에서는 암호화된 비밀번호 비교 로직 필요
        return this.tempPassword.equals(sha256Encode(password));
    }

    public void setPendingUpdate(UpdateArticleRequest request) {
        this.pendingUpdate = request;
    }

    public void setPendingFileUpdate(List<UpdateArticleRequest.NewFileRequest> request) {
        this.pendingFileUpdate = request;
    }
}
