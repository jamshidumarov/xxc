package dzamsheed.uz.xxcback.dto.response;

import dzamsheed.uz.xxcback.domain.entity.PortfolioItem;
import dzamsheed.uz.xxcback.domain.enums.Category;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PortfolioResponse(
        UUID id,
        String titleUz,
        String titleRu,
        String titleEn,
        Category category,
        String descriptionUz,
        String descriptionRu,
        String descriptionEn,
        Integer year,
        Boolean isFeatured,
        List<ImageResponse> images,
        Instant createdAt
) {
    public record ImageResponse(UUID id, String url, Boolean isPrimary, Integer sortOrder) {}

    public static PortfolioResponse from(PortfolioItem item) {
        List<ImageResponse> images = item.getImages().stream()
                .map(img -> new ImageResponse(img.getId(), img.getUrl(), img.getIsPrimary(), img.getSortOrder()))
                .toList();
        return new PortfolioResponse(
                item.getId(), item.getTitleUz(), item.getTitleRu(), item.getTitleEn(),
                item.getCategory(), item.getDescriptionUz(), item.getDescriptionRu(), item.getDescriptionEn(),
                item.getYear(), item.getIsFeatured(), images, item.getCreatedAt()
        );
    }
}
