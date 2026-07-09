package com.bibbidi.domain.user;

import com.bibbidi.domain.user.dto.UserLoginRequest;
import com.bibbidi.domain.user.dto.UserResponse;
import com.bibbidi.domain.user.dto.UserSignupRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 가입된 이메일입니다.");
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
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
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
