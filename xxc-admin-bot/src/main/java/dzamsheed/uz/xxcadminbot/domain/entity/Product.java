package dzamsheed.uz.xxcadminbot.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {

    @Id
    private UUID id;

    @Column(name = "name_uz")
    private String nameUz;

    @Column(name = "name_ru")
    private String nameRu;

    @Column(name = "description_uz")
    private String descriptionUz;

    private String category;
    private Integer price;
    private String currency;
    private String availability;
    private String dimensions;
    private String weight;
    private String material;

    @Column(name = "is_custom_order")
    private Boolean isCustomOrder;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private Instant createdAt;
}
