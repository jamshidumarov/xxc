package dzamsheed.uz.xxcback.dto.response;

import dzamsheed.uz.xxcback.domain.entity.Product;
import dzamsheed.uz.xxcback.domain.enums.Availability;
import dzamsheed.uz.xxcback.domain.enums.Category;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String nameUz,
        String nameRu,
        String nameEn,
        String descriptionUz,
        String descriptionRu,
        String descriptionEn,
        Category category,
        Integer price,
        String currency,
        Availability availability,
        Boolean isCustomOrder,
        String dimensions,
        String weight,
        String material,
        List<ImageResponse> images,
        Instant createdAt,
        Instant updatedAt
) {
    public record ImageResponse(UUID id, String url, Boolean isPrimary, Integer sortOrder) {}

    public static ProductResponse from(Product p) {
        List<ImageResponse> images = p.getImages().stream()
                .map(img -> new ImageResponse(img.getId(), img.getUrl(), img.getIsPrimary(), img.getSortOrder()))
                .toList();
        return new ProductResponse(
                p.getId(), p.getNameUz(), p.getNameRu(), p.getNameEn(),
                p.getDescriptionUz(), p.getDescriptionRu(), p.getDescriptionEn(),
                p.getCategory(), p.getPrice(), p.getCurrency(),
                p.getAvailability(), p.getIsCustomOrder(),
                p.getDimensions(), p.getWeight(), p.getMaterial(),
                images, p.getCreatedAt(), p.getUpdatedAt()
        );
    }
}
