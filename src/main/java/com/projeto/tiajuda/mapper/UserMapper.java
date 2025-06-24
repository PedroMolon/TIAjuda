package com.projeto.tiajuda.mapper;

import com.projeto.tiajuda.dto.request.RegisterRequest;
import com.projeto.tiajuda.dto.response.LoginResponse;
import com.projeto.tiajuda.dto.response.UserResponse;
import com.projeto.tiajuda.entity.User;
import com.projeto.tiajuda.entity.enums.Role;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Component
public class UserMapper {

    public User toEntity(RegisterRequest request) {
        if (request == null) {
            return null;
        }

        Role assignedRole = Role.valueOf(request.getProfileType().toUpperCase());

        Set<Role> roles = Collections.singleton(assignedRole);

        return new User(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                roles
        );
    }

    public static UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRoles(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public LoginResponse toLoginResponse(User user, String jwtToken) {
        if (user == null) {
            return null;
        }

        return new LoginResponse(
                jwtToken,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRoles()
        );
    }

}
