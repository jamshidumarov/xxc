package dzamsheed.uz.xxcadminbot.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "portfolio_items")
@Getter
@Setter
public class PortfolioItem {

    @Id
    private UUID id;

    @Column(name = "title_uz")
    private String titleUz;

    @Column(name = "title_ru")
    private String titleRu;

    @Column(name = "description_uz")
    private String descriptionUz;

    private String category;
    private Integer year;

    @Column(name = "is_featured")
    private Boolean isFeatured;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private Instant createdAt;
}
