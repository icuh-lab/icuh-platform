package re.kr.icuh.icuhplatform.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.testcontainers.shaded.com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static re.kr.icuh.icuhplatform.domain.Article.ArticleStatus.ACTIVE;

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
    @JoinColumn(name = "classification_id")
    private Classification classification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_type_id")
    private ServiceType serviceType;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FileEntity> files = new ArrayList<>();

    @Builder
    public Article(String title, String description, String author, String authorOrganization, String department, String tempPassword, Integer views, ArticleStatus status, Classification classification, ServiceType serviceType) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.authorOrganization = authorOrganization;
        this.department = department;
        this.tempPassword = sha256Encode(tempPassword);
        this.views = views == null ? 0 : views;
        this.status = status == null ? ACTIVE : status;
        this.classification = classification;
        this.serviceType = serviceType;
    }

    public String sha256Encode(String tempPassword) {
        return Hashing.sha256().hashString(tempPassword, StandardCharsets.UTF_8).toString();
    }

    // 소프트 삭제 메서드
    public void softDelete() {
        this.status = ArticleStatus.DELETED;
    }

    // 조회수 증가 메서드
    public void increaseViews() {
        this.views++;
    }

    // 파일 추가 메서드
    public void addFile(FileEntity file) {
        this.files.add(file);
        file.setArticle(this);
    }

    // 비밀번호 검증 메서드
    public boolean validatePassword(String password) {
        // 실제 구현에서는 암호화된 비밀번호 비교 로직 필요
        return this.tempPassword.equals(password);
    }

    // 게시글 상태를 나타내는 열거형
    public enum ArticleStatus {
        ACTIVE, DELETED
    }
}
