package dzamsheed.uz.xxcback.domain.entity;

import dzamsheed.uz.xxcback.domain.enums.CertificateType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "certificates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title_uz", nullable = false, length = 300)
    private String titleUz;

    @Column(name = "title_ru", length = 300)
    private String titleRu;

    @Column(name = "title_en", length = 300)
    private String titleEn;

    @Column(name = "description_uz", columnDefinition = "TEXT")
    private String descriptionUz;

    @Column(name = "description_ru", columnDefinition = "TEXT")
    private String descriptionRu;

    @Column(name = "description_en", columnDefinition = "TEXT")
    private String descriptionEn;

    private Integer year;

    @Column(name = "issued_by", length = 200)
    private String issuedBy;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "cloudinary_id")
    private String cloudinaryId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private CertificateType type = CertificateType.CERTIFICATE;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
