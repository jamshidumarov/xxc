package dzamsheed.uz.xxcback.dto.request;

import dzamsheed.uz.xxcback.domain.enums.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PortfolioRequest(
        @NotBlank String titleUz,
        String titleRu,
        String titleEn,
        @NotNull Category category,
        String descriptionUz,
        String descriptionRu,
        String descriptionEn,
        Integer year,
        Boolean isFeatured
) {}
