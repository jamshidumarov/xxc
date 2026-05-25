package dzamsheed.uz.xxcback.dto.request;

import dzamsheed.uz.xxcback.domain.enums.ContactType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ContactRequest(
        @NotBlank String name,
        @NotBlank @Pattern(regexp = "^\\+?[0-9\\s\\-()]{7,20}$") String phone,
        String email,
        @NotNull ContactType type,
        String message
) {}
