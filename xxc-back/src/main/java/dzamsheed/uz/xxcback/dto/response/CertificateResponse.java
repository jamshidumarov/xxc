package dzamsheed.uz.xxcback.dto.response;

import dzamsheed.uz.xxcback.domain.entity.Certificate;
import dzamsheed.uz.xxcback.domain.enums.CertificateType;

import java.time.Instant;
import java.util.UUID;

public record CertificateResponse(
        UUID id,
        String titleUz,
        String titleRu,
        String titleEn,
        String descriptionUz,
        String descriptionRu,
        String descriptionEn,
        Integer year,
        String issuedBy,
        String imageUrl,
        CertificateType type,
        Integer sortOrder,
        Instant createdAt
) {
    public static CertificateResponse from(Certificate c) {
        return new CertificateResponse(
                c.getId(), c.getTitleUz(), c.getTitleRu(), c.getTitleEn(),
                c.getDescriptionUz(), c.getDescriptionRu(), c.getDescriptionEn(),
                c.getYear(), c.getIssuedBy(), c.getImageUrl(),
                c.getType(), c.getSortOrder(), c.getCreatedAt()
        );
    }
}
