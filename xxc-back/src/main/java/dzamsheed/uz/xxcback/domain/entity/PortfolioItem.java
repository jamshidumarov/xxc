package dzamsheed.uz.xxcback.domain.entity;

import dzamsheed.uz.xxcback.domain.enums.Category;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "portfolio_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title_uz", nullable = false, length = 200)
    private String titleUz;

    @Column(name = "title_ru", length = 200)
    private String titleRu;

    @Column(name = "title_en", length = 200)
    private String titleEn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Category category;

    @Column(name = "description_uz", columnDefinition = "TEXT")
    private String descriptionUz;

    @Column(name = "description_ru", columnDefinition = "TEXT")
    private String descriptionRu;

    @Column(name = "description_en", columnDefinition = "TEXT")
    private String descriptionEn;

    private Integer year;

    @Column(name = "is_featured", nullable = false)
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "portfolioItem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<PortfolioImage> images = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
