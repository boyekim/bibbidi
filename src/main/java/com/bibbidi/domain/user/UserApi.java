package com.bibbidi.domain.user;

import com.bibbidi.domain.user.dto.UserLoginRequest;
import com.bibbidi.domain.user.dto.UserResponse;
import com.bibbidi.domain.user.dto.UserSignupRequest;
import com.bibbidi.support.auth.Auth;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserApi {

    private final UserService userService;

    @PostMapping("/api/users/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody UserSignupRequest request) {
        return ResponseEntity.ok(userService.signup(request));
    }

    @PostMapping("/api/users/login")
    public ResponseEntity<UserResponse> login(
        @Valid @RequestBody UserLoginRequest request,
        HttpServletRequest httpRequest
    ) {
        HttpSession session = httpRequest.getSession(true);

        return ResponseEntity.ok(userService.login(request, session));
    }

    @GetMapping("/api/users/me")
    public ResponseEntity<UserResponse> me(@Auth User user) {
        return ResponseEntity.ok(userService.get(user));
    }

    @PostMapping("/api/users/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            userService.logout(session, response);
        }

        return ResponseEntity.noContent().build();
    }
}
