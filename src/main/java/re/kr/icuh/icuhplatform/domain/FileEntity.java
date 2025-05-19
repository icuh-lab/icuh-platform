package re.kr.icuh.icuhplatform.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "files")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @Column(nullable = false, length = 255, name = "original_filename")
    private String originalFilename;

    @Column(nullable = false, length = 255, name = "stored_filename")
    private String storedFilename;

    @Column(nullable = false, length = 512, name = "file_path")
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @CreationTimestamp
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "extension_id", nullable = false)
    private Extension extension;

    // 소프트 삭제 메서드
    public void softDelete() {
        this.status = FileStatus.DELETED;
    }

    public void setArticle(Article article) {
        article = this.article;
    }

    // 파일 상태를 나타내는 열거형
    public enum FileStatus {
        ACTIVE, DELETED
    }
}
