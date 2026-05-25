package dzamsheed.uz.xxcback.dto.request;

import dzamsheed.uz.xxcback.domain.enums.CertificateType;
import jakarta.validation.constraints.NotBlank;

public record CertificateRequest(
        @NotBlank String titleUz,
        String titleRu,
        String titleEn,
        String descriptionUz,
        String descriptionRu,
        String descriptionEn,
        Integer year,
        String issuedBy,
        CertificateType type,
        Integer sortOrder
) {}
