package dzamsheed.uz.xxcback.domain.entity;

import dzamsheed.uz.xxcback.domain.enums.Availability;
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
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name_uz", nullable = false, length = 200)
    private String nameUz;

    @Column(name = "name_ru", length = 200)
    private String nameRu;

    @Column(name = "name_en", length = 200)
    private String nameEn;

    @Column(name = "description_uz", columnDefinition = "TEXT")
    private String descriptionUz;

    @Column(name = "description_ru", columnDefinition = "TEXT")
    private String descriptionRu;

    @Column(name = "description_en", columnDefinition = "TEXT")
    private String descriptionEn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Category category;

    private Integer price;

    @Column(length = 10)
    @Builder.Default
    private String currency = "UZS";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Availability availability = Availability.AVAILABLE;

    @Column(name = "is_custom_order", nullable = false)
    @Builder.Default
    private Boolean isCustomOrder = false;

    @Column(length = 100)
    private String dimensions;

    @Column(length = 50)
    private String weight;

    @Column(length = 200)
    private String material;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC, createdAt ASC")
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
