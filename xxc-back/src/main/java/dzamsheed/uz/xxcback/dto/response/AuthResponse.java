package dzamsheed.uz.xxcback.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        String tokenType
) {
    public static AuthResponse of(String accessToken, String refreshToken, long expiresIn) {
        return new AuthResponse(accessToken, refreshToken, expiresIn, "Bearer");
    }
}
