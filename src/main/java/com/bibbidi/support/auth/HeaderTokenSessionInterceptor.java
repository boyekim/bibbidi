package com.bibbidi.support.auth;

import com.bibbidi.domain.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class HeaderTokenSessionInterceptor implements HandlerInterceptor {

    private static final String TOKEN_HEADER = "token";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = resolveToken(request);
        if (token == null) {
            return true;
        }

        HttpSession session = request.getSession(true);
        if (!userService.isLoggedIn(session)) {
            userService.loginWithToken(token, session);
        }

        return true;
    }

    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER);
        if (hasText(token)) {
            return token.trim();
        }

        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        if (hasText(authorization) && authorization.startsWith(BEARER_PREFIX)) {
            String bearerToken = authorization.substring(BEARER_PREFIX.length()).trim();
            if (hasText(bearerToken)) {
                return bearerToken;
            }
        }

        return null;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
