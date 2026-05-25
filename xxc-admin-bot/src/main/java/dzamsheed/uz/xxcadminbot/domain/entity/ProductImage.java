package dzamsheed.uz.xxcadminbot.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "product_images")
@Getter
@Setter
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name = "cloudinary_id")
    private String cloudinaryId;

    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary = false;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;
}
