package re.kr.icuh.icuhplatform.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "ATTACHMENT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attachment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "original_name")
    private String originalName;

    @Column(name = "saved_path")
    private String savedPath;

    @Column(name = "saved_name")
    private String savedName;

    @Column(name = "extension_name")
    private String extensionName;

    @Column(name = "size")
    private Integer size;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Attachment(String originalName, String savedPath, String savedName, String extensionName, Integer size, LocalDateTime createdAt) {
        this.originalName = originalName;
        this.savedPath = savedPath;
        this.savedName = savedName;
        this.extensionName = extensionName;
        this.size = size;
        this.createdAt = createdAt;
    }

}
