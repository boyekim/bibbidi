package com.bibbidi.domain.user.dto;

import com.bibbidi.domain.user.User;

public record UserResponse(
    Long id,
    String email,
    String name
) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getName());
    }
}
