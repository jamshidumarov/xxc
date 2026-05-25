package dzamsheed.uz.xxcback.service;

import dzamsheed.uz.xxcback.dto.request.LoginRequest;
import dzamsheed.uz.xxcback.dto.response.AuthResponse;
import dzamsheed.uz.xxcback.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        String accessToken = jwtUtils.generateAccessToken(request.username());
        String refreshToken = jwtUtils.generateRefreshToken(request.username());
        return AuthResponse.of(accessToken, refreshToken, jwtUtils.getAccessTokenExpiry());
    }

    public AuthResponse refresh(String refreshToken) {
        if (!jwtUtils.validateToken(refreshToken) || !jwtUtils.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        String username = jwtUtils.extractUsername(refreshToken);
        String newAccessToken = jwtUtils.generateAccessToken(username);
        String newRefreshToken = jwtUtils.generateRefreshToken(username);
        return AuthResponse.of(newAccessToken, newRefreshToken, jwtUtils.getAccessTokenExpiry());
    }
}
