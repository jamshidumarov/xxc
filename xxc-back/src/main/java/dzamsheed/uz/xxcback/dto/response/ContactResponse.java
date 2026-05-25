package dzamsheed.uz.xxcback.dto.response;

import dzamsheed.uz.xxcback.domain.entity.Contact;
import dzamsheed.uz.xxcback.domain.enums.ContactStatus;
import dzamsheed.uz.xxcback.domain.enums.ContactType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ContactResponse(
        UUID id,
        String name,
        String phone,
        String email,
        ContactType type,
        String message,
        ContactStatus status,
        List<String> attachmentUrls,
        Instant createdAt
) {
    public static ContactResponse from(Contact c) {
        List<String> urls = c.getAttachments().stream()
                .map(a -> a.getUrl())
                .toList();
        return new ContactResponse(
                c.getId(), c.getName(), c.getPhone(), c.getEmail(),
                c.getType(), c.getMessage(), c.getStatus(), urls, c.getCreatedAt()
        );
    }
}
