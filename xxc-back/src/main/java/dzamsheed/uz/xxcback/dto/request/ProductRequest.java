package dzamsheed.uz.xxcback.dto.request;

import dzamsheed.uz.xxcback.domain.enums.Availability;
import dzamsheed.uz.xxcback.domain.enums.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductRequest(
        @NotBlank String nameUz,
        String nameRu,
        String nameEn,
        String descriptionUz,
        String descriptionRu,
        String descriptionEn,
        @NotNull Category category,
        Integer price,
        Availability availability,
        Boolean isCustomOrder,
        String dimensions,
        String weight,
        String material
) {}
