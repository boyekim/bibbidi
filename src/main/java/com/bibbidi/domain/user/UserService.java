package com.bibbidi.domain.user;

import com.bibbidi.domain.user.dto.UserLoginRequest;
import com.bibbidi.domain.user.dto.UserResponse;
import com.bibbidi.domain.user.dto.UserSignupRequest;
import com.bibbidi.support.exception.ConflictException;
import com.bibbidi.support.exception.UnauthorizedException;
import com.bibbidi.support.exception.errors.UserErrors;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private static final String USER_ID_SESSION_KEY = "userId";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse signup(UserSignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException(UserErrors.DUPLICATE_EMAIL);
        }

        User user = new User(
            request.email(),
            passwordEncoder.encode(request.password()),
            request.name()
        );
        userRepository.save(user);

        return UserResponse.from(user);
    }

    public UserResponse login(UserLoginRequest request, HttpSession session) {
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new UnauthorizedException(UserErrors.INVALID_LOGIN));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException(UserErrors.INVALID_LOGIN);
        }

        session.setAttribute(USER_ID_SESSION_KEY, user.getId());

        return UserResponse.from(user);
    }

    public void logout(HttpSession session, HttpServletResponse response) {
        session.invalidate();

        Cookie sessionCookie = new Cookie("JSESSIONID", null);
        sessionCookie.setMaxAge(0);
        sessionCookie.setPath("/");
        response.addCookie(sessionCookie);
    }

    public UserResponse get(User user) {
        return UserResponse.from(user);
    }
}
