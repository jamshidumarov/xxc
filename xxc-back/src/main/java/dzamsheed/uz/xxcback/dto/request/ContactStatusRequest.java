package dzamsheed.uz.xxcback.dto.request;

import dzamsheed.uz.xxcback.domain.enums.ContactStatus;
import jakarta.validation.constraints.NotNull;

public record ContactStatusRequest(@NotNull ContactStatus status) {}
