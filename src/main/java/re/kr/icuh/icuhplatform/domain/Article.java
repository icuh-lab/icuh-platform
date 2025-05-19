package re.kr.icuh.icuhplatform.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
    @JoinColumn(name = "classification_id")
    private Classification classification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_type_id")
    private ServiceType serviceType;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FileEntity> files = new ArrayList<>();

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
