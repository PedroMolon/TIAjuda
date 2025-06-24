package com.projeto.tiajuda.configuration.security;

import lombok.Builder;

import java.util.List;

@Builder
public record JWTUserData(Long id, String name, String email, List<String> roles) {

}
