package com.bibbidi.support.auth;

import com.bibbidi.domain.user.User;
import com.bibbidi.domain.user.UserRepository;
import com.bibbidi.support.exception.UnauthorizedException;
import com.bibbidi.support.exception.errors.UserErrors;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class AuthArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String USER_ID_SESSION_KEY = "userId";

    private final UserRepository userRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Auth.class)
            && parameter.getParameterType().equals(User.class);
    }

    @Override
    public Object resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            throw new UnauthorizedException(UserErrors.LOGIN_REQUIRED);
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new UnauthorizedException(UserErrors.LOGIN_REQUIRED);
        }

        Object userId = session.getAttribute(USER_ID_SESSION_KEY);
        if (!(userId instanceof Long id)) {
            throw new UnauthorizedException(UserErrors.LOGIN_REQUIRED);
        }

        return userRepository.findById(id)
            .orElseThrow(() -> new UnauthorizedException(UserErrors.LOGIN_REQUIRED));
    }
}
