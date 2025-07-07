package re.kr.icuh.icuhplatform.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "files")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @Column(name = "original_filename")
    private String originalFilename;

    @Column(name = "stored_filename")
    private String storedFilename;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "extension_id", nullable = false)
    private Extension extension;


    @Builder
    public FileEntity(Article article, String originalFilename, String storedFilename, String filePath, Long fileSize, Extension extension) {
        this.article = article;
        this.originalFilename = originalFilename;
        this.storedFilename = storedFilename;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.status = FileStatus.ACTIVE;
        this.extension = extension;
    }

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
